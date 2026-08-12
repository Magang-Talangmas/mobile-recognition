package com.example.javatraining.ui.main.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.javatraining.R;
import com.example.javatraining.data.model.NotificationItem;
import com.example.javatraining.ui.main.MainActivity;
import com.example.javatraining.ui.main.home.HomeFragment;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.switchToFragment(new HomeFragment());
                mainActivity.selectNavTab(0);
            }
        });

        RecyclerView rvNotifications = view.findViewById(R.id.rvNotifications);
        NotificationAdapter adapter = new NotificationAdapter();
        rvNotifications.setAdapter(adapter);

        com.example.javatraining.data.repository.AbsensiTMRepository repository = new com.example.javatraining.data.repository.AbsensiTMRepository(
                requireActivity().getApplication());

        adapter.setListener(new NotificationAdapter.OnNotificationActionListener() {
            @Override
            public void onConfirm(NotificationItem item, int position) {
                if (item.getRecognitionId() != null) {
                    repository.confirmRecognition(item.getRecognitionId());
                    adapter.removeItem(position);
                    android.widget.Toast.makeText(getContext(), "Kehadiran Dikonfirmasi", android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReject(NotificationItem item, int position) {
                if (item.getRecognitionId() != null) {
                    repository.rejectRecognition(item.getRecognitionId());
                    adapter.removeItem(position);
                    android.widget.Toast.makeText(getContext(), "Kehadiran Ditolak", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        repository.getNotificationsApi().observe(getViewLifecycleOwner(), notifications -> {
            List<NotificationItem> items = new ArrayList<>();
            if (notifications != null && !notifications.isEmpty()) {
                for (com.example.javatraining.data.remote.response.NotificationData n : notifications) {
                    
                    if (!n.isRead()) {
                        repository.markNotificationAsRead(n.getId());
                    }

                    boolean isWarning = "WARNING".equalsIgnoreCase(n.getType())
                            || "ALERT".equalsIgnoreCase(n.getType());
                    boolean requiresConfirmation = "REQUIRE_CONFIRMATION".equalsIgnoreCase(n.getType());
                    items.add(new NotificationItem(
                            n.getId(),
                            n.getTitle() != null ? n.getTitle() : "Notifikasi Absensi",
                            n.getBody() != null ? n.getBody() : "",
                            n.getCreatedAt() != null ? n.getCreatedAt() : "Terbaru",
                            isWarning,
                            n.getImageUrl(),
                            requiresConfirmation,
                            n.getRecognitionId()));
                }
            } else {
                // Default notifications if none returned from server
                items.add(new NotificationItem("1", "Konfirmasi Absensi",
                        "Pengajuan absen manual Anda telah berhasil diproses oleh sistem.", "Baru saja", false, null, false, null));
                items.add(new NotificationItem("2", "Wajah Tidak Dikenali",
                        "Kamera Pintu Utama mendeteksi wajah yang tidak terdaftar. Harap lakukan absensi manual.",
                        "10 mins ago", true, null, false, null));
                items.add(new NotificationItem("3", "Sinkronisasi Sukses",
                        "Data absensi Anda hari ini telah tersinkronisasi dengan server HRD.", "1 hour ago", false, null, false, null));
                items.add(new NotificationItem("4", "Akurasi Wajah Rendah",
                        "Kamera mendeteksi Anda dengan akurasi 82%. Status Anda tetap dikonfirmasi.", "Yesterday",
                        true, null, false, null));
            }
            adapter.updateData(items);
        });

        return view;
    }
}
