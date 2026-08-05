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
        
        // Dummy implementation to bypass actual network call for now 
        // since backend endpoint might not be ready or reachable
        new android.os.Handler().postDelayed(() -> {
            isLoading.setValue(false);
            if (email.equals("admin@test.com") && password.equals("admin123")) {
                loginSuccess.setValue("dummy_jwt_token_12345");
            } else {
                loginError.setValue("Kredensial tidak valid");
            }
        }, 1500);

        /* Real implementation for later
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginSuccess.setValue(response.body().getToken());
                } else {
                    loginError.setValue("Login Gagal: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                isLoading.setValue(false);
                loginError.setValue("Koneksi Error: " + t.getMessage());
            }
        });
        */
    }
}
