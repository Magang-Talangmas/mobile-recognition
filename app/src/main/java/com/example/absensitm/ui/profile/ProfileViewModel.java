package com.example.absensitm.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.absensitm.data.model.ProfileResponse;
import com.example.absensitm.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    
    private final MutableLiveData<ProfileResponse.EmployeeData> profileData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
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
}
