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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        filteredLogs = new ArrayList<>();
        
        adapter = new HistoryLogAdapter(filteredLogs);
        rvHistory.setAdapter(adapter);

        repository = new AbsensiTMRepository(requireActivity().getApplication());
        fetchAttendanceHistory();

        View ivProfile = view.findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(getActivity(), com.example.javatraining.ui.main.profile.ProfileActivity.class);
                startActivity(intent);
            });
        }

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
        repository.getAttendancesApi(1, 100).observe(getViewLifecycleOwner(), new Observer<List<AttendanceData>>() {
            @Override
            public void onChanged(List<AttendanceData> attendanceDataList) {
                if (attendanceDataList != null) {
                    allLogs = attendanceDataList;
                    applyFilter();
                }
            }
        });
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
                    Date detectedAt = isoFormat.parse(p.getTimestamp());
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
                            
                            AttendanceEvent event = new AttendanceEvent(
                                0, p.getCameraId(), 0, p.getEmployeeId(), null,
                                null, 
                                "CHECK_IN".equalsIgnoreCase(p.getEventType()) ? LogType.CHECK_IN : LogType.CHECK_OUT,
                                p.getSimilarity() != null ? p.getSimilarity() : 0.0,
                                null, null, detectedAt, detectedAt, null
                            );
                            event.setLate(p.getIsLate());
                            event.setConfirmationStatus(p.getConfirmationStatus());
                            
                            if ("CHECK_IN".equalsIgnoreCase(p.getEventType())) {
                                daily.setCheckInEvent(event);
                            } else if ("CHECK_OUT".equalsIgnoreCase(p.getEventType())) {
                                daily.setCheckOutEvent(event);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        filteredLogs.addAll(dailyMap.values());
        
        // Sort newest first
        Collections.sort(filteredLogs, (p1, p2) -> p2.getDate().compareTo(p1.getDate()));
        
        adapter.notifyDataSetChanged();
        if (rvHistory != null) {
            rvHistory.scheduleLayoutAnimation();
        }
    }
}
