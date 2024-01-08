package com.example.music.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.FrameLayout;

import com.example.music.Database.AppDatabase;
import com.example.music.Entity.SongEntity;
import com.example.music.Fragments.HomeFragment;
import com.example.music.Fragments.LibraryFragment;
import com.example.music.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentHolder;
    private AppDatabase appDatabase;
    private BottomNavigationView bottomNavigationView;

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
                getMP3();
                setFragment(new LibraryFragment());
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);

    }
    private void getMP3() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 0);
        } else {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        int titleIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                        int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

                        while (cursor.moveToNext()) {
                            String songTitle = cursor.getString(titleIndex);
                            long songId = cursor.getLong(idIndex);
                            Uri songUri = Uri.withAppendedPath(uri, String.valueOf(songId));

                            if (appDatabase.songDao().getSongById(String.valueOf(songId)) == null) {
                                SongEntity songEntity = new SongEntity();
                                songEntity.setId(String.valueOf(songId));
                                songEntity.setName(songTitle);
                                songEntity.setLinkMP3(songUri.toString());
                                appDatabase.songDao().insertSong(songEntity);
                            }
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        cursor.close();
                    }
                }.execute();
            }
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolder.getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
