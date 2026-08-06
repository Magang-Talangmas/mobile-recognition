package com.example.javatraining.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.javatraining.data.local.AppDatabase;
import com.example.javatraining.data.local.AttendanceEntity;
import com.example.javatraining.data.local.dao.AttendanceDao;
import com.example.javatraining.data.model.User;
import com.example.javatraining.data.remote.ApiClient;
import com.example.javatraining.data.remote.ApiService;
import com.example.javatraining.data.remote.request.LoginRequest;
import com.example.javatraining.data.local.SessionManager;

import com.example.javatraining.data.remote.response.AttendanceData;
import com.example.javatraining.data.remote.response.EmployeeData;
import com.example.javatraining.data.remote.response.PaginatedResponse;
import com.example.javatraining.data.remote.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Mock Repository for MVP
public class AbsensiTMRepository {
    private AttendanceDao attendanceDao;
    private ExecutorService executorService;
    private Handler mainThreadHandler;
    private Application application;

    public AbsensiTMRepository(Application application) {
        this.application = application;
        AppDatabase db = AppDatabase.getDatabase(application);
        attendanceDao = db.attendanceDao();
        executorService = Executors.newFixedThreadPool(4);
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<User> login(String email, String password) {
        MutableLiveData<User> result = new MutableLiveData<>();
        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);
        
        apiService.login(request).enqueue(new retrofit2.Callback<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.LoginData>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.LoginData>> call, retrofit2.Response<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.LoginData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    com.example.javatraining.data.remote.response.LoginData data = response.body().getData();
                    
                    SessionManager sessionManager = new SessionManager(application);
                    sessionManager.saveSession(data.getToken(), data.getEmployee());
                    
                    result.setValue(data.getEmployee());
                    
                    initMockAttendanceData();
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.LoginData>> call, Throwable t) {
                result.setValue(null);
            }
        });
        
        return result;
    }

    public LiveData<List<AttendanceEntity>> getAttendanceHistory() {
        return attendanceDao.getAllAttendance();
    }
    
    public LiveData<AttendanceEntity> getTodayAttendance(String todayDate) {
        return attendanceDao.getAttendanceByDate(todayDate);
    }
    
    public void performCheckIn(String source) {
        executorService.execute(() -> {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
            String today = dateFormat.format(new java.util.Date());
            String now = timeFormat.format(new java.util.Date());
            
            AttendanceEntity existing = attendanceDao.getAttendanceByDateSync(today);
            if (existing != null) {
                // If already checked in, assume this is a check out
                existing.checkOutTime = now;
                // If it was a mock, we update it
                attendanceDao.insert(existing);
            } else {
                // Check in
                AttendanceEntity newRecord = new AttendanceEntity(today, now, "--:--:--", "Hadir (" + source + ")", "");
                attendanceDao.insert(newRecord);
            }
        });
    }

    private void initMockAttendanceData() {
        List<AttendanceEntity> mocks = new ArrayList<>();
        mocks.add(new AttendanceEntity("2026-08-01", "07:55:00", "17:05:00", "Hadir", ""));
        mocks.add(new AttendanceEntity("2026-08-02", "08:15:00", "17:00:00", "Terlambat", ""));
        mocks.add(new AttendanceEntity("2026-08-03", "07:50:00", "--:--:--", "Hadir", "")); // Today, not checked out
        
        executorService.execute(() -> {
            attendanceDao.clearAll();
            attendanceDao.insertAll(mocks);
        });
    }


    public LiveData<List<EmployeeData>> getEmployeesApi(int page, int perPage) {
        MutableLiveData<List<EmployeeData>> result = new MutableLiveData<>();
        // Mock empty list since the API endpoint was removed in the mobile contract
        result.setValue(new ArrayList<>());
        return result;
    }

    public LiveData<List<AttendanceData>> getAttendancesApi(int page, int perPage) {
        MutableLiveData<List<AttendanceData>> result = new MutableLiveData<>();
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getAttendances(page, perPage).enqueue(new retrofit2.Callback<BaseResponse<PaginatedResponse<AttendanceData>>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<PaginatedResponse<AttendanceData>>> call, retrofit2.Response<BaseResponse<PaginatedResponse<AttendanceData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(response.body().getData().getItems());
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<BaseResponse<PaginatedResponse<AttendanceData>>> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }
}
