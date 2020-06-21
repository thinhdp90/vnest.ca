package ai.kitt.snowboy.triggerword;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;

import java.util.List;
import java.util.Objects;

import ai.kitt.snowboy.activities.MainActivity;
import ai.kitt.snowboy.util.Utils;


public class TriggerOnline extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    private String LOG_TAG = "TriggerOnline";
    public static SpeechDelegate delegate;

    private String[] listHotWord = new String[]{"xinchaoalex", "okealex", "xinchaoem",
            "okealice", "xinchaoalice", "alexa", "Alaska"};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
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
            Log.d(LOG_TAG, "onSpeechPartialResults: " + partial);
        }
    }

    private void lauchingApp() {
        Log.d(LOG_TAG, "====================== wake up ============================");
        Speech.getInstance().shutdown();
        Intent dialogIntent = new Intent(this, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }

    @Override
    public void onSpeechResult(String result) {
        Log.d("LOG_TAG", "onSpeechResult:" + result);
        if (!TextUtils.isEmpty(result)) {
            result = Utils.formatString(result).replace(" ", "").toLowerCase().trim();
            for (String hw : listHotWord) {
                if (result.contains(hw)) {
                    lauchingApp();
                }
            }
        }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }

            if (Speech.getInstance().isListening()) {
                muteBeepSoundOfRecorder();
                Speech.getInstance().stopListening();
            } else {
                Speech.getInstance().stopTextToSpeech();
                try {
                    Speech.getInstance().startListening(null, this);
                } catch (SpeechRecognitionNotAvailable speechRecognitionNotAvailable) {
                    speechRecognitionNotAvailable.printStackTrace();
                } catch (GoogleVoiceTypingDisabledException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        muteBeepSoundOfRecorder();
    }


    /**
     * Function to remove the beep sound of voice recognizer.
     */
    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            } else {
                amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //Restarting the service if it is removed.
//        PendingIntent service =
//                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
//                        new Intent(getApplicationContext(), TriggerOnline.class), PendingIntent.FLAG_ONE_SHOT);
//
//        Intent dialogIntent = new Intent(this, MainActivity.class);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(dialogIntent);
    }
}