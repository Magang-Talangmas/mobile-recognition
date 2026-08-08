package com.example.javatraining.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javatraining.R;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> finish());

        com.example.javatraining.data.repository.AbsensiTMRepository repository = new com.example.javatraining.data.repository.AbsensiTMRepository(
                getApplication());
        repository.getProfileApi().observe(this,
                new androidx.lifecycle.Observer<com.example.javatraining.data.remote.response.EmployeeData>() {
                    @Override
                    public void onChanged(com.example.javatraining.data.remote.response.EmployeeData user) {
                        if (user != null) {
                            android.widget.TextView tvProfileName = findViewById(R.id.tvProfileName);
                            android.widget.TextView tvProfileEmail = findViewById(R.id.tvProfileEmail);
                            tvProfileName.setText(user.getName());

                            if (user.getPosition() != null && user.getDepartment() != null) {
                                tvProfileEmail.setText(user.getPosition() + " • " + user.getDepartment());
                            } else if (user.getPosition() != null) {
                                tvProfileEmail.setText(user.getPosition());
                            } else {
                                tvProfileEmail.setText(user.getEmail());
                            }

                            android.widget.TextView tvProfileEmpId = findViewById(R.id.tvProfileEmpId);
                            if (tvProfileEmpId != null && user.getId() != null) {
                                String displayId = user.getId();
                                if (displayId.length() > 8) {
                                    displayId = displayId.substring(0, 8).toUpperCase();
                                }
                                tvProfileEmpId.setText(displayId);
                            }

                            // You could add faceRegistered check here if needed
                        }
                    }
                });

        android.widget.Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(
                    ProfileActivity.this);
            sessionManager.clearSession();
            // Logout logic
            Intent intent = new Intent(ProfileActivity.this, com.example.javatraining.ui.auth.WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
