package com.vnest.ca

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter


class SpeechRecognitionListener(
        private val mListener: OnResultReady,
        var onErrorNoMatch: () -> Unit,
        val onMuteVolume: (shouldMute: Boolean) -> Unit
) : RecognitionListenerAdapter() {

    override fun onReadyForSpeech(params: Bundle?) {
    }


    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onRmsChanged(rmsdB: Float) {
//        Log.e("onRmsChanged", rmsdB.toString())
    }

    override fun onBeginningOfSpeech() {
        Log.e("onBeginningOfSpeech", "onBeginningOfSpeech")
        onMuteVolume(true)
    }

    override fun onEndOfSpeech() {
        Log.e("onEndOfSpeech", "onEndOfSpeech")
        onMuteVolume(false)
    }

    @Synchronized
    override fun onError(error: Int) {
        Log.e("OnError", error.toString())
        when (error) {

            SpeechRecognizer.ERROR_NO_MATCH, SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                onMuteVolume(false)
                onErrorNoMatch()
            }
            SpeechRecognizer.ERROR_NETWORK -> {

            }

        }

    }

    override fun onPartialResults(partialResults: Bundle?) {
//        onMuteVolume(false)
        Log.e("onPartialResults", "onPartialResults")
        if (partialResults != null) {
            val texts = partialResults.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
            texts?.let { mListener.onStreamResult(it) }
        }
    }

    override fun onResults(results: Bundle?) {
        onMuteVolume(false)

        if (results != null) {
            val text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (text != null) {
                mListener.onResults(text)
            }
        }
    }

}