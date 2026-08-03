package com.example.javatraining.ui.main.employees;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
        holder.tvRole.setText(employee.role + " · " + employee.department);
        holder.tvInitials.setText(employee.initials);
        holder.tvStatus.setText(employee.status);
        
        // Dynamic colors for avatar background
        GradientDrawable bgShape = (GradientDrawable) holder.flAvatar.getBackground();
        bgShape.setColor(Color.parseColor(employee.avatarColor));
        
        // Dynamic status badge color
        GradientDrawable statusBg = (GradientDrawable) holder.tvStatus.getBackground();
        if (employee.status.equalsIgnoreCase("Hadir")) {
            statusBg.setColor(Color.parseColor("#065F46")); // Dark green background
            holder.tvStatus.setTextColor(Color.parseColor("#10B981")); // Green text
        } else if (employee.status.equalsIgnoreCase("Terlambat")) {
            statusBg.setColor(Color.parseColor("#78350F")); // Dark yellow/orange background
            holder.tvStatus.setTextColor(Color.parseColor("#F59E0B")); // Yellow text
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvInitials, tvStatus;
        FrameLayout flAvatar;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            flAvatar = itemView.findViewById(R.id.flAvatar);
        }
    }

    public static class Employee {
        public String name, role, department, initials, status, avatarColor;

        public Employee(String name, String role, String department, String initials, String status, String avatarColor) {
            this.name = name;
            this.role = role;
            this.department = department;
            this.initials = initials;
            this.status = status;
            this.avatarColor = avatarColor;
        }
    }
}
