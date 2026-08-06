package com.example.absensitm.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.absensitm.R;
import com.example.absensitm.data.local.SessionManager;
import com.example.absensitm.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.setApiService(com.example.absensitm.data.network.ApiClient.getApiService(requireContext()));

        viewModel.getProfileData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.tvProfileName.setText(profile.getName());
                binding.tvEmailDetail.setText(profile.getEmail());
                
                String roleText = profile.getPosition();
                if (profile.getDepartment() != null && !profile.getDepartment().isEmpty()) {
                    roleText += " - " + profile.getDepartment();
                }
                binding.tvProfileRole.setText(roleText);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.fetchProfile();

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
        });

        binding.btnRegisterFace.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), FaceRegistrationActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_profile_to_login);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
