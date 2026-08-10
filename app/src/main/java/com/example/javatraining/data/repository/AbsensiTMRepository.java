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
        
        apiService.login(request).enqueue(new retrofit2.Callback<com.example.javatraining.data.remote.response.LoginData>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.javatraining.data.remote.response.LoginData> call, retrofit2.Response<com.example.javatraining.data.remote.response.LoginData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.javatraining.data.remote.response.LoginData data = response.body();
                    
                    SessionManager sessionManager = new SessionManager(application);
                    // Temporarily save to inject token for the next request
                    sessionManager.saveSession(data.getToken(), data.getEmployee());
                    
                    apiService.getProfile("eq." + email).enqueue(new retrofit2.Callback<java.util.List<com.example.javatraining.data.remote.response.EmployeeData>>() {
                        @Override
                        public void onResponse(retrofit2.Call<java.util.List<com.example.javatraining.data.remote.response.EmployeeData>> call, retrofit2.Response<java.util.List<com.example.javatraining.data.remote.response.EmployeeData>> profileResponse) {
                            if (profileResponse.isSuccessful() && profileResponse.body() != null && !profileResponse.body().isEmpty()) {
                                com.example.javatraining.data.remote.response.EmployeeData empData = profileResponse.body().get(0);
                                String role = data.getEmployee() != null ? data.getEmployee().getRole() : "EMPLOYEE";
                                String finalId = empData.getEmployeeId() != null ? empData.getEmployeeId() : empData.getId();
                                User realUser = new User(finalId, empData.getName(), empData.getEmail(), role, "", "");
                                sessionManager.saveSession(data.getToken(), realUser);
                                result.setValue(realUser);
                            } else {
                                result.setValue(data.getEmployee());
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<java.util.List<com.example.javatraining.data.remote.response.EmployeeData>> call, Throwable t) {
                            android.util.Log.e("LOGIN_ERROR", "Failed to fetch profile: " + t.getMessage());
                            result.setValue(data.getEmployee());
                        }
                    });
                } else {
                    try {
                        String errBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        android.util.Log.e("LOGIN_ERROR", "Code: " + response.code() + ", Body: " + errBody);
                        
                        // Parse the error message if possible
                        String displayMsg = "Login Failed: " + response.code();
                        if (errBody.contains("message")) {
                            try {
                                org.json.JSONObject jObjError = new org.json.JSONObject(errBody);
                                displayMsg = jObjError.getString("message");
                            } catch (Exception e) {}
                        }
                        
                        final String finalMsg = displayMsg;
                        mainThreadHandler.post(() -> {
                            android.widget.Toast.makeText(application, finalMsg, android.widget.Toast.LENGTH_LONG).show();
                        });
                    } catch (Exception e) {}
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.javatraining.data.remote.response.LoginData> call, Throwable t) {
                android.util.Log.e("LOGIN_ERROR", "Exception: " + t.getMessage());
                mainThreadHandler.post(() -> {
                    android.widget.Toast.makeText(application, "Network Error: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                });
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

    public LiveData<Boolean> submitLeaveRequest(com.example.javatraining.data.remote.request.LeaveRequest request) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.submitLeaveRequest(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(true);
                } else {
                    result.setValue(false);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }

    public LiveData<List<AttendanceData>> getAttendancesApi(int page, int perPage) {
        MutableLiveData<List<AttendanceData>> result = new MutableLiveData<>();
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : null;
        
        String filter = (employeeId != null && !employeeId.trim().isEmpty()) ? "eq." + employeeId : null;
        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getAttendances(filter, perPage).enqueue(new retrofit2.Callback<List<AttendanceData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<AttendanceData>> call, retrofit2.Response<List<AttendanceData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<AttendanceData>> call, Throwable t) {
                result.setValue(new ArrayList<>());
            }
        });
        return result;
    }

    public LiveData<com.example.javatraining.data.remote.response.ScheduleData> getScheduleTodayApi() {
        MutableLiveData<com.example.javatraining.data.remote.response.ScheduleData> result = new MutableLiveData<>();
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getScheduleToday().enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.ScheduleData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.ScheduleData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.ScheduleData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    result.setValue(response.body().get(0));
                } else {
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.ScheduleData>> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<com.example.javatraining.data.remote.response.EmployeeData> getProfileApi() {
        MutableLiveData<com.example.javatraining.data.remote.response.EmployeeData> data = new MutableLiveData<>();
        SessionManager sm = new SessionManager(application);
        String email = sm.getUser() != null ? sm.getUser().getEmail() : "";

        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getProfile("eq." + email).enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.EmployeeData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.EmployeeData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.EmployeeData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    data.setValue(response.body().get(0));
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.EmployeeData>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<List<com.example.javatraining.data.remote.response.NotificationData>> getNotificationsApi() {
        MutableLiveData<List<com.example.javatraining.data.remote.response.NotificationData>> result = new MutableLiveData<>();
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getNotifications().enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.NotificationData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.NotificationData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.NotificationData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(new java.util.ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.NotificationData>> call, Throwable t) {
                result.setValue(new java.util.ArrayList<>());
            }
        });
        return result;
    }
}
