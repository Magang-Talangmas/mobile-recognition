package com.example.javatraining.ui.main.employees;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private List<Employee> employeeList;

    public EmployeeAdapter(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        holder.tvName.setText(employee.name);
        holder.tvRoleDept.setText(employee.role);
        holder.tvTime.setText(employee.timeText);
        holder.tvStatusBadge.setText(employee.statusType.name().replace("_", " "));

        // Initials vs Avatar Image
        if (employee.initials != null && !employee.initials.isEmpty()) {
            holder.ivAvatarImage.setVisibility(View.GONE);
            holder.flAvatarInitials.setVisibility(View.VISIBLE);
            holder.tvInitials.setText(employee.initials);
        } else {
            holder.ivAvatarImage.setVisibility(View.VISIBLE);
            holder.flAvatarInitials.setVisibility(View.GONE);
        }

        // Apply status colors - MUST MUTATE so they don't share state
        GradientDrawable badgeBg = (GradientDrawable) holder.tvStatusBadge.getBackground().mutate();
        GradientDrawable dotBg = (GradientDrawable) holder.vStatusDot.getBackground().mutate();
        GradientDrawable ringBg = (GradientDrawable) holder.vAvatarRing.getBackground().mutate();

        // Clear grayscale filter
        holder.ivAvatarImage.clearColorFilter();

        switch (employee.statusType) {
            case TRACKING_RUNNING:
                holder.vStatusLine.setBackgroundColor(Color.parseColor("#82f9be")); // tertiary_fixed
                badgeBg.setColor(Color.parseColor("#3382F9BE")); // 20% opacity tertiary_fixed
                holder.tvStatusBadge.setTextColor(Color.parseColor("#004e32")); // tertiary
                dotBg.setColor(Color.parseColor("#82f9be"));
                ringBg.setColor(Color.parseColor("#4D82F9BE")); // 30% tertiary_fixed
                holder.itemView.setAlpha(1.0f);
                break;
            case BREAK_DI_AREA:
            case BREAK:
                holder.vStatusLine.setBackgroundColor(Color.parseColor("#d7e3fb")); // secondary_fixed
                badgeBg.setColor(Color.parseColor("#4DD7E3FB")); // 30% opacity secondary_fixed
                holder.tvStatusBadge.setTextColor(Color.parseColor("#535f73")); // secondary
                dotBg.setColor(Color.parseColor("#d7e3fb"));
                ringBg.setColor(Color.parseColor("#4DD7E3FB")); // 30% secondary_fixed
                holder.itemView.setAlpha(1.0f);
                break;
            case TRACKING_PAUSE:
                holder.vStatusLine.setBackgroundColor(Color.parseColor("#ba1a1a")); // error
                badgeBg.setColor(Color.parseColor("#ffdad6")); // error_container
                holder.tvStatusBadge.setTextColor(Color.parseColor("#ba1a1a")); // error
                dotBg.setColor(Color.parseColor("#ba1a1a"));
                ringBg.setColor(Color.parseColor("#ffdad6")); // error_container
                holder.itemView.setAlpha(0.75f);
                
                // Grayscale filter
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                holder.ivAvatarImage.setColorFilter(new ColorMatrixColorFilter(matrix));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoleDept, tvInitials, tvStatusBadge, tvTime;
        FrameLayout flAvatarInitials;
        ImageView ivAvatarImage;
        View vStatusDot, vStatusLine, vAvatarRing;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRoleDept = itemView.findViewById(R.id.tvRoleDept);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            flAvatarInitials = itemView.findViewById(R.id.flAvatarInitials);
            ivAvatarImage = itemView.findViewById(R.id.ivAvatarImage);
            vStatusDot = itemView.findViewById(R.id.vStatusDot);
            vStatusLine = itemView.findViewById(R.id.vStatusLine);
            vAvatarRing = itemView.findViewById(R.id.vAvatarRing);
        }
    }

    public enum StatusType {
        TRACKING_RUNNING, BREAK_DI_AREA, BREAK, TRACKING_PAUSE
    }

    public static class Employee {
        public String name;
        public String role;
        public String timeText;
        public String initials;
        public StatusType statusType;

        public Employee(String name, String role, String timeText, String initials, StatusType statusType) {
            this.name = name;
            this.role = role;
            this.timeText = timeText;
            this.initials = initials;
            this.statusType = statusType;
        }
    }
}
