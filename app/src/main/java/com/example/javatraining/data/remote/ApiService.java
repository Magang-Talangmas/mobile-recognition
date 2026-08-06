package com.example.javatraining.data.remote;

import com.example.javatraining.data.remote.request.LoginRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.LoginData;
import com.example.javatraining.data.remote.response.PaginatedResponse;
import com.example.javatraining.data.remote.response.EmployeeData;
import com.example.javatraining.data.remote.response.AttendanceData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.Query;
import com.example.javatraining.data.remote.request.FcmTokenRequest;
import com.example.javatraining.data.remote.request.ManualAttendanceRequest;

public interface ApiService {
    @POST("mobile/auth/login")
    Call<BaseResponse<LoginData>> login(@Body LoginRequest request);

    @GET("mobile/profile")
    Call<BaseResponse<EmployeeData>> getProfile();

    @GET("mobile/attendance/history")
    Call<BaseResponse<PaginatedResponse<AttendanceData>>> getAttendances(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("mobile/schedule/today")
    Call<Void> getScheduleToday();

    @PATCH("mobile/device-token")
    Call<BaseResponse<EmployeeData>> updateFcmToken(
            @Body FcmTokenRequest request
    );

    @POST("mobile/attendance")
    Call<BaseResponse<AttendanceData>> submitManualAttendance(
            @Body ManualAttendanceRequest request
    );
}
