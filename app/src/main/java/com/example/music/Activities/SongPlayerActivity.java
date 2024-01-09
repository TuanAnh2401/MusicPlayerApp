package com.example.music.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.Dao.SongDao;
import com.example.music.Database.AppDatabase;

import com.example.music.Entity.SongEntity;
import com.example.music.Models.SongModel;
import com.example.music.R;
import com.example.music.Utils.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileDescriptor;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SongPlayerActivity extends AppCompatActivity {
    private List<SongModel> songList;
    private MediaPlayer mediaPlayer;

    private TextView song_name, artist_name, duration_played, duration_total;
    private ImageView cover_art, nextbtn, prevbtn, backbtn, repeatbtn,shufflebtn,downloadbtn;
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

    private AppDatabase appDatabase;
    private SongDao songDao;
    private Context mContext;
    private MusicService musicService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_song_player);
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        songDao = appDatabase.songDao();
        initViews();
        getIntentMethod();
        initSeekBar();
        initMediaPlayer();
        updateUI();
        playSong();
    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.artist_name);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        playpausebtn = findViewById(R.id.play_pause);
        seekbar = findViewById(R.id.seekBar);
        nextbtn = findViewById(R.id.id_next);
        prevbtn = findViewById(R.id.id_prev);
        backbtn = findViewById(R.id.back_btn);
        repeatbtn = findViewById(R.id.id_repeat);
        shufflebtn = findViewById(R.id.id_shuffle);
        downloadbtn = findViewById(R.id.btn_download);
        shufflebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleShuffle();
            }
        });
        repeatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRepeat();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });

        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevSong();
            }
        });
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadCurrentSong();
            }
        });

    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        songList = getIntent().getParcelableArrayListExtra("songList");
    }

    private void initSeekBar() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateDurationPlayed(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
            }
        });
    }
    private void downloadCurrentSong() {
        if (position >= 0 && position < songList.size()) {
            SongModel currentSong = songList.get(position);
            SongEntity songEntity = new SongEntity();
            songEntity.setId(currentSong.getId());
            songEntity.setCategory(currentSong.getCategory());
            songEntity.setDateTime(getCurrentDateTime());
            songEntity.setLinkImage(currentSong.getLinkImage());
            songEntity.setLinkMP3(currentSong.getLinkMP3());
            songEntity.setLyric(currentSong.getLyric());
            songEntity.setName(currentSong.getName());
            songEntity.setSinger(currentSong.getSinger());

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    SongEntity existingSong = appDatabase.songDao().getSongById(currentSong.getId());
                    if (existingSong == null) {
                        appDatabase.songDao().insertSong(songEntity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Tải bài hát thành công", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDuplicateIdError();
                            }
                        });
                    }
                }
            });
        }
    }

    private void showDuplicateIdError() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Bài hát đã tồn tại trong danh sách", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static class SaveSongAsyncTask extends AsyncTask<SongEntity, Void, Boolean> {
        private final WeakReference<SongPlayerActivity> activityReference;

        SaveSongAsyncTask(SongPlayerActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(SongEntity... songEntities) {
            try {
                SongPlayerActivity activity = activityReference.get();
                if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                    return false;
                }
                AppDatabase appDatabase = AppDatabase.getInstance(activity.getApplicationContext());
                SongEntity existingSong = appDatabase.songDao().getSongById(songEntities[0].getId());
                if (existingSong == null) {
                    appDatabase.songDao().insertSong(songEntities[0]);
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            SongPlayerActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            if (success) {
                Toast.makeText(activity, "Tải bài hát thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Bài hát đã tồn tại trong danh sách", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
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
    private void toggleShuffle() {
        isShuffle = !isShuffle;
        updateShuffleIcon();
        if (isShuffle) {
            shuffleSongs();
        }
    }
    private void updateShuffleIcon() {
        ImageView shuffleBtn = findViewById(R.id.id_shuffle);

        if (isShuffle) {
            shuffleBtn.setImageResource(R.drawable.baseline_shuffle_on);
        } else {
            shuffleBtn.setImageResource(R.drawable.baseline_shuffle_off);
        }
    }
    private void shuffleSongs() {
        if (songList != null && songList.size() > 1) {
            int currentPosition = position;
            Collections.shuffle(songList);
            position = songList.indexOf(songList.get(currentPosition));
            if (position == -1) {
                position = new Random().nextInt(songList.size());
            }
            playSong();
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
    private void toggleRepeat() {
        isRepeat = !isRepeat;
        updateRepeatIcon();
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(isRepeat);
        }
    }
    private void updateRepeatIcon() {
        ImageView repeatBtn = findViewById(R.id.id_repeat);
        if (isRepeat) {
            repeatBtn.setImageResource(R.drawable.baseline_repeat_on);
        } else {
            repeatBtn.setImageResource(R.drawable.baseline_repeat_off);
        }
    }
    private static class PrepareMediaTask extends AsyncTask<String, Void, Boolean> {
        private final WeakReference<SongPlayerActivity> activityReference;

        PrepareMediaTask(SongPlayerActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SongPlayerActivity activity = activityReference.get();
                if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                    return false;
                }
                activity.mediaPlayer.setDataSource(params[0]);
                activity.mediaPlayer.prepare();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean isPrepared) {
            SongPlayerActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
            if (isPrepared) {
                activity.handleMediaPlayerPrepared(activity.mediaPlayer);
            } else {

            }
        }
    }
    private void togglePlayPause() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                musicService.pause();
            } else {
                musicService.play();
            }
            updatePlayPauseIcon();
        }
        if (isPlaying) {
            mediaPlayer.pause();
            sendCustomNotification(isPlaying);
        } else {
            isStartRequested = true;
            if (mediaPlayer != null && !isSeeking) {
                mediaPlayer.seekTo(progress);
                updateDurationPlayed(progress);
            }
            sendCustomNotification(isPlaying);
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
                .error(R.drawable.ic_image)
                .into(cover_art);
    }
    private void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }
            String linkMP3 = songList.get(position).getLinkMP3();

            if (isMediaStoreAudio(linkMP3)) {
                ContentResolver contentResolver = getContentResolver();
                Uri mediaUri = Uri.parse(linkMP3);
                AssetFileDescriptor assetFileDescriptor = contentResolver.openAssetFileDescriptor(mediaUri, "r");

                if (assetFileDescriptor != null) {
                    FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
                    mediaPlayer.setDataSource(fileDescriptor, assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    isPlaying = true;
                    assetFileDescriptor.close();
                }
            } else {
                new PrepareMediaTask(this).execute(linkMP3);
            }
            sendCustomNotification(isPlaying);
            updateUI();
            seekbar.setProgress(0);
            updateDurationPlayed(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCustomNotification(boolean isPlaying) {
        String imageUrl = songList.get(position).getLinkImage();

        int notificationId = 1;
        String channelId = "channel_id";
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.cover_art, R.drawable.ic_image);

        remoteViews.setTextViewText(R.id.song_name, songList.get(position).getName());
        remoteViews.setTextViewText(R.id.artist_name, songList.get(position).getSinger());

        int playPauseIcon = isPlaying ? R.drawable.baseline_pause : R.drawable.baseline_play;
        remoteViews.setImageViewResource(R.id.play_pause, playPauseIcon);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        try {
            managerCompat.notify(notificationId, notification);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean isMediaStoreAudio(String linkMP3) {
        Uri mediaUri = Uri.parse(linkMP3);

        return ContentResolver.SCHEME_CONTENT.equals(mediaUri.getScheme())
                && MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.getHost().equals(mediaUri.getHost());
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

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            initMediaPlayer();
            playSong();
            updateUI();

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
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }
    private String formatDuration(int durationInMillis) {
        int seconds = (durationInMillis / 1000) % 60;
        int minutes = (durationInMillis / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}