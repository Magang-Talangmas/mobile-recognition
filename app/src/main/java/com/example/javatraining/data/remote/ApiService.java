package com.example.javatraining.data.remote;

import com.example.javatraining.data.remote.request.LoginRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.LoginData;
import com.example.javatraining.data.remote.response.PaginatedResponse;
import com.example.javatraining.data.remote.response.EmployeeData;
import com.example.javatraining.data.remote.response.AttendanceData;
import com.example.javatraining.data.remote.response.ScheduleData;
import com.example.javatraining.data.remote.response.NotificationData;
import com.example.javatraining.data.remote.request.FcmTokenRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/v1/token?grant_type=password")
    Call<LoginData> login(@Body LoginRequest request);

    @GET("rest/v1/employees?select=*")
    Call<List<EmployeeData>> getProfile(@Query("email") String email);

    @GET("rest/v1/attendance_events?select=*,employees(*)&order=detected_at.desc")
    Call<List<AttendanceData>> getAttendances(
            @Query("employee_id") String employeeId,
            @Query("limit") Integer limit
    );

    @GET("rest/v1/schedule?select=*")
    Call<List<ScheduleData>> getScheduleToday();

    @PATCH("rest/v1/employees")
    Call<Void> updateFcmToken(
            @Query("email") String email,
            @Body FcmTokenRequest request
    );

    @Multipart
    @POST("rest/v1/attendance_events")
    Call<Void> submitManualAttendance(
            @Part MultipartBody.Part photo,
            @Part("event_type") RequestBody eventType,
            @Part("employee_id") RequestBody employeeId,
            @Part("direction") RequestBody direction
    );

    @GET("rest/v1/notifications?select=*")
    Call<List<NotificationData>> getNotifications();

    @PATCH("rest/v1/notifications")
    Call<Void> readNotification(@Query("id") String id, @Body RequestBody body);
}
