package com.kt.triggerword;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;

import java.util.List;
import java.util.Objects;

public class TriggerBindService extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    public static SpeechDelegate delegate;
    private final IBinder mBinder = new LocalBinder();

    public static void startService(Context context) {
        Intent intent = new Intent(context, TriggerBindService.class);
        context.startService(intent);
    }

    public class LocalBinder extends Binder {
        public TriggerBindService getServiceInstance() {
            return TriggerBindService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ((AudioManager) Objects.requireNonNull(
                    getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TriggerConstant.LOG_TAG, "onStartCommand ...");
        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
//            muteBeepSoundOfRecorder();
        } else {
            try {
                Speech.getInstance().stopTextToSpeech();
                Speech.getInstance().startListening(null, this);
            } catch (SpeechRecognitionNotAvailable exc) {
//                showSpeechNotSupportedDialog();

            } catch (GoogleVoiceTypingDisabledException exc) {
//                showEnableGoogleVoiceTyping();
            }
            muteBeepSoundOfRecorder();
        }
        return Service.START_STICKY;
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        Log.d(TriggerConstant.LOG_TAG, "onSpecifiedCommandPronounced ...");
        try {
            ((AudioManager) Objects.requireNonNull(getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);

            if (Speech.getInstance().isListening()) {
                muteBeepSoundOfRecorder();
                Speech.getInstance().stopListening();
            } else {
                Speech.getInstance().stopTextToSpeech();
                try {
                    Speech.getInstance().startListening(null, this);
                } catch (SpeechRecognitionNotAvailable | GoogleVoiceTypingDisabledException speechRecognitionNotAvailable) {
                    speechRecognitionNotAvailable.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        muteBeepSoundOfRecorder();
    }

    @Override
    public void onStartOfSpeech() {

    }

    @Override
    public void onSpeechRmsChanged(float value) {

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d(TriggerConstant.LOG_TAG, "onSpeechPartialResults: " + partial);
        }
    }

    @Override
    public void onSpeechResult(String result) {
        if (!TextUtils.isEmpty(result)) {
            result = TriggerUtils.formatString(result).replace(" ", "").toLowerCase().trim();
            for (String hw : TriggerConstant.listHotWord) {
                if (result.contains(hw)) {
                    Intent intent = new Intent();
                    intent.setAction(TriggerBroadCast.ACTION_TURN_MIC_ON);
                    sendBroadcast(intent);
                }
            }
        }
    }

    public void muteBeepSoundOfRecorder() {
        AudioVolumeManager.getInstance(this).muteVolume(true);
    }
}
