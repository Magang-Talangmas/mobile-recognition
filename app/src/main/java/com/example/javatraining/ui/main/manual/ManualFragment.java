package com.example.javatraining.ui.main.manual;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.javatraining.data.local.SessionManager;
import com.example.javatraining.data.remote.ApiClient;
import com.example.javatraining.data.remote.ApiService;
import com.example.javatraining.data.repository.AbsensiTMRepository;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.AttendanceData;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.javatraining.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ManualFragment extends Fragment {

    private TextView etDate, etTime, tvSuccessTitle, tvSuccessSubtitle;
    private LinearLayout loadingState, formContainer, successState;
    private Button btnSubmit;

    private Calendar calendar;
    private boolean hasPhoto = false;
    private boolean isCheckIn = true;
    private com.example.javatraining.data.remote.response.ScheduleData todaySchedule = null;
    private boolean hasPendingRecognitions = false;

    private android.os.Handler timerHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable timerRunnable;


    private final ActivityResultLauncher<String> requestCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent intent = new Intent(getActivity(), com.example.javatraining.ui.main.home.FaceScanActivity.class);
                    intent.putExtra("eventType", isCheckIn ? "CHECK_IN" : "CHECK_OUT");
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Izin kamera diperlukan untuk foto selfie", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual, container, false);

        initViews(view);
        setupDefaults();
        setupListeners(view);

        return view;
    }

    private void initViews(View view) {
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        tvSuccessTitle = view.findViewById(R.id.tvSuccessTitle);
        tvSuccessSubtitle = view.findViewById(R.id.tvSuccessSubtitle);
        loadingState = view.findViewById(R.id.loadingState);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        formContainer = view.findViewById(R.id.formContainer);
        successState = view.findViewById(R.id.successState);
    }

    private void setupDefaults() {
        calendar = Calendar.getInstance();
        updateDateLabel();
        updateTimeLabel();

        fetchAttendanceStatus();

        // Start real-time clock
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                calendar = Calendar.getInstance();
                updateDateLabel();
                updateTimeLabel();
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAttendanceStatus();
        fetchSchedule();
        checkPendingRecognitions();
    }

    private void checkPendingRecognitions() {
        AbsensiTMRepository repository = new AbsensiTMRepository(requireActivity().getApplication());
        repository.getPendingRecognitions().observe(getViewLifecycleOwner(), pendingRecognitions -> {
            hasPendingRecognitions = pendingRecognitions != null && !pendingRecognitions.isEmpty();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void setupListeners(View view) {
        btnSubmit.setOnClickListener(v -> {
            String eventType = isCheckIn ? "CHECK_IN" : "CHECK_OUT";
            if (!isCheckIn) {
                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Konfirmasi Check-Out")
                        .setMessage("Apakah Anda yakin ingin melakukan Check-Out sekarang?")
                        .setPositiveButton("Ya, Check-Out", (dialog, which) -> {
                            btnSubmit.setEnabled(false);
                            AbsensiTMRepository repository = new AbsensiTMRepository(requireActivity().getApplication());
                            repository.submitManualAttendance(eventType).observe(getViewLifecycleOwner(), response -> {
                                btnSubmit.setEnabled(true);
                                if (response != null && response.isSuccess()) {
                                    android.widget.Toast.makeText(getContext(), "Check-Out berhasil!", android.widget.Toast.LENGTH_SHORT).show();
                                    fetchAttendanceStatus();
                                } else {
                                    android.widget.Toast.makeText(getContext(), "Check-Out gagal. Silakan coba lagi.", android.widget.Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            } else {
                launchFaceScan(eventType);
            }
        });
    }

    private void launchFaceScan(String eventType) {
        if (hasPendingRecognitions) {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("Perhatian")
                    .setMessage("Anda memiliki deteksi wajah dari CCTV yang belum dikonfirmasi.\n\nHarap buka menu Beranda dan konfirmasi/tolak data tersebut terlebih dahulu sebelum menggunakan absensi manual.")
                    .setPositiveButton("Mengerti", null)
                    .show();
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(getActivity(), com.example.javatraining.ui.main.home.FaceScanActivity.class);
            intent.putExtra("eventType", eventType);
            startActivity(intent);
        } else {
            requestCameraLauncher.launch(Manifest.permission.CAMERA);
        }
    }



    private void fetchSchedule() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getScheduleToday().enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.ScheduleData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.ScheduleData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.ScheduleData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    todaySchedule = response.body().get(0);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.ScheduleData>> call, Throwable t) {
                // Ignore failure, todaySchedule will remain null
            }
        });
    }

    private void fetchAttendanceStatus() {
        fetchSchedule();
        loadingState.setVisibility(View.VISIBLE);
        formContainer.setVisibility(View.GONE);
        successState.setVisibility(View.GONE);

        final boolean[] attendancesLoaded = {false};
        final boolean[] leavesLoaded = {false};
        final com.example.javatraining.data.remote.response.AttendanceData[] latestAttendance = {null};
        final com.example.javatraining.data.remote.response.LeaveData[] latestLeave = {null};

        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        SessionManager sm = new SessionManager(getContext());
        String empId = sm.getUser() != null ? sm.getUser().getId() : "";

        apiService.getAttendances("eq." + empId, 1).enqueue(new Callback<List<AttendanceData>>() {
            @Override
            public void onResponse(Call<List<AttendanceData>> call, Response<List<AttendanceData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AttendanceData> data = response.body();
                    if (data != null && !data.isEmpty()) {
                        latestAttendance[0] = data.get(0);
                    }
                }
                attendancesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
            @Override
            public void onFailure(Call<List<AttendanceData>> call, Throwable t) {
                attendancesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
        });

        apiService.getLeaveRequests("eq." + empId, 1).enqueue(new Callback<List<com.example.javatraining.data.remote.response.LeaveData>>() {
            @Override
            public void onResponse(Call<List<com.example.javatraining.data.remote.response.LeaveData>> call, Response<List<com.example.javatraining.data.remote.response.LeaveData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    latestLeave[0] = response.body().get(0);
                }
                leavesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
            @Override
            public void onFailure(Call<List<com.example.javatraining.data.remote.response.LeaveData>> call, Throwable t) {
                leavesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
        });
    }

    private void checkStatus(boolean[] attendancesLoaded, boolean[] leavesLoaded, com.example.javatraining.data.remote.response.AttendanceData latestAtt, com.example.javatraining.data.remote.response.LeaveData latestLeave) {
        if (!isAdded()) return;
        if (attendancesLoaded[0] && leavesLoaded[0]) {
            boolean hasLeaveToday = false;
            boolean hasAttToday = false;
            boolean isCheckOut = false;
            
            java.util.Calendar calToday = java.util.Calendar.getInstance();

            if (latestLeave != null) {
                String timeStr = latestLeave.getCreatedAt() != null ? latestLeave.getCreatedAt() : latestLeave.getDate();
                java.util.Date d = parseIsoDate(timeStr);
                if (d == null && latestLeave.getDate() != null) d = parseIsoDate(latestLeave.getDate() + "T00:00:00Z");
                if (d != null) {
                    java.util.Calendar calEvent = java.util.Calendar.getInstance();
                    calEvent.setTime(d);
                    if (calEvent.get(java.util.Calendar.YEAR) == calToday.get(java.util.Calendar.YEAR) &&
                        calEvent.get(java.util.Calendar.DAY_OF_YEAR) == calToday.get(java.util.Calendar.DAY_OF_YEAR)) {
                        hasLeaveToday = true;
                    }
                }
            }

            if (latestAtt != null && latestAtt.getTimestamp() != null) {
                java.util.Date d = parseIsoDate(latestAtt.getTimestamp());
                if (d != null) {
                    java.util.Calendar calEvent = java.util.Calendar.getInstance();
                    calEvent.setTime(d);
                    if (calEvent.get(java.util.Calendar.YEAR) == calToday.get(java.util.Calendar.YEAR) &&
                        calEvent.get(java.util.Calendar.DAY_OF_YEAR) == calToday.get(java.util.Calendar.DAY_OF_YEAR)) {
                        hasAttToday = true;
                        String eventType = latestAtt.getEventType();
                        if ("CHECK_OUT".equalsIgnoreCase(eventType) || "OUT".equalsIgnoreCase(eventType)) {
                            isCheckOut = true;
                        }
                    }
                }
            }

            if (hasLeaveToday) {
                setCompletedState("Anda sudah mengajukan izin hari ini. Tidak dapat melakukan absen.");
            } else if (hasAttToday && isCheckOut) {
                setCompletedState("Anda telah berhasil menyelesaikan absensi hari ini.");
            } else if (hasAttToday && !isCheckOut) {
                setCheckOutState(latestAtt);
            } else {
                setCheckInState();
            }
        }
    }

    private void setCheckInState() {
        isCheckIn = true;
        loadingState.setVisibility(View.GONE);
        formContainer.setVisibility(View.VISIBLE);
        successState.setVisibility(View.GONE);

        btnSubmit.setText("Submit Check In");
        btnSubmit.setBackgroundTintList(android.content.res.ColorStateList
                .valueOf(getResources().getColor(R.color.html_primary, getActivity().getTheme())));
    }

    private void setCheckOutState(AttendanceData latest) {
        isCheckIn = false;
        loadingState.setVisibility(View.GONE);
        formContainer.setVisibility(View.VISIBLE);
        successState.setVisibility(View.GONE);

        String timeStr = "earlier today";
        if (latest.getTimestamp() != null && latest.getTimestamp().length() > 16) {
            try {
                SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                iso.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date date = iso.parse(latest.getTimestamp());
                SimpleDateFormat local = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timeStr = local.format(date);
            } catch (Exception e) {
            }
        }

        btnSubmit.setText("Submit Check Out");
        btnSubmit.setBackgroundTintList(android.content.res.ColorStateList
                .valueOf(getResources().getColor(R.color.html_error, getActivity().getTheme())));
    }

    private void setCompletedState(String message) {
        loadingState.setVisibility(View.GONE);
        formContainer.setVisibility(View.GONE);
        successState.setVisibility(View.VISIBLE);

        tvSuccessTitle.setText("Selesai untuk hari ini!");
        tvSuccessSubtitle.setText(message);

        ImageView ivSuccessAnim = getView().findViewById(R.id.ivSuccessAnim);
        if (ivSuccessAnim != null && ivSuccessAnim.getDrawable() instanceof android.graphics.drawable.Animatable) {
            ((android.graphics.drawable.Animatable) ivSuccessAnim.getDrawable()).start();
        }
    }



    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etTime.setText(sdf.format(calendar.getTime()));
    }

    private java.util.Date parseIsoDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return null;
        String raw = dateStr.trim();
        String normalized = raw.replace(" ", "T");

        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String fmt : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.US);
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date d = sdf.parse(raw);
                if (d != null)
                    return d;
            } catch (Exception ignored) {
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.US);
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date d = sdf.parse(normalized);
                if (d != null)
                    return d;
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
