package com.example.absensitm.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.absensitm.R;
import com.example.absensitm.databinding.FragmentCameraBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private CameraViewModel viewModel;
    
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(requireContext(), "Izin kamera diperlukan untuk absensi", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigateUp();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        viewModel.setApiService(com.example.absensitm.data.network.ApiClient.getApiService(requireContext()));
        cameraExecutor = Executors.newSingleThreadExecutor();

        setupObservers();
        
        // Touch to capture when face is ready
        binding.viewFinder.setOnClickListener(v -> {
            Integer status = viewModel.getFaceStatus().getValue();
            if (status != null && status == 1) { // 1 = Face Detected and Ready
                takePhotoAndProcess();
            }
        });

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void setupObservers() {
        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), message -> {
            binding.tvCameraStatus.setText(message);
        });

        viewModel.getFaceStatus().observe(getViewLifecycleOwner(), status -> {
            if (status == 2) {
                // Processing UI
                binding.loadingLayout.setVisibility(View.VISIBLE);
                binding.faceOverlay.setBackgroundResource(R.drawable.bg_face_overlay); // reset color
                
                // Navigate back automatically after mock process (replace with actual logic)
                binding.getRoot().postDelayed(() -> {
                    if (isAdded()) {
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                }, 2000);
            } else if (status == 1) {
                // Ready to capture UI (Green Overlay)
                // In real app, you might want to create a green variant of bg_face_overlay
                binding.faceOverlay.setAlpha(0.8f);
            } else {
                // Default UI
                binding.faceOverlay.setAlpha(1.0f);
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                // Image Analysis for Face Detection
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new FaceAnalyzer(hasFace -> {
                    // Update ViewModel on Main Thread
                    requireActivity().runOnUiThread(() -> viewModel.updateFaceStatus(hasFace));
                }));

                // Select front camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraFragment", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }
    
    private void takePhotoAndProcess() {
        if (imageCapture == null) return;
        
        viewModel.startAttendanceProcess(null); // Just UI update

        java.io.File photoFile = new java.io.File(requireContext().getCacheDir(), "attendance_photo_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                viewModel.uploadAttendance(photoFile);
            }

            @Override
            public void onError(@NonNull androidx.camera.core.ImageCaptureException exception) {
                Log.e("CameraFragment", "Photo capture failed: " + exception.getMessage(), exception);
                requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Gagal mengambil foto", Toast.LENGTH_SHORT).show());
                viewModel.resetStatus(); // reset state
            }
        });
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
        binding = null;
    }
}
