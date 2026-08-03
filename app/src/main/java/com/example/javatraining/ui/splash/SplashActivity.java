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

        // Wait for 2 seconds (loading), then animate logo for 0.5s before moving to LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Hide progress bar and texts
            findViewById(R.id.progressBar).animate().alpha(0f).setDuration(200).start();
            findViewById(R.id.tvCompanyName).animate().alpha(0f).setDuration(200).start();
            findViewById(R.id.tvSubtitle).animate().alpha(0f).setDuration(200).start();

            // Animate logo expanding
            findViewById(R.id.ivSplashLogo).animate()
                    .scaleX(10f)
                    .scaleY(10f)
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction(() -> {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    })
                    .start();
        }, 2000);
    }
}
