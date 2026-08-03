package com.example.javatraining.ui.auth;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javatraining.R;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        MaterialButton btnReset = findViewById(R.id.btnReset);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);
        EditText etEmail = findViewById(R.id.etEmail);

        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email Anda terlebih dahulu.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tautan reset password telah dikirim ke " + email, Toast.LENGTH_LONG).show();
                finish(); // Close activity after success Simulation
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}
