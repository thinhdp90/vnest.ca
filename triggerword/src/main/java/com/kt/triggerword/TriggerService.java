package com.kt.triggerword;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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

import static com.kt.triggerword.TriggerConstant.LOG_TAG;

public class TriggerService extends Service implements SpeechDelegate, Speech.stopDueToDelay {
    private final static String[] listHotWord = new String[]{"xinchaoalex", "okealex", "xinchaoem",
            "okealice", "xinchaoalice", "alexa", "Alaska"};

    public static void startService(Context context) {
        Intent intent = new Intent(context, TriggerService.class);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, TriggerService.class);
        context.stopService(intent);
    }

    public SpeechDelegate delegate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ((AudioManager) Objects.requireNonNull(
                    getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(LOG_TAG, "onStartCommand ...");
        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);

        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            try {
                Speech.getInstance().stopTextToSpeech();
                Speech.getInstance().startListening(null, this);
            } catch (SpeechRecognitionNotAvailable exc) {
//                showSpeechNotSupportedDialog();

            } catch (GoogleVoiceTypingDisabledException exc) {
//                showEnableGoogleVoiceTyping();
            }
            AudioVolumeManager.getInstance(this).muteVolume(true);
        }
        return Service.START_STICKY;
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            ((AudioManager) Objects.requireNonNull(
                    getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);

            if (Speech.getInstance().isListening()) {
                AudioVolumeManager.getInstance(this).muteVolume(true);
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
        AudioVolumeManager.getInstance(this).muteVolume(true);
    }

    @Override
    public void onStartOfSpeech() {
        Log.e(LOG_TAG,"onStartOfSpeech");

    }

    @Override
    public void onSpeechRmsChanged(float value) {
//        Log.e("onSpeechRmsChanged",""+value);

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String word: results){
            Log.e("onSpeechPartialResults",word);
        }
    }

    @Override
    public void onSpeechResult(String result) {
        Log.e("onSpeechResult",result);

        if (!TextUtils.isEmpty(result)) {
            result = TriggerUtils.formatString(result).replace(" ", "").toLowerCase().trim();
            for (String hw : TriggerConstant.listHotWord) {
                if (result.contains(hw)) {
                    lauchingApp();
                }
            }
        }
    }

    public void lauchingApp() {
        Log.d(LOG_TAG, "====================== wake up ============================");
        Speech.getInstance().shutdown();
        Intent intent = new Intent();
        intent.setAction(TriggerBroadCast.ACTION_RESTART_APP);
        sendBroadcast(intent);
    }
}
