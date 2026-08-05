package com.example.absensitm.ui.camera;

import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class FaceAnalyzer implements ImageAnalysis.Analyzer {

    private final FaceDetector detector;
    private final FaceDetectionListener listener;
    private boolean isProcessing = false;

    public interface FaceDetectionListener {
        void onFaceDetected(boolean hasFace);
    }

    public FaceAnalyzer(FaceDetectionListener listener) {
        this.listener = listener;

        // High-accuracy face detection setup
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .build();

        detector = FaceDetection.getClient(options);
    }

    @Override
    @SuppressLint("UnsafeOptInUsageError")
    public void analyze(@NonNull ImageProxy imageProxy) {
        if (isProcessing) {
            imageProxy.close();
            return;
        }

        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            isProcessing = true;
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            detector.process(image)
                    .addOnSuccessListener(faces -> {
                        // Notify if exactly 1 face is found (prevent multiple faces for security)
                        listener.onFaceDetected(faces.size() == 1);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FaceAnalyzer", "Face detection failed", e);
                        listener.onFaceDetected(false);
                    })
                    .addOnCompleteListener(task -> {
                        isProcessing = false;
                        imageProxy.close();
                    });
        } else {
            imageProxy.close();
        }
    }
}
