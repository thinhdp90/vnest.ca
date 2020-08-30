package ai.kitt.vnest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ai.kitt.snowboy.service.TriggerBroadCast;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if(action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent();
            i.setAction(TriggerBroadCast.ACTION_RESTART_SERVICE);
            context.sendBroadcast(i);
        }
    }
}
