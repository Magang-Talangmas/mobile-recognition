package com.example.javatraining.ui.main.history;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.javatraining.data.local.AttendanceEntity;
import com.example.javatraining.data.repository.AbsensioRepository;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private AbsensioRepository repository;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new AbsensioRepository(application);
    }

    public LiveData<List<AttendanceEntity>> getHistory() {
        return repository.getAttendanceHistory();
    }
}
