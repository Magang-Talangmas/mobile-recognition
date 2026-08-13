package com.example.javatraining.ui.main.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.AttendanceEvent;
import com.example.javatraining.data.model.DailyAttendance;
import com.example.javatraining.data.remote.response.AttendanceData;
import com.example.javatraining.data.repository.AbsensiTMRepository;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.datepicker.MaterialDatePicker;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Collections;

public class HistoryFragment extends Fragment {

    private HistoryLogAdapter adapter;
    private List<AttendanceData> allLogs;
    private List<com.example.javatraining.data.remote.response.LeaveData> allLeaves;
    private List<com.example.javatraining.data.remote.response.RecognitionEventData> allPendingRecognitions;
    private List<DailyAttendance> filteredLogs;
    private RecyclerView rvHistory;
    private AbsensiTMRepository repository;

    private Date selectedDate = new Date();
    private TextView tvSelectedDate;
    private TextView tvActivityLogTitle;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    private SimpleDateFormat titleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvActivityLogTitle = view.findViewById(R.id.tvActivityLogTitle);
        View btnPickDate = view.findViewById(R.id.btnPickDate);

        if (btnPickDate != null) {
            btnPickDate.setOnClickListener(v -> showDatePicker());
        }

        allLogs = new ArrayList<>();
        allLeaves = new ArrayList<>();
        allPendingRecognitions = new ArrayList<>();
        filteredLogs = new ArrayList<>();

        adapter = new HistoryLogAdapter(filteredLogs);
        adapter.setListener(event -> {
            showConfirmationDialog(event);
        });
        rvHistory.setAdapter(adapter);

        repository = new AbsensiTMRepository(requireActivity().getApplication());

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = view
                .findViewById(R.id.swipeRefreshLayout);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                fetchAttendanceHistory();
            });
        }

        fetchAttendanceHistory();

        updateDateLabels();
        applyFilter();

        return view;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(selectedDate.getTime())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = new Date(selection);
            updateDateLabels();
            applyFilter();
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showConfirmationDialog(AttendanceEvent event) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_attendance, null);
        builder.setView(dialogView);

        android.widget.ImageView ivSnapshot = dialogView.findViewById(R.id.ivSnapshot);
        if (event.getThumbnailUrl() != null && !event.getThumbnailUrl().isEmpty()) {
            String url = event.getThumbnailUrl();
            if (url.startsWith("/")) url = com.example.javatraining.BuildConfig.SUPABASE_URL + url;
            com.bumptech.glide.Glide.with(this).load(url).centerCrop().into(ivSnapshot);
        }

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            com.example.javatraining.data.remote.response.RecognitionEventData mockEvent = new com.example.javatraining.data.remote.response.RecognitionEventData();
            mockEvent.setId(event.getRecognitionId());
            mockEvent.setEmployeeId(event.getEmployeeId());
            mockEvent.setCameraId(event.getCameraId());
            
            repository.confirmRecognition(mockEvent, () -> {
                dialog.dismiss();
                android.widget.Toast.makeText(getContext(), "Kehadiran Dikonfirmasi", android.widget.Toast.LENGTH_SHORT).show();
                fetchAttendanceHistory(); // Refresh history
            });
        });

        dialogView.findViewById(R.id.btnReject).setOnClickListener(v -> {
            repository.rejectRecognition(event.getRecognitionId(), () -> {
                dialog.dismiss();
                android.widget.Toast.makeText(getContext(), "Kehadiran Ditolak", android.widget.Toast.LENGTH_SHORT).show();
                fetchAttendanceHistory(); // Refresh history
            });
        });

        dialog.show();
    }

    private void updateDateLabels() {
        if (tvSelectedDate != null) {
            tvSelectedDate.setText(dateFormat.format(selectedDate));
        }
        if (tvActivityLogTitle != null) {
            Calendar today = Calendar.getInstance();
            Calendar selected = Calendar.getInstance();
            selected.setTime(selectedDate);
            if (today.get(Calendar.YEAR) == selected.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == selected.get(Calendar.DAY_OF_YEAR)) {
                tvActivityLogTitle.setText("Activity Log");
            } else {
                tvActivityLogTitle.setText(titleDateFormat.format(selectedDate) + " Activity");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data every time the tab is switched to
        fetchAttendanceHistory();
    }

    private void fetchAttendanceHistory() {
        final boolean[] attendancesLoaded = {false};
        final boolean[] leavesLoaded = {false};
        final boolean[] recognitionsLoaded = {false};

        repository.getAttendancesApi(1, 100).observe(getViewLifecycleOwner(), new Observer<List<AttendanceData>>() {
            @Override
            public void onChanged(List<AttendanceData> attendanceDataList) {
                if (attendanceDataList != null) {
                    allLogs = attendanceDataList;
                }
                attendancesLoaded[0] = true;
                checkBothLoaded(attendancesLoaded, leavesLoaded, recognitionsLoaded);
            }
        });

        repository.getLeavesApi(1, 100).observe(getViewLifecycleOwner(), new Observer<List<com.example.javatraining.data.remote.response.LeaveData>>() {
            @Override
            public void onChanged(List<com.example.javatraining.data.remote.response.LeaveData> leaveDataList) {
                if (leaveDataList != null) {
                    allLeaves = leaveDataList;
                }
                leavesLoaded[0] = true;
                checkBothLoaded(attendancesLoaded, leavesLoaded, recognitionsLoaded);
            }
        });

        repository.getPendingRecognitions().observe(getViewLifecycleOwner(), new Observer<List<com.example.javatraining.data.remote.response.RecognitionEventData>>() {
            @Override
            public void onChanged(List<com.example.javatraining.data.remote.response.RecognitionEventData> recognitionEventData) {
                if (recognitionEventData != null) {
                    allPendingRecognitions = recognitionEventData;
                }
                recognitionsLoaded[0] = true;
                checkBothLoaded(attendancesLoaded, leavesLoaded, recognitionsLoaded);
            }
        });
    }

    private void checkBothLoaded(boolean[] attendancesLoaded, boolean[] leavesLoaded, boolean[] recognitionsLoaded) {
        if (attendancesLoaded[0] && leavesLoaded[0] && recognitionsLoaded[0]) {
            applyFilter();
            androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout = getView() != null
                    ? getView().findViewById(R.id.swipeRefreshLayout)
                    : null;
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void applyFilter() {
        filteredLogs.clear();
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);

        java.util.Map<String, DailyAttendance> dailyMap = new java.util.HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        for (AttendanceData p : allLogs) {
            if (p.getTimestamp() != null) {
                try {
                    Date detectedAt = parseIsoDate(p.getTimestamp());
                    if (detectedAt != null) {
                        Calendar pCal = Calendar.getInstance();
                        pCal.setTime(detectedAt);
                        if (pCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                pCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)) {

                            String dateKey = sdf.format(detectedAt);
                            DailyAttendance daily = dailyMap.get(dateKey);
                            if (daily == null) {
                                daily = new DailyAttendance(detectedAt);
                                dailyMap.put(dateKey, daily);
                            }

                            String evtType = p.getEventType();
                            LogType type = ("CHECK_IN".equalsIgnoreCase(evtType) || "IN".equalsIgnoreCase(evtType)) ? LogType.CHECK_IN : LogType.CHECK_OUT;
                            
                            AttendanceEvent event = new AttendanceEvent(
                                    0, p.getCameraId(), 0, p.getEmployeeId(), null,
                                    null,
                                    type,
                                    p.getSimilarity() != null ? p.getSimilarity() : 0.0,
                                    null, null, detectedAt, detectedAt, null);
                            event.setLate(p.getIsLate());
                            event.setConfirmationStatus(p.getConfirmationStatus());

                            if (type == LogType.CHECK_IN) {
                                daily.setCheckInEvent(event);
                            } else {
                                daily.setCheckOutEvent(event);
                            }
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
                            Calendar pCal = Calendar.getInstance();
                            pCal.setTime(detectedAt);
                            if (pCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                    pCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)) {
                                String dateKey = sdf.format(detectedAt);
                                DailyAttendance daily = dailyMap.get(dateKey);
                                if (daily == null) {
                                    daily = new DailyAttendance(detectedAt);
                                    dailyMap.put(dateKey, daily);
                                }
                                daily.setLeaveData(l);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (allPendingRecognitions != null) {
            for (com.example.javatraining.data.remote.response.RecognitionEventData pr : allPendingRecognitions) {
                if (pr.getCreatedAt() != null) {
                    try {
                        Date detectedAt = parseIsoDate(pr.getCreatedAt());
                        if (detectedAt != null) {
                            Calendar pCal = Calendar.getInstance();
                            pCal.setTime(detectedAt);
                            if (pCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                    pCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)) {
                                String dateKey = sdf.format(detectedAt);
                                DailyAttendance daily = dailyMap.get(dateKey);
                                if (daily != null && (daily.getFirstIn() != null || daily.getLastOut() != null || daily.getLeaveData() != null)) {
                                    continue; // Sudah ada aktivitas absensi/izin hari ini, abaikan pending recognition
                                }
                                if (daily == null) {
                                    daily = new DailyAttendance(detectedAt);
                                    dailyMap.put(dateKey, daily);
                                }
                                
                                AttendanceEvent event = new AttendanceEvent(
                                        0, pr.getCameraId(), 0, pr.getEmployeeId(), null,
                                        null, LogType.CHECK_IN, 0.0, null, null, detectedAt, detectedAt, null);
                                event.setConfirmationStatus("PENDING");
                                event.setRecognitionId(pr.getId());
                                event.setThumbnailUrl(pr.getThumbnail());
                                daily.setCheckInEvent(event);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        filteredLogs.addAll(dailyMap.values());

        // If month filter returned no logs but allLogs has data, show all available
        // logs
        if (filteredLogs.isEmpty() && !allLogs.isEmpty()) {
            java.util.Map<String, DailyAttendance> fallbackMap = new java.util.HashMap<>();
            for (AttendanceData p : allLogs) {
                if (p.getTimestamp() != null) {
                    Date detectedAt = parseIsoDate(p.getTimestamp());
                    if (detectedAt != null) {
                        String dateKey = sdf.format(detectedAt);
                        DailyAttendance daily = fallbackMap.get(dateKey);
                        if (daily == null) {
                            daily = new DailyAttendance(detectedAt);
                            fallbackMap.put(dateKey, daily);
                        }
                        String evtType = p.getEventType();
                        LogType type = ("CHECK_IN".equalsIgnoreCase(evtType) || "IN".equalsIgnoreCase(evtType)) ? LogType.CHECK_IN : LogType.CHECK_OUT;
                        
                        AttendanceEvent event = new AttendanceEvent(
                                0, p.getCameraId(), 0, p.getEmployeeId(), null,
                                null,
                                type,
                                p.getSimilarity() != null ? p.getSimilarity() : 0.0,
                                null, null, detectedAt, detectedAt, null);
                        event.setLate(p.getIsLate());
                        event.setConfirmationStatus(p.getConfirmationStatus());
                        if (type == LogType.CHECK_IN) {
                            daily.setCheckInEvent(event);
                        } else {
                            daily.setCheckOutEvent(event);
                        }
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
                                DailyAttendance daily = fallbackMap.get(dateKey);
                                if (daily == null) {
                                    daily = new DailyAttendance(detectedAt);
                                    fallbackMap.put(dateKey, daily);
                                }
                                daily.setLeaveData(l);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (allPendingRecognitions != null) {
                for (com.example.javatraining.data.remote.response.RecognitionEventData pr : allPendingRecognitions) {
                    if (pr.getCreatedAt() != null) {
                        try {
                            Date detectedAt = parseIsoDate(pr.getCreatedAt());
                            if (detectedAt != null) {
                                String dateKey = sdf.format(detectedAt);
                                DailyAttendance daily = fallbackMap.get(dateKey);
                                if (daily == null) {
                                    daily = new DailyAttendance(detectedAt);
                                    fallbackMap.put(dateKey, daily);
                                }
                                
                                AttendanceEvent event = new AttendanceEvent(
                                        0, pr.getCameraId(), 0, pr.getEmployeeId(), null,
                                        null, LogType.CHECK_IN, 0.0, null, null, detectedAt, detectedAt, null);
                                event.setConfirmationStatus("PENDING");
                                event.setRecognitionId(pr.getId());
                                event.setThumbnailUrl(pr.getThumbnail());
                                daily.setCheckInEvent(event);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            filteredLogs.addAll(fallbackMap.values());
        }

        // Sort newest first
        Collections.sort(filteredLogs, (p1, p2) -> p2.getDate().compareTo(p1.getDate()));

        adapter.notifyDataSetChanged();
        if (rvHistory != null) {
            rvHistory.scheduleLayoutAnimation();
        }
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
        return null; // Return null to avoid fake current time
    }
}
