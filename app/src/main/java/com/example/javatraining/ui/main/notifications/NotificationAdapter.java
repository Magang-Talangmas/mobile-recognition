package com.example.javatraining.ui.main.notifications;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.javatraining.BuildConfig;
import com.example.javatraining.R;
import com.example.javatraining.data.model.NotificationItem;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationActionListener {
        void onConfirm(NotificationItem item, int position);
        void onReject(NotificationItem item, int position);
    }

    private List<NotificationItem> items = new ArrayList<>();
    private OnNotificationActionListener listener;

    public void setListener(OnNotificationActionListener listener) {
        this.listener = listener;
    }

    public void updateData(List<NotificationItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        }
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
            holder.ivNotifIcon.setImageTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusAbsent)));
            holder.flIconBg.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_surface_variant)));
            holder.tvNotifTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.statusAbsent));
        } else {
            holder.ivNotifIcon.setImageResource(android.R.drawable.ic_dialog_info);
            holder.ivNotifIcon.setImageTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_on_primary_container)));
            holder.flIconBg.setBackgroundTintList(ColorStateList
                    .valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_primary_container)));
            holder.tvNotifTime.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.html_primary));
        }

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            holder.ivSnapshot.setVisibility(View.VISIBLE);
            String url = item.getImageUrl();
            if (url.startsWith("/")) {
                url = BuildConfig.SUPABASE_URL + url;
            }
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .centerCrop()
                    .into(holder.ivSnapshot);
        } else {
            holder.ivSnapshot.setVisibility(View.GONE);
        }

        if (item.requiresConfirmation() && item.getRecognitionId() != null) {
            holder.llActionButtons.setVisibility(View.VISIBLE);
            holder.btnConfirm.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConfirm(item, holder.getAdapterPosition());
                }
            });
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReject(item, holder.getAdapterPosition());
                }
            });
        } else {
            holder.llActionButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifTitle, tvNotifMessage, tvNotifTime;
        ImageView ivNotifIcon, ivSnapshot;
        FrameLayout flIconBg;
        LinearLayout llActionButtons;
        Button btnConfirm, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvNotifTime = itemView.findViewById(R.id.tvNotifTime);
            ivNotifIcon = itemView.findViewById(R.id.ivNotifIcon);
            flIconBg = itemView.findViewById(R.id.flIconBg);
            ivSnapshot = itemView.findViewById(R.id.ivSnapshot);
            llActionButtons = itemView.findViewById(R.id.llActionButtons);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
