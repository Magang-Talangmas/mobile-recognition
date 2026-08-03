package com.example.javatraining.ui.main.request;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.javatraining.databinding.FragmentRequestBinding;

public class RequestFragment extends Fragment {

    private FragmentRequestBinding binding;

    private androidx.activity.result.ActivityResultLauncher<String> galleryLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestBinding.inflate(inflater, container, false);
        
        galleryLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        binding.ivAttachmentPreview.setVisibility(View.VISIBLE);
                        com.bumptech.glide.Glide.with(this)
                                .load(uri)
                                .into(binding.ivAttachmentPreview);
                    }
                }
        );
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] requestTypes = new String[] {"Koreksi Absensi", "Izin Sakit", "Izin Cuti"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, requestTypes);
        binding.spinnerType.setAdapter(adapter);

        binding.btnAttach.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        binding.btnSubmit.setOnClickListener(v -> {
            String type = binding.spinnerType.getText().toString();
            String reason = binding.etReason.getText() != null ? binding.etReason.getText().toString() : "";

            if (type.isEmpty() || reason.isEmpty()) {
                Toast.makeText(getContext(), "Mohon isi semua field", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Pengajuan " + type + " berhasil dikirim!", Toast.LENGTH_SHORT).show();
                binding.etReason.setText("");
                binding.ivAttachmentPreview.setVisibility(View.GONE);
                binding.ivAttachmentPreview.setImageDrawable(null);
            }
        });
    }
}
