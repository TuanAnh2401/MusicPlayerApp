package com.example.music.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.Models.SongModel;
import com.example.music.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.List;

public class SongPlayerActivity extends AppCompatActivity {

    private List<SongModel> songList;
    private MediaPlayer mediaPlayer;

    private TextView song_name, artist_name, duration_played, duration_total;
    private ImageView cover_art, nextbtn, prevbtn, backbtn;
    private FloatingActionButton playpausebtn;
    private SeekBar seekbar;

    private int position = -1;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
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

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
                playNextSong();
            }
        });

        playpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
    }

    private void handleMediaPlayerPrepared(MediaPlayer mp) {
        if (mp != null && mp.getDuration() > 0) {
            int duration = mp.getDuration();
            duration_total.setText(formatDuration(duration));
            seekbar.setMax(duration);
            playpausebtn.setEnabled(true);
            updateSeekBar();
            updateDurationPlayed(0);
            mp.start();
            updatePlayPauseIcon();
            isPlaying = true;
        } else {
            // Xử lý khi MediaPlayer không chuẩn bị được
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
                // Xử lý khi không thể chuẩn bị MediaPlayer
            }
        }
    }

    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
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
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekbar.setProgress(currentPosition);
                    updateDurationPlayed(currentPosition);
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
    private void playSong() {
        try {
            mediaPlayer.reset();
            String linkMP3 = songList.get(position).getLinkMP3();
            new PrepareMediaTask(this).execute(linkMP3);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    private void playNextSong() {
        position = (position + 1) % songList.size();
        if (position < 0) {
            position = songList.size() - 1;
        }
        Log.d("SongPlayerActivity", "Next Song - Position: " + position);
        playSong();
        updateUI();
    }

    private void playPrevSong() {
        position = (position - 1 + songList.size()) % songList.size();
        if (position < 0) {
            position = songList.size() - 1;
        }
        Log.d("SongPlayerActivity", "Prev Song - Position: " + position);
        playSong();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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
