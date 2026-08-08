package com.example.javatraining.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javatraining.R;
import com.example.javatraining.ui.auth.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView ivSplashLogo = findViewById(R.id.ivSplashLogo);
        ProgressBar pbSplashLoading = findViewById(R.id.pbSplashLoading);

        // Sequence: Logo Fade In & Scale (0ms -> 600ms)
        ivSplashLogo.setScaleX(0.8f);
        ivSplashLogo.setScaleY(0.8f);
        ivSplashLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Sequence: Loading Spinner Appears (400ms -> 900ms)
        pbSplashLoading.setTranslationY(10f);
        pbSplashLoading.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(400)
            .setDuration(500)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Transition to Welcome after 2200ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2200);
    }
}
