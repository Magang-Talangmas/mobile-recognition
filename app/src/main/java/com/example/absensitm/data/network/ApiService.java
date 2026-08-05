package com.example.absensitm.data.network;

import com.example.absensitm.data.model.LoginRequest;
import com.example.absensitm.data.model.LoginResponse;
import com.example.absensitm.data.model.ProfileResponse;
import com.example.absensitm.data.model.BaseResponse;
import com.example.absensitm.data.model.HistoryResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    @GET("profile")
    Call<ProfileResponse> getProfile();
    
    @Multipart
    @POST("attendance")
    Call<BaseResponse> checkIn(@Part okhttp3.MultipartBody.Part photo, @Part("eventType") okhttp3.RequestBody eventType);
    
    @GET("attendance/history")
    Call<HistoryResponse> getHistory(@retrofit2.http.Query("page") int page, @retrofit2.http.Query("limit") int limit);
    
    @retrofit2.http.PATCH("device-token")
    Call<BaseResponse> updateDeviceToken(@Body com.example.absensitm.data.model.TokenRequest request);
}
