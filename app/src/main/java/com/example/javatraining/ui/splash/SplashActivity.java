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

        ImageView ivScanFrame = findViewById(R.id.ivScanFrame);
        ImageView ivSplashLogo = findViewById(R.id.ivSplashLogo);
        View vScanLine = findViewById(R.id.vScanLine);

        float density = getResources().getDisplayMetrics().density;
        float startY = -75f * density;
        float endY = 75f * density;

        // 1. Frame Fade In & Scale
        ivScanFrame.setScaleX(0.85f);
        ivScanFrame.setScaleY(0.85f);
        ivScanFrame.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // 2. Logo Fade In & Scale
        ivSplashLogo.setScaleX(0.8f);
        ivSplashLogo.setScaleY(0.8f);
        ivSplashLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        // 3. Laser Scan Bar Animation Inside Frame
        vScanLine.setTranslationY(startY);
        vScanLine.animate()
            .alpha(1f)
            .setStartDelay(300)
            .setDuration(300)
            .withEndAction(() -> {
                scanAnimator = ObjectAnimator.ofFloat(vScanLine, "translationY", startY, endY);
                scanAnimator.setDuration(1100);
                scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
                scanAnimator.setRepeatMode(ValueAnimator.REVERSE);
                scanAnimator.setInterpolator(new LinearInterpolator());
                scanAnimator.start();
            })
            .start();

        // Transition to Welcome after 2600ms
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (scanAnimator != null) {
                scanAnimator.cancel();
            }
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2600);
    }
}
