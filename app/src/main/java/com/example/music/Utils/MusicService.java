package com.example.music.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {
    public static final String ACTION_STOP = "com.example.music.STOP";
    public static final String ACTION_TOGGLE_PLAY_PAUSE = "com.example.music.TOGGLE_PLAY_PAUSE";

    private boolean isPlaying = false;
    private BroadcastReceiver playPauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ACTION_TOGGLE_PLAY_PAUSE)) {
                togglePlayPause();
            }
        }
    };

    private void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                pause();
            } else {
                play();
            }
            updateNotification();
        }
    }

    private static MediaPlayer mediaPlayer;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createMediaPlayer();
    }

    private static void createMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateNotification();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void stop() {
        mediaPlayer.reset();
        createMediaPlayer();
        updateNotification();
    }

    private static void updateNotification() {
        // Cập nhật notification
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (playPauseReceiver != null) {
            unregisterReceiver(playPauseReceiver);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Đảm bảo giải phóng resources khi dịch vụ bị hủy
        }
    }
}
