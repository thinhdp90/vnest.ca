package ai.kitt.snowboy.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.kwabenaberko.openweathermaplib.models.common.Main;

import ai.kitt.snowboy.App;
import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.activities.MainActivity;
import ai.kitt.snowboy.activities.splash.SplashActivity;
import ai.kitt.snowboy.audio.AudioDataReceivedListener;
import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.audio.PlaybackThread;
import ai.kitt.snowboy.audio.RecordingThread;
import ai.kitt.snowboy.util.AppUtil;

public class TriggerOfflineService extends Service {
    public static int keyStartService;
    private RecordingThread recordingThread;
    private PlaybackThread playbackThread;

    public final static int WAKE_UP = 0;
    public final static int TURN_ON_MIC = 1;
    public static String KEY_START = "extra_trigger";
    public String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };



    public static void startService(Context context, boolean isWakeUp) {
        Intent intent = new Intent(context, TriggerOfflineService.class);
        if (isWakeUp) {
            intent.putExtra(KEY_START, WAKE_UP);
        }
        try {
            context.startService(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, TriggerOfflineService.class);
        try {
            context.stopService(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAlexaDetect();
    }

    @SuppressLint("HandlerLeak")
    public Handler handlerHotWordDetect = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(android.os.Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch (message) {
                case MSG_ACTIVE:
                    updateLog(" ============== Detected Offline ==============");
                    updateIfActive();
                    break;
                case MSG_INFO:
                    updateLog(" ============== " + message + "==============");
                    break;
                case MSG_VAD_SPEECH:
                    updateLog(" ============== normal voice" + " Offline==============");
                    break;
                case MSG_VAD_NOSPEECH:
                    updateLog(" ============== no speech" + " Offline==============");
                    break;
                case MSG_ERROR:
                    startOfflineRecording();
                    updateLog(" ============== Error " + msg.toString() + " Offline==============");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        keyStartService = intent.getIntExtra(KEY_START, TURN_ON_MIC);
        startOfflineRecording();
        return START_NOT_STICKY;
    }

    private void initAlexaDetect() {
        AppResCopy.copyResFromAssetsToSD(this);
        recordingThread = RecordingThread.getInstance(handlerHotWordDetect, AudioDataSaver.getInstance());
        playbackThread = PlaybackThread.getInstance();
    }


    public void updateLog(final String text) {
        Log.e("Speech offline log", text);
    }

    public void startOfflineRecording() {

        if (recordingThread != null) {
            recordingThread.startRecording();
        }
    }

    public void updateIfActive() {
        stopOfflineRecording();
        Intent intent = new Intent();
        switch (keyStartService) {
            case WAKE_UP:
                intent.setAction(TriggerBroadCast.ACTION_START_APP);
                break;
            case TURN_ON_MIC:
                intent.setAction(TriggerBroadCast.ACTION_TURN_MIC_ON);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + keyStartService);
        }
        sendBroadcast(intent);
//        stopSelf();
    }

    public void stopOfflineRecording() {
        try {
            recordingThread.stopRecording();
            updateLog(" ==============> Offline recording stopped ==============");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    public static final String TAG = "VnestService";

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopOfflineRecording();
        recordingThread = null;
        playbackThread = null;
    }
}
