package com.example.javatraining.ui.main.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;
import com.example.javatraining.data.model.AttendanceEvent;
import com.example.javatraining.data.model.LogType;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private List<AttendanceEvent> presensiList;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());

    public RecentActivityAdapter(List<AttendanceEvent> presensiList) {
        this.presensiList = presensiList;
    }

    public void updateData(List<AttendanceEvent> newList) {
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
        AttendanceEvent event = presensiList.get(position);

        Calendar calEvent = Calendar.getInstance();
        calEvent.setTime(event.getDetectedAt());
        Calendar calToday = Calendar.getInstance();

        boolean isToday = calEvent.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                calEvent.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR);

        calToday.add(Calendar.DAY_OF_YEAR, -1);
        boolean isYesterday = calEvent.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                calEvent.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR);

        String dayStr = isToday ? "Today"
                : (isYesterday ? "Yesterday"
                        : new SimpleDateFormat("dd MMM", Locale.getDefault()).format(event.getDetectedAt()));

        holder.tvRelativeDate.setText(dayStr);
        holder.tvDate.setText(dateFormat.format(event.getDetectedAt()));

        if (event.getEventType() == LogType.CHECK_IN) {
            holder.tvCheckInTime.setText(timeFormat.format(event.getDetectedAt()));
            holder.tvCheckOutTime.setText("--:--");
            
            holder.tvStatusBadge.setText("Active");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#0052CC"));
            holder.llStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6F0FF")));
            holder.tvTotalHours.setText("Active");
            holder.tvTotalHours.setTextColor(Color.parseColor("#10B981"));
        } else {
            holder.tvCheckInTime.setText("--:--");
            holder.tvCheckOutTime.setText(timeFormat.format(event.getDetectedAt()));
            
            holder.tvStatusBadge.setText("Finished");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#434654"));
            holder.llStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3F4F6")));
            holder.tvTotalHours.setText("Finished");
            holder.tvTotalHours.setTextColor(Color.parseColor("#434654"));
        }

        holder.tvLocation.setText("HQ Office");

        double acc = event.getSimilarity() * 100;
        if (acc > 0) {
            holder.tvAccuracy.setText(String.format(Locale.getDefault(), "Acc: %.0f%%", acc));
            holder.tvAccuracy.setVisibility(View.VISIBLE);
        } else {
            holder.tvAccuracy.setVisibility(View.GONE);
        }
        
        // Hide top line for first item, hide bottom line for last item
        holder.vLineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.vLineBottom.setVisibility(position == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
        
        if (isToday) {
            holder.vInnerDot.setBackgroundTintList(null);
            holder.vInnerDot.setBackgroundResource(R.drawable.bg_circle_primary);
        } else {
            holder.vInnerDot.setBackgroundResource(R.drawable.bg_circle_primary); // Use the same shape
            holder.vInnerDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#737685")));
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(presensiList.size(), 3);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View vLineTop, vLineBottom, vInnerDot;
        FrameLayout flDotContainer;
        TextView tvRelativeDate, tvDate, tvAccuracy, tvStatusBadge, tvCheckInTime, tvLocation, tvCheckOutTime, tvTotalHours;
        LinearLayout llStatusBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vLineTop = itemView.findViewById(R.id.vLineTop);
            vLineBottom = itemView.findViewById(R.id.vLineBottom);
            vInnerDot = itemView.findViewById(R.id.vInnerDot);
            flDotContainer = itemView.findViewById(R.id.flDotContainer);
            tvRelativeDate = itemView.findViewById(R.id.tvRelativeDate);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            llStatusBadge = itemView.findViewById(R.id.llStatusBadge);
            tvCheckInTime = itemView.findViewById(R.id.tvCheckInTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCheckOutTime = itemView.findViewById(R.id.tvCheckOutTime);
            tvTotalHours = itemView.findViewById(R.id.tvTotalHours);
        }
    }
}
