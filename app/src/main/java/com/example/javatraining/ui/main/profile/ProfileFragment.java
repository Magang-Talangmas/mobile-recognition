package com.example.javatraining.ui.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.javatraining.databinding.FragmentProfileBinding;
import com.example.javatraining.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
            
            com.bumptech.glide.Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .into(binding.ivAvatar);
        }

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            android.content.Intent intent = new android.content.Intent(requireContext(),
                    com.example.javatraining.ui.auth.WelcomeActivity.class);
            intent.setFlags(
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.btnPersonalInfo.setOnClickListener(v -> {
            startActivity(new android.content.Intent(requireContext(), ProfileActivity.class));
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            showChangePasswordBottomSheet();
        });

        return binding.getRoot();
    }

    private void showChangePasswordBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_change_password, null);
        bottomSheetDialog.setContentView(view);
        
        View btnSave = view.findViewById(R.id.btnSavePassword);
        btnSave.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            android.widget.Toast.showText(requireContext(), "Password saved successfully", android.widget.Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
