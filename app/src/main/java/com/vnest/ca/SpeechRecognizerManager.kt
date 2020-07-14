package com.vnest.ca

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
        var speechRecognizer: SpeechRecognizer
) {
    val TAG = "Vnest"
    private var speechIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    private var timeOut = 20000
    private var isListening = false
    val speechListener = SpeechRecognitionListener(
            onResultReady, {
        if (isListening) {
            restartListening()
        }
    }, {
        muteVolume(it)
    })

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
        speechRecognizer.cancel()
        speechRecognizer.startListening(speechIntent)
        isListening = true

    }

    fun startListening() {
        try {
            if (!isListening) {
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
                speechRecognizer.startListening(speechIntent)
                isListening = true
            }
        } catch (e: Exception) {
            recreateVoicRecog()
        }

    }

    fun recreateVoicRecog() {
        speechRecognizer.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(speechListener)
    }

    fun stopListening() {
        Log.e(TAG, "=============Stop listening=============")
        speechRecognizer.let {
            it.stopListening()
            it.cancel()
        }
        isListening = false
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
//            try {
//                alarmManager.setStreamMute(AudioManager.STREAM_RING, shouldMute)
//                alarmManager.setStreamMute(AudioManager.STREAM_SYSTEM, shouldMute)
//            } catch (e: Exception) {
//                Log.e("Mute volume", e.javaClass.name)
//            }
        }
    }

}