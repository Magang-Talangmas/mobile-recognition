package com.example.absensitm.ui.camera;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CameraViewModel extends ViewModel {

    // Status: 0=Mencari wajah, 1=Wajah ditemukan (Siap difoto), 2=Sedang memproses absensi ke server
    private final MutableLiveData<Integer> faceStatus = new MutableLiveData<>(0);
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>("Arahkan wajah ke dalam area frame");

    public LiveData<Integer> getFaceStatus() {
        return faceStatus;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public void updateFaceStatus(boolean faceDetected) {
        if (faceStatus.getValue() == null || faceStatus.getValue() == 2) return; // Ignore if currently processing attendance

        if (faceDetected) {
            if (faceStatus.getValue() != 1) {
                faceStatus.postValue(1);
                statusMessage.postValue("Wajah terdeteksi! Sentuh layar untuk Absen");
            }
        } else {
            if (faceStatus.getValue() != 0) {
                faceStatus.postValue(0);
                statusMessage.postValue("Arahkan wajah ke dalam area frame");
            }
        }
    }

    public void startAttendanceProcess() {
        faceStatus.postValue(2);
        statusMessage.postValue("Memproses data absensi...");
        
        // Mock API call to process image for facial recognition
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            // Assume success
            statusMessage.postValue("Absensi Berhasil!");
        }, 2000);
    }
}
