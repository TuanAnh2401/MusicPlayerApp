package com.example.music.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.music.Database.AppDatabase;
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
                setFragment(new LibraryFragment());
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolder.getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

}
