package com.kt.triggerword;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import java.lang.ref.WeakReference;

public class AudioVolumeManager {
    private WeakReference<Context> contextRef;
    private static AudioVolumeManager INSTANCE;

    private AudioVolumeManager(Context context) {
        contextRef = new WeakReference<>(context);
    }

    public static AudioVolumeManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AudioVolumeManager(context);
        } else if (INSTANCE.contextRef.get() == null) {
            INSTANCE.contextRef = new WeakReference<>(context);
        }
        return INSTANCE;
    }

    public void muteVolume(Boolean shouldMute) {
        AudioManager alarmManager = (AudioManager) contextRef.get().getSystemService(Context.AUDIO_SERVICE);
        if (alarmManager != null) {
            alarmManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, shouldMute);
            alarmManager.setStreamMute(AudioManager.STREAM_ALARM, shouldMute);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int adjustMuteOrUnMute = shouldMute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE;
                alarmManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, adjustMuteOrUnMute, 0);
                alarmManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, adjustMuteOrUnMute, 0);
                alarmManager.adjustStreamVolume(AudioManager.STREAM_ALARM, adjustMuteOrUnMute, 0);
                alarmManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, adjustMuteOrUnMute, 0);
            } else {
                alarmManager.setStreamMute(AudioManager.STREAM_MUSIC, shouldMute);
            }
        }
    }
}
