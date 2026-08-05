package com.example.javatraining.ui.main.history;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.javatraining.data.local.AttendanceEntity;
import com.example.javatraining.data.repository.AbsensiTMRepository;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private AbsensiTMRepository repository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new AbsensiTMRepository(application);
    }

    public LiveData<List<AttendanceEntity>> getHistory() {
        return repository.getAttendanceHistory();
    }
}
