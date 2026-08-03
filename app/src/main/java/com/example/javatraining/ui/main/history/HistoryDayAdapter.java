package com.example.javatraining.ui.main.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;

import java.util.List;

public class HistoryDayAdapter extends RecyclerView.Adapter<HistoryDayAdapter.ViewHolder> {

    private List<HistoryDay> dayList;

    public HistoryDayAdapter(List<HistoryDay> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryDay day = dayList.get(position);
        
        holder.tvDate.setText(day.date);
        holder.tvAttendanceSummary.setText(day.summary);
        
        HistoryEmployeeAdapter employeeAdapter = new HistoryEmployeeAdapter(day.employees);
        holder.rvEmployees.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvEmployees.setAdapter(employeeAdapter);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAttendanceSummary;
        RecyclerView rvEmployees;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAttendanceSummary = itemView.findViewById(R.id.tvAttendanceSummary);
            rvEmployees = itemView.findViewById(R.id.rvEmployees);
        }
    }

    public static class HistoryDay {
        public String date;
        public String summary;
        public List<HistoryEmployeeAdapter.HistoryEmployee> employees;

        public HistoryDay(String date, String summary, List<HistoryEmployeeAdapter.HistoryEmployee> employees) {
            this.date = date;
            this.summary = summary;
            this.employees = employees;
        }
    }
}
