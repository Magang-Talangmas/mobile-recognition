package com.example.javatraining.ui.main.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;
import com.example.javatraining.data.model.Presensi;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private List<Presensi> presensiList;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public RecentActivityAdapter(List<Presensi> presensiList) {
        this.presensiList = presensiList;
    }

    public void updateData(List<Presensi> newList) {
        this.presensiList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Presensi presensi = presensiList.get(position);
        
        // Icon Text
        holder.tvIconText.setText("C"); // 'C' for Clock
        
        // Action & Time
        boolean isCheckIn = presensi.getCheckInTime() != null;
        boolean isCheckOut = presensi.getCheckOutTime() != null;
        
        if (isCheckIn && !isCheckOut) {
            holder.tvAction.setText("Clocked In");
            holder.tvTime.setText("Today at " + timeFormat.format(presensi.getCheckInTime()));
        } else if (isCheckOut) {
            holder.tvAction.setText("Clocked Out");
            holder.tvTime.setText("Today at " + timeFormat.format(presensi.getCheckOutTime()));
        } else {
            holder.tvAction.setText("Detected");
            holder.tvTime.setText("Today at " + timeFormat.format(presensi.getWaktuTerdeteksi() != null ? presensi.getWaktuTerdeteksi() : new java.util.Date()));
        }
        
        // Badge
        String status = "On Time";
        if (isCheckIn && presensi.getCheckInTime().getHours() >= 9) {
            status = "Late";
        }
        
        holder.tvStatusBadge.setText(status);
        
        if (status.equalsIgnoreCase("Late") || status.equalsIgnoreCase("Terlambat")) {
            holder.tvStatusBadge.setTextColor(Color.parseColor("#BA1A1A"));
            holder.tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFDAD6")));
        } else {
            holder.tvStatusBadge.setTextColor(Color.parseColor("#1A2D72"));
            holder.tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E4EFFF")));
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(presensiList.size(), 3); // Max 3 items for Recent Activity
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIconText, tvAction, tvTime, tvStatusBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIconText = itemView.findViewById(R.id.tvIconText);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
        }
    }
}
