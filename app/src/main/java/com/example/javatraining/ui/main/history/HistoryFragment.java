package com.example.javatraining.ui.main.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;
import com.example.javatraining.data.local.AttendanceEntity;
import com.example.javatraining.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        
        adapter = new HistoryAdapter(new ArrayList<>());
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(adapter);

        viewModel.getHistory().observe(getViewLifecycleOwner(), historyList -> {
            if (historyList != null) {
                adapter.updateData(historyList);
            }
        });
    }

    // Inner adapter for brevity in MVP
    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<AttendanceEntity> data;

        public HistoryAdapter(List<AttendanceEntity> data) {
            this.data = data;
        }

        public void updateData(List<AttendanceEntity> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AttendanceEntity entity = data.get(position);
            holder.tvDate.setText(entity.date);
            holder.tvCheckIn.setText("In: " + entity.checkInTime);
            holder.tvCheckOut.setText("Out: " + entity.checkOutTime);
            holder.tvStatus.setText(entity.status);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvCheckIn, tvCheckOut, tvStatus;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvCheckIn = itemView.findViewById(R.id.tvCheckIn);
                tvCheckOut = itemView.findViewById(R.id.tvCheckOut);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }
        }
    }
}
