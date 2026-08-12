package com.example.javatraining.ui.main.profile;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.javatraining.R;
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

            TextView tvFullName = findViewById(R.id.tvFullName);
            TextView tvEmployeeId = findViewById(R.id.tvEmployeeId);
            TextView tvDepartment = findViewById(R.id.tvDepartment);
            TextView tvPosition = findViewById(R.id.tvPosition);
            TextView tvEmail = findViewById(R.id.tvEmail);

            if (tvFullName != null) {
                tvFullName.setText(user.getName() != null && !user.getName().isEmpty() ? user.getName() : "-");
            }
            if (tvEmployeeId != null) {
                tvEmployeeId.setText(user.getEmployeeId() != null && !user.getEmployeeId().isEmpty() ? user.getEmployeeId() : "-");
            }
            if (tvDepartment != null) {
                tvDepartment.setText(user.getDepartment() != null && !user.getDepartment().isEmpty() ? user.getDepartment() : "-");
            }
            if (tvPosition != null) {
                String positionText = "";
                if (user.getPosition() != null && !user.getPosition().isEmpty()) {
                    positionText = user.getPosition();
                }
                if (user.getRole() != null && !user.getRole().isEmpty()) {
                    positionText += (positionText.isEmpty() ? "" : " / ") + user.getRole();
                }
                tvPosition.setText(positionText.isEmpty() ? "-" : positionText);
            }
            if (tvEmail != null) {
                tvEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : "-");
            }
        }
    }
}
