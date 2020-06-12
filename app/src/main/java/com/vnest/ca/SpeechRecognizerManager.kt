package com.vnest.ca

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.*


class SpeechRecognizerManager(
        val context: Context,
        var onResultReady: OnResultReady,
        var speechRecognizer: SpeechRecognizer
) {
    private var speechIntent: Intent
    private var timeOut = 3000
    private var isListening = false
    private var mAudioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var streamVolume: Int? = null
    val speechListener = SpeechRecognitionListener(
            onResultReady,
            mAudioManager
    ) {
        restartListening()
    }

    init {
        streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
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
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
        speechRecognizer.stopListening()
        speechRecognizer.cancel()
        speechRecognizer.startListening(speechIntent)
        isListening = true

    }

    fun startListening() {
        if (!isListening) {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.startListening(speechIntent)
            isListening = true

        }
    }

    fun stopListening() {
        if (isListening) {
            speechRecognizer.let {
                it.stopListening()
                it.cancel()
            }
            isListening = false
        }
    }

    fun destroy() {
        isListening = false
        speechRecognizer.let {
            it.stopListening();
            it.cancel();
            it.destroy();
        }
    }

}