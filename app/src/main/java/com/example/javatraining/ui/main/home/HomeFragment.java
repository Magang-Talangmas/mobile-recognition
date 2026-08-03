package com.example.javatraining.ui.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.javatraining.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        viewModel.getTodayAttendance(today).observe(getViewLifecycleOwner(), attendance -> {
            if (attendance != null) {
                binding.tvStatus.setText(attendance.status);
            } else {
                binding.tvStatus.setText("Belum Hadir");
            }
        });
        
        binding.btnManualCheckIn.setOnClickListener(v -> {
            viewModel.performCheckIn("Manual via HP");
            android.widget.Toast.makeText(getContext(), "Berhasil memproses absen masuk", android.widget.Toast.LENGTH_SHORT).show();
        });

        binding.btnCheckOut.setOnClickListener(v -> {
            viewModel.performCheckIn("Checkout via HP");
            android.widget.Toast.makeText(getContext(), "Berhasil memproses absen keluar", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}
