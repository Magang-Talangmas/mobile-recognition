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

    private boolean isCheckedIn = false; // Based on latest log
    private AbsensiTMRepository repository;

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
        view.findViewById(R.id.imgAvatar).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

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

    private void loadDataAndRefreshUI(View view) {
        // Initialize Data
        com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(
                requireContext());
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
                    java.util.Map<String, com.example.javatraining.data.model.DailyAttendance> dailyMap = new java.util.HashMap<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    
                    for (AttendanceData p : attendanceDataList) {
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
                                            "CHECK_IN".equalsIgnoreCase(p.getEventType()) ? LogType.CHECK_IN : LogType.CHECK_OUT,
                                            p.getSimilarity() != null ? p.getSimilarity() : 0.0,
                                            null, null, detectedAt, detectedAt, null);
                                    event.setLate(p.getIsLate());
                                    event.setConfirmationStatus(p.getConfirmationStatus());
                                    
                                    if ("CHECK_IN".equalsIgnoreCase(p.getEventType())) {
                                        daily.setCheckInEvent(event);
                                    } else if ("CHECK_OUT".equalsIgnoreCase(p.getEventType())) {
                                        daily.setCheckOutEvent(event);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    List<com.example.javatraining.data.model.DailyAttendance> groupedLogs = new ArrayList<>(dailyMap.values());
                    java.util.Collections.sort(groupedLogs, (p1, p2) -> p2.getDate().compareTo(p1.getDate()));

                    // We need a flat list of events for the Live Status (Checked In/Out)
                    List<AttendanceEvent> flatLogs = new ArrayList<>();
                    for (AttendanceData data : attendanceDataList) {
                        Date detectedAt = parseIsoDate(data.getTimestamp());
                        if (detectedAt != null) {
                            flatLogs.add(new AttendanceEvent(0, data.getCameraId(), 0, data.getEmployeeId(), null, null,
                                "CHECK_IN".equalsIgnoreCase(data.getEventType()) ? LogType.CHECK_IN : LogType.CHECK_OUT,
                                data.getSimilarity() != null ? data.getSimilarity() : 0.0, null, null, detectedAt, detectedAt, null));
                        }
                    }
                    java.util.Collections.sort(flatLogs, (e1, e2) -> e2.getDetectedAt().compareTo(e1.getDetectedAt()));

                    updateDashboard(view, groupedLogs, flatLogs);
                }

                androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = view
                        .findViewById(R.id.swipeRefreshLayout);
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        // Fetch Today's Schedule
        repository.getScheduleTodayApi().observe(getViewLifecycleOwner(), new Observer<ScheduleData>() {
            @Override
            public void onChanged(ScheduleData scheduleData) {
                if (scheduleData != null) {
                    TextView tvScheduleTime = view.findViewById(R.id.tvScheduleTime);
                    TextView tvScheduleName = view.findViewById(R.id.tvScheduleName);

                    if (tvScheduleTime != null && tvScheduleName != null) {
                        tvScheduleTime.setText(scheduleData.getCheckInTime() + " - " + scheduleData.getCheckOutTime());
                        tvScheduleName.setText(scheduleData.getName());
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

    private void updateDashboard(View view, List<com.example.javatraining.data.model.DailyAttendance> groupedLogs, List<AttendanceEvent> flatLogs) {
        // Live Status Logic
        TextView tvStatusTitle = view.findViewById(R.id.tvStatusTitle);
        TextView tvStatusTime = view.findViewById(R.id.tvStatusTime);
        View vStatusDot = view.findViewById(R.id.vStatusDot);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());

        if (!flatLogs.isEmpty()) {
            AttendanceEvent latestLog = flatLogs.get(0);
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
        
        // Take up to 3 grouped logs
        List<com.example.javatraining.data.model.DailyAttendance> recentLogs = new ArrayList<>();
        for (int i = 0; i < Math.min(groupedLogs.size(), 3); i++) {
            recentLogs.add(groupedLogs.get(i));
        }

        com.example.javatraining.ui.main.history.HistoryLogAdapter dashboardLogAdapter = new com.example.javatraining.ui.main.history.HistoryLogAdapter(recentLogs);
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
}
