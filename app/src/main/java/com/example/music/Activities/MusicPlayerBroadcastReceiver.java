package com.example.music.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicPlayerBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PLAY = "com.example.music.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.music.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.music.ACTION_STOP";
    public static final String ACTION_NEXT = "com.example.music.ACTION_NEXT";
    public static final String ACTION_PREV = "com.example.music.ACTION_PREV";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MusicPlayerService.class);
        if (ACTION_PLAY.equals(intent.getAction())) {
            serviceIntent.setAction(MusicPlayerService.ACTION_PLAY);
        } else if (ACTION_PAUSE.equals(intent.getAction())) {
            serviceIntent.setAction(MusicPlayerService.ACTION_PAUSE);
        } else if (ACTION_STOP.equals(intent.getAction())) {
            serviceIntent.setAction(MusicPlayerService.ACTION_STOP);
        } else if (ACTION_NEXT.equals(intent.getAction())) {
            serviceIntent.setAction(MusicPlayerService.ACTION_NEXT);
        } else if (ACTION_PREV.equals(intent.getAction())) {
            serviceIntent.setAction(MusicPlayerService.ACTION_PREV);
        }
        context.startService(serviceIntent);
    }
}
