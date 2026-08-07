package com.example.javatraining.ui.auth;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

import com.example.javatraining.databinding.ActivityLoginBinding;
import com.example.javatraining.ui.main.MainActivity;
import com.example.javatraining.data.remote.ApiClient;
import com.example.javatraining.data.remote.ApiService;
import com.example.javatraining.data.remote.request.FcmTokenRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.EmployeeData;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import com.example.javatraining.R;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Removed vScanLine animation

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Native Biometric Prompt
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Ignore if the user intentionally cancelled the prompt
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED || 
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    return;
                }
                Toast.makeText(getApplicationContext(),
                        "Biometric error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Biometrik terverifikasi! Masuk Mode Dummy...", Toast.LENGTH_SHORT).show();
                performLogin("dummy@test.com", "dummy");
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Autentikasi gagal",
                        Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login Biometrik")
                .setSubtitle("Gunakan Face ID atau Fingerprint untuk masuk")
                .setNegativeButtonText("Batal")
                .build();

        // Check if biometric button exists in the layout (ivBiometric)
        if (binding.ivBiometric != null) {
            binding.ivBiometric.setOnClickListener(v -> {
                biometricPrompt.authenticate(promptInfo);
            });
        }
        
        binding.ivTogglePassword.setOnClickListener(v -> {
            // Animate eye click
            binding.ivTogglePassword.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100).withEndAction(() -> {
                if (binding.etPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                    // Show password
                    binding.etPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                    binding.ivTogglePassword.setImageResource(com.example.javatraining.R.drawable.ic_eye_off);
                } else {
                    // Hide password
                    binding.etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                    binding.ivTogglePassword.setImageResource(com.example.javatraining.R.drawable.ic_eye);
                }
                binding.ivTogglePassword.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                // Keep cursor at the end
                binding.etPassword.setSelection(binding.etPassword.getText().length());
            }).start();
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            performLogin(email, password);
        });

        // tvForgotPassword removed in new sci-fi design
    }

    private void performLogin(String email, String password) {
        viewModel.login(email, password).observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                
                // Upload FCM Token
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("LoginActivity", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    ApiService apiService = ApiClient.getClient(LoginActivity.this).create(ApiService.class);
                    apiService.updateFcmToken(email, new FcmTokenRequest(token)).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.d("LoginActivity", "FCM Token updated successfully");
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("LoginActivity", "FCM Token update failed", t);
                        }
                    });
                });

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }
}
