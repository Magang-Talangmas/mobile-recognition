package com.example.javatraining.ui.main.employees;

import android.graphics.Color;
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
        holder.tvRoleDept.setText(employee.role + " • " + employee.idCode);
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

        switch (employee.statusType) {
            case IN_OFFICE:
                badgeBg.setColor(Color.parseColor("#001456")); // html_primary
                holder.tvStatusBadge.setTextColor(Color.parseColor("#ffffff")); // html_on_primary
                dotBg.setColor(Color.parseColor("#001456"));
                holder.itemView.setAlpha(1.0f);
                break;
            case ON_BREAK:
                badgeBg.setColor(Color.parseColor("#feb31b")); // html_secondary_container
                holder.tvStatusBadge.setTextColor(Color.parseColor("#6b4800")); // html_on_secondary_container
                dotBg.setColor(Color.parseColor("#feb31b"));
                holder.itemView.setAlpha(1.0f);
                break;
            case REMOTE:
                badgeBg.setColor(Color.parseColor("#dde1ff")); // html_primary_fixed
                holder.tvStatusBadge.setTextColor(Color.parseColor("#001355")); // html_on_primary_fixed
                dotBg.setColor(Color.parseColor("#dde1ff"));
                holder.itemView.setAlpha(1.0f);
                break;
            case OFFLINE:
                badgeBg.setColor(Color.parseColor("#d3e4fe")); // html_surface_variant
                holder.tvStatusBadge.setTextColor(Color.parseColor("#454650")); // html_on_surface_variant
                dotBg.setColor(Color.parseColor("#767681")); // html_outline
                holder.itemView.setAlpha(0.6f);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoleDept, tvInitials, tvStatusBadge;
        FrameLayout flAvatarInitials;
        ImageView ivAvatarImage;
        View vStatusDot;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRoleDept = itemView.findViewById(R.id.tvRoleDept);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            flAvatarInitials = itemView.findViewById(R.id.flAvatarInitials);
            ivAvatarImage = itemView.findViewById(R.id.ivAvatarImage);
            vStatusDot = itemView.findViewById(R.id.vStatusDot);
        }
    }

    public enum StatusType {
        IN_OFFICE, ON_BREAK, REMOTE, OFFLINE
    }

    public static class Employee {
        public String name;
        public String role;
        public String idCode;
        public String initials;
        public StatusType statusType;

        public Employee(String name, String role, String idCode, String initials, StatusType statusType) {
            this.name = name;
            this.role = role;
            this.idCode = idCode;
            this.initials = initials;
            this.statusType = statusType;
        }
    }
}
