package ai.kitt.snowboy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import ai.kitt.snowboy.activities.MainActivity;


public class ActivityUtil {
    public static void lauchingApp(Context context) {
        Intent dialogIntent = new Intent(context, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
