package com.example.music.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.music.Fragments.SignInFragment;
import com.example.music.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private FirebaseAuth mAuth;
    private SignInFragment signInFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        mAuth = FirebaseAuth.getInstance();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                SharedPreferences preferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
                String savedEmail = preferences.getString("email", "");
                String savedPassword = preferences.getString("password", "");
                if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
                    signInFragment.signInWithEmail(savedEmail, savedPassword);
                }
                if(mAuth.getCurrentUser() != null){
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this, RegisterActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },1000);
    }
}