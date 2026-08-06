package com.example.absensitm.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.absensitm.data.model.ProfileResponse;
import com.example.absensitm.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends ViewModel {
    
    private final MutableLiveData<ProfileResponse.EmployeeData> profileData = new MutableLiveData<>();
    private final MutableLiveData<com.example.absensitm.data.model.StatusResponse.Data> liveStatus = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> checkoutSuccess = new MutableLiveData<>();
    
    private ApiService apiService;

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<ProfileResponse.EmployeeData> getProfileData() {
        return profileData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<com.example.absensitm.data.model.StatusResponse.Data> getLiveStatus() {
        return liveStatus;
    }

    public LiveData<String> getCheckoutSuccess() {
        return checkoutSuccess;
    }

    private final MutableLiveData<com.example.absensitm.data.model.StatsResponse.StatsData> statsData = new MutableLiveData<>();
    public LiveData<com.example.absensitm.data.model.StatsResponse.StatsData> getStatsData() {
        return statsData;
    }

    private final MutableLiveData<com.example.absensitm.data.model.ScheduleResponse.ScheduleData> scheduleData = new MutableLiveData<>();
    public LiveData<com.example.absensitm.data.model.ScheduleResponse.ScheduleData> getScheduleData() {
        return scheduleData;
    }

    public void fetchProfile() {
        if (apiService == null) return;
        
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    profileData.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Gagal mengambil data profil");
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                errorMessage.setValue("Koneksi Error: " + t.getMessage());
            }
        });
    }

    public void fetchLiveStatus() {
        if (apiService == null) return;

        apiService.getLiveStatus().enqueue(new Callback<com.example.absensitm.data.model.StatusResponse>() {
            @Override
            public void onResponse(Call<com.example.absensitm.data.model.StatusResponse> call, Response<com.example.absensitm.data.model.StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveStatus.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Gagal mengambil status live");
                }
            }

            @Override
            public void onFailure(Call<com.example.absensitm.data.model.StatusResponse> call, Throwable t) {
                errorMessage.setValue("Koneksi Error: " + t.getMessage());
            }
        });
    }

    public void checkOut() {
        if (apiService == null) return;

        apiService.checkOut().enqueue(new Callback<com.example.absensitm.data.model.BaseResponse>() {
            @Override
            public void onResponse(Call<com.example.absensitm.data.model.BaseResponse> call, Response<com.example.absensitm.data.model.BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    checkoutSuccess.setValue("Berhasil Check-Out");
                    fetchLiveStatus();
                } else {
                    errorMessage.setValue("Gagal Check-Out");
                }
            }

            @Override
            public void onFailure(Call<com.example.absensitm.data.model.BaseResponse> call, Throwable t) {
                errorMessage.setValue("Koneksi Error: " + t.getMessage());
            }
        });
    }

    public void uploadAttendance(java.io.File photoFile) {
        if (apiService == null) return;
        
        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), photoFile);
        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("photos", photoFile.getName(), requestFile);
        
        okhttp3.RequestBody eventType = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "CHECK_IN");

        apiService.checkIn(body, eventType).enqueue(new Callback<com.example.absensitm.data.model.BaseResponse>() {
            @Override
            public void onResponse(Call<com.example.absensitm.data.model.BaseResponse> call, Response<com.example.absensitm.data.model.BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    checkoutSuccess.setValue("Absensi Berhasil");
                    fetchLiveStatus();
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
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<com.example.absensitm.data.model.BaseResponse> call, Throwable t) {
                errorMessage.setValue("Koneksi Error: " + t.getMessage());
            }
        });
    }

    public void fetchStats() {
        if (apiService == null) return;
        
        apiService.getMonthlyStats().enqueue(new Callback<com.example.absensitm.data.model.StatsResponse>() {
            @Override
            public void onResponse(Call<com.example.absensitm.data.model.StatsResponse> call, Response<com.example.absensitm.data.model.StatsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    statsData.setValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<com.example.absensitm.data.model.StatsResponse> call, Throwable t) {
                // Silently fail or log
            }
        });
    }

    public void fetchSchedule() {
        if (apiService == null) return;
        
        apiService.getTodaySchedule().enqueue(new Callback<com.example.absensitm.data.model.ScheduleResponse>() {
            @Override
            public void onResponse(Call<com.example.absensitm.data.model.ScheduleResponse> call, Response<com.example.absensitm.data.model.ScheduleResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    scheduleData.setValue(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<com.example.absensitm.data.model.ScheduleResponse> call, Throwable t) {
                // Silently fail or log
            }
        });
    }
}
