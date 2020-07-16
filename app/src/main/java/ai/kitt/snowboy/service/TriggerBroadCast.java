package ai.kitt.snowboy.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import ai.kitt.snowboy.util.AppUtil;

public class TriggerBroadCast extends BroadcastReceiver {
    public final static String ACTION_TURN_MIC_ON = "turn on";
    public final static String ACTION_TURN_MIC_OFF = "turn off";
    public final static String ACTION_START_APP = "start_app";
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
        Log.e("Action","sdfsdfsdf"+action);
        assert action != null;
        if (action.equals(ACTION_TURN_MIC_ON)) {
            Log.e("Action", "Turn on mic");
            onHandleTrigger.onActionTurnOn();
            return;
        }
        if (action.equals(ACTION_START_APP)) {
            Log.e("Action", "Start app from broadcast");
            try {
//                Intent startAppIntent = new Intent(context, activity);
//                startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(startAppIntent);
//                Runtime.getRuntime().exit(0);

//                Intent i = context.getPackageManager().
//                        getLaunchIntentForPackage(context.getPackageName());
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);

                Intent i = new Intent();
                i.setClassName("ai.kitt.snowboy", "ai.kitt.snowboy.activities.splash.SplashActivity");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

//                Intent dialogIntent = new Intent(context, activity);
//                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(dialogIntent);
            } catch (Exception e) {
                Log.e("Error start app", e.getMessage(), e);
            }
            return;
        }
        if (action.equals(ACTION_TURN_MIC_OFF)) {
            Log.e("Action", "Turn off mic");
            onHandleTrigger.onActionTurnOff();
        }
    }

    public interface OnHandleTrigger {
        void onActionTurnOn();

        void onActionTurnOff();

        void onActionStartApp();
    }

}
