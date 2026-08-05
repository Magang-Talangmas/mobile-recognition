package com.example.javatraining.data.remote;

import com.example.javatraining.data.remote.request.LoginRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.LoginData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<BaseResponse<LoginData>> login(@Body LoginRequest request);

    // Keep dummy endpoint for compatibility
    @GET("attendance/today")
    Call<Void> getTodayAttendance();
}
