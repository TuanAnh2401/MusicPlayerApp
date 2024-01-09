package com.example.music.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.music.Database.AppDatabase;
import com.example.music.Fragments.HomeFragment;
import com.example.music.Fragments.LibraryFragment;
import com.example.music.Models.SongModel;
import com.example.music.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentHolder;
    private AppDatabase appDatabase;
    private BottomNavigationView bottomNavigationView;
    private BroadcastReceiver musicPlayerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Lấy thông tin từ Intent và cập nhật UI
            updatePlayerUI(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Huỷ đăng ký BroadcastReceiver khi Activity không còn tồn tại
        unregisterReceiver(musicPlayerReceiver);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appDatabase = AppDatabase.getInstance(this);

        fragmentHolder = findViewById(R.id.main_frame_layout);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                setFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.library) {
                setFragment(new LibraryFragment());
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
        IntentFilter filter = new IntentFilter("ACTION_UPDATE_UI");
        registerReceiver(musicPlayerReceiver, filter);

    }
    private void updatePlayerUI(@NonNull Intent intent) {
        SongModel song = intent.getParcelableExtra("current_song");
        if (song != null) {
            // Cập nhật thông tin bài hát và ca sĩ
            TextView songNameTextView = findViewById(R.id.song_name);
            TextView artistNameTextView = findViewById(R.id.artist_name);
            songNameTextView.setText(song.getName());
            artistNameTextView.setText(song.getSinger());

            // Cập nhật ảnh bìa
            ImageView coverArtImageView = findViewById(R.id.cover_art);
            Glide.with(this).load(song.getLinkImage()).into(coverArtImageView);

            // Hiển thị LinearLayout chứa thông tin bài hát
            LinearLayout linearLayout = findViewById(R.id.linearLayout5);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolder.getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        updatePlayerUI(intent);
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

}
