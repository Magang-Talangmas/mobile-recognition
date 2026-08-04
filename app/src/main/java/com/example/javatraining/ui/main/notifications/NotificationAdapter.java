package com.example.javatraining.ui.main.notifications;

import android.content.res.ColorStateList;
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
import com.example.javatraining.data.model.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> items = new ArrayList<>();

    public void updateData(List<NotificationItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = items.get(position);
        holder.tvNotifTitle.setText(item.getTitle());
        holder.tvNotifMessage.setText(item.getMessage());
        holder.tvNotifTime.setText(item.getTime());

        if (item.isWarning()) {
            holder.ivNotifIcon.setImageResource(android.R.drawable.ic_dialog_alert);
            holder.ivNotifIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusAbsent)));
            holder.flIconBg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_surface_variant)));
            holder.tvNotifTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusAbsent));
        } else {
            holder.ivNotifIcon.setImageResource(android.R.drawable.ic_dialog_info);
            holder.ivNotifIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_on_primary_container)));
            holder.flIconBg.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_primary_container)));
            holder.tvNotifTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_primary));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifTitle, tvNotifMessage, tvNotifTime;
        ImageView ivNotifIcon;
        FrameLayout flIconBg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvNotifTime = itemView.findViewById(R.id.tvNotifTime);
            ivNotifIcon = itemView.findViewById(R.id.ivNotifIcon);
            flIconBg = itemView.findViewById(R.id.flIconBg);
        }
    }
}
