package com.example.javatraining.ui.main.home;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import com.example.javatraining.R;
import com.example.javatraining.data.repository.AbsensiTMRepository;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

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
import android.media.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceScanActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private View vScanningLine;
    private ImageView ivFaceBracket;
    private TextView tvInstruction;
    private ValueAnimator scanningAnimator;
    private ObjectAnimator breathingAnimator;
    
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private boolean livenessVerified = false;
    private boolean isProcessing = false;
    private AbsensiTMRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        viewFinder = findViewById(R.id.viewFinder);
        vScanningLine = findViewById(R.id.vScanningLine);
        ivFaceBracket = findViewById(R.id.ivFaceBracket);
        tvInstruction = findViewById(R.id.tvInstruction);
        
        repository = new AbsensiTMRepository(getApplication());
        cameraExecutor = Executors.newSingleThreadExecutor();

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        faceDetector = FaceDetection.getClient(options);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSimulateFail).setVisibility(View.GONE);
        findViewById(R.id.btnSimulateSuccess).setVisibility(View.GONE);

        tvInstruction.setText("Tantangan: Silakan Tersenyum Lebar!");

        startCamera();
        setupAnimations();
    }

    private void setupAnimations() {
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

        breathingAnimator = ObjectAnimator.ofPropertyValuesHolder(
                ivFaceBracket,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.05f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.05f, 1.0f));
        breathingAnimator.setDuration(2000);
        breathingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        breathingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        breathingAnimator.start();
    }

    private void triggerSuccessState() {
        if (scanningAnimator != null) scanningAnimator.cancel();
        if (breathingAnimator != null) breathingAnimator.cancel();

        vScanningLine.setVisibility(View.GONE);
        ivFaceBracket.setColorFilter(Color.parseColor("#198754")); 
        tvInstruction.setText("Liveness Berhasil! Memotret...");
        tvInstruction.setTextColor(Color.parseColor("#198754"));

        ivFaceBracket.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

        ivFaceBracket.animate()
                .scaleX(1.1f).scaleY(1.1f)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .withEndAction(this::takePhoto).start();
    }

    private void triggerFailureState(String errorMsg) {
        runOnUiThread(() -> {
            ivFaceBracket.setColorFilter(Color.parseColor("#BA1A1A")); 
            tvInstruction.setText(errorMsg);
            tvInstruction.setTextColor(Color.parseColor("#BA1A1A"));

            ivFaceBracket.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

            ObjectAnimator shake = ObjectAnimator.ofFloat(ivFaceBracket, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
            shake.setDuration(400);
            shake.start();

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                ivFaceBracket.clearColorFilter();
                tvInstruction.setText("Tantangan: Silakan Tersenyum Lebar!");
                tvInstruction.setTextColor(Color.WHITE);
                livenessVerified = false;
                isProcessing = false;
            }, 2000);
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        if (livenessVerified || isProcessing) {
                            imageProxy.close();
                            return;
                        }
                        
                        Image mediaImage = imageProxy.getImage();
                        if (mediaImage != null) {
                            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                            faceDetector.process(image)
                                    .addOnSuccessListener(faces -> {
                                        for (Face face : faces) {
                                            if (face.getSmilingProbability() != null && face.getSmilingProbability() > 0.7f) {
                                                livenessVerified = true;
                                                runOnUiThread(() -> triggerSuccessState());
                                                break;
                                            }
                                        }
                                    })
                                    .addOnCompleteListener(task -> imageProxy.close());
                        } else {
                            imageProxy.close();
                        }
                    }
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                if (!cameraProvider.hasCamera(cameraSelector)) {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                }

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);

            } catch (Exception exc) {
                android.util.Log.e("FaceScanActivity", "Gagal membuka kamera", exc);
                Toast.makeText(this, "Gagal membuka kamera: " + exc.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;
        isProcessing = true;

        File photoFile = new File(getExternalFilesDir(null), 
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> tvInstruction.setText("Mengirim data ke server..."));
                String eventType = getIntent().getStringExtra("eventType");
                if (eventType == null) eventType = "CHECK_IN";
                repository.submitLivenessAttendance(photoFile, eventType).observe(FaceScanActivity.this, response -> {
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(FaceScanActivity.this, "Absen Liveness Berhasil!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        triggerFailureState("Gagal mengirim absensi");
                    }
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                android.util.Log.e("FaceScanActivity", "Photo capture failed: " + exception.getMessage(), exception);
                triggerFailureState("Gagal mengambil foto");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (faceDetector != null) faceDetector.close();
    }
}
