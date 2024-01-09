package com.example.music.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.media.MediaPlayer;
import androidx.annotation.Nullable;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
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

    private void createMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop(); // Stop khi hoàn thành một bài hát
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
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification();
        }
    }

    public void stop() {
        mediaPlayer.reset();
        createMediaPlayer(); // Tạo mới mediaPlayer sau khi stop
        updateNotification();
    }

    private void updateNotification() {
        // Cập nhật notification
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Đảm bảo giải phóng resources khi dịch vụ bị hủy
        }
    }
}

