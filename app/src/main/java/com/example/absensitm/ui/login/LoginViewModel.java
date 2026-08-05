package com.example.absensitm.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.absensitm.data.model.LoginRequest;
import com.example.absensitm.data.model.LoginResponse;
import com.example.absensitm.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> loginError = new MutableLiveData<>();
    
    private ApiService apiService;

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getLoginError() {
        return loginError;
    }

    public void login(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            loginError.setValue("Email dan Password tidak boleh kosong");
            return;
        }

        isLoading.setValue(true);
        
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    loginSuccess.setValue(response.body().getData().getToken());
                } else {
                    String errorMsg = "Login Gagal";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            // Extract error message from JSON if possible, otherwise use fallback
                            org.json.JSONObject jObjError = new org.json.JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (Exception e) {
                            errorMsg = "Email atau Password Salah";
                        }
                    }
                    loginError.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                isLoading.setValue(false);
                loginError.setValue("Koneksi Error: " + t.getMessage());
            }
        });
    }
}
