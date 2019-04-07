package com.andrewtse.testdemo.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * @author xk
 * @date 2019/4/7
 */
public class SystemVolume {

    private static AudioManager audioManager;

    public static int getVolume(Context context) {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setVolume(Context context, int volumeValue) {
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, AudioManager.FLAG_PLAY_SOUND);
    }
}
