package com.example.javatraining.ui.main.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;
import com.example.javatraining.data.repository.MockDatabase;
import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.AttendanceEvent;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.User;
import com.example.javatraining.ui.main.MainActivity;
import com.example.javatraining.ui.main.profile.ProfileActivity;
import com.example.javatraining.ui.main.notifications.NotificationsFragment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    private RecentActivityAdapter activityAdapter;
    private boolean isCheckedIn = false; // Based on latest log

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

        loadDataAndRefreshUI(view);
    }
    
    private void loadDataAndRefreshUI(View view) {
        // Initialize Data
        MockDatabase db = MockDatabase.getInstance();
        User currentUser = db.getCurrentUser();
        Karyawan currentKaryawan = db.getCurrentKaryawan();
        String karyawanId = currentKaryawan != null ? currentKaryawan.getId() : "";

        // Greeting
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        String name = currentKaryawan != null ? currentKaryawan.getNamaLengkap() : "Budi";
        if (name.contains(" ")) {
            name = name.substring(0, name.indexOf(" "));
        }
        tvGreeting.setText("Good morning, " + name + ".");

        // Fetch user logs
        List<AttendanceEvent> allLogs = db.getAttendanceHistory();
        List<AttendanceEvent> userLogs = new ArrayList<>();
        for (AttendanceEvent p : allLogs) {
            if (p.getEmployeeId() != null && p.getEmployeeId().equals(karyawanId)) {
                userLogs.add(p);
            }
        }

        // Live Status Logic
        TextView tvStatusTitle = view.findViewById(R.id.tvStatusTitle);
        TextView tvStatusTime = view.findViewById(R.id.tvStatusTime);
        View vStatusDot = view.findViewById(R.id.vStatusDot);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());

        if (!userLogs.isEmpty()) {
            AttendanceEvent latestLog = userLogs.get(0);
            if (latestLog.getEventType() == LogType.CHECK_OUT) {
                isCheckedIn = false;
                tvStatusTitle.setText("Checked Out");
                tvStatusTime.setText("Since " + sdf.format(latestLog.getDetectedAt()));
                vStatusDot.setVisibility(View.GONE);
            } else {
                isCheckedIn = true;
                tvStatusTitle.setText("Checked In");
                tvStatusTime.setText("Since " + sdf.format(latestLog.getDetectedAt()));
                vStatusDot.setVisibility(View.VISIBLE);
            }
        } else {
            isCheckedIn = false;
            tvStatusTitle.setText("Checked Out");
            tvStatusTime.setText("No activity today");
            vStatusDot.setVisibility(View.GONE);
        }

        // Handle Notifications Icon click
        View btnNotifications = view.findViewById(R.id.btnNotifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToFragment(new NotificationsFragment());
                }
            });
        }
        
        // Setup Recent Activity RecyclerView
        RecyclerView rvRecentActivity = view.findViewById(R.id.rvRecentActivity);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        
        if (activityAdapter == null) {
            activityAdapter = new RecentActivityAdapter(userLogs);
            rvRecentActivity.setAdapter(activityAdapter);
        } else {
            activityAdapter.updateData(userLogs);
        }
    }
}
