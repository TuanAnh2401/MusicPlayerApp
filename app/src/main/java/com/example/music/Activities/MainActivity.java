package com.example.music.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.music.Fragments.HomeFragment;
import com.example.music.Fragments.SignInFragment;
import com.example.music.R;

public class MainActivity extends AppCompatActivity {

    private FrameLayout fragmentHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHolder = findViewById(R.id.main_frame_layout);

        setFragment(new HomeFragment());
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolder.getId(),fragment);
        fragmentTransaction.commit();
    }
}