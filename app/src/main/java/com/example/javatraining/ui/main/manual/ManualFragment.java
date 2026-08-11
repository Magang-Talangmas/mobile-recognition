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
import com.example.javatraining.data.remote.request.ManualAttendanceRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.PaginatedResponse;
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

    private TextView etDate, etTime, tvSuccessTitle, tvSuccessSubtitle, tvVerificationTitle,
            tvSelfieLabel;
    private FrameLayout flPhotoUpload;
    private LinearLayout loadingState, llUploadPlaceholder, formContainer, successState;
    private ImageView ivPhotoPreview;
    private ImageButton btnRemovePhoto;
    private Button btnSubmit;

    private Calendar calendar;
    private boolean hasPhoto = false;
    private boolean isCheckIn = true;
    private com.example.javatraining.data.remote.response.ScheduleData todaySchedule = null;

    private android.os.Handler timerHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable timerRunnable;
    private java.io.File currentPhotoFile;
    private android.net.Uri photoUri;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    try {
                        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        android.graphics.BitmapFactory.decodeFile(currentPhotoFile.getAbsolutePath(), options);
                        
                        int reqWidth = 1080;
                        int reqHeight = 1920;
                        int inSampleSize = 1;
                        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                            final int halfHeight = options.outHeight / 2;
                            final int halfWidth = options.outWidth / 2;
                            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                                inSampleSize *= 2;
                            }
                        }
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = inSampleSize;
                        
                        Bitmap imageBitmap = android.graphics.BitmapFactory.decodeFile(currentPhotoFile.getAbsolutePath(), options);

                        // Fix rotation using ExifInterface
                        if (imageBitmap != null) {
                            try {
                                android.media.ExifInterface exif = new android.media.ExifInterface(currentPhotoFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_UNDEFINED);
                                int rotationDegrees = 0;
                                switch (orientation) {
                                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                        rotationDegrees = 90;
                                        break;
                                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                        rotationDegrees = 180;
                                        break;
                                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                                        rotationDegrees = 270;
                                        break;
                                }
                                if (rotationDegrees != 0) {
                                    android.graphics.Matrix matrix = new android.graphics.Matrix();
                                    matrix.postRotate(rotationDegrees);
                                    Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                                    if (rotatedBitmap != imageBitmap) {
                                        imageBitmap.recycle();
                                        imageBitmap = rotatedBitmap;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        // If imageBitmap is somehow null, just fallback to thumbnail if present
                        if (imageBitmap == null && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                imageBitmap = (Bitmap) extras.get("data");
                            }
                        }

                        if (imageBitmap != null) {
                            java.io.FileOutputStream stream = new java.io.FileOutputStream(currentPhotoFile);
                            
                            // Use WEBP format for conversion. 
                            // In Android, .compress() is the method used to encode/convert the image.
                            // Setting quality to 100 with WEBP will just convert it to WebP without aggressive compression.
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                imageBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream);
                            } else {
                                @SuppressWarnings("deprecation")
                                Bitmap.CompressFormat webpFormat = Bitmap.CompressFormat.WEBP;
                                imageBitmap.compress(webpFormat, 100, stream);
                            }
                            
                            stream.close();
                            
                            ivPhotoPreview.setImageBitmap(imageBitmap);
                            ivPhotoPreview.setVisibility(View.VISIBLE);
                            btnRemovePhoto.setVisibility(View.VISIBLE);
                            llUploadPlaceholder.setVisibility(View.GONE);
                            flPhotoUpload.setBackgroundResource(0);
                            hasPhoto = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    private final ActivityResultLauncher<String> requestCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
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
        tvVerificationTitle = view.findViewById(R.id.tvVerificationTitle);
        tvSelfieLabel = view.findViewById(R.id.tvSelfieLabel);
        flPhotoUpload = view.findViewById(R.id.flPhotoUpload);
        loadingState = view.findViewById(R.id.loadingState);
        llUploadPlaceholder = view.findViewById(R.id.llUploadPlaceholder);
        ivPhotoPreview = view.findViewById(R.id.ivPhotoPreview);
        btnRemovePhoto = view.findViewById(R.id.btnRemovePhoto);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void setupListeners(View view) {

        // HANYA DIBUKA KETIKA AREA FOTO DI KLIK (TIDAK OTOMATIS)
        flPhotoUpload.setOnClickListener(v -> {
            if (!hasPhoto) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    requestCameraLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });

        btnRemovePhoto.setOnClickListener(v -> {
            hasPhoto = false;
            ivPhotoPreview.setVisibility(View.GONE);
            btnRemovePhoto.setVisibility(View.GONE);
            llUploadPlaceholder.setVisibility(View.VISIBLE);
            flPhotoUpload.setBackgroundResource(R.drawable.bg_photo_upload);
        });

        btnSubmit.setOnClickListener(v -> {
            if (isCheckIn && !hasPhoto) {
                Toast.makeText(getContext(), "Harap ambil foto selfie untuk verifikasi", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmit.setText("Processing...");
            btnSubmit.setEnabled(false);

            String eventType = isCheckIn ? "CHECK_IN" : "CHECK_OUT";

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String combinedTime = isoFormat.format(Calendar.getInstance().getTime());

            SessionManager sessionManager = new SessionManager(getContext());
            String empId = sessionManager.getUser() != null ? sessionManager.getUser().getId() : "";

            String directionStr = isCheckIn ? "IN" : "OUT";
            String statusStr = isCheckIn ? "CHECKED_IN" : "CHECKED_OUT";

            boolean isLateCalculated = false;
            if (isCheckIn && todaySchedule != null) {
                try {
                    String checkInTimeStr = todaySchedule.getCheckInTime(); // e.g. "08:30" or "08:30:00"
                    Integer tolerance = todaySchedule.getToleranceMinutes();
                    if (checkInTimeStr != null && tolerance != null) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", Locale.getDefault());
                        java.util.Date scheduleTime = sdf.parse(checkInTimeStr);
                        
                        java.util.Calendar calNow = java.util.Calendar.getInstance();
                        String nowStr = sdf.format(calNow.getTime());
                        java.util.Date currentTime = sdf.parse(nowStr);
                        
                        java.util.Calendar calThreshold = java.util.Calendar.getInstance();
                        calThreshold.setTime(scheduleTime);
                        calThreshold.add(java.util.Calendar.MINUTE, tolerance);
                        
                        if (currentTime != null && currentTime.after(calThreshold.getTime())) {
                            isLateCalculated = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            final boolean finalIsLate = isLateCalculated;

            if (currentPhotoFile != null && currentPhotoFile.exists()) {
                // Build filename with checkins folder path to match backend structure
                String filename = "checkins/" + empId + "/manual_" + System.currentTimeMillis() + ".webp";
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(currentPhotoFile,
                        okhttp3.MediaType.parse("image/webp"));

                ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
                apiService.uploadStorageObject("recognition", filename, requestFile)
                        .enqueue(new Callback<okhttp3.ResponseBody>() {
                            @Override
                            public void onResponse(Call<okhttp3.ResponseBody> call,
                                    Response<okhttp3.ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    String publicUrl = com.example.javatraining.BuildConfig.SUPABASE_URL
                                            + "storage/v1/object/public/recognition/" + filename;
                                    sendManualAttendance(view, empId, directionStr, eventType, statusStr, combinedTime,
                                            publicUrl, finalIsLate);
                                } else {
                                    String errorMsg = "Upload Failed: " + response.code();
                                    try {
                                        if (response.errorBody() != null)
                                            errorMsg += " " + response.errorBody().string();
                                    } catch (Exception e) {
                                    }
                                    android.util.Log.e("IMAGE_UPLOAD", errorMsg);

                                    // Make sure we show a Toast to the user so they know image failed
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();

                                    sendManualAttendance(view, empId, directionStr, eventType, statusStr, combinedTime,
                                            null, finalIsLate);
                                }
                            }

                            @Override
                            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                                sendManualAttendance(view, empId, directionStr, eventType, statusStr, combinedTime,
                                        null, finalIsLate);
                            }
                        });
            } else {
                sendManualAttendance(view, empId, directionStr, eventType, statusStr, combinedTime, null, finalIsLate);
            }
        });
    }

    private void sendManualAttendance(View view, String empId, String directionStr, String eventType, String statusStr,
            String combinedTime, String imageUrl, boolean isLate) {
        com.example.javatraining.data.remote.request.ManualAttendanceRequest request = new com.example.javatraining.data.remote.request.ManualAttendanceRequest(
                empId,
                directionStr,
                eventType,
                statusStr,
                combinedTime,
                imageUrl,
                isLate);

        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.submitManualAttendance(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    formContainer.setVisibility(View.GONE);
                    successState.setVisibility(View.VISIBLE);

                    if (isCheckIn) {
                        tvSuccessTitle.setText("Check-In Terkirim");
                        tvSuccessSubtitle.setText("Check-in Anda telah dikirim ke atasan untuk ditinjau.");
                    } else {
                        tvSuccessTitle.setText("Check-Out Terkirim");
                        tvSuccessSubtitle.setText("Check-out Anda telah dikirim ke atasan untuk ditinjau.");
                    }

                    ImageView ivSuccessAnim = view.findViewById(R.id.ivSuccessAnim);
                    if (ivSuccessAnim != null
                            && ivSuccessAnim.getDrawable() instanceof android.graphics.drawable.Animatable) {
                        ((android.graphics.drawable.Animatable) ivSuccessAnim.getDrawable()).start();
                    }
                } else {
                    String errorMsg = "Gagal: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                    }
                    android.util.Log.e("MANUAL_ATTENDANCE", errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    btnSubmit.setText("Submit Request");
                    btnSubmit.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setText("Submit Request");
                btnSubmit.setEnabled(true);
            }
        });
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
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    latestAttendance[0] = response.body().get(0);
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

        tvVerificationTitle.setVisibility(View.VISIBLE);
        tvSelfieLabel.setVisibility(View.VISIBLE);
        flPhotoUpload.setVisibility(View.VISIBLE);
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

        tvVerificationTitle.setVisibility(View.GONE);
        tvSelfieLabel.setVisibility(View.GONE);
        flPhotoUpload.setVisibility(View.GONE);
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

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                java.io.File cachePath = new java.io.File(getContext().getCacheDir(), "images");
                cachePath.mkdirs();
                currentPhotoFile = new java.io.File(cachePath, "selfie_" + System.currentTimeMillis() + ".webp");
                photoUri = androidx.core.content.FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", currentPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
