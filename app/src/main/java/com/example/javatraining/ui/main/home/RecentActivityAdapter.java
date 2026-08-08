package com.example.javatraining.ui.main.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

        // Icon & Colors
        if (event.getEventType() == LogType.CHECK_IN) {
            holder.tvAction.setText("Checked In");
            holder.ivIcon.setImageResource(android.R.drawable.ic_input_add); // Or some login icon
            holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_primary));
            holder.flIconBg.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_surface_container)));
        } else if (event.getEventType() == LogType.CHECK_OUT) {
            holder.tvAction.setText("Checked Out");
            holder.ivIcon.setImageResource(android.R.drawable.ic_menu_revert);
            holder.ivIcon.setColorFilter(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.html_on_surface_variant));
            holder.flIconBg.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_surface_variant)));
        } else {
            holder.tvAction.setText(event.getEventType().name());
        }

        // Date Logic for Subtitle
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

        holder.tvSubtitle.setText(dayStr + " • Main Office");

        // Time
        holder.tvTime.setText(timeFormat.format(event.getDetectedAt()));

        // Indicator dot for today's check in
        if (isToday && event.getEventType() == LogType.CHECK_IN) {
            holder.vStatusIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.vStatusIndicator.setVisibility(View.GONE);
        }

        // Update Time text color based on date
        if (isToday) {
            holder.tvTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_on_surface));
        } else {
            holder.tvTime.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.html_on_surface_variant));
        }

        // Accuracy
        double acc = event.getSimilarity() * 100;
        if (acc > 0) {
            holder.tvAccuracy.setText(String.format(Locale.getDefault(), "Acc: %.0f%%", acc));
            holder.tvAccuracy.setVisibility(View.VISIBLE);
        } else {
            holder.tvAccuracy.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(presensiList.size(), 3); // Max 3 items for Recent Activity
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout flIconBg;
        ImageView ivIcon;
        View vStatusIndicator;
        TextView tvAction, tvSubtitle, tvTime, tvAccuracy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flIconBg = itemView.findViewById(R.id.flIconBg);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            vStatusIndicator = itemView.findViewById(R.id.vStatusIndicator);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
        }
    }
}
