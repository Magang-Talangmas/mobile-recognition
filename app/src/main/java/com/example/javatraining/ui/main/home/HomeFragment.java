package com.example.javatraining.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;
import com.example.javatraining.data.repository.MockDatabase;
import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.Presensi;
import com.example.javatraining.data.model.User;
import com.example.javatraining.ui.main.profile.ProfileActivity;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecentActivityAdapter activityAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header
        view.findViewById(R.id.imgAvatar).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        // Initialize Data
        MockDatabase db = MockDatabase.getInstance();
        User currentUser = db.getCurrentUser();
        Karyawan currentKaryawan = db.getCurrentKaryawan();
        String karyawanId = currentKaryawan != null ? currentKaryawan.getId() : "";

        // Greeting
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        String name = currentKaryawan != null ? currentKaryawan.getNamaLengkap() : "Budi";
        // Extract first name
        if (name.contains(" ")) {
            name = name.substring(0, name.indexOf(" "));
        }
        tvGreeting.setText("Good Morning, " + name);

        // Fetch user logs
        List<Presensi> allLogs = db.getAttendanceHistory();
        List<Presensi> userLogs = new ArrayList<>();
        for (Presensi p : allLogs) {
            if (p.getKaryawanId() != null && p.getKaryawanId().equals(karyawanId)) {
                userLogs.add(p);
            }
        }

        // Calculate Stats (Mocking logic for demonstration since data is limited)
        int daysPresent = userLogs.size(); // Simplified mock logic
        int daysTotal = 22; // Typical working days in a month
        
        TextView tvDaysPresent = view.findViewById(R.id.tvDaysPresent);
        TextView tvDaysTotal = view.findViewById(R.id.tvDaysTotal);
        ProgressBar pbPresence = view.findViewById(R.id.pbPresence);
        
        tvDaysPresent.setText(String.valueOf(daysPresent));
        tvDaysTotal.setText("/ " + daysTotal + " Days");
        int progress = (int) (((float) daysPresent / daysTotal) * 100);
        pbPresence.setProgress(progress);

        // Live Status Logic
        TextView tvLiveStatus = view.findViewById(R.id.tvLiveStatus);
        TextView tvLiveStatusTime = view.findViewById(R.id.tvLiveStatusTime);
        
        if (!userLogs.isEmpty()) {
            // Get the most recent log (assuming index 0 is latest for mock data)
            Presensi latestLog = userLogs.get(0);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
            
            if (latestLog.getCheckOutTime() != null) {
                tvLiveStatus.setText("Checked Out");
                tvLiveStatusTime.setText("Last seen at " + sdf.format(latestLog.getCheckOutTime()));
            } else if (latestLog.getCheckInTime() != null) {
                tvLiveStatus.setText("In Office");
                tvLiveStatusTime.setText("Clocked in at " + sdf.format(latestLog.getCheckInTime()));
            } else if (latestLog.getWaktuTerdeteksi() != null) {
                tvLiveStatus.setText("In Office");
                tvLiveStatusTime.setText("Detected at " + sdf.format(latestLog.getWaktuTerdeteksi()));
            } else {
                tvLiveStatus.setText("Not Detected");
                tvLiveStatusTime.setText("No activity yet today");
            }
        } else {
            tvLiveStatus.setText("Not Detected");
            tvLiveStatusTime.setText("No activity yet today");
        }

        // Setup Recent Activity RecyclerView
        RecyclerView rvRecentActivity = view.findViewById(R.id.rvRecentActivity);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Only show up to 3 recent activities
        activityAdapter = new RecentActivityAdapter(userLogs);
        rvRecentActivity.setAdapter(activityAdapter);
    }
}
