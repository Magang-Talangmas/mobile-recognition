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
        TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        TextView tvAppName = findViewById(R.id.tvAppName);

        // Sequence: Logo Fade In (0ms -> 600ms)
        ivSplashLogo.animate()
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Sequence: Company Name Appears (900ms -> 1500ms)
        tvCompanyName.setTranslationY(20f);
        tvCompanyName.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(900)
            .setDuration(600)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // Sequence: App Name Fades In (1200ms -> 1800ms)
        tvAppName.setTranslationY(20f);
        tvAppName.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(1200)
            .setDuration(600)
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
