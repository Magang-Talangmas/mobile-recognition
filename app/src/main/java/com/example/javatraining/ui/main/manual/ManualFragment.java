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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView etDate, etTime, tvCheckIn, tvCheckOut;
    private Spinner spnReason;
    private FrameLayout flPhotoUpload;
    private LinearLayout llUploadPlaceholder, formContainer, successState;
    private ImageView ivPhotoPreview;
    private ImageButton btnRemovePhoto;
    private Button btnSubmit, btnSubmitAnother;

    private Calendar calendar;
    private boolean hasPhoto = false;
    private boolean isCheckIn = true;

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
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
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        etDate = view.findViewById(R.id.etDate);
        etTime = view.findViewById(R.id.etTime);
        tvCheckIn = view.findViewById(R.id.tvCheckIn);
        tvCheckOut = view.findViewById(R.id.tvCheckOut);
        spnReason = view.findViewById(R.id.spnReason);
        flPhotoUpload = view.findViewById(R.id.flPhotoUpload);
        llUploadPlaceholder = view.findViewById(R.id.llUploadPlaceholder);
        ivPhotoPreview = view.findViewById(R.id.ivPhotoPreview);
        btnRemovePhoto = view.findViewById(R.id.btnRemovePhoto);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmitAnother = view.findViewById(R.id.btnSubmitAnother);
        formContainer = view.findViewById(R.id.formContainer);
        successState = view.findViewById(R.id.successState);
        
        String[] reasons = {"Device / Scanner Offline", "Face Recognition Failed", "Offsite Work / Meeting", "Forgot ID / Badge", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, reasons);
        spnReason.setAdapter(adapter);
    }

    private void setupDefaults() {
        calendar = Calendar.getInstance();
        updateDateLabel();
        updateTimeLabel();
        updateToggleState();
    }

    private void setupListeners() {
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateTimeLabel();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        tvCheckIn.setOnClickListener(v -> {
            isCheckIn = true;
            updateToggleState();
        });

        tvCheckOut.setOnClickListener(v -> {
            isCheckIn = false;
            updateToggleState();
        });

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
            
            btnSubmit.postDelayed(() -> {
                formContainer.setVisibility(View.GONE);
                successState.setVisibility(View.VISIBLE);
            }, 1200);
        });

        btnSubmitAnother.setOnClickListener(v -> {
            hasPhoto = false;
            ivPhotoPreview.setVisibility(View.GONE);
            btnRemovePhoto.setVisibility(View.GONE);
            llUploadPlaceholder.setVisibility(View.VISIBLE);
            flPhotoUpload.setBackgroundResource(R.drawable.bg_photo_upload);
            
            btnSubmit.setText("Submit Request");
            btnSubmit.setEnabled(true);
            
            successState.setVisibility(View.GONE);
            formContainer.setVisibility(View.VISIBLE);
        });
    }

    private void updateToggleState() {
        if (isCheckIn) {
            tvCheckIn.setBackgroundResource(R.drawable.bg_toggle_selected_in);
            tvCheckIn.setTextColor(getResources().getColor(R.color.html_on_primary_container, getActivity().getTheme()));
            
            tvCheckOut.setBackgroundResource(R.drawable.bg_toggle_unselected);
            tvCheckOut.setTextColor(getResources().getColor(R.color.html_on_surface, getActivity().getTheme()));
        } else {
            tvCheckIn.setBackgroundResource(R.drawable.bg_toggle_unselected);
            tvCheckIn.setTextColor(getResources().getColor(R.color.html_on_surface, getActivity().getTheme()));
            
            tvCheckOut.setBackgroundResource(R.drawable.bg_toggle_selected_out);
            tvCheckOut.setTextColor(getResources().getColor(R.color.html_on_secondary_container, getActivity().getTheme()));
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
