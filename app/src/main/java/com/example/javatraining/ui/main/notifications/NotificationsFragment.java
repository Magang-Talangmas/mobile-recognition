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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        // Mock data suited for ai-recognition
        List<NotificationItem> mockData = new ArrayList<>();
        mockData.add(new NotificationItem("Wajah Tidak Dikenali", "Kamera Pintu Utama mendeteksi wajah yang tidak terdaftar. Harap lakukan absensi manual.", "10 mins ago", true));
        mockData.add(new NotificationItem("Sinkronisasi Sukses", "Data absensi Anda hari ini telah tersinkronisasi dengan server HRD.", "1 hour ago", false));
        mockData.add(new NotificationItem("Akurasi Wajah Rendah", "Kamera mendeteksi Anda dengan akurasi 82% (karena pencahayaan). Status Anda tetap dikonfirmasi.", "Yesterday", true));
        mockData.add(new NotificationItem("Sistem AI Offline", "Kamera Lorong A sempat terputus koneksinya. Harap cek riwayat absensi Anda.", "2 days ago", true));
        
        adapter.updateData(mockData);

        return view;
    }
}
