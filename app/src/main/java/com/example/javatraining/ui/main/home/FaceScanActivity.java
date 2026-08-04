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

public class FaceScanActivity extends AppCompatActivity {

    private PreviewView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_scan);

        viewFinder = findViewById(R.id.viewFinder);

        startCamera();

        findViewById(R.id.btnSimulateFail).setOnClickListener(v -> {
            Toast.makeText(this, "Wajah tidak dikenali! Absen ditolak.", Toast.LENGTH_LONG).show();
            // Do not finish immediately so they can see the error, or finish with error result
            // finish();
        });

        findViewById(R.id.btnSimulateSuccess).setOnClickListener(v -> {
            Karyawan currentUser = MockDatabase.getInstance().getCurrentKaryawan();
            if (currentUser != null) {
                // If already checked in, then check out. Else check in.
                boolean isCheckedIn = MockDatabase.getInstance().isCheckedIn(currentUser.getId());
                if (isCheckedIn) {
                    MockDatabase.getInstance().checkOut(currentUser.getId());
                    Toast.makeText(this, "Face Scan Berhasil: Check Out Sukses!", Toast.LENGTH_LONG).show();
                } else {
                    MockDatabase.getInstance().checkIn(currentUser.getId());
                    Toast.makeText(this, "Face Scan Berhasil: Check In Sukses!", Toast.LENGTH_LONG).show();
                }
            }
            finish();
        });
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
