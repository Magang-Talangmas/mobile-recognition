package com.example.absensitm.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.absensitm.data.model.AttendanceRecord;
import com.example.absensitm.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends ViewModel {

    private final MutableLiveData<List<AttendanceRecord>> historyList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    private ApiService apiService;

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<AttendanceRecord>> getHistoryList() {
        return historyList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchHistory() {
        isLoading.setValue(true);
        
        // Mocking API call
        new android.os.Handler().postDelayed(() -> {
            List<AttendanceRecord> dummyData = new ArrayList<>();
            dummyData.add(new AttendanceRecord("1", "Senin, 01 Ags 2026", "07:50", "17:10", "Hadir"));
            dummyData.add(new AttendanceRecord("2", "Selasa, 02 Ags 2026", "08:15", "17:05", "Terlambat"));
            dummyData.add(new AttendanceRecord("3", "Rabu, 03 Ags 2026", "07:45", "17:20", "Hadir"));
            dummyData.add(new AttendanceRecord("4", "Kamis, 04 Ags 2026", "07:55", "17:00", "Hadir"));
            
            historyList.setValue(dummyData);
            isLoading.setValue(false);
        }, 1000);
    }
}
