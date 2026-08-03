package com.example.javatraining.ui.main.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.javatraining.databinding.FragmentHomeBinding;
import com.example.javatraining.data.model.ActivityLog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecentActivityAdapter adapter;
    private Handler handler = new Handler();

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null) {
                String currentTime = new SimpleDateFormat("HH.mm", Locale.getDefault()).format(new Date());
                binding.tvLiveTime.setText(currentTime);
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup live clock
        handler.post(updateTimeRunnable);

        // Setup Date
        String todayDate = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("id", "ID")).format(new Date());
        binding.tvDate.setText(todayDate);

        // Setup RecyclerView
        binding.rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecentActivityAdapter();
        binding.rvRecentActivity.setAdapter(adapter);

        // Mock Data
        List<ActivityLog> dummyData = new ArrayList<>();
        dummyData.add(new ActivityLog("Budi Santoso", "BS", "#F59E0B", "Check In", "08:02", "Tepat", "green"));
        dummyData.add(new ActivityLog("Sari Dewi", "SD", "#38BDF8", "Check In", "08:15", "Terlambat", "yellow"));
        dummyData.add(new ActivityLog("Andi Pratama", "AP", "#34D399", "Check Out", "17:05", "Tepat", "green"));
        dummyData.add(new ActivityLog("Rina Wahyu", "RW", "#FB7185", "Check In", "07:55", "Tepat", "green"));
        adapter.submitList(dummyData);

        binding.cardScanAbsensi.setOnClickListener(v -> {
            android.widget.Toast.makeText(getContext(), "Buka kamera untuk Face Recognition...", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimeRunnable);
        binding = null;
    }
}
