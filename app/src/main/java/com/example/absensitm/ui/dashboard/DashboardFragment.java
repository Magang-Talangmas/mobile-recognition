package com.example.absensitm.ui.dashboard;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.absensitm.R;
import com.example.absensitm.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private android.net.Uri photoUri;
    private java.io.File photoFile;
    private DashboardViewModel viewModel;

    private final androidx.activity.result.ActivityResultLauncher<android.net.Uri> takePictureLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    binding.cardAttendanceStatus.setAlpha(0.5f);
                    viewModel.uploadAttendance(photoFile);
                } else {
                    android.widget.Toast.makeText(requireContext(), "Batal mengambil foto", android.widget.Toast.LENGTH_SHORT).show();
                }
            });

    private void launchCamera() {
        try {
            photoFile = new java.io.File(requireContext().getCacheDir(), "attendance_" + System.currentTimeMillis() + ".jpg");
            photoUri = androidx.core.content.FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );
            takePictureLauncher.launch(photoUri);
        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(requireContext(), "Gagal membuka kamera: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Apply Layout Transition animation to the root for smooth changes
        binding.getRoot().setLayoutTransition(new LayoutTransition());

        // Simple enter animation for card
        binding.cardAttendanceStatus.setAlpha(0f);
        binding.cardAttendanceStatus.setTranslationY(50f);
        binding.cardAttendanceStatus.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(100)
                .start();

        // Setup ViewModel
        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(DashboardViewModel.class);
        viewModel.setApiService(com.example.absensitm.data.network.ApiClient.getApiService(requireContext()));
        
        viewModel.getProfileData().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.tvUserName.setText(profile.getName());
            }
        });
        
        viewModel.getLiveStatus().observe(getViewLifecycleOwner(), statusData -> {
            binding.cardAttendanceStatus.setAlpha(1f);
            if (statusData != null) {
                binding.tvStatusValue.setText(statusData.getStatus());
                
                if (statusData.getStatus().equals("Sedang Bekerja")) {
                    binding.btnDoAttendance.setVisibility(View.GONE);
                    binding.btnCheckout.setVisibility(View.VISIBLE);
                    binding.chronometer.setVisibility(View.VISIBLE);
                    
                    if (statusData.getCheckInTime() != null) {
                        try {
                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            java.util.Date checkInDate = format.parse(statusData.getCheckInTime());
                            long timeSinceCheckIn = System.currentTimeMillis() - checkInDate.getTime();
                            binding.chronometer.setBase(android.os.SystemClock.elapsedRealtime() - timeSinceCheckIn);
                            binding.chronometer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (statusData.getStatus().equals("Selesai Bekerja")) {
                    binding.btnDoAttendance.setVisibility(View.GONE);
                    binding.btnCheckout.setVisibility(View.GONE);
                    binding.chronometer.stop();
                } else {
                    binding.btnDoAttendance.setVisibility(View.VISIBLE);
                    binding.btnCheckout.setVisibility(View.GONE);
                    binding.chronometer.setVisibility(View.GONE);
                    binding.chronometer.stop();
                }
            }
        });

        viewModel.getCheckoutSuccess().observe(getViewLifecycleOwner(), msg -> {
             binding.cardAttendanceStatus.setAlpha(1f);
             if (msg != null) {
                 android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
             }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            binding.cardAttendanceStatus.setAlpha(1f);
            if (msg != null) {
                android.widget.Toast.makeText(requireContext(), msg, android.widget.Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getStatsData().observe(getViewLifecycleOwner(), stats -> {
            if (stats != null) {
                binding.tvTotalHadir.setText(String.valueOf(stats.getPresentCount()));
                binding.tvTotalTelat.setText(String.valueOf(stats.getLateCount()));
            }
        });

        viewModel.getScheduleData().observe(getViewLifecycleOwner(), schedule -> {
            if (schedule != null) {
                binding.tvScheduleName.setText("Jadwal Hari Ini");
                binding.tvScheduleTime.setText(schedule.getCheckInTime() + " - " + schedule.getCheckOutTime() + " (Toleransi: " + schedule.getToleranceMinutes() + " mnt)");
            } else {
                binding.tvScheduleName.setText("Libur");
                binding.tvScheduleTime.setText("Tidak ada jadwal kerja hari ini");
            }
        });

        viewModel.fetchProfile();
        viewModel.fetchLiveStatus();
        viewModel.fetchStats();
        viewModel.fetchSchedule();

        // Fetch FCM Token and update device token
        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                        if (!task.isSuccessful()) {
                            android.util.Log.w("DashboardFragment", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        com.example.absensitm.data.network.ApiClient.getApiService(requireContext())
                                .updateDeviceToken(new com.example.absensitm.data.model.TokenRequest(token))
                                .enqueue(new retrofit2.Callback<com.example.absensitm.data.model.BaseResponse>() {
                                    @Override
                                    public void onResponse(retrofit2.Call<com.example.absensitm.data.model.BaseResponse> call, retrofit2.Response<com.example.absensitm.data.model.BaseResponse> response) {
                                        android.util.Log.d("DashboardFragment", "Token updated to server successfully");
                                    }

                                    @Override
                                    public void onFailure(retrofit2.Call<com.example.absensitm.data.model.BaseResponse> call, Throwable t) {
                                        android.util.Log.e("DashboardFragment", "Failed to update token: " + t.getMessage());
                                    }
                                });
                    }
                });

        // Setup Button listener
        binding.btnDoAttendance.setOnClickListener(v -> {
            launchCamera();
        });

        binding.btnCheckout.setOnClickListener(v -> {
            viewModel.checkOut();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
