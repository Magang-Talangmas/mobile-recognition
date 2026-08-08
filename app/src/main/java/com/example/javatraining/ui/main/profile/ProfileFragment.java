package com.example.javatraining.ui.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.javatraining.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(
                requireContext());
        com.example.javatraining.data.model.User user = sessionManager.getUser();

        if (user != null) {
            binding.tvName
                    .setText(user.getName() != null && !user.getName().isEmpty() ? user.getName() : user.getEmail());
            binding.tvPosition.setText(user.getRole() != null ? user.getRole() : "Employee");
            String displayId = user.getId() != null ? user.getId() : "N/A";
            if (displayId.length() > 8) {
                displayId = displayId.substring(0, 8).toUpperCase();
            }
            binding.tvEmployeeId.setText(displayId);
        }

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            android.content.Intent intent = new android.content.Intent(requireContext(),
                    com.example.javatraining.ui.auth.WelcomeActivity.class);
            intent.setFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
