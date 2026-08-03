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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Mock Repository for MVP
public class AbsensioRepository {
    private AttendanceDao attendanceDao;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    public AbsensioRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        attendanceDao = db.attendanceDao();
        executorService = Executors.newFixedThreadPool(4);
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<User> login(String email, String password) {
        MutableLiveData<User> result = new MutableLiveData<>();
        // Mock API call delay
        executorService.execute(() -> {
            try {
                Thread.sleep(1500); // Simulate network latency
                User mockUser = new User("EMP-001", "John Doe", email, "Staff", "Morning (08:00 - 17:00)", "");
                mainThreadHandler.post(() -> result.setValue(mockUser));
                
                // Mock initializing local data after login
                initMockAttendanceData();
            } catch (InterruptedException e) {
                e.printStackTrace();
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
}
