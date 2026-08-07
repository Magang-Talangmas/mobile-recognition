package com.example.javatraining.ui.splash;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
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
        TextView tvAppName = findViewById(R.id.tvAppName);
        ProgressBar pbSplashLoading = findViewById(R.id.pbSplashLoading);

        // Sequence: Logo Fade In (0ms -> 600ms)
        ivSplashLogo.animate()
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Sequence: App Name and Loading Appear (600ms -> 1100ms)
        tvAppName.setTranslationY(20f);
        tvAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(600)
            .setDuration(500)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
            
        pbSplashLoading.setTranslationY(20f);
        pbSplashLoading.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(800)
            .setDuration(500)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Transition to Welcome after 2500ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2800);
    }
}
