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

    @GET("rest/v1/attendances?select=*,employees(*)&order=timestamp.desc")
    Call<List<AttendanceData>> getAttendances(
            @Query("employeeId") String employeeId,
            @Query("limit") Integer limit
    );

    @GET("rest/v1/attendance_permissions?select=*&order=createdAt.desc")
    Call<List<com.example.javatraining.data.remote.response.LeaveData>> getLeaveRequests(
            @Query("employeeId") String employeeId,
            @Query("limit") Integer limit
    );

    @GET("rest/v1/work_schedules?select=*")
    Call<List<ScheduleData>> getScheduleToday();

    @PATCH("rest/v1/employees")
    Call<Void> updateFcmToken(
            @Query("email") String email,
            @Body FcmTokenRequest request
    );

    @POST("rest/v1/attendances")
    Call<Void> submitManualAttendance(@Body com.example.javatraining.data.remote.request.ManualAttendanceRequest request);

    @POST("rest/v1/attendance_permissions")
    Call<Void> submitLeaveRequest(@Body com.example.javatraining.data.remote.request.LeaveRequest request);

    @POST("storage/v1/object/{bucket}/{path}")
    Call<okhttp3.ResponseBody> uploadStorageObject(
            @Path("bucket") String bucket,
            @Path(value = "path", encoded = true) String path,
            @Body okhttp3.RequestBody imageBody
    );

    @GET("rest/v1/notifications?select=*&order=createdAt.desc")
    Call<List<NotificationData>> getNotifications(@Query("employeeId") String employeeIdEq);

    @PATCH("rest/v1/notifications")
    Call<Void> readNotification(@Query("id") String id, @Body RequestBody body);
}
