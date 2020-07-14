package com.kt.triggerword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TriggerBroadCast extends BroadcastReceiver {
    public static final String ACTION_TURN_MIC_ON = "turn_on_mic";
    public static final String ACTION_RESTART_APP = "restart_app";
    private TriggerCallBack callBack;

    public TriggerBroadCast() {

    }

    public TriggerBroadCast(TriggerCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case ACTION_RESTART_APP:
                callBack.onRestartApp();
                break;
            case ACTION_TURN_MIC_ON:
                callBack.onTurnMicOn();
                break;
            default:
                throw new IllegalStateException("No action found!");
        }
    }


}
