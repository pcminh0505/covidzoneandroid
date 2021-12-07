package com.example.covidquarantinemanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_loading);

        TextView textView = findViewById(R.id.splash_text);
        LottieAnimationView animationView = findViewById(R.id.splash_animation);

        textView.animate().translationY(1500).setDuration(500).setStartDelay(3000);
        animationView.animate().translationY(-1500).setDuration(500).setStartDelay(3000);

        // Set timer for splash screen and create intent to link to "MapsActivity"
        final int SPLASH = 3500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH);
    }
}