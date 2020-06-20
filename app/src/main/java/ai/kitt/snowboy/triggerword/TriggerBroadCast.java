package ai.kitt.snowboy.triggerword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kwabenaberko.openweathermaplib.models.common.Main;

import ai.kitt.snowboy.activities.MainActivity;

public class TriggerBroadCast extends BroadcastReceiver {
    public static String ACTION_TURN_MIC_ON = "turn on";
    public static String ACTION_TURN_MIC_OFF = "turn off";
    public static String ACTION_START_APP = "start_app";

    private OnHandleTrigger onHandleTrigger;

    public TriggerBroadCast(OnHandleTrigger onHandleTrigger) {
        this.onHandleTrigger = onHandleTrigger;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if (action.equals(ACTION_TURN_MIC_ON)) {
            //
            onHandleTrigger.onActionTurnOn();
            return;
        }
        if (action.equals(ACTION_START_APP)) {
            Log.e("Action", "Start app from broadcast");
            try{
                Intent i = new Intent();
                i.setClassName(context.getPackageName(), MainActivity.class.getName());
                i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                context.startActivity(i);
                onHandleTrigger.onActionStartApp();
            }catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (action.equals(ACTION_TURN_MIC_OFF)) {
            onHandleTrigger.onActionTurnOff();
        }
    }

    public interface OnHandleTrigger {
        void onActionTurnOn();

        void onActionTurnOff();

        void onActionStartApp();
    }
}
