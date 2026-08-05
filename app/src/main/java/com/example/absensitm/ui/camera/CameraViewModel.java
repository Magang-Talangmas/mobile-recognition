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

    private com.example.absensitm.data.network.ApiService apiService;

    public void setApiService(com.example.absensitm.data.network.ApiService apiService) {
        this.apiService = apiService;
    }

    public void startAttendanceProcess(java.io.File file) {
        faceStatus.postValue(2);
        statusMessage.postValue("Mengambil foto...");
    }

    public void uploadAttendance(java.io.File photoFile) {
        if (apiService == null) return;
        statusMessage.postValue("Memproses data absensi...");

        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), photoFile);
        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("photo", photoFile.getName(), requestFile);
        
        okhttp3.RequestBody eventType = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "CHECK_IN");

        apiService.checkIn(body, eventType).enqueue(new retrofit2.Callback<com.example.absensitm.data.model.BaseResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.absensitm.data.model.BaseResponse> call, retrofit2.Response<com.example.absensitm.data.model.BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    statusMessage.postValue("Absensi Berhasil!");
                    // Stay at status 2 so UI navigates back
                } else {
                    String errorMsg = "Absensi Gagal";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            org.json.JSONObject jObjError = new org.json.JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (Exception e) {}
                    }
                    statusMessage.postValue(errorMsg);
                    
                    // reset status after a delay so user can try again
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        resetStatus();
                    }, 3000);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.absensitm.data.model.BaseResponse> call, Throwable t) {
                statusMessage.postValue("Koneksi Error: " + t.getMessage());
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    resetStatus();
                }, 3000);
            }
        });
    }
    
    public void resetStatus() {
        faceStatus.postValue(0);
        statusMessage.postValue("Arahkan wajah ke dalam area frame");
    }
}
