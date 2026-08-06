package com.example.javatraining.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.javatraining.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnLogin = findViewById(R.id.btnLogin);
        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        LottieAnimationView lottieTransition = findViewById(R.id.lottieTransition);

        // Set initial states for animation
        View[] animatedViews = {ivLogo, tvWelcome, tvSubtitle, btnLogin};
        for (View v : animatedViews) {
            v.setAlpha(0f);
            v.setTranslationY(60f);
        }

        // Start staggered animations
        long delay = 100;
        for (View v : animatedViews) {
            v.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(delay)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
            delay += 150;
        }

        btnLogin.setOnClickListener(v -> {
            lottieTransition.setVisibility(View.VISIBLE);
            lottieTransition.playAnimation();
            lottieTransition.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    overridePendingTransition(0, 0); // No animation needed since lottie covered the screen
                }
            });
        });
    }
}
