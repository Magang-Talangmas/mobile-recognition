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
import com.example.javatraining.data.remote.response.AttendanceData;
import com.example.javatraining.data.remote.response.ScheduleData;
import com.example.javatraining.data.repository.AbsensiTMRepository;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecentActivityAdapter activityAdapter;
    private boolean isCheckedIn = false; // Based on latest log
    private AbsensiTMRepository repository;

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
        com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(requireContext());
        User currentUser = sessionManager.getUser();
        String karyawanId = currentUser != null ? currentUser.getId() : "";

        // Greeting
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        String name = currentUser != null ? currentUser.getName() : "Guest";
        if (name != null && name.contains(" ")) {
            name = name.substring(0, name.indexOf(" "));
        }
        tvGreeting.setText("Good morning, " + name + ".");

        // Fetch user logs from API
        repository = new AbsensiTMRepository(requireActivity().getApplication());
        repository.getAttendancesApi(1, 10).observe(getViewLifecycleOwner(), new Observer<List<AttendanceData>>() {
            @Override
            public void onChanged(List<AttendanceData> attendanceDataList) {
                if (attendanceDataList != null) {
                    List<AttendanceEvent> userLogs = new ArrayList<>();
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    
                    for (AttendanceData data : attendanceDataList) {
                        try {
                            Date detectedAt = isoFormat.parse(data.getTimestamp());
                            if (detectedAt != null) {
                                userLogs.add(new AttendanceEvent(
                                        0, data.getCameraId(), 0, data.getEmployeeId(), null,
                                        null, 
                                        "CHECK_IN".equalsIgnoreCase(data.getEventType()) ? LogType.CHECK_IN : LogType.CHECK_OUT,
                                        data.getSimilarity() != null ? data.getSimilarity() : 0.0,
                                        null, null, detectedAt, detectedAt, null
                                ));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Update UI with logs
                    updateDashboard(view, userLogs);
                }
            }
        });

        // Fetch Today's Schedule
        repository.getScheduleTodayApi().observe(getViewLifecycleOwner(), new Observer<ScheduleData>() {
            @Override
            public void onChanged(ScheduleData scheduleData) {
                TextView tvScheduleShiftName = view.findViewById(R.id.tvScheduleShiftName);
                TextView tvScheduleTime = view.findViewById(R.id.tvScheduleTime);
                TextView tvScheduleLocation = view.findViewById(R.id.tvScheduleLocation);
                
                if (scheduleData != null) {
                    if (tvScheduleShiftName != null) tvScheduleShiftName.setText(scheduleData.getShiftName() != null ? scheduleData.getShiftName() : "Normal Shift");
                    if (tvScheduleTime != null) {
                        String start = scheduleData.getStartTime() != null ? scheduleData.getStartTime() : "--:--";
                        String end = scheduleData.getEndTime() != null ? scheduleData.getEndTime() : "--:--";
                        tvScheduleTime.setText(start + " - " + end);
                    }
                    if (tvScheduleLocation != null) tvScheduleLocation.setText("Head Office");
                } else {
                    if (tvScheduleShiftName != null) tvScheduleShiftName.setText("No Schedule");
                    if (tvScheduleTime != null) tvScheduleTime.setText("--:-- - --:--");
                    if (tvScheduleLocation != null) tvScheduleLocation.setText("-");
                }
            }
        });

        // Handle Notifications Icon click
        View btnNotifications = view.findViewById(R.id.btnNotifications);
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToFragment(new NotificationsFragment());
                }
            });
        }
    }
    
    private void updateDashboard(View view, List<AttendanceEvent> userLogs) {
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
