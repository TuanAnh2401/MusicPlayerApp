package com.example.music.Activities;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.music.Models.SongModel;
import com.example.music.R;

import java.lang.ref.WeakReference;
import java.util.List;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MusicPlayerService extends Service {
    private List<SongModel> songList;
    private MediaPlayer mediaPlayer;
    public static final String CHANNEL_ID = "music_player_channel";
    private TextView song_name, artist_name, duration_played, duration_total;
    private ImageView cover_art, nextbtn, prevbtn, backbtn, repeatbtn,shufflebtn;
    private FloatingActionButton playpausebtn;
    private SeekBar seekbar;
    private boolean isRepeat = false;

    private int position = -1;
    private Handler handler = new Handler();
    private boolean isPlaying = false;
    private boolean isSeeking = false;
    private boolean isStartRequested = false;
    private boolean isShuffle = false;

    private int progress = 0;
    @SuppressLint("RemoteViewLayout")
    RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification);

    public class LocalBinder extends Binder {
        MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public static final String ACTION_PLAY = "com.example.music.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.music.ACTION_PAUSE";
    public static final String ACTION_STOP = "com.example.music.ACTION_STOP";
    public static final String ACTION_NEXT = "com.example.music.ACTION_NEXT";
    public static final String ACTION_PREV = "com.example.music.ACTION_PREV";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY:
                    playSong();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
                case ACTION_STOP:
                    stopService();
                    break;
                case ACTION_NEXT:
                    playNextSong();
                    break;
                case ACTION_PREV:
                    playPrevSong();

            }
        }
        return START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.song_name); // Tên kênh thông báo
                String description = getString(R.string.app_name); // Mô tả kênh
                int importance = NotificationManager.IMPORTANCE_LOW; // Mức độ quan trọng của kênh
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Đăng ký kênh với hệ thống; không thể thay đổi tầm quan trọng hoặc các thông báo hành vi khác sau này
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    private void pause() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            createNotification();
        }
    }

    private void stopService() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        stopSelf();
    }

    private void createNotification(SongModel song) {
        createNotificationChannel();
        String channelId = "music_player_channel";
        String channelName = "Music Player";
        notificationLayout.setTextViewText(R.id.song_name, songList.get(position).getName());
        notificationLayout.setTextViewText(R.id.artist_name, songList.get(position).getSinger());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(this, SongPlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(songList.get(position).getName())
                .setContentText(songList.get(position).getSinger())
                .setContentIntent(pendingIntent)
                .setOngoing(true);
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause();
        } else {
            isStartRequested = true;
            if (mediaPlayer != null && !isSeeking) {
                mediaPlayer.seekTo(progress); // Sử dụng giá trị progress
                updateDurationPlayed(progress);
            }
        }
        isPlaying = !isPlaying;
        updatePlayPauseIcon();
    }
    private void updatePlayPauseIcon() {
        if (isPlaying) {
            playpausebtn.setImageResource(R.drawable.baseline_pause);
        } else {
            playpausebtn.setImageResource(R.drawable.baseline_play);
        }
    }
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying() && !isSeeking) {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                        updateDurationPlayed(currentPosition);
                        int duration = mediaPlayer.getDuration();
                        if (currentPosition >= duration - 1000 && isRepeat) {
                            seekbar.setProgress(duration);
                            updateDurationPlayed(duration);
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, 1000);
            }
        }, 0);
    }
    private void updateDurationPlayed(int progress) {
        duration_played.setText(formatDuration(progress));
    }
    private void updateUI() {
        song_name.setText(songList.get(position).getName());
        artist_name.setText(songList.get(position).getSinger());
        String linkImage = songList.get(position).getLinkImage();
        Glide.with(this)
                .load(linkImage)
                .error(R.drawable.error_image)
                .into(cover_art);
    }
    private static class PrepareMediaTask extends AsyncTask<String, Void, Boolean> {
        private final WeakReference <MusicPlayerService> musicPlayerService;

        PrepareMediaTask(MusicPlayerService service) {
            this.musicPlayerService = new WeakReference<>(service);
        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                MusicPlayerService service = musicPlayerService.get();
                if (service == null ) {
                    return false;
                }
                service.mediaPlayer.setDataSource(params[0]);
                service.mediaPlayer.prepare();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean isPrepared) {
            MusicPlayerService service = musicPlayerService.get();
            if (service == null ) {
                return;
            }
            if (isPrepared) {
                service.handleMediaPlayerPrepared(service.mediaPlayer);
            } else {

            }
        }
    }
    private void handleMediaPlayerPrepared(MediaPlayer mp) {
        if (mp != null && mp.getDuration() > 0) {
            int duration = mp.getDuration();
            duration_total.setText(formatDuration(duration));
            seekbar.setMax(duration);
            playpausebtn.setEnabled(true);
            if (isRepeat) {
                seekbar.setProgress(duration);
                updateDurationPlayed(duration);
                mp.setLooping(true);
            } else {
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playNextSong();
                    }
                });
            }
            mp.start();
            updatePlayPauseIcon();
            isPlaying = true;
            updateSeekBar();
        } else {

        }
    }
    private void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }
            String linkMP3 = songList.get(position).getLinkMP3();
            new PrepareMediaTask(this).execute(linkMP3);
            updateUI();
            seekbar.setProgress(0);
            updateDurationPlayed(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                handleMediaPlayerPrepared(mp);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isRepeat) {
                    playSong();
                } else {
                    playNextSong();
                }
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying() && isStartRequested) {
                    mediaPlayer.start();
                    updatePlayPauseIcon();
                    isPlaying = true;
                    isStartRequested = false;
                }
            }
        });
        playpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
    }
    private void playNextSong() {
        if (isRepeat) {
            playSong();
        } else {
            position = (position + 1) % songList.size();
            if (position < 0) {
                position = songList.size() - 1;
            }
            boolean wasPlaying = mediaPlayer.isPlaying();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            initMediaPlayer();
            playSong();
            updateUI();
            if (wasPlaying) {
                isStartRequested = true;
                progress = 0;
                seekbar.setProgress(0);
                updateDurationPlayed(0);
                mediaPlayer.seekTo(progress);
                mediaPlayer.start();
                updatePlayPauseIcon();
                isPlaying = true;
                updateSeekBar();
            }
        }
    }
    private void playPrevSong() {
        if (isRepeat) {
            playSong();
        } else {
            position = (position - 1 + songList.size()) % songList.size();
            if (position < 0) {
                position = songList.size() - 1;
            }
            playSong();
            updateUI();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    private String formatDuration(int durationInMillis) {
        int seconds = (durationInMillis / 1000) % 60;
        int minutes = (durationInMillis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, MusicPlayerService.class);
        intent.setAction(action);
        int requestCode = action.hashCode(); // Đảm bảo mỗi PendingIntent có requestCode duy nhất
        return PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
