//package ai.kitt.snowboy.triggerword;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.IBinder;
//import android.os.Vibrator;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//
//import org.kaldi.Assets;
//import org.kaldi.Model;
//import org.kaldi.RecognitionListener;
//import org.kaldi.SpeechRecognizer;
//import org.kaldi.Vosk;
//import ai.kitt.snowboy.activities.MainActivity;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//
//import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
//
//
//public class Trigger extends Service implements RecognitionListener {
//
//    static {
//        System.loadLibrary("kaldi_jni");
//    }
//
//    public enum Type {
//        BACKGROUND, DETECT_START_VOICE
//    }
//
//    static private final int STATE_START = 0;
//    static private final int STATE_READY = 1;
//    static private final int STATE_DONE = 2;
//    static private final int STATE_FILE = 3;
//    static private final int STATE_MIC = 4;
//
//    private static final String KEY_START_SERVICE = "type";
//    private static final int BACKGROUND = 0;
//    private static final int DETECT_START_VOICE = 1;
//
//    /* Used to handle permission request */
//    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
//
//    public static void startService(Context context, Type type) {
//        Intent intent = new Intent(context, Trigger.class);
//        if (type == Type.BACKGROUND) {
//            intent.putExtra(KEY_START_SERVICE, BACKGROUND);
//        } else {
//            intent.putExtra(KEY_START_SERVICE, DETECT_START_VOICE);
//        }
//        context.startService(intent);
//    }
//
//    public static void stopService(Context context) {
//        Intent intent = new Intent(context, Trigger.class);
//        context.stopService(intent);
//    }
//
//    private Model model;
//    private org.kaldi.SpeechRecognizer recognizerOffline;
//
//
//    private String LOG_TAG = "TRIGGER";
//    private SpeechRecognizer mRecognizer;
//    private Vibrator mVibrator;
//    private static int sensibility = 10;
//    private static final String WAKEWORD_SEARCH = "hey";
//    private static final String KEYWORD_SEARCH = "oke";
//
//    private int keyStartService;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        setupTrigger();
//        keyStartService = intent.getIntExtra(KEY_START_SERVICE, BACKGROUND);
////        if(keyStartService ==)
////        stopSelf();
//        return START_NOT_STICKY;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (recognizerOffline != null) {
//            recognizerOffline.removeListener(this);
//            recognizerOffline.cancel();
//            recognizerOffline.shutdown();
//            recognizerOffline = null;
//            Log.d(LOG_TAG, "Kaldi Recognizer was shutdown");
//        }
//    }
//// Trigger Recognition
//
//    /**
//     * Setup the Recognizer with a sensitivity value in the range [1..100]
//     * Where 1 means no false alarms but many true matches might be missed.
//     * and 100 most of the words will be correctly detected, but you will have many false alarms.
//     */
//    private void setupTrigger() {
//        new SetupTask(this).execute();
//    }
//
//    private void stopTrigger() {
//        if (recognizerOffline != null) {
//            recognizerOffline.removeListener(this);
//            recognizerOffline.cancel();
//            recognizerOffline.shutdown();
//            recognizerOffline = null;
//            Log.d(LOG_TAG, "Kaldi Recognizer was shutdown");
//        }
//    }
//
//    private void lauchingApp() {
////        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.kaldi.demo");
////        Intent dialogIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
////        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startActivity(launchIntent);
////
////        Intent mStartActivity = new Intent(this, MainActivity.class);
////        int mPendingIntentId = 123456;
////        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
////        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
////        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
////        System.exit(0);
//
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        Runtime.getRuntime().exit(0);
//    }
//
//
//    //
//    // RecognitionListener Implementation
//    //
//
//    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
//        WeakReference<Trigger> activityReference;
//
//        SetupTask(Trigger activity) {
//            this.activityReference = new WeakReference<>(activity);
//        }
//
//        @Override
//        protected Exception doInBackground(Void... params) {
//            try {
//                Assets assets = new Assets(activityReference.get());
//                File assetDir = assets.syncAssets();
//                Log.e("KaldiDemo", "Sync files in the folder " + assetDir.toString());
//
//                Vosk.SetLogLevel(0);
//
//                activityReference.get().model = new Model(assetDir.toString() + "/model-android");
//            } catch (IOException e) {
//                return e;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Exception result) {
//            if (result != null) {
//                activityReference.get().setErrorState(result.getMessage());
//            } else {
//                activityReference.get().setUiState(STATE_READY);
//            }
//        }
//    }
//
//
//    private void setUiState(int state) {
//        switch (state) {
//            case STATE_START:
//                Log.e("State", "STATE_START");
//                break;
//            case STATE_READY:
//                Log.e("State", "STATE_READY");
//                recognizeMicrophone();
//                break;
//            case STATE_DONE:
//                Log.e("State", "STATE_DONE");
//
//                break;
//            case STATE_FILE:
//                Log.e("State", "STATE_FILE");
//
//                break;
//            case STATE_MIC:
//                Log.e("State", "STATE_MIC");
//
//
//                break;
//        }
//    }
//
//
//    @Override
//    public void onResult(String hypothesis) {
//        Log.e("Result", hypothesis);
//        if (hypothesis.contains("xin ch") || hypothesis.contains("xin") || hypothesis.contains("sim")) {
//            stopTrigger();
//            if (keyStartService == BACKGROUND) {
//                lauchingApp();
//            } else {
//                sendBroadcast(new Intent(TriggerBroadCast.ACTION_TURN_MIC_ON));
//            }
//
//        } else {
//            Log.e("Start activity", "False");
//        }
//    }
//
//    @Override
//    public void onPartialResult(String hypothesis) {
////        if(hypothesis.contains("xin ch")) {
////            speechRecognizerManager.startListening();
////        }
//        if (hypothesis.toLowerCase().contains("xin ch")) {
////            stopTrigger();
//            if (keyStartService == BACKGROUND) {
//                lauchingApp();
//            }
//
//        } else {
//            Log.e("Start activity", "False");
//        }
//    }
//
//    @Override
//    public void onError(Exception e) {
//        setErrorState(e.getMessage());
//    }
//
//    @Override
//    public void onTimeout() {
//        recognizerOffline.cancel();
//        recognizerOffline = null;
//        setUiState(STATE_READY);
//    }
//
//
//    private void setErrorState(String message) {
//
//    }
//
//
//    public void recognizeMicrophone() {
//        if (recognizerOffline != null) {
//            setUiState(STATE_DONE);
//            recognizerOffline.cancel();
//            recognizerOffline = null;
//        } else {
//            setUiState(STATE_MIC);
//            try {
//                recognizerOffline = new org.kaldi.SpeechRecognizer(model);
//                recognizerOffline.addListener(this);
//                recognizerOffline.startListening();
//            } catch (IOException e) {
//                setErrorState(e.getMessage());
//            }
//        }
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void startForeground() {
//        String NOTIFICATION_CHANNEL_ID = "example.permanence";
//        String channelName = "Background Service";
//        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
////        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
////        restartServiceIntent.setPackage(getPackageName());
////
////        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
////        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
////        alarmService.set(
////                AlarmManager.ELAPSED_REALTIME,
////                SystemClock.elapsedRealtime() + 1000,
////                restartServicePendingIntent);
//        super.onTaskRemoved(rootIntent);
//        if (recognizerOffline != null) {
//            recognizerOffline.removeListener(this);
//            recognizerOffline.cancel();
//            recognizerOffline.shutdown();
//            recognizerOffline = null;
//            Log.d(LOG_TAG, "Kaldi Recognizer was shutdown");
//        }
//
//    }
//
//}
