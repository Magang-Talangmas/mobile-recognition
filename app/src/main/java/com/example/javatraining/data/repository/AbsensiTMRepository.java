package com.example.javatraining.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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
                    
                    User rawUser = data.getEmployee();
                    String email = rawUser.getEmail();
                    
                    // SAVE TEMPORARY SESSION WITH TOKEN SO GETPROFILE CAN BE AUTHENTICATED
                    SessionManager sessionManager = new SessionManager(application);
                    sessionManager.saveSession(data.getToken(), rawUser);
                    
                    // Fetch full profile from employees table to get the true employeeId and name
                    apiService.getProfile("eq." + email).enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.EmployeeData>>() {
                        @Override
                        public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.EmployeeData>> profileCall, retrofit2.Response<List<com.example.javatraining.data.remote.response.EmployeeData>> profileResponse) {
                            User realUser;
                            if (profileResponse.isSuccessful() && profileResponse.body() != null && !profileResponse.body().isEmpty()) {
                                com.example.javatraining.data.remote.response.EmployeeData profile = profileResponse.body().get(0);
                                String finalId = profile.getEmployeeId() != null ? profile.getEmployeeId() : rawUser.getId();
                                realUser = new User(finalId, profile.getName(), email, "EMPLOYEE", profile.getEmployeeId(), profile.getDepartment(), profile.getPosition());
                            } else {
                                String finalId = rawUser.getEmployeeId() != null ? rawUser.getEmployeeId() : rawUser.getId();
                                realUser = new User(finalId, rawUser.getName(), email, "EMPLOYEE", rawUser.getEmployeeId(), rawUser.getDepartment(), rawUser.getPosition());
                            }
                            
                            sessionManager.saveSession(data.getToken(), realUser);
                            result.setValue(realUser);
                        }

                        @Override
                        public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.EmployeeData>> profileCall, Throwable t) {
                            String finalId = rawUser.getEmployeeId() != null ? rawUser.getEmployeeId() : rawUser.getId();
                            User realUser = new User(finalId, rawUser.getName(), email, "EMPLOYEE", rawUser.getEmployeeId(), rawUser.getDepartment(), rawUser.getPosition());
                            
                            sessionManager.saveSession(data.getToken(), realUser);
                            result.setValue(realUser);
                        }
                    });
                } else {
                    try {
                        String errBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        android.util.Log.e("LOGIN_ERROR", "Code: " + response.code() + ", Body: " + errBody);
                        
                        String displayMsg = "Login Failed: " + response.code();
                        mainThreadHandler.post(() -> android.widget.Toast.makeText(application, displayMsg, android.widget.Toast.LENGTH_LONG).show());
                    } catch (Exception e) {}
                    result.setValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.javatraining.data.remote.response.LoginData> call, Throwable t) {
                android.util.Log.e("LOGIN_ERROR", "Failure: " + t.getMessage());
                mainThreadHandler.post(() -> android.widget.Toast.makeText(application, "Connection error: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show());
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
        String query = employeeId != null ? "eq." + employeeId : null;
        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getAttendances(query, 50).enqueue(new retrofit2.Callback<List<AttendanceData>>() {
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

    public LiveData<List<com.example.javatraining.data.remote.response.LeaveData>> getLeavesApi(int page, int perPage) {
        MutableLiveData<List<com.example.javatraining.data.remote.response.LeaveData>> result = new MutableLiveData<>();
        result.setValue(new ArrayList<>());
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
        
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : null;
        String filter = (employeeId != null && !employeeId.trim().isEmpty()) ? "eq." + employeeId : null;

        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getNotifications(filter).enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.NotificationData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.NotificationData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.NotificationData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.javatraining.data.remote.response.NotificationData> notifs = response.body();
                    
                    java.util.List<String> recognitionIds = new java.util.ArrayList<>();
                    for (com.example.javatraining.data.remote.response.NotificationData notif : notifs) {
                        if (notif.getRecognitionId() != null && !notif.getRecognitionId().trim().isEmpty()) {
                            recognitionIds.add(notif.getRecognitionId());
                        }
                    }

                    if (recognitionIds.isEmpty()) {
                        result.setValue(notifs);
                    } else {
                        String idIn = "in.(" + android.text.TextUtils.join(",", recognitionIds) + ")";
                        apiService.getRecognitionEvents(idIn).enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.RecognitionEventData>>() {
                            @Override
                            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.RecognitionEventData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.RecognitionEventData>> thumbnailResponse) {
                                if (thumbnailResponse.isSuccessful() && thumbnailResponse.body() != null) {
                                    java.util.Map<String, String> thumbnails = new java.util.HashMap<>();
                                    for (com.example.javatraining.data.remote.response.RecognitionEventData event : thumbnailResponse.body()) {
                                        thumbnails.put(event.getId(), event.getThumbnail());
                                    }
                                    for (com.example.javatraining.data.remote.response.NotificationData notif : notifs) {
                                        if (notif.getRecognitionId() != null && thumbnails.containsKey(notif.getRecognitionId())) {
                                            notif.setImageUrl(thumbnails.get(notif.getRecognitionId()));
                                        }
                                    }
                                }
                                result.setValue(notifs);
                            }

                            @Override
                            public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.RecognitionEventData>> call, Throwable t) {
                                result.setValue(notifs);
                            }
                        });
                    }
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

    public void markNotificationAsRead(String notifId) {
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        String json = "{\"isRead\": true}";
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), json);
        apiService.readNotification("eq." + notifId, body).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
            }
        });
    }

    public LiveData<List<com.example.javatraining.data.remote.response.RecognitionEventData>> getPendingRecognitions() {
        MutableLiveData<List<com.example.javatraining.data.remote.response.RecognitionEventData>> result = new MutableLiveData<>();
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : null;
        if (employeeId == null) {
            result.setValue(new ArrayList<>());
            return result;
        }

        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.getPendingRecognitions("eq." + employeeId, "eq.Unknown").enqueue(new retrofit2.Callback<List<com.example.javatraining.data.remote.response.RecognitionEventData>>() {
            @Override
            public void onResponse(retrofit2.Call<List<com.example.javatraining.data.remote.response.RecognitionEventData>> call, retrofit2.Response<List<com.example.javatraining.data.remote.response.RecognitionEventData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    result.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<com.example.javatraining.data.remote.response.RecognitionEventData>> call, Throwable t) {
                result.setValue(new ArrayList<>());
            }
        });
        
        return result;
    }

    public void confirmRecognition(com.example.javatraining.data.remote.response.RecognitionEventData event, Runnable onSuccess) {
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        String json = "{\"status\": \"Verified\"}";
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), json);
        apiService.updateRecognitionStatus("eq." + event.getId(), body).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    if (onSuccess != null) mainThreadHandler.post(onSuccess);
                } else {
                    mainThreadHandler.post(() -> android.widget.Toast.makeText(application, "Gagal konfirmasi: " + response.code(), android.widget.Toast.LENGTH_LONG).show());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                mainThreadHandler.post(() -> android.widget.Toast.makeText(application, "Koneksi gagal: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show());
            }
        });
    }

    public void rejectRecognition(String recognitionId, Runnable onSuccess) {
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        String json = "{\"status\": \"Rejected\"}";
        okhttp3.RequestBody body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"), json);
        apiService.updateRecognitionStatus("eq." + recognitionId, body).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    if (onSuccess != null) mainThreadHandler.post(onSuccess);
                } else {
                    mainThreadHandler.post(() -> android.widget.Toast.makeText(application, "Gagal menolak: " + response.code(), android.widget.Toast.LENGTH_LONG).show());
                }
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                mainThreadHandler.post(() -> android.widget.Toast.makeText(application, "Koneksi gagal: " + t.getMessage(), android.widget.Toast.LENGTH_LONG).show());
            }
        });
    }

    public void deleteAllNotifications(Runnable onSuccess) {
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : null;
        if (employeeId == null) return;
        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.deleteAllNotifications("eq." + employeeId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (onSuccess != null) mainThreadHandler.post(onSuccess);
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
        });
    }

    public LiveData<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData>> submitManualAttendance(String eventType) {
        MutableLiveData<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData>> result = new MutableLiveData<>();
        
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : "unknown";
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).format(new java.util.Date());
        String eventTypeDb = eventType;
        if ("CHECK_IN".equals(eventTypeDb)) eventTypeDb = "IN";
        else if ("CHECK_OUT".equals(eventTypeDb)) eventTypeDb = "OUT";
        
        java.util.Map<String, Object> request = new java.util.HashMap<>();
        String newId = java.util.UUID.randomUUID().toString();
        request.put("id", newId);
        request.put("employeeId", employeeId);
        
        String evtType = eventTypeDb;
        if ("IN".equalsIgnoreCase(eventTypeDb)) evtType = "CHECK_IN";
        else if ("OUT".equalsIgnoreCase(eventTypeDb)) evtType = "CHECK_OUT";
        request.put("eventType", evtType);
        
        request.put("cameraId", "MANUAL");
        request.put("timestamp", timestamp);
        String statusValue = "UNKNOWN";
        if ("IN".equals(eventTypeDb)) statusValue = "CHECKED_IN";
        else if ("OUT".equals(eventTypeDb)) statusValue = "CHECKED_OUT";
        request.put("status", statusValue);
        request.put("confirmationStatus", "CONFIRMED");
        request.put("createdAt", timestamp);
        request.put("updatedAt", timestamp);
                        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.submitManualAttendance(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    String mockJson = "{\"success\":true,\"message\":\"Attendance successful\",\"data\":{\"id\":\"" + newId + "\",\"status\":\"PRESENT\",\"timestamp\":\"" + timestamp + "\"}}";
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData>>(){}.getType();
                    com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData> mockResponse = new com.google.gson.Gson().fromJson(mockJson, type);
                    result.postValue(mockResponse);
                } else {
                    try {
                        android.util.Log.e("MANUAL_ATT", "Failed submit: " + response.code() + ", body: " + response.errorBody().string());
                    } catch (Exception e) {}
                    result.postValue(null);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                android.util.Log.e("MANUAL_ATT", "Error submit: " + t.getMessage());
                result.postValue(null);
            }
        });
        
        return result;
    }

    public void deleteOldNotifications() {
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : null;
        if (employeeId == null) return;
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_YEAR, -7);
        String dateThreshold = "lt." + sdf.format(cal.getTime());
        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.deleteOldNotifications("eq." + employeeId, dateThreshold).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
        });
    }

    public LiveData<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData>> submitLivenessAttendance(java.io.File photoFile, String eventType) {
        MutableLiveData<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData>> result = new MutableLiveData<>();
        
        SessionManager sm = new SessionManager(application);
        String employeeId = sm.getUser() != null ? sm.getUser().getId() : "unknown";
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).format(new java.util.Date());
        String eventTypeDb = eventType;
        if ("CHECK_IN".equals(eventTypeDb)) eventTypeDb = "IN";
        else if ("CHECK_OUT".equals(eventTypeDb)) eventTypeDb = "OUT";
        
        java.util.Map<String, Object> request = new java.util.HashMap<>();
        String newId = java.util.UUID.randomUUID().toString();
        request.put("id", newId);
        request.put("employeeId", employeeId);
        request.put("eventType", eventTypeDb);
        request.put("cameraId", "LIVENESS");
        request.put("timestamp", timestamp);
        String statusValue = "UNKNOWN";
        if ("IN".equals(eventTypeDb)) statusValue = "CHECKED_IN";
        else if ("OUT".equals(eventTypeDb)) statusValue = "CHECKED_OUT";
        request.put("status", statusValue);
        request.put("confirmationStatus", "CONFIRMED");
        request.put("createdAt", timestamp);
        request.put("updatedAt", timestamp);
                        
        ApiService apiService = ApiClient.getClient(application).create(ApiService.class);
        apiService.submitManualAttendance(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    String mockJson = "{\"success\":true,\"message\":\"Attendance successful\",\"data\":{\"id\":\"" + newId + "\",\"status\":\"PRESENT\",\"timestamp\":\"" + timestamp + "\"}}";
                    java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData>>(){}.getType();
                    com.example.javatraining.data.remote.response.BaseResponse<com.example.javatraining.data.remote.response.AttendanceData> mockResponse = new com.google.gson.Gson().fromJson(mockJson, type);
                    result.postValue(mockResponse);
                } else {
                    try {
                        android.util.Log.e("LIVENESS_ATT", "Failed submit: " + response.code() + ", body: " + response.errorBody().string());
                    } catch (Exception e) {}
                    result.postValue(null);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                android.util.Log.e("LIVENESS_ATT", "Error submit: " + t.getMessage());
                result.postValue(null);
            }
        });
        
        return result;
    }
}
