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

import java.util.List;

public class HistoryEmployeeAdapter extends RecyclerView.Adapter<HistoryEmployeeAdapter.ViewHolder> {

    private List<HistoryEmployee> employeeList;

    public HistoryEmployeeAdapter(List<HistoryEmployee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEmployee emp = employeeList.get(position);
        
        holder.tvName.setText(emp.name);
        holder.tvInitials.setText(emp.initials);
        holder.tvTime.setText("CI: " + emp.checkIn + " - CO: " + emp.checkOut);
        holder.tvStatus.setText(emp.status);
        
        // Avatar color
        GradientDrawable bgShape = (GradientDrawable) holder.flAvatar.getBackground();
        bgShape.setColor(Color.parseColor(emp.avatarColor));
        
        // Status badge color and icon
        GradientDrawable statusBg = (GradientDrawable) holder.llStatus.getBackground();
        if (emp.status.equalsIgnoreCase("Tepat Waktu")) {
            statusBg.setColor(Color.parseColor("#065F46"));
            holder.tvStatus.setTextColor(Color.parseColor("#10B981"));
            holder.ivStatusIcon.setImageResource(R.drawable.ic_check_circle_outline);
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#10B981"));
        } else if (emp.status.equalsIgnoreCase("Terlambat")) {
            statusBg.setColor(Color.parseColor("#78350F"));
            holder.tvStatus.setTextColor(Color.parseColor("#F59E0B"));
            holder.ivStatusIcon.setImageResource(R.drawable.ic_cancel_outline); // Placeholder for warning icon
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#F59E0B"));
        } else if (emp.status.equalsIgnoreCase("Belum CO")) {
            statusBg.setColor(Color.parseColor("#4C1D95"));
            holder.tvStatus.setTextColor(Color.parseColor("#A78BFA"));
            holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_recent_history);
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#A78BFA"));
        } else if (emp.status.equalsIgnoreCase("Izin")) {
            statusBg.setColor(Color.parseColor("#1E3A8A"));
            holder.tvStatus.setTextColor(Color.parseColor("#60A5FA"));
            holder.ivStatusIcon.setImageResource(android.R.drawable.ic_delete); // Placeholder for minus icon
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#60A5FA"));
        } else if (emp.status.equalsIgnoreCase("Unknown Person")) {
            statusBg.setColor(Color.parseColor("#374151")); // Gray-700
            holder.tvStatus.setTextColor(Color.parseColor("#9CA3AF")); // Gray-400
            holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_camera); // Placeholder for camera
            holder.ivStatusIcon.setColorFilter(Color.parseColor("#9CA3AF"));
            
            holder.tvInitials.setText("?");
            bgShape.setColor(Color.parseColor("#1F2937")); // Darker gray for avatar
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvInitials, tvStatus;
        FrameLayout flAvatar;
        LinearLayout llStatus;
        ImageView ivStatusIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            flAvatar = itemView.findViewById(R.id.flAvatar);
            llStatus = itemView.findViewById(R.id.llStatus);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
        }
    }

    public static class HistoryEmployee {
        public String name, checkIn, checkOut, initials, status, avatarColor;

        public HistoryEmployee(String name, String checkIn, String checkOut, String initials, String status, String avatarColor) {
            this.name = name;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.initials = initials;
            this.status = status;
            this.avatarColor = avatarColor;
        }
    }
}
