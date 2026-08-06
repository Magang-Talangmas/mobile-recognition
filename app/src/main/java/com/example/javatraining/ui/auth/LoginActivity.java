package com.example.javatraining.ui.auth;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
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

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Animate the wave background
        ObjectAnimator waveAnimator = ObjectAnimator.ofFloat(binding.ivWave, "translationY", 0f, 40f, 0f);
        waveAnimator.setDuration(4000);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        waveAnimator.start();

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
                        "Biometrik terverifikasi!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
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

        biometricPrompt.authenticate(promptInfo);
        
        binding.ivTogglePassword.setOnClickListener(v -> {
            if (binding.etPassword.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
                // Show password
                binding.etPassword.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(com.example.javatraining.R.drawable.ic_eye_off);
            } else {
                // Hide password
                binding.etPassword.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                binding.ivTogglePassword.setImageResource(com.example.javatraining.R.drawable.ic_eye);
            }
            // Keep cursor at the end
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

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
                        apiService.updateFcmToken(user.getId(), new FcmTokenRequest(token)).enqueue(new Callback<BaseResponse<EmployeeData>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<EmployeeData>> call, Response<BaseResponse<EmployeeData>> response) {
                                Log.d("LoginActivity", "FCM Token updated successfully");
                            }
                            @Override
                            public void onFailure(Call<BaseResponse<EmployeeData>> call, Throwable t) {
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
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
}
