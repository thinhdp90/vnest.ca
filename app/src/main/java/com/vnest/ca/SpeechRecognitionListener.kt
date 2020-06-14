package com.vnest.ca

import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter


class SpeechRecognitionListener(
        private val mListener: OnResultReady,
        private var audioManager: AudioManager,
        var onErrorNoMatch: () -> Unit
) : RecognitionListenerAdapter() {
    private val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

    //    override fun onReadyForSpeech(params: Bundle?) {
//        Log.e("Onready for speech", "Onready for speech")
//    }
//
    override fun onRmsChanged(rmsdB: Float) {
        Log.e("onRmsChanged", rmsdB.toString())
    }
//
//    override fun onBufferReceived(buffer: ByteArray?) {
//        Log.e("onBufferReceived", "onBufferReceived")
//    }
//
//    override fun onEvent(eventType: Int, params: Bundle?) {
//        Log.e("onEvent", "onEvent")
//    }

    override fun onBeginningOfSpeech() {
        Log.e("onBeginningOfSpeech", "onBeginningOfSpeech")
        resetVolume()
    }

    override fun onEndOfSpeech() {
        Log.e("onEndOfSpeech", "onEndOfSpeech")
        resetVolume()
    }

    @Synchronized
    override fun onError(error: Int) {
        Log.e("OnError", error.toString())
        when (error) {

            SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                onErrorNoMatch()
            }
            SpeechRecognizer.ERROR_NETWORK -> {

            }

        }

    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.e("onPartialResults", "onPartialResults")
        if (partialResults != null) {
            val texts =
                    partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
            texts?.let { mListener.onStreamResult(it) }
        }
    }

    override fun onResults(results: Bundle?) {
        resetVolume()
        if (results != null) {
            val text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (text != null) {
                mListener.onResults(text)
            }
        }
    }
    private fun resetVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
    }

}