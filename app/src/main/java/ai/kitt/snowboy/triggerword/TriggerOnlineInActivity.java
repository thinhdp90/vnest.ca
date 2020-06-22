package ai.kitt.snowboy.triggerword;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;

import java.util.List;
import java.util.Objects;

import ai.kitt.snowboy.activities.MainActivity;
import ai.kitt.snowboy.util.Utils;


public class TriggerOnlineInActivity extends Service implements SpeechDelegate, Speech.stopDueToDelay {

    private String LOG_TAG = "TriggerOnlineInActivity";
    public static SpeechDelegate delegate;

    private String[] listHotWord = new String[]{"xinchaoalex", "okealex", "xinchaoem",
            "okealice", "xinchaoalice", "alexa", "Alaska"};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Log.e("On start conmmand", "ok");
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                ((AudioManager) Objects.requireNonNull(
                        getSystemService(Context.AUDIO_SERVICE))).setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "onStartCommand ...");
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
        Log.e("Onstart command", "Binder");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public TriggerOnlineInActivity getServiceInstance() {
            return TriggerOnlineInActivity.this;
        }
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
//        Log.e("Speech change", "" + value);
    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        for (String partial : results) {
            Log.d(LOG_TAG, "onSpeechPartialResults: " + partial);
        }
    }

    @Override
    public void onSpeechResult(String result) {
        Log.d(LOG_TAG, "onSpeechResult:" + result);
        if (!TextUtils.isEmpty(result)) {
            result = Utils.formatString(result).replace(" ", "").toLowerCase().trim();
            for (String hw : listHotWord) {
                if (result.contains(hw)) {
//                    handler.post(serviceRunnable);
                    Intent intent = new Intent();
                    intent.setAction(TriggerBroadCast.ACTION_TURN_MIC_ON);
                    sendBroadcast(intent);

//                    Speech.getInstance().shutdown();
//                    activity.serviceCallback(); //Update Activity (client) by the implementd callback
                }
            }
        }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        Log.d(LOG_TAG, "onSpecifiedCommandPronounced ...");
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

    }

    //callbacks interface for communication with service clients!
    public interface Callbacks {
        public void serviceCallback();
    }

    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            Speech.getInstance().shutdown();
            activity.serviceCallback(); //Update Activity (client) by the implementd callback
        }
    };

    public void registerClient(Activity activity) {
        this.activity = (Callbacks) activity;
    }
}

