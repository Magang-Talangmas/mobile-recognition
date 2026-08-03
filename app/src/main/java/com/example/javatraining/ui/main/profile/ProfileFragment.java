package com.example.javatraining.ui.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.javatraining.databinding.FragmentProfileBinding;
import com.example.javatraining.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private androidx.activity.result.ActivityResultLauncher<Intent> cameraLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        
        cameraLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        String photoUriString = result.getData().getStringExtra("photoUri");
                        if (photoUriString != null) {
                            android.net.Uri photoUri = android.net.Uri.parse(photoUriString);
                            // Using Glide to load image gracefully
                            com.bumptech.glide.Glide.with(this)
                                    .load(photoUri)
                                    .circleCrop()
                                    .into(binding.ivProfile);
                            Toast.makeText(getContext(), "Foto master berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dummy data for MVP
        binding.tvName.setText("John Doe (Dummy)");
        binding.tvRole.setText("Staff | Morning Shift");

        binding.btnUpdateFace.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelfEnrollActivity.class);
            cameraLauncher.launch(intent);
        });

        binding.btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
