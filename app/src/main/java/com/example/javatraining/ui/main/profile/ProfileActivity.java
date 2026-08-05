package com.example.javatraining.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javatraining.R;
import com.example.javatraining.ui.auth.LoginActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());
        
        com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(this);
        com.example.javatraining.data.model.User user = sessionManager.getUser();
        if (user != null) {
            android.widget.TextView tvProfileName = findViewById(R.id.tvProfileName);
            android.widget.TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
            tvProfileName.setText(user.getName());
            tvProfileEmail.setText(user.getUsername());
        }

        android.widget.Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            // Logout logic
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
