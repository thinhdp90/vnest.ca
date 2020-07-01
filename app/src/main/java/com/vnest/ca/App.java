package com.vnest.ca;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import kun.kt.opencam.ipc.ITransitService;
import kun.kt.opencam.air.*;

public class App extends Application {
    public static final String CAM_PACKAGE_NAME = "com.syu.camera360";
    public static final String AIR_PACKAGE_NAME = "com.tpms3";


    private static App INSTANCE;

    public static App get() {
        return INSTANCE;
    }

    public App() {
        INSTANCE = this;
    }


    public static ITransitService ipcService;
    private static ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ipcService = ITransitService.Stub.asInterface(service);
            AirControl.initialize(ipcService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void bindService() {
        Intent intent = new Intent("com.syu.sha.TransitService");
        intent.setPackage("kun.kt.opencam");
        App.get().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void open(String packageName, String className) {
        try {
            Intent intent;
            if (className == null || className.isEmpty()) {
                intent = getPackageManager().getLaunchIntentForPackage(packageName);
            } else {
                intent = new Intent();
                intent.setComponent(new ComponentName(packageName, className));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void closeApp(String packageName) {
        try {
            ipcService.closeApp(packageName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
