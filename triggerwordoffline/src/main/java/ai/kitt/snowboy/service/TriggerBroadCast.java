package ai.kitt.snowboy.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class TriggerBroadCast extends BroadcastReceiver {
    public final static String ACTION_TURN_MIC_ON = "turn on";
    public final static String ACTION_TURN_MIC_OFF = "turn off";
    public final static String ACTION_START_APP = "start_app";
    private final static String TAG = "Trigger broadcast";
    private OnHandleTrigger onHandleTrigger;
    private Class<?> activity;

    public static TriggerBroadCast initBroadCast(Context context, OnHandleTrigger onHandleTrigger, Class<?> activity) {
        TriggerBroadCast triggerBroadCast = new TriggerBroadCast(onHandleTrigger, activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TURN_MIC_ON);
        intentFilter.addAction(ACTION_TURN_MIC_OFF);
        intentFilter.addAction(ACTION_START_APP);
        context.registerReceiver(triggerBroadCast, intentFilter);
        return triggerBroadCast;
    }

    public static void unregisterBroadCast(Context context, TriggerBroadCast triggerBroadCast) {
        if (triggerBroadCast != null) {
            context.unregisterReceiver(triggerBroadCast);
        }
    }

    public TriggerBroadCast(OnHandleTrigger onHandleTrigger, Class<?> activity) {
        this.onHandleTrigger = onHandleTrigger;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if (action.equals(ACTION_TURN_MIC_ON)) {
            Log.e(TAG, "Turn on mic");
            onHandleTrigger.onActionTurnOn();
            return;
        }
        if (action.equals(ACTION_START_APP)) {
            Log.e(TAG, "Start app from broadcast");
            try {
                Intent i = new Intent(context, Class.forName("ai.kitt.vnest.feature.splash.SplashActivity"));
//                i.setClassName("ai.kitt.vnest", "ai.kitt.vnest.feature.splash.SplashActivity");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return;
        }
        if (action.equals(ACTION_TURN_MIC_OFF)) {
            Log.e(TAG, "Turn off mic");
            onHandleTrigger.onActionTurnOff();
        }
    }

    public interface OnHandleTrigger {
        void onActionTurnOn();

        void onActionTurnOff();

        void onActionStartApp();
    }

}
