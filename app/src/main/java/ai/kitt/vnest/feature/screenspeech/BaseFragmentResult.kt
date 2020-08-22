package ai.kitt.vnest.feature.screenspeech

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment

abstract class BaseFragmentResult : Fragment() {
//    var timer = Handler()
//    var timerSpeech = Runnable {
//        val message = Message()
//        message.what = FragmentResult.SPEECH_TIME_OUT
//        message.target = handlerSpeechRecordTimeManager
//        message.sendToTarget()
//    }
//    var handlerSpeechRecordTimeManager: Handler = object : Handler(Looper.getMainLooper()) {
//        override fun handleMessage(msg: Message) {
//            super.handleMessage(msg)
//            when (msg.what) {
//                FragmentResult.SPEECH_TIME_OUT -> if (FragmentResult.isPlayingRecognition) {
//                    finishRecognition()
//                    getMainActivity().getTextToSpeech().speak("Xin lỗi, không thể phát hiện giọng nói của bạn!", false)
//                }
//                FragmentResult.START_SPEECH_TIME_COUNT -> timer.postDelayed(timerSpeech, FragmentResult.MAX_SPEECH_TIME_OUT * 1000.toLong())
//                FragmentResult.STOP_SPEECH_TIME_COUNT -> {
//                    timer.removeCallbacks(timerSpeech)
//                    speechCountTime = FragmentResult.MAX_SPEECH_TIME_OUT
//                }
//            }
//        }
//    }
}