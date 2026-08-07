package com.example.javatraining.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javatraining.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        Button btnLogin = findViewById(R.id.btnLogin);

        // Set initial states for animation
        View[] animatedViews = {ivLogo, tvWelcome, tvSubtitle, btnLogin};
        for (View v : animatedViews) {
            v.setAlpha(0f);
            v.setTranslationY(50f);
        }

        // Animate elements sequentially
        long delay = 0;
        for (View v : animatedViews) {
            v.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay(delay)
                .start();
            delay += 100; // slightly faster delay
        }

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
        });
    }
}
