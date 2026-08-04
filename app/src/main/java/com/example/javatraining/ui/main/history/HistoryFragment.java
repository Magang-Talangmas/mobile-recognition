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
import com.example.javatraining.data.model.Presensi;
import com.example.javatraining.data.repository.MockDatabase;

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
    private List<Presensi> allLogs;
    private List<Presensi> filteredLogs;

    private Date selectedDate = new Date();
    private TextView tvSelectedDate;
    private TextView tvActivityLogTitle;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    private SimpleDateFormat titleDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvActivityLogTitle = view.findViewById(R.id.tvActivityLogTitle);
        View btnPickDate = view.findViewById(R.id.btnPickDate);
        
        if (btnPickDate != null) {
            btnPickDate.setOnClickListener(v -> showDatePicker());
        }

        allLogs = MockDatabase.getInstance().getAttendanceHistory();
        filteredLogs = new ArrayList<>();
        
        adapter = new HistoryLogAdapter(filteredLogs);
        rvHistory.setAdapter(adapter);

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
        allLogs = MockDatabase.getInstance().getAttendanceHistory();
        applyFilter();
    }

    private void applyFilter() {
        filteredLogs.clear();
        String currentKaryawanId = MockDatabase.getInstance().getCurrentKaryawan().getId();
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        
        for (Presensi p : allLogs) {
            if (p.getKaryawanId() != null && p.getKaryawanId().equals(currentKaryawanId)) {
                if (p.getTanggal() != null) {
                    Calendar pCal = Calendar.getInstance();
                    pCal.setTime(p.getTanggal());
                    if (pCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                        pCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)) { // Note: changed to month filter based on UI (Oct 2023)
                        filteredLogs.add(p);
                    }
                }
            }
        }
        
        // Sort newest first
        Collections.sort(filteredLogs, (p1, p2) -> p2.getTanggal().compareTo(p1.getTanggal()));
        
        adapter.notifyDataSetChanged();
    }
}
