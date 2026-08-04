package com.example.javatraining.ui.main.history;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;
import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.DailyAttendance;
import com.example.javatraining.data.repository.MockDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryLogAdapter extends RecyclerView.Adapter<HistoryLogAdapter.ViewHolder> {

    private List<DailyAttendance> logs;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());

    public HistoryLogAdapter(List<DailyAttendance> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyAttendance p = logs.get(position);

        // Timeline line visibility
        holder.vLineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.vLineBottom.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);

        if (p.getDate() != null) {
            // Set Date Text
            holder.tvDate.setText(dateFormat.format(p.getDate()));
            
            // Set Relative Date Text
            Calendar today = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTime(p.getDate());
            
            long diffInMillis = today.getTimeInMillis() - cal.getTimeInMillis();
            long diffDays = diffInMillis / (24 * 60 * 60 * 1000);
            
            if (today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) {
                holder.tvRelativeDate.setText("Today");
            } else if (diffDays == 1 || (today.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) == 1)) {
                holder.tvRelativeDate.setText("Yesterday");
            } else {
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                holder.tvRelativeDate.setText(dayFormat.format(p.getDate()));
            }
        }

        // Times
        Date inTime = p.getCheckInEvent() != null ? p.getCheckInEvent().getDetectedAt() : null;
        Date outTime = p.getCheckOutEvent() != null ? p.getCheckOutEvent().getDetectedAt() : null;

        if (inTime != null) {
            holder.tvCheckInTime.setText(timeFormat.format(inTime));
        } else {
            holder.tvCheckInTime.setText("--:--");
        }
        
        if (outTime != null) {
            holder.tvCheckOutTime.setText(timeFormat.format(outTime));
            
            // Calculate Total Hours
            if (inTime != null) {
                long duration = outTime.getTime() - inTime.getTime();
                long hours = duration / (1000 * 60 * 60);
                long minutes = (duration / (1000 * 60)) % 60;
                holder.tvTotalHours.setText(hours + "h " + minutes + "m");
                holder.tvTotalHours.setTextColor(Color.parseColor("#0052CC"));
            } else {
                holder.tvTotalHours.setText("N/A");
            }
        } else {
            holder.tvCheckOutTime.setText("--:--");
            holder.tvTotalHours.setText("Active");
            holder.tvTotalHours.setTextColor(Color.parseColor("#10B981"));
        }

        // Status Badge Logic
        if (inTime != null) {
            Calendar inCal = Calendar.getInstance();
            inCal.setTime(inTime);
            if (inCal.get(Calendar.HOUR_OF_DAY) > 9 || (inCal.get(Calendar.HOUR_OF_DAY) == 9 && inCal.get(Calendar.MINUTE) > 0)) {
                // Late
                holder.tvStatusBadge.setText("Late");
                holder.tvStatusBadge.setTextColor(Color.parseColor("#D97706"));
                holder.llStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_orange);
            } else {
                // On Time
                holder.tvStatusBadge.setText("On Time");
                holder.tvStatusBadge.setTextColor(Color.parseColor("#0052CC"));
                holder.llStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_primary);
            }
        } else {
            holder.tvStatusBadge.setText("Pending");
        }
        
        // Accuracy
        if (inTime != null && p.getCheckInEvent() != null) {
            double acc = p.getCheckInEvent().getSimilarity() * 100;
            holder.tvAccuracy.setText(String.format(Locale.getDefault(), "Acc: %.0f%%", acc));
            holder.tvAccuracy.setVisibility(View.VISIBLE);
        } else {
            holder.tvAccuracy.setVisibility(View.GONE);
        }

        // Mock locations
        if (position % 2 == 0) {
            holder.tvLocation.setText("HQ Office");
        } else {
            holder.tvLocation.setText("WFH");
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * android.content.res.Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public void updateData(List<DailyAttendance> newLogs) {
        this.logs = newLogs;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View vLineTop, vLineBottom, vInnerDot;
        FrameLayout flDotContainer;
        TextView tvRelativeDate, tvDate, tvStatusBadge, tvCheckInTime, tvCheckOutTime, tvTotalHours, tvLocation, tvAccuracy;
        LinearLayout llStatusBadge;
        com.google.android.material.card.MaterialCardView cardContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vLineTop = itemView.findViewById(R.id.vLineTop);
            vLineBottom = itemView.findViewById(R.id.vLineBottom);
            flDotContainer = itemView.findViewById(R.id.flDotContainer);
            vInnerDot = itemView.findViewById(R.id.vInnerDot);
            
            tvRelativeDate = itemView.findViewById(R.id.tvRelativeDate);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            
            tvDate = itemView.findViewById(R.id.tvDate);
            llStatusBadge = itemView.findViewById(R.id.llStatusBadge);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            
            tvCheckInTime = itemView.findViewById(R.id.tvCheckInTime);
            tvCheckOutTime = itemView.findViewById(R.id.tvCheckOutTime);
            tvTotalHours = itemView.findViewById(R.id.tvTotalHours);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
        }
    }
}
