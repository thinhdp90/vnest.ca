package ai.kitt.vnest.speechmanager.speechonline

import ai.kitt.snowboy.service.TriggerOfflineService
import ai.kitt.vnest.App
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.*


class SpeechRecognizerManager(
        val context: Context,
        private var onResultReady: OnResultReady,
        var speechRecognizer: SpeechRecognizer,
        var onRecreateVoiceRecord: OnRecreateVoiceRecord,
        var onErrorTimeOut: () -> Unit
) {
    companion object {
        var INSTANCE: SpeechRecognizerManager? = null

        @JvmStatic
        fun getInstance(context: Context, onResultReady: OnResultReady, speechRecognizer: SpeechRecognizer, onRecreateVoiceRecord: OnRecreateVoiceRecord, onError: OnSpeechError): SpeechRecognizerManager {
                INSTANCE = SpeechRecognizerManager(context, onResultReady, speechRecognizer, onRecreateVoiceRecord, onErrorTimeOut = {
                    onError.onErrorTimeOut()
                })
            return INSTANCE!!
        }

        @JvmStatic
        fun getInstance() = INSTANCE
    }

    val TAG = "Vnest"
    private var speechIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var timeOut = 2000
    private var isListening = false
    val speechListener = SpeechRecognitionListener(
            onResultReady, {
        if (isListening) {
            recreateVoiceRecord()
        } else {
            muteVolume(false)
        }
    }, {
        muteVolume(it)
    }, onErrorTimeOut)

    init {
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault());
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        speechIntent.putExtra(
                RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
                Locale.getDefault()
        );
        speechIntent.putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                timeOut
        )
        speechRecognizer.setRecognitionListener(speechListener)
    }

    fun restartListening() {
        muteVolume(true)
        speechRecognizer.stopListening()
        speechRecognizer.startListening(speechIntent)
        isListening = true
    }

    fun startListening() {
        TriggerOfflineService.stopService(App.get())
            try {
                if (!isListening) {
                    speechRecognizer.stopListening()
                    speechRecognizer.startListening(speechIntent)
                    isListening = true
                }
            } catch (e: Exception) {
                recreateVoiceRecord()
            }

    }

    private fun recreateVoiceRecord() {
        speechRecognizer.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(speechListener)
        speechRecognizer.startListening(speechIntent)
        onRecreateVoiceRecord.onRecreate()
    }

    fun stopListening() {
        speechRecognizer.let {
            speechRecognizer.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer.setRecognitionListener(speechListener)
            onRecreateVoiceRecord.onRecreate()
            it.stopListening()
            it.cancel()
        }
        isListening = false
        muteVolume(false)
        TriggerOfflineService.startService(App.get(),false)
    }

    fun destroy() {
        isListening = false
        speechRecognizer.let {
            it.stopListening();
            it.cancel();
            it.destroy();
        }
    }

    fun muteVolume(shouldMute: Boolean) {
        val alarmManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        if (alarmManager != null) {
            alarmManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, shouldMute)
            alarmManager.setStreamMute(AudioManager.STREAM_ALARM, shouldMute)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val adjustMuteOrUnMute = if (shouldMute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
                alarmManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, adjustMuteOrUnMute, 0)
                alarmManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, adjustMuteOrUnMute, 0)
                alarmManager.adjustStreamVolume(AudioManager.STREAM_ALARM, adjustMuteOrUnMute, 0)
                alarmManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, adjustMuteOrUnMute, 0)
            } else {
                alarmManager.setStreamMute(AudioManager.STREAM_MUSIC, shouldMute)
            }
        }
    }
    interface OnRecreateVoiceRecord {
        fun onRecreate()
    }
    interface OnSpeechError{
        fun onErrorTimeOut()
    }

}