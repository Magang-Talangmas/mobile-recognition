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
import android.os.CountDownTimer;
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

    
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;
    private boolean livenessVerified = false;
    private boolean isProcessing = false;
    private AbsensiTMRepository repository;
    private FaceGuideView ivVisualGuide;
    
    private enum LivenessStep {
        BLINK, TURN_LEFT, TURN_RIGHT, SMILE, DONE
    }
    
    private LivenessStep[] steps = {LivenessStep.BLINK, LivenessStep.TURN_LEFT, LivenessStep.TURN_RIGHT, LivenessStep.SMILE};
    private int currentStepIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        viewFinder = findViewById(R.id.viewFinder);
        vScanningLine = findViewById(R.id.vScanningLine);
        ivFaceBracket = findViewById(R.id.ivFaceBracket);
        tvInstruction = findViewById(R.id.tvInstruction);
        ivVisualGuide = findViewById(R.id.ivVisualGuide);
        
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

        findViewById(R.id.btnSimulateSuccess).setVisibility(View.GONE);

        updateStepUI();

        startCamera();
        setupAnimations();
    }

    private void setupAnimations() {
        scanningAnimator = ValueAnimator.ofFloat(0f, 300f * getResources().getDisplayMetrics().density - 8f);
        scanningAnimator.setDuration(1100);
        scanningAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanningAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scanningAnimator.setInterpolator(new android.view.animation.LinearInterpolator());
        scanningAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            vScanningLine.setTranslationY(value);
        });
        scanningAnimator.start();
    }

    private void updateStepUI() {
        if (currentStepIndex >= steps.length) return;
        LivenessStep step = steps[currentStepIndex];
        switch (step) {
            case BLINK:
                ivFaceBracket.setColorFilter(Color.parseColor("#BA1A1A")); // Red
                tvInstruction.setText("Tantangan 1/4: Kedipkan Mata!");
                ivVisualGuide.animateBlink();
                break;
            case TURN_LEFT:
                ivFaceBracket.setColorFilter(Color.parseColor("#FD8A14")); // Orange
                tvInstruction.setText("Tantangan 2/4: Toleh Kiri!");
                ivVisualGuide.animateLookLeft();
                break;
            case TURN_RIGHT:
                ivFaceBracket.setColorFilter(Color.parseColor("#FDE047")); // Yellow
                tvInstruction.setText("Tantangan 3/4: Toleh Kanan!");
                ivVisualGuide.animateLookRight();
                break;
            case SMILE:
                ivFaceBracket.setColorFilter(Color.parseColor("#86EFAC")); // Light Green
                tvInstruction.setText("Tantangan 4/4: Senyum & Tahan!");
                ivVisualGuide.animateSmile();
                break;
        }
    }

    private void advanceStep() {
        if (isProcessing) return;
        currentStepIndex++;
        if (currentStepIndex >= steps.length) {
            livenessVerified = true;
            runOnUiThread(() -> {
                ivFaceBracket.setColorFilter(Color.parseColor("#198754")); 
                tvInstruction.setTextColor(Color.parseColor("#198754"));
                ivVisualGuide.resetState();
                triggerSuccessState();
            });
        } else {
            runOnUiThread(() -> {
                ivFaceBracket.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                updateStepUI();
            });
            // debounce processing
            isProcessing = true;
            new Handler(Looper.getMainLooper()).postDelayed(() -> isProcessing = false, 1500);
        }
    }

    private void triggerSuccessState() {
        if (scanningAnimator != null) scanningAnimator.cancel();

        vScanningLine.setVisibility(View.GONE);
        tvInstruction.setText("Memotret...");

        ivFaceBracket.performHapticFeedback(HapticFeedbackConstants.CONFIRM);

        ivFaceBracket.animate()
                .scaleX(0.9f).scaleY(0.9f)
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
                tvInstruction.setTextColor(Color.WHITE);
                currentStepIndex = 0;
                updateStepUI();
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
                                        if (faces.isEmpty()) {
                                            runOnUiThread(() -> tvInstruction.setText("Wajah tidak terdeteksi"));
                                            return;
                                        }
                                        
                                        for (Face face : faces) {
                                            android.graphics.Rect box = face.getBoundingBox();
                                            int imgW = image.getWidth();
                                            int imgH = image.getHeight();
                                            
                                            float centerX = box.exactCenterX();
                                            float centerY = box.exactCenterY();
                                            
                                            float diffX = Math.abs(centerX - imgW / 2f);
                                            float diffY = Math.abs(centerY - imgH / 2f);
                                            
                                            float maxFaceSize = Math.max(box.width(), box.height());
                                            float minImgDim = Math.min(imgW, imgH);
                                            
                                            // Constraint checks (Relaxed)
                                            if (diffX > imgW * 0.45f || diffY > imgH * 0.45f) {
                                                runOnUiThread(() -> tvInstruction.setText("Posisikan wajah di tengah lingkaran"));
                                                break;
                                            }
                                            
                                            if (maxFaceSize < minImgDim * 0.15f) {
                                                runOnUiThread(() -> tvInstruction.setText("Wajah terlalu jauh"));
                                                break;
                                            }
                                            
                                            if (maxFaceSize > minImgDim * 0.95f) {
                                                runOnUiThread(() -> tvInstruction.setText("Wajah terlalu dekat"));
                                                break;
                                            }

                                            // Mask / Occlusion Check Heuristic
                                            if (face.getSmilingProbability() == null || face.getLeftEyeOpenProbability() == null) {
                                                runOnUiThread(() -> tvInstruction.setText("Wajah tertutup (Lepas masker/kacamata)"));
                                                break;
                                            }

                                            // If constraints passed, evaluate steps
                                            if (currentStepIndex >= steps.length) break;
                                            LivenessStep current = steps[currentStepIndex];
                                            
                                            boolean stepPassed = false;
                                            switch (current) {
                                                case SMILE:
                                                    if (face.getSmilingProbability() != null && face.getSmilingProbability() > 0.7f) stepPassed = true;
                                                    break;
                                                case BLINK:
                                                    if (face.getLeftEyeOpenProbability() != null && face.getRightEyeOpenProbability() != null &&
                                                        face.getLeftEyeOpenProbability() < 0.2f && face.getRightEyeOpenProbability() < 0.2f) {
                                                        stepPassed = true;
                                                    }
                                                    break;
                                                case TURN_LEFT:
                                                    if (face.getHeadEulerAngleY() > 25f) stepPassed = true;
                                                    break;
                                                case TURN_RIGHT:
                                                    if (face.getHeadEulerAngleY() < -25f) stepPassed = true;
                                                    break;
                                            }
                                            
                                            if (stepPassed) {
                                                advanceStep();
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
