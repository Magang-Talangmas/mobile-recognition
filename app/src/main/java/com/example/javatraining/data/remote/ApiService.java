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
import retrofit2.http.Query;
import com.example.javatraining.data.remote.request.FcmTokenRequest;
import com.example.javatraining.data.remote.request.ManualAttendanceRequest;

public interface ApiService {
    @POST("auth/login")
    Call<BaseResponse<LoginData>> login(@Body LoginRequest request);

    @GET("employees")
    Call<BaseResponse<PaginatedResponse<EmployeeData>>> getEmployees(
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    @GET("attendance")
    Call<BaseResponse<PaginatedResponse<AttendanceData>>> getAttendances(
            @Query("employee_id") String employeeId,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    // Keep dummy endpoint for compatibility
    @GET("attendance/today")
    Call<Void> getTodayAttendance();

    @PUT("employees/{id}/fcm-token")
    Call<BaseResponse<EmployeeData>> updateFcmToken(
            @Path("id") String id,
            @Body FcmTokenRequest request
    );

    @POST("attendance/manual")
    Call<BaseResponse<AttendanceData>> submitManualAttendance(
            @Body ManualAttendanceRequest request
    );
}
