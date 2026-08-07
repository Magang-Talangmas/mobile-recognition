package com.example.javatraining.ui.auth;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.example.javatraining.R;
import com.example.javatraining.data.remote.ApiClient;
import com.example.javatraining.data.remote.ApiService;
import com.example.javatraining.data.remote.request.FcmTokenRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.EmployeeData;
import com.example.javatraining.ui.main.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends AppCompatActivity {

    private ConstraintLayout clWelcomeContent;
    private View vDimOverlay;
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;
    private LoginViewModel viewModel;

    // Login Form Views
    private LinearLayout llFormContainer;
    private EditText etEmail, etPassword;
    private ImageView ivTogglePassword;
    private FrameLayout btnLoginContainer;
    private TextView tvLoginText;
    private ProgressBar pbLoginLoading;
    private FrameLayout flBiometric;
    
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Bind Views
        clWelcomeContent = findViewById(R.id.clWelcomeContent);
        vDimOverlay = findViewById(R.id.vDimOverlay);
        NestedScrollView bottomSheet = findViewById(R.id.bottomSheetLogin);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        
        FrameLayout btnGetStartedContainer = findViewById(R.id.btnGetStartedContainer);
        
        llFormContainer = findViewById(R.id.llFormContainer);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLoginContainer = findViewById(R.id.btnLoginContainer);
        tvLoginText = findViewById(R.id.tvLoginText);
        pbLoginLoading = findViewById(R.id.pbLoginLoading);
        flBiometric = findViewById(R.id.flBiometric);

        // Initial states
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        
        // Hide form fields initially for staggered entry
        for (int i = 0; i < llFormContainer.getChildCount(); i++) {
            View child = llFormContainer.getChildAt(i);
            child.setAlpha(0f);
            child.setTranslationY(50f);
        }

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    vDimOverlay.animate().alpha(0f).setDuration(300)
                        .withEndAction(() -> vDimOverlay.setVisibility(View.GONE)).start();
                    clWelcomeContent.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset >= 0) {
                    vDimOverlay.setAlpha(slideOffset);
                    float scale = 1f - (0.06f * slideOffset);
                    clWelcomeContent.setScaleX(scale);
                    clWelcomeContent.setScaleY(scale);
                }
            }
        });

        // --- Animations & Interactions ---

        btnGetStartedContainer.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150).start()).start();

            // Sequence Timeline
            vDimOverlay.setVisibility(View.VISIBLE);
            
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                triggerBottomSheetStagger();
            }, 100);
        });

        // Toggle Password
        ivTogglePassword.setOnClickListener(v -> {
            ivTogglePassword.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100).withEndAction(() -> {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                    ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
                } else {
                    etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                    ivTogglePassword.setImageResource(R.drawable.ic_eye);
                }
                etPassword.setSelection(etPassword.getText().length());
                ivTogglePassword.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            }).start();
        });

        // Login Action
        btnLoginContainer.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
            attemptLogin();
        });

        setupBiometrics();
    }

    private void triggerBottomSheetStagger() {
        long delay = 200; // Wait for sheet to slide up
        for (int i = 0; i < llFormContainer.getChildCount(); i++) {
            View child = llFormContainer.getChildAt(i);
            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator(0.8f))
                .start();
            delay += 50;
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            // Shake animation
            ObjectAnimator shake = ObjectAnimator.ofFloat(btnLoginContainer, "translationX", 0, 20, -20, 20, -20, 10, -10, 5, -5, 0);
            shake.setDuration(400);
            shake.start();
            return;
        }

        // Morph to Loading
        tvLoginText.animate().alpha(0f).setDuration(200).start();
        pbLoginLoading.setVisibility(View.VISIBLE);
        pbLoginLoading.setAlpha(0f);
        pbLoginLoading.animate().alpha(1f).setDuration(200).start();

        viewModel.login(email, password).observe(this, user -> {
            if (user != null) {
                // Success Morph
                pbLoginLoading.animate().alpha(0f).setDuration(200).start();
                tvLoginText.setText("Success");
                tvLoginText.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                btnLoginContainer.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_green_dark));
                tvLoginText.animate().alpha(1f).setDuration(200).withEndAction(() -> {
                    handleLoginSuccess();
                }).start();
                
                // Upload FCM Token
                registerFCM();
            } else {
                // Failure
                pbLoginLoading.animate().alpha(0f).setDuration(200).start();
                tvLoginText.animate().alpha(1f).setDuration(200).start();
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginSuccess() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 500);
    }

    private void setupBiometrics() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(WelcomeActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                handleLoginSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                flBiometric.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login Biometrik")
                .setSubtitle("Gunakan Face ID atau Fingerprint untuk masuk")
                .setNegativeButtonText("Batal")
                .build();

        flBiometric.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                biometricPrompt.authenticate(promptInfo);
            }).start();
        });
    }

    private void registerFCM() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) return;
            String token = task.getResult();
            ApiService apiService = ApiClient.getClient(WelcomeActivity.this).create(ApiService.class);
            apiService.updateFcmToken(new FcmTokenRequest(token)).enqueue(new Callback<BaseResponse<EmployeeData>>() {
                @Override
                public void onResponse(Call<BaseResponse<EmployeeData>> call, Response<BaseResponse<EmployeeData>> response) {}
                @Override
                public void onFailure(Call<BaseResponse<EmployeeData>> call, Throwable t) {}
            });
        });
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
}
