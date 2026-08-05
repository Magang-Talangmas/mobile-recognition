package com.example.absensitm.data.network;

import com.example.absensitm.data.model.LoginRequest;
import com.example.absensitm.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    // Add other endpoints here (e.g., submit attendance, get history)
}
