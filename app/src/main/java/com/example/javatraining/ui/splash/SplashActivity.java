package com.example.javatraining.ui.splash;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javatraining.R;
import com.example.javatraining.ui.auth.WelcomeActivity;

public class SplashActivity extends AppCompatActivity {

    private ObjectAnimator scanAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView ivSplashLogo = findViewById(R.id.ivSplashLogo);
        View vGlowRing = findViewById(R.id.vGlowRing);
        View vScanLine = findViewById(R.id.vScanLine);
        ProgressBar pbSplashLoading = findViewById(R.id.pbSplashLoading);

        float density = getResources().getDisplayMetrics().density;
        float startY = -55f * density;
        float endY = 55f * density;

        // 1. Logo & Glow Ring Animation (0ms -> 500ms)
        ivSplashLogo.setScaleX(0.8f);
        ivSplashLogo.setScaleY(0.8f);
        ivSplashLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        vGlowRing.animate()
            .alpha(1f)
            .setDuration(600)
            .start();

        // 2. Start Laser Scanning Beam Sweep (at 300ms)
        vScanLine.setTranslationY(startY);
        vScanLine.animate()
            .alpha(1f)
            .setStartDelay(300)
            .setDuration(300)
            .withEndAction(() -> {
                scanAnimator = ObjectAnimator.ofFloat(vScanLine, "translationY", startY, endY);
                scanAnimator.setDuration(1000);
                scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
                scanAnimator.setRepeatMode(ValueAnimator.REVERSE);
                scanAnimator.setInterpolator(new LinearInterpolator());
                scanAnimator.start();
            })
            .start();

        // 3. Loading Spinner Animation (at 400ms)
        pbSplashLoading.setAlpha(0f);
        pbSplashLoading.animate()
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(500)
            .start();

        // Transition to Welcome after 2800ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (scanAnimator != null) {
                scanAnimator.cancel();
            }
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2800);
    }
}
