package com.example.javatraining.ui.main.profile;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javatraining.R;
import com.google.android.material.button.MaterialButton;
import com.example.javatraining.data.local.SessionManager;
import com.example.javatraining.data.model.User;
import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        SessionManager sessionManager = new SessionManager(this);
        User user = sessionManager.getUser();

        if (user != null) {
            ImageView ivAvatar = findViewById(R.id.ivAvatar);
            if (ivAvatar != null) {
                Glide.with(this)
                        .load(user.getAvatar())
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(ivAvatar);
            }

            EditText etFirstName = findViewById(R.id.etFirstName);
            EditText etLastName = findViewById(R.id.etLastName);
            EditText etEmail = findViewById(R.id.etEmail);
            EditText etMobile = findViewById(R.id.etMobile);

            if (user.getName() != null) {
                String[] nameParts = user.getName().split(" ", 2);
                if (etFirstName != null) etFirstName.setText(nameParts[0]);
                if (nameParts.length > 1 && etLastName != null) {
                    etLastName.setText(nameParts[1]);
                }
            }

            if (etEmail != null) {
                etEmail.setText(user.getEmail());
            }
            
            // Note: Mobile is not currently in the User model, leaving blank or placeholder
        }
        
        MaterialButton btnSave = findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                android.widget.Toast.makeText(this, "Profile updated", android.widget.Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
