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
    @POST("mobile/auth/login")
    Call<BaseResponse<LoginData>> login(@Body LoginRequest request);

    @GET("mobile/profile")
    Call<BaseResponse<EmployeeData>> getProfile();

    @GET("mobile/attendance/history")
    Call<PaginatedResponse<AttendanceData>> getAttendances(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("mobile/schedule/today")
    Call<BaseResponse<ScheduleData>> getScheduleToday();

    @PATCH("mobile/device-token")
    Call<BaseResponse<EmployeeData>> updateFcmToken(
            @Body FcmTokenRequest request
    );

    @Multipart
    @POST("mobile/attendance")
    Call<BaseResponse<AttendanceData>> submitManualAttendance(
            @Part MultipartBody.Part photo,
            @Part("eventType") RequestBody eventType,
            @Part("location") RequestBody location,
            @Part("reason") RequestBody reason,
            @Part("status") RequestBody status,
            @Part("time") RequestBody time
    );

    @GET("mobile/notifications")
    Call<BaseResponse<List<NotificationData>>> getNotifications();

    @PATCH("mobile/notifications/{id}/read")
    Call<BaseResponse<NotificationData>> readNotification(@Path("id") String id);
}
