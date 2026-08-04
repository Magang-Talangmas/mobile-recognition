package com.example.javatraining.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.javatraining.databinding.FragmentHomeBinding;
import com.example.javatraining.ui.main.profile.ProfileActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler = new Handler();

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null) {
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                binding.tvLiveTime.setText(currentTime);
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        
        binding.imgAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup live clock
        handler.post(updateTimeRunnable);

        // Setup Date
        String todayDate = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID")).format(new Date());
        binding.tvDate.setText(todayDate);

        // Initial UI Update
        updateUI();

        // Simulated Check In / Check Out logic with MockDatabase
        binding.btnCheckIn.setOnClickListener(v -> {
            com.example.javatraining.data.model.Karyawan currentUser = com.example.javatraining.data.repository.MockDatabase.getInstance().getCurrentKaryawan();
            if (currentUser != null) {
                com.example.javatraining.data.repository.MockDatabase.getInstance().checkIn(currentUser.getId());
                updateUI();
                android.widget.Toast.makeText(getContext(), "Check In Sukses!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnCheckOut.setOnClickListener(v -> {
            com.example.javatraining.data.model.Karyawan currentUser = com.example.javatraining.data.repository.MockDatabase.getInstance().getCurrentKaryawan();
            if (currentUser != null) {
                com.example.javatraining.data.repository.MockDatabase.getInstance().checkOut(currentUser.getId());
                updateUI();
                android.widget.Toast.makeText(getContext(), "Check Out Sukses!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateUI() {
        com.example.javatraining.data.model.Karyawan currentUser = com.example.javatraining.data.repository.MockDatabase.getInstance().getCurrentKaryawan();
        if (currentUser != null) {
            boolean isCheckedIn = com.example.javatraining.data.repository.MockDatabase.getInstance().isCheckedIn(currentUser.getId());
            if (isCheckedIn) {
                binding.tvCurrentStatus.setText("Tracking\nRunning");
                binding.tvCurrentStatus.setTextColor(android.graphics.Color.parseColor("#10B981")); // Green
                binding.btnCheckIn.setAlpha(0.5f);
                binding.btnCheckIn.setEnabled(false);
                binding.btnCheckOut.setAlpha(1.0f);
                binding.btnCheckOut.setEnabled(true);
            } else {
                binding.tvCurrentStatus.setText("Tracking\nPause");
                binding.tvCurrentStatus.setTextColor(android.graphics.Color.parseColor("#EF4444")); // Red
                binding.btnCheckIn.setAlpha(1.0f);
                binding.btnCheckIn.setEnabled(true);
                binding.btnCheckOut.setAlpha(0.5f);
                binding.btnCheckOut.setEnabled(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimeRunnable);
        binding = null;
    }
}
