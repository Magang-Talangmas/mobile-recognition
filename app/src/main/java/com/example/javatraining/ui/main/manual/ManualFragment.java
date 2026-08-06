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

    private TextView etDate, etTime, tvActionTitle, tvSuccessTitle, tvSuccessSubtitle;
    private FrameLayout flPhotoUpload;
    private LinearLayout loadingState, llUploadPlaceholder, formContainer, successState;
    private ImageView ivPhotoPreview;
    private ImageButton btnRemovePhoto;
    private Button btnSubmit;

    private Calendar calendar;
    private boolean hasPhoto = false;
    private boolean isCheckIn = true;
    
    private android.os.Handler timerHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable timerRunnable;
    private java.io.File currentPhotoFile;

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    
                    try {
                        java.io.File cachePath = new java.io.File(getContext().getCacheDir(), "images");
                        cachePath.mkdirs();
                        currentPhotoFile = new java.io.File(cachePath, "selfie_" + System.currentTimeMillis() + ".jpg");
                        java.io.FileOutputStream stream = new java.io.FileOutputStream(currentPhotoFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        stream.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }

                    ivPhotoPreview.setImageBitmap(imageBitmap);
                    ivPhotoPreview.setVisibility(View.VISIBLE);
                    btnRemovePhoto.setVisibility(View.VISIBLE);
                    llUploadPlaceholder.setVisibility(View.GONE);
                    flPhotoUpload.setBackgroundResource(0);
                    hasPhoto = true;
                }
            });

    private final ActivityResultLauncher<String> requestCameraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission required for selfie", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual, container, false);
        
        initViews(view);
        setupDefaults();
        setupListeners(view);
        
        return view;
    }

    private void initViews(View view) {
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        tvActionTitle = view.findViewById(R.id.tvActionTitle);
        tvSuccessTitle = view.findViewById(R.id.tvSuccessTitle);
        tvSuccessSubtitle = view.findViewById(R.id.tvSuccessSubtitle);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void setupListeners(View view) {

        View ivProfile = view.findViewById(R.id.ivProfile);
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(getActivity(), com.example.javatraining.ui.main.profile.ProfileActivity.class);
                startActivity(intent);
            });
        }

        // HANYA DIBUKA KETIKA AREA FOTO DI KLIK (TIDAK OTOMATIS)
        flPhotoUpload.setOnClickListener(v -> {
            if (!hasPhoto) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
            if (!hasPhoto) {
                Toast.makeText(getContext(), "Please take a selfie for verification", Toast.LENGTH_SHORT).show();
                return;
            }
            
            btnSubmit.setText("Processing...");
            btnSubmit.setEnabled(false);

            String eventType = isCheckIn ? "CHECK_IN" : "CHECK_OUT";
            String status = isCheckIn ? "CHECKED_IN" : "CHECKED_OUT";
            
            // Get precise current time instead of relying on the UI text
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String combinedTime = isoFormat.format(Calendar.getInstance().getTime());
            
            String reason = ""; // Removed from UI
            String location = "Menara Thamrin, Jakarta"; // Dummy location

            okhttp3.RequestBody eventTypeBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), eventType);
            okhttp3.MultipartBody.Part photoPart = null;
            if (currentPhotoFile != null && currentPhotoFile.exists()) {
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), currentPhotoFile);
                photoPart = okhttp3.MultipartBody.Part.createFormData("photos", currentPhotoFile.getName(), requestFile);
            }

            ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
            apiService.submitManualAttendance(photoPart, eventTypeBody).enqueue(new Callback<BaseResponse<AttendanceData>>() {
                @Override
                public void onResponse(Call<BaseResponse<AttendanceData>> call, Response<BaseResponse<AttendanceData>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        formContainer.setVisibility(View.GONE);
                        successState.setVisibility(View.VISIBLE);
                        
                        if (isCheckIn) {
                            tvSuccessTitle.setText("Check In Submitted");
                            tvSuccessSubtitle.setText("Your check-in has been sent to your supervisor for review.");
                        } else {
                            tvSuccessTitle.setText("Check Out Submitted");
                            tvSuccessSubtitle.setText("Your check-out has been sent to your supervisor for review.");
                        }
                        
                        ImageView ivSuccessAnim = view.findViewById(R.id.ivSuccessAnim);
                        if (ivSuccessAnim != null && ivSuccessAnim.getDrawable() instanceof android.graphics.drawable.Animatable) {
                            ((android.graphics.drawable.Animatable) ivSuccessAnim.getDrawable()).start();
                        }
                    } else {
                        Toast.makeText(getContext(), "Gagal submit manual attendance", Toast.LENGTH_SHORT).show();
                        btnSubmit.setText("Submit Request");
                        btnSubmit.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<AttendanceData>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSubmit.setText("Submit Request");
                    btnSubmit.setEnabled(true);
                }
            });
        });

    }

    private void fetchAttendanceStatus() {
        loadingState.setVisibility(View.VISIBLE);
        formContainer.setVisibility(View.GONE);
        successState.setVisibility(View.GONE);
        
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getAttendances(1, 1).enqueue(new Callback<PaginatedResponse<AttendanceData>>() {
            @Override
            public void onResponse(Call<PaginatedResponse<AttendanceData>> call, Response<PaginatedResponse<AttendanceData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    AttendanceData latest = response.body().getData().get(0);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String todayStr = sdf.format(Calendar.getInstance().getTime());
                    
                    if (latest.getTimestamp() != null && latest.getTimestamp().startsWith(todayStr)) {
                        if ("CHECK_IN".equals(latest.getEventType())) {
                            setCheckOutState(latest);
                        } else if ("CHECK_OUT".equals(latest.getEventType())) {
                            setCompletedState();
                        } else {
                            setCheckInState();
                        }
                    } else {
                        setCheckInState();
                    }
                } else {
                    setCheckInState();
                }
            }

            @Override
            public void onFailure(Call<PaginatedResponse<AttendanceData>> call, Throwable t) {
                loadingState.setVisibility(View.GONE);
            }
        });
    }

    private void setCheckInState() {
        isCheckIn = true;
        loadingState.setVisibility(View.GONE);
        formContainer.setVisibility(View.VISIBLE);
        successState.setVisibility(View.GONE);
        
        tvActionTitle.setText("Good Morning! Ready to start?");
        btnSubmit.setText("Submit Check In");
        btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.html_primary, getActivity().getTheme())));
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
            } catch (Exception e) {}
        }
        
        tvActionTitle.setText("Checked in at " + timeStr + ". Ready to wrap up?");
        btnSubmit.setText("Submit Check Out");
        btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.html_error, getActivity().getTheme())));
    }

    private void setCompletedState() {
        loadingState.setVisibility(View.GONE);
        formContainer.setVisibility(View.GONE);
        successState.setVisibility(View.VISIBLE);
        
        tvSuccessTitle.setText("All done for today!");
        tvSuccessSubtitle.setText("You've successfully completed your attendance for today.");
        
        ImageView ivSuccessAnim = getView().findViewById(R.id.ivSuccessAnim);
        if (ivSuccessAnim != null && ivSuccessAnim.getDrawable() instanceof android.graphics.drawable.Animatable) {
            ((android.graphics.drawable.Animatable) ivSuccessAnim.getDrawable()).start();
        }
    }
    
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
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
}
