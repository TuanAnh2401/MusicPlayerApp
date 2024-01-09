package com.example.music.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicPlayerBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_STOP = "com.example.music.STOP";
    public static final String ACTION_TOGGLE_PLAY_PAUSE = "com.example.music.TOGGLE_PLAY_PAUSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_STOP)) {
                stopMusicService(context);
            } else if (intent.getAction().equals(ACTION_TOGGLE_PLAY_PAUSE)) {
                togglePlayPause(context);
            }
        }
    }

    private void stopMusicService(Context context) {
        Intent stopIntent = new Intent(MusicService.ACTION_STOP);
        context.sendBroadcast(stopIntent);
    }

    private void togglePlayPause(Context context) {
        Intent toggleIntent = new Intent(MusicService.ACTION_TOGGLE_PLAY_PAUSE);
        context.sendBroadcast(toggleIntent);
    }
}

