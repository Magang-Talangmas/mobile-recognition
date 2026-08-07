package com.example.javatraining.ui.main.home;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.example.javatraining.R;
import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.repository.MockDatabase;
import com.google.common.util.concurrent.ListenableFuture;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.graphics.Color;

public class FaceScanActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private View vScanningLine;
    private ImageView ivFaceBracket;
    private TextView tvInstruction;
    private ValueAnimator scanningAnimator;
    private ObjectAnimator breathingAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        viewFinder = findViewById(R.id.viewFinder);
        vScanningLine = findViewById(R.id.vScanningLine);
        ivFaceBracket = findViewById(R.id.ivFaceBracket);
        tvInstruction = findViewById(R.id.tvInstruction);
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        startCamera();
        setupAnimations();

        findViewById(R.id.btnSimulateFail).setOnClickListener(v -> {
            triggerFailureState();
        });

        findViewById(R.id.btnSimulateSuccess).setOnClickListener(v -> {
            triggerSuccessState();
        });
    }

    private void setupAnimations() {
        // Scanning Line Loop
        scanningAnimator = ValueAnimator.ofFloat(0f, 320f * getResources().getDisplayMetrics().density - 8f);
        scanningAnimator.setDuration(1500);
        scanningAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanningAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scanningAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scanningAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            vScanningLine.setTranslationY(value);
        });
        scanningAnimator.start();

        // Frame Breathing Effect
        breathingAnimator = ObjectAnimator.ofPropertyValuesHolder(
                ivFaceBracket,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.05f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.05f, 1.0f)
        );
        breathingAnimator.setDuration(2000);
        breathingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        breathingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        breathingAnimator.start();
    }
    
    private void triggerSuccessState() {
        if (scanningAnimator != null) scanningAnimator.cancel();
        if (breathingAnimator != null) breathingAnimator.cancel();
        
        vScanningLine.setVisibility(View.GONE);
        ivFaceBracket.setColorFilter(Color.parseColor("#198754")); // Enterprise Green
        tvInstruction.setText("Wajah Terverifikasi!");
        tvInstruction.setTextColor(Color.parseColor("#198754"));
        
        ivFaceBracket.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
        
        ivFaceBracket.animate()
            .scaleX(1.1f).scaleY(1.1f)
            .setDuration(200)
            .setInterpolator(new OvershootInterpolator())
            .withEndAction(() -> {
                Karyawan currentUser = MockDatabase.getInstance().getCurrentKaryawan();
                if (currentUser != null) {
                    if (MockDatabase.getInstance().isCheckedIn(currentUser.getId())) {
                        MockDatabase.getInstance().checkOut(currentUser.getId());
                        Toast.makeText(this, "Check Out Sukses", Toast.LENGTH_SHORT).show();
                    } else {
                        MockDatabase.getInstance().checkIn(currentUser.getId());
                        Toast.makeText(this, "Check In Sukses", Toast.LENGTH_SHORT).show();
                    }
                }
                new Handler(Looper.getMainLooper()).postDelayed(this::finish, 500);
            }).start();
    }
    
    private void triggerFailureState() {
        ivFaceBracket.setColorFilter(Color.parseColor("#BA1A1A")); // Enterprise Red
        tvInstruction.setText("Wajah Tidak Dikenali!");
        tvInstruction.setTextColor(Color.parseColor("#BA1A1A"));
        
        ivFaceBracket.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        
        // Shake Animation
        ObjectAnimator shake = ObjectAnimator.ofFloat(ivFaceBracket, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        shake.setDuration(400);
        shake.start();
        
        // Reset after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ivFaceBracket.clearColorFilter();
            tvInstruction.setText("Posisikan wajah di dalam bingkai");
            tvInstruction.setTextColor(Color.WHITE);
        }, 2000);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                // Try front camera first, fallback to back camera if emulator doesn't have front camera
                CameraSelector cameraSelector;
                if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                } else {
                    Toast.makeText(this, "Tidak ada kamera yang tersedia di perangkat ini.", Toast.LENGTH_LONG).show();
                    return;
                }

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (Exception exc) {
                android.util.Log.e("FaceScanActivity", "Gagal membuka kamera", exc);
                Toast.makeText(this, "Gagal membuka kamera: " + exc.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }
}
