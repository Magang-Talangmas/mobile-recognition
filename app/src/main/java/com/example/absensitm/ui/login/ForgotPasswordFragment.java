package com.example.absensitm.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.absensitm.R;

public class ForgotPasswordFragment extends Fragment {

    private EditText etEmail;
    private Button btnReset;
    private TextView tvBackToLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.etEmail);
        btnReset = view.findViewById(R.id.btnReset);
        tvBackToLogin = view.findViewById(R.id.tvBackToLogin);

        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }
            Toast.makeText(requireContext(), "Tautan reset telah dikirim ke " + email, Toast.LENGTH_SHORT).show();
            // TODO: Implement actual forgot password API call if needed
        });

        tvBackToLogin.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }
}
