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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;

import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.AttendanceEvent;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.User;
import com.example.javatraining.ui.main.MainActivity;
import com.example.javatraining.ui.main.profile.ProfileActivity;
import com.example.javatraining.ui.main.notifications.NotificationsFragment;
import com.example.javatraining.data.remote.response.AttendanceData;
import com.example.javatraining.data.remote.response.EmployeeData;
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

    private boolean isCheckedIn = false; // Based on latest log
    private AbsensiTMRepository repository;
    private List<AttendanceData> allLogs = new ArrayList<>();
    private List<com.example.javatraining.data.remote.response.LeaveData> allLeaves = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header

        // View All Recent Log
        View tvViewAll = view.findViewById(R.id.tvViewAll);
        if (tvViewAll != null) {
            tvViewAll.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToHistory();
                }
            });
        }

        // Initial Staggered Entry Animation for Cards
        View cvStatusCard = view.findViewById(R.id.cvStatusCard);
        View cvScheduleCard = view.findViewById(R.id.cvScheduleCard);

        if (cvStatusCard != null && cvScheduleCard != null) {
            cvStatusCard.setVisibility(View.INVISIBLE);
            cvScheduleCard.setVisibility(View.INVISIBLE);

            Animation anim1 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade);
            Animation anim2 = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade);
            anim2.setStartOffset(100); // 100ms delay for staggered effect

            cvStatusCard.post(() -> {
                cvStatusCard.setVisibility(View.VISIBLE);
                cvStatusCard.startAnimation(anim1);
            });
            cvScheduleCard.post(() -> {
                cvScheduleCard.setVisibility(View.VISIBLE);
                cvScheduleCard.startAnimation(anim2);
            });
        }

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = view
                .findViewById(R.id.swipeRefreshLayout);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadDataAndRefreshUI(view);
            });
        }

        loadDataAndRefreshUI(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            loadDataAndRefreshUI(getView());
        }
    }

    private void loadDataAndRefreshUI(View view) {
        // Initialize Data
        com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(
                requireContext());
        User currentUser = sessionManager.getUser();
        String karyawanId = currentUser != null ? currentUser.getId() : "";

        // Default Greeting (from Session)
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        String name = currentUser != null ? currentUser.getName() : "Guest";
        if (name != null && name.contains(" ")) {
            name = name.substring(0, name.indexOf(" "));
        }
        if (name != null)
            tvGreeting.setText(getGreeting() + ", " + name + ".");

        repository = new AbsensiTMRepository(requireActivity().getApplication());

        // Observe Profile API to update greeting with accurate name
        repository.getProfileApi().observe(getViewLifecycleOwner(), new Observer<EmployeeData>() {
            @Override
            public void onChanged(EmployeeData employeeData) {
                if (employeeData != null && employeeData.getName() != null) {
                    String updatedName = employeeData.getName();
                    if (updatedName.contains(" ")) {
                        updatedName = updatedName.substring(0, updatedName.indexOf(" "));
                    }
                    tvGreeting.setText(getGreeting() + ", " + updatedName + ".");
                }
            }
        });

        final boolean[] attendancesLoaded = {false};
        final boolean[] leavesLoaded = {false};
        final boolean[] recognitionsLoaded = {false};
        final List<com.example.javatraining.data.remote.response.RecognitionEventData>[] allPendingRecognitions = new List[]{new ArrayList<>()};

        // Fetch user logs from API
        repository.getAttendancesApi(1, 10).observe(getViewLifecycleOwner(), new Observer<List<AttendanceData>>() {
            @Override
            public void onChanged(List<AttendanceData> attendanceDataList) {
                if (attendanceDataList != null) {
                    allLogs = attendanceDataList;
                }
                attendancesLoaded[0] = true;
                checkBothLoaded(view, attendancesLoaded, leavesLoaded, recognitionsLoaded, allPendingRecognitions[0]);
            }
        });

        repository.getPendingRecognitions().observe(getViewLifecycleOwner(), new Observer<List<com.example.javatraining.data.remote.response.RecognitionEventData>>() {
            @Override
            public void onChanged(List<com.example.javatraining.data.remote.response.RecognitionEventData> recognitionEventData) {
                if (recognitionEventData != null) {
                    allPendingRecognitions[0] = recognitionEventData;
                }
                recognitionsLoaded[0] = true;
                checkBothLoaded(view, attendancesLoaded, leavesLoaded, recognitionsLoaded, allPendingRecognitions[0]);
            }
        });

        // Fetch user leaves from API
        repository.getLeavesApi(1, 10).observe(getViewLifecycleOwner(), new Observer<List<com.example.javatraining.data.remote.response.LeaveData>>() {
            @Override
            public void onChanged(List<com.example.javatraining.data.remote.response.LeaveData> leaveDataList) {
                if (leaveDataList != null) {
                    allLeaves = leaveDataList;
                }
                leavesLoaded[0] = true;
                checkBothLoaded(view, attendancesLoaded, leavesLoaded, recognitionsLoaded, allPendingRecognitions[0]);
            }
        });

        // Fetch Today's Schedule
        repository.getScheduleTodayApi().observe(getViewLifecycleOwner(), new Observer<ScheduleData>() {
            @Override
            public void onChanged(ScheduleData scheduleData) {
                TextView tvScheduleTime = view.findViewById(R.id.tvScheduleTime);
                TextView tvScheduleName = view.findViewById(R.id.tvScheduleName);
                TextView tvScheduleTolerance = view.findViewById(R.id.tvScheduleTolerance);

                if (scheduleData != null) {
                    if (tvScheduleTime != null && tvScheduleName != null) {
                        tvScheduleTime.setText(scheduleData.getCheckInTime() + " - " + scheduleData.getCheckOutTime());
                        tvScheduleName.setText(scheduleData.getName());
                    }
                    if (tvScheduleTolerance != null && scheduleData.getToleranceMinutes() != null) {
                        tvScheduleTolerance
                                .setText("Toleransi Terlambat: " + scheduleData.getToleranceMinutes() + " menit");
                        tvScheduleTolerance.setVisibility(View.VISIBLE);
                    } else if (tvScheduleTolerance != null) {
                        tvScheduleTolerance.setVisibility(View.GONE);
                    }
                } else {
                    // No schedule found for this user
                    if (tvScheduleTime != null) {
                        tvScheduleTime.setText("Tidak ada jadwal");
                    }
                    if (tvScheduleName != null) {
                        tvScheduleName.setText("-");
                    }
                    if (tvScheduleTolerance != null) {
                        tvScheduleTolerance.setVisibility(View.GONE);
                    }
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

        // Fetch notifications to update Unread Badge
        repository.getNotificationsApi().observe(getViewLifecycleOwner(), notifications -> {
            View vUnreadBadge = view.findViewById(R.id.vUnreadNotificationBadge);
            if (vUnreadBadge != null) {
                boolean hasUnread = false;
                if (notifications != null) {
                    for (com.example.javatraining.data.remote.response.NotificationData n : notifications) {
                        if (!n.isRead()) {
                            hasUnread = true;
                            break;
                        }
                    }
                }
                vUnreadBadge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void checkBothLoaded(View view, boolean[] attendancesLoaded, boolean[] leavesLoaded, boolean[] recognitionsLoaded, List<com.example.javatraining.data.remote.response.RecognitionEventData> pendingRecognitions) {
        if (attendancesLoaded[0] && leavesLoaded[0] && recognitionsLoaded[0]) {
            java.util.Map<String, com.example.javatraining.data.model.DailyAttendance> dailyMap = new java.util.HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

            for (AttendanceData p : allLogs) {
                if (p.getTimestamp() != null) {
                    try {
                        Date detectedAt = parseIsoDate(p.getTimestamp());
                        if (detectedAt != null) {
                            String dateKey = sdf.format(detectedAt);
                            com.example.javatraining.data.model.DailyAttendance daily = dailyMap.get(dateKey);
                            if (daily == null) {
                                daily = new com.example.javatraining.data.model.DailyAttendance(detectedAt);
                                dailyMap.put(dateKey, daily);
                            }

                            AttendanceEvent event = new AttendanceEvent(
                                    0, p.getCameraId(), 0, p.getEmployeeId(), null,
                                    null,
                                    "CHECK_IN".equalsIgnoreCase(p.getEventType()) ? LogType.CHECK_IN
                                            : LogType.CHECK_OUT,
                                    p.getSimilarity() != null ? p.getSimilarity() : 0.0,
                                    null, null, detectedAt, detectedAt, null);
                            event.setLate(p.getIsLate());
                            event.setConfirmationStatus(p.getConfirmationStatus());

                            if ("CHECK_IN".equalsIgnoreCase(p.getEventType()) || "IN".equalsIgnoreCase(p.getEventType())) {
                                daily.setCheckInEvent(event);
                            } else if ("CHECK_OUT".equalsIgnoreCase(p.getEventType()) || "OUT".equalsIgnoreCase(p.getEventType())) {
                                daily.setCheckOutEvent(event);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (allLeaves != null) {
                for (com.example.javatraining.data.remote.response.LeaveData l : allLeaves) {
                    if (l.getCreatedAt() != null || l.getDate() != null) {
                        try {
                            String timeStr = l.getCreatedAt() != null ? l.getCreatedAt() : l.getDate();
                            Date detectedAt = parseIsoDate(timeStr);
                            if (detectedAt == null && l.getDate() != null) {
                                detectedAt = parseIsoDate(l.getDate() + "T00:00:00Z");
                            }
                            if (detectedAt != null) {
                                String dateKey = sdf.format(detectedAt);
                                com.example.javatraining.data.model.DailyAttendance daily = dailyMap.get(dateKey);
                                if (daily == null) {
                                    daily = new com.example.javatraining.data.model.DailyAttendance(detectedAt);
                                    dailyMap.put(dateKey, daily);
                                }
                                daily.setLeaveData(l);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            List<com.example.javatraining.data.model.DailyAttendance> groupedLogs = new ArrayList<>(
                    dailyMap.values());
            java.util.Collections.sort(groupedLogs, (p1, p2) -> p2.getDate().compareTo(p1.getDate()));

            // We need a flat list of events for the Live Status (Checked In/Out)
            List<AttendanceEvent> flatLogs = new ArrayList<>();
            for (AttendanceData data : allLogs) {
                if ("REJECTED".equalsIgnoreCase(data.getConfirmationStatus())) {
                    continue; // Skip rejected events for live status
                }
                Date detectedAt = parseIsoDate(data.getTimestamp());
                if (detectedAt != null) {
                    String evtType = data.getEventType();
                    LogType type = ("CHECK_IN".equalsIgnoreCase(evtType) || "IN".equalsIgnoreCase(evtType))
                            ? LogType.CHECK_IN
                            : LogType.CHECK_OUT;
                    flatLogs.add(new AttendanceEvent(0, data.getCameraId(), 0, data.getEmployeeId(), null, null,
                            type,
                            data.getSimilarity() != null ? data.getSimilarity() : 0.0, null, null, detectedAt,
                            detectedAt, null));
                }
            }
            java.util.Collections.sort(flatLogs, (e1, e2) -> e2.getDetectedAt().compareTo(e1.getDetectedAt()));

            updateDashboard(view, groupedLogs, flatLogs, pendingRecognitions);

            androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = view
                    .findViewById(R.id.swipeRefreshLayout);
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void updateDashboard(View view, List<com.example.javatraining.data.model.DailyAttendance> groupedLogs,
            List<AttendanceEvent> flatLogs, List<com.example.javatraining.data.remote.response.RecognitionEventData> pendingRecognitions) {
        // Confirmation Status Logic
        View llNormalStatus = view.findViewById(R.id.llNormalStatus);
        View llConfirmationStatus = view.findViewById(R.id.llConfirmationStatus);
        
        llNormalStatus.setVisibility(View.VISIBLE);
        llConfirmationStatus.setVisibility(View.GONE);

        android.widget.TextView tvStatusTitle = view.findViewById(R.id.tvStatusTitle);
        android.widget.TextView tvStatusTime = view.findViewById(R.id.tvStatusTime);
        View vStatusDot = view.findViewById(R.id.vStatusDot);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        java.util.Calendar calToday = java.util.Calendar.getInstance();
        boolean hasLeaveToday = false;
        String leaveStatus = "";
        String leaveType = "";
        
        if (allLeaves != null) {
            for (com.example.javatraining.data.remote.response.LeaveData leave : allLeaves) {
                String timeStr = leave.getCreatedAt() != null ? leave.getCreatedAt() : leave.getDate();
                java.util.Date d = parseIsoDate(timeStr);
                if (d == null && leave.getDate() != null) d = parseIsoDate(leave.getDate() + "T00:00:00Z");
                if (d != null) {
                    java.util.Calendar calEvent = java.util.Calendar.getInstance();
                    calEvent.setTime(d);
                    if (calEvent.get(java.util.Calendar.YEAR) == calToday.get(java.util.Calendar.YEAR) &&
                        calEvent.get(java.util.Calendar.DAY_OF_YEAR) == calToday.get(java.util.Calendar.DAY_OF_YEAR)) {
                        hasLeaveToday = true;
                        leaveStatus = leave.getStatus() != null ? leave.getStatus() : "PENDING";
                        leaveType = leave.getType() != null ? leave.getType() : "Izin";
                        break;
                    }
                }
            }
        }

        boolean hasAttendanceToday = false;
        boolean hasConfirmedAttendanceToday = false;
        if (!flatLogs.isEmpty()) {
            AttendanceEvent latestLog = flatLogs.get(0);
            java.util.Calendar calEvent = java.util.Calendar.getInstance();
            calEvent.setTime(latestLog.getDetectedAt());
            if (calEvent.get(java.util.Calendar.YEAR) == calToday.get(java.util.Calendar.YEAR) &&
                calEvent.get(java.util.Calendar.DAY_OF_YEAR) == calToday.get(java.util.Calendar.DAY_OF_YEAR)) {
                hasAttendanceToday = true;
                if (!"PENDING".equalsIgnoreCase(latestLog.getConfirmationStatus()) && !"REJECTED".equalsIgnoreCase(latestLog.getConfirmationStatus())) {
                    hasConfirmedAttendanceToday = true;
                }
            }
        }

        boolean hasActivityToday = hasLeaveToday || hasAttendanceToday;

        if (pendingRecognitions != null && !pendingRecognitions.isEmpty() && !hasConfirmedAttendanceToday) {
            llNormalStatus.setVisibility(View.GONE);
            llConfirmationStatus.setVisibility(View.VISIBLE);
            com.example.javatraining.data.remote.response.RecognitionEventData latestPending = pendingRecognitions.get(0);
            
            android.widget.ImageView ivConfirmationSnapshot = view.findViewById(R.id.ivConfirmationSnapshot);
            if (latestPending.getThumbnail() != null && !latestPending.getThumbnail().isEmpty()) {
                String url = latestPending.getThumbnail();
                if (url.startsWith("/")) url = com.example.javatraining.BuildConfig.SUPABASE_URL + url;
                com.bumptech.glide.Glide.with(requireContext()).load(url).centerCrop().into(ivConfirmationSnapshot);
            }
            
            view.findViewById(R.id.btnConfirmAttendance).setOnClickListener(v -> {
                repository.confirmRecognition(latestPending, () -> {
                    android.widget.Toast.makeText(getContext(), "Kehadiran Dikonfirmasi", android.widget.Toast.LENGTH_SHORT).show();
                    loadDataAndRefreshUI(view);
                });
            });
            view.findViewById(R.id.btnRejectAttendance).setOnClickListener(v -> {
                repository.rejectRecognition(latestPending.getId(), () -> {
                    android.widget.Toast.makeText(getContext(), "Kehadiran Ditolak", android.widget.Toast.LENGTH_SHORT).show();
                    loadDataAndRefreshUI(view);
                });
            });
            
            setupRecentActivity(view, groupedLogs);
            return;
        }
        
        llNormalStatus.setVisibility(View.VISIBLE);
        llConfirmationStatus.setVisibility(View.GONE);

        if (hasLeaveToday) {
            isCheckedIn = false;
            String statusId = "Menunggu";
            if ("APPROVED".equalsIgnoreCase(leaveStatus)) statusId = "Diterima";
            else if ("REJECTED".equalsIgnoreCase(leaveStatus)) statusId = "Ditolak";
            
            tvStatusTitle.setText(leaveType + " (" + statusId + ")");
            tvStatusTime.setText("Hari ini");
            vStatusDot.setVisibility(View.GONE);
        } else if (hasAttendanceToday) {
            AttendanceEvent latestLog = flatLogs.get(0);
            if ("REJECTED".equalsIgnoreCase(latestLog.getConfirmationStatus())) {
                isCheckedIn = false;
                tvStatusTitle.setText("Absen Ditolak");
                tvStatusTime.setText("Silakan absen ulang");
                vStatusDot.setVisibility(View.GONE);
            } else if (latestLog.getEventType() == LogType.CHECK_OUT) {
                isCheckedIn = false;
                if ("PENDING".equalsIgnoreCase(latestLog.getConfirmationStatus())) {
                    tvStatusTitle.setText("Menunggu Konfirmasi");
                    vStatusDot.setVisibility(View.GONE);
                } else {
                    tvStatusTitle.setText("Sudah Check-Out");
                    vStatusDot.setVisibility(View.VISIBLE);
                }
                tvStatusTime.setText("Sejak " + sdf.format(latestLog.getDetectedAt()));
            } else {
                isCheckedIn = true;
                if ("PENDING".equalsIgnoreCase(latestLog.getConfirmationStatus())) {
                    tvStatusTitle.setText("Menunggu Konfirmasi");
                    vStatusDot.setVisibility(View.GONE);
                } else {
                    tvStatusTitle.setText("Sudah Check-In");
                    vStatusDot.setVisibility(View.VISIBLE);
                }
                tvStatusTime.setText("Sejak " + sdf.format(latestLog.getDetectedAt()));
            }
        } else {
            isCheckedIn = false;
            tvStatusTitle.setText("Belum Absen");
            tvStatusTime.setText("Belum ada aktivitas hari ini");
            vStatusDot.setVisibility(View.GONE);
        }

        setupRecentActivity(view, groupedLogs);
    }

    private void setupRecentActivity(View view, List<com.example.javatraining.data.model.DailyAttendance> groupedLogs) {
        // Setup Recent Activity RecyclerView
        RecyclerView rvRecentActivity = view.findViewById(R.id.rvRecentActivity);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));

        // Take up to 3 grouped logs
        List<com.example.javatraining.data.model.DailyAttendance> recentLogs = new ArrayList<>();
        for (int i = 0; i < Math.min(groupedLogs.size(), 3); i++) {
            recentLogs.add(groupedLogs.get(i));
        }

        com.example.javatraining.ui.main.history.HistoryLogAdapter dashboardLogAdapter = new com.example.javatraining.ui.main.history.HistoryLogAdapter(
                recentLogs);
        rvRecentActivity.setAdapter(dashboardLogAdapter);

        // Apply LayoutAnimationController for staggered list item entry
        Animation slideUpAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.item_animation_slide_up);
        LayoutAnimationController controller = new LayoutAnimationController(slideUpAnim);
        controller.setDelay(0.15f); // 15% delay between items
        rvRecentActivity.setLayoutAnimation(controller);
        rvRecentActivity.scheduleLayoutAnimation();
    }

    private Date parseIsoDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return null;
        String raw = dateStr.trim();
        String normalized = raw.replace(" ", "T");

        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String fmt : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.US);
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date d = sdf.parse(raw);
                if (d != null)
                    return d;
            } catch (Exception ignored) {
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.US);
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date d = sdf.parse(normalized);
                if (d != null)
                    return d;
            } catch (Exception ignored) {
            }
        }
        return null; // Return null instead of new Date() to avoid fake current time logs
    }

    private String getGreeting() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int timeOfDay = c.get(java.util.Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 11){
            return "Selamat pagi";
        } else if(timeOfDay >= 11 && timeOfDay < 15){
            return "Selamat siang";
        } else if(timeOfDay >= 15 && timeOfDay < 18){
            return "Selamat sore";
        } else {
            return "Selamat malam";
        }
    }
}
