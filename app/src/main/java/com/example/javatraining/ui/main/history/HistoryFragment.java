package com.example.javatraining.ui.main.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView rvHistoryDays = view.findViewById(R.id.rvHistoryDays);
        rvHistoryDays.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup dummy data
        List<HistoryEmployeeAdapter.HistoryEmployee> day1Employees = new ArrayList<>();
        day1Employees.add(new HistoryEmployeeAdapter.HistoryEmployee("Budi Santoso", "07:58", "17:02", "BS", "Tepat Waktu", "#F59E0B"));
        day1Employees.add(new HistoryEmployeeAdapter.HistoryEmployee("Sari Dewi", "08:20", "17:15", "SD", "Terlambat", "#FB7185"));
        day1Employees.add(new HistoryEmployeeAdapter.HistoryEmployee("Andi Pratama", "07:45", "17:00", "AP", "Tepat Waktu", "#34D399"));
        day1Employees.add(new HistoryEmployeeAdapter.HistoryEmployee("Rina Wahyu", "08:00", "-", "RW", "Belum CO", "#38BDF8"));
        day1Employees.add(new HistoryEmployeeAdapter.HistoryEmployee("Doni Kusuma", "-", "-", "DK", "Izin", "#F59E0B"));

        List<HistoryDayAdapter.HistoryDay> daysList = new ArrayList<>();
        daysList.add(new HistoryDayAdapter.HistoryDay("Senin, 3 Agu 2026", "4/5 hadir", day1Employees));
        daysList.add(new HistoryDayAdapter.HistoryDay("Jumat, 31 Jul 2026", "5/5 hadir", day1Employees));

        HistoryDayAdapter adapter = new HistoryDayAdapter(daysList);
        rvHistoryDays.setAdapter(adapter);

        return view;
    }
}
