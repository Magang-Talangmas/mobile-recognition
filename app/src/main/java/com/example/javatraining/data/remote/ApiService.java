package com.example.javatraining.data.remote;

import com.example.javatraining.data.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("api/login")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // Dummy endpoints for MVP
    @GET("api/attendance/today")
    Call<Void> getTodayAttendance();
}
