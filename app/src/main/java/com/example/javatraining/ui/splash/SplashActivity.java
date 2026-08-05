package com.example.javatraining.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javatraining.R;
import com.example.javatraining.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide Action Bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        android.widget.ImageView ivSplashLogo = findViewById(R.id.ivSplashLogo);
        android.widget.TextView tvCompanyName = findViewById(R.id.tvCompanyName);
        android.widget.TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        android.widget.ProgressBar progressBar = findViewById(R.id.progressBar);

        if (ivSplashLogo != null && tvCompanyName != null && tvSubtitle != null && progressBar != null) {
            ivSplashLogo.setAlpha(0f);
            ivSplashLogo.setScaleX(0.8f);
            ivSplashLogo.setScaleY(0.8f);
            tvCompanyName.setAlpha(0f);
            tvSubtitle.setAlpha(0f);
            progressBar.setAlpha(0f);

            ivSplashLogo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(1000).start();
            tvCompanyName.animate().alpha(1f).setDuration(1000).setStartDelay(300).start();
            tvSubtitle.animate().alpha(1f).setDuration(1000).setStartDelay(600).start();
            progressBar.animate().alpha(1f).setDuration(1000).setStartDelay(900).start();
        }

        // Wait for 2 seconds (loading), then move to LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000);
    }
}
