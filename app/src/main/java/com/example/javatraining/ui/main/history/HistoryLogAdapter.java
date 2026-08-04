package com.example.javatraining.ui.main.history;

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
import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.Presensi;
import com.example.javatraining.data.repository.MockDatabase;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryLogAdapter extends RecyclerView.Adapter<HistoryLogAdapter.ViewHolder> {

    private List<Presensi> logs;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public HistoryLogAdapter(List<Presensi> logs) {
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
        Presensi p = logs.get(position);

        // Set time
        if (p.getWaktuTerdeteksi() != null) {
            holder.tvTime.setText(timeFormat.format(p.getWaktuTerdeteksi()));
        } else if (p.getCheckInTime() != null) {
            holder.tvTime.setText(timeFormat.format(p.getCheckInTime()));
        }

        // Set icon background color and content based on LogType
        GradientDrawable iconBg = (GradientDrawable) holder.flIcon.getBackground().mutate();

        switch (p.getTipeLog()) {
            case CHECK_IN:
                holder.tvTitle.setText("Check In");
                holder.tvTitle.setTextColor(Color.parseColor("#1B1B1F"));
                iconBg.setColor(Color.parseColor("#3F51B5")); // primary-ish blue
                holder.ivIcon.setImageResource(R.drawable.ic_exit);
                holder.ivIcon.setColorFilter(Color.WHITE);
                
                // Find employee name
                String empName = getEmployeeName(p.getKaryawanId());
                holder.tvSubtitle.setText(empName != null ? empName : "Karyawan");
                holder.tvSubtitle.setVisibility(View.VISIBLE);
                break;

            case CHECK_OUT:
                holder.tvTitle.setText("Check Out");
                holder.tvTitle.setTextColor(Color.parseColor("#1B1B1F"));
                iconBg.setColor(Color.parseColor("#E53935")); // red
                holder.ivIcon.setImageResource(R.drawable.ic_exit);
                holder.ivIcon.setColorFilter(Color.WHITE);

                String empNameCo = getEmployeeName(p.getKaryawanId());
                holder.tvSubtitle.setText(empNameCo != null ? empNameCo : "Karyawan");
                holder.tvSubtitle.setVisibility(View.VISIBLE);

                if (p.getCheckOutTime() != null) {
                    holder.tvTime.setText(timeFormat.format(p.getCheckOutTime()));
                }
                break;

            case FACE_DETECTED:
                holder.tvTitle.setText("Wajah Terdeteksi");
                holder.tvTitle.setTextColor(Color.parseColor("#1B1B1F"));
                iconBg.setColor(Color.parseColor("#10B981")); // green
                holder.ivIcon.setImageResource(android.R.drawable.ic_menu_camera);
                holder.ivIcon.setColorFilter(Color.WHITE);
                
                String empNameFd = getEmployeeName(p.getKaryawanId());
                holder.tvSubtitle.setText(empNameFd != null ? empNameFd : "Karyawan");
                holder.tvSubtitle.setVisibility(View.VISIBLE);
                break;

            case UNKNOWN_DETECTED:
                holder.tvTitle.setText("Unknown Person Detected");
                holder.tvTitle.setTextColor(Color.parseColor("#EF4444")); // red
                iconBg.setColor(Color.parseColor("#374151")); // gray-700
                holder.ivIcon.setImageResource(android.R.drawable.ic_menu_camera);
                holder.ivIcon.setColorFilter(Color.parseColor("#9CA3AF"));
                
                holder.tvSubtitle.setText("⚠️ Security Log");
                holder.tvSubtitle.setVisibility(View.VISIBLE);
                holder.tvSubtitle.setTextColor(Color.parseColor("#B91C1C"));
                break;

            case TRACKING_RUNNING:
                holder.tvTitle.setText("Tracking Running");
                holder.tvTitle.setTextColor(Color.parseColor("#1B1B1F"));
                iconBg.setColor(Color.parseColor("#10B981")); // green
                holder.ivIcon.setImageResource(android.R.drawable.ic_menu_mylocation);
                holder.ivIcon.setColorFilter(Color.WHITE);
                
                String empNameTr = getEmployeeName(p.getKaryawanId());
                holder.tvSubtitle.setText(empNameTr != null ? empNameTr : "Karyawan");
                holder.tvSubtitle.setVisibility(View.VISIBLE);
                break;

            case TRACKING_PAUSE:
                holder.tvTitle.setText("Tracking Pause");
                holder.tvTitle.setTextColor(Color.parseColor("#1B1B1F"));
                iconBg.setColor(Color.parseColor("#F59E0B")); // amber
                holder.ivIcon.setImageResource(android.R.drawable.ic_media_pause);
                holder.ivIcon.setColorFilter(Color.WHITE);

                String empNameTp = getEmployeeName(p.getKaryawanId());
                holder.tvSubtitle.setText(empNameTp != null ? empNameTp : "Karyawan");
                holder.tvSubtitle.setVisibility(View.VISIBLE);
                break;
        }
    }

    private String getEmployeeName(String karyawanId) {
        if (karyawanId == null) return null;
        List<Karyawan> all = MockDatabase.getInstance().getAllKaryawan();
        for (Karyawan k : all) {
            if (k.getId().equals(karyawanId)) {
                return k.getNamaLengkap();
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public void updateData(List<Presensi> newLogs) {
        this.logs = newLogs;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle, tvTime;
        FrameLayout flIcon;
        ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            flIcon = itemView.findViewById(R.id.flIcon);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}
