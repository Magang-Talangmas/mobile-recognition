package com.example.javatraining.ui.main.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;
import com.example.javatraining.data.model.ActivityLog;
import java.util.ArrayList;
import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private List<ActivityLog> items = new ArrayList<>();

    public void submitList(List<ActivityLog> newItems) {
        items.clear();
        items.addAll(newItems);
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
        ActivityLog log = items.get(position);
        holder.tvInitials.setText(log.getInitials());
        holder.tvName.setText(log.getName());
        holder.tvStatus.setText(log.getStatusText());
        holder.tvTime.setText(log.getTime());
        holder.tvBadge.setText(log.getStatusBadgeText());

        // Parse colors safely
        try {
            holder.tvInitials.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(log.getInitialsColor())));
        } catch (Exception e) {}

        if ("green".equals(log.getStatusBadgeColor())) {
            holder.tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#065F46")));
            holder.tvBadge.setTextColor(Color.parseColor("#34D399"));
        } else if ("yellow".equals(log.getStatusBadgeColor())) {
            holder.tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#78350F")));
            holder.tvBadge.setTextColor(Color.parseColor("#FBBF24"));
        } else {
            // Default styling
            holder.tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1E3A8A")));
            holder.tvBadge.setTextColor(Color.parseColor("#60A5FA"));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials, tvName, tvStatus, tvTime, tvBadge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvBadge = itemView.findViewById(R.id.tvBadge);
        }
    }
}
