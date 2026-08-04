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

public class HistoryFragment extends Fragment {

    private HistoryLogAdapter adapter;
    private List<Presensi> allLogs;
    private List<Presensi> filteredLogs;
    private String currentFilter = "Semua";

    private TextView chipSemua, chipKehadiran, chipUnknown;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        allLogs = MockDatabase.getInstance().getAttendanceHistory();
        filteredLogs = new ArrayList<>(allLogs);
        adapter = new HistoryLogAdapter(filteredLogs);
        rvHistory.setAdapter(adapter);

        // Filter Chips
        chipSemua = view.findViewById(R.id.chipSemua);
        chipKehadiran = view.findViewById(R.id.chipKehadiran);
        chipUnknown = view.findViewById(R.id.chipUnknown);

        View.OnClickListener chipListener = v -> {
            String tag = ((TextView) v).getText().toString();
            currentFilter = tag;
            resetChipStyles();
            v.setBackgroundResource(R.drawable.bg_chip_active);
            ((TextView) v).setTextColor(getResources().getColor(R.color.html_on_primary));
            applyFilter();
        };

        chipSemua.setOnClickListener(chipListener);
        chipKehadiran.setOnClickListener(chipListener);
        chipUnknown.setOnClickListener(chipListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data every time the tab is switched to
        allLogs = MockDatabase.getInstance().getAttendanceHistory();
        applyFilter();
    }

    private void resetChipStyles() {
        chipSemua.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipKehadiran.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipUnknown.setBackgroundResource(R.drawable.bg_chip_inactive);
        chipSemua.setTextColor(getResources().getColor(R.color.html_on_surface_variant));
        chipKehadiran.setTextColor(getResources().getColor(R.color.html_on_surface_variant));
        chipUnknown.setTextColor(getResources().getColor(R.color.html_on_surface_variant));
    }

    private void applyFilter() {
        filteredLogs.clear();
        for (Presensi p : allLogs) {
            if (currentFilter.equals("Semua")) {
                filteredLogs.add(p);
            } else if (currentFilter.equals("Kehadiran")) {
                if (p.getTipeLog() == LogType.CHECK_IN || p.getTipeLog() == LogType.CHECK_OUT || p.getTipeLog() == LogType.FACE_DETECTED) {
                    filteredLogs.add(p);
                }
            } else if (currentFilter.equals("Unknown Person")) {
                if (p.getTipeLog() == LogType.UNKNOWN_DETECTED) {
                    filteredLogs.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
