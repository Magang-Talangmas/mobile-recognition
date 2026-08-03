package com.example.javatraining.ui.main.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.javatraining.data.local.AttendanceEntity;
import com.example.javatraining.data.repository.AbsensioRepository;

public class HomeViewModel extends AndroidViewModel {
    private AbsensioRepository repository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new AbsensioRepository(application);
    }

    public LiveData<AttendanceEntity> getTodayAttendance(String date) {
        return repository.getTodayAttendance(date);
    }
    
    public void performCheckIn(String source) {
        repository.performCheckIn(source);
    }
}
