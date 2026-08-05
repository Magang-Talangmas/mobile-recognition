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
        if (apiService == null) return;
        
        isLoading.setValue(true);
        apiService.getHistory(1, 20).enqueue(new retrofit2.Callback<com.example.absensitm.data.model.HistoryResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.absensitm.data.model.HistoryResponse> call, retrofit2.Response<com.example.absensitm.data.model.HistoryResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<com.example.absensitm.data.model.HistoryResponse.AttendanceRecord> rawData = response.body().getData();
                    List<AttendanceRecord> uiList = new ArrayList<>();
                    
                    if (rawData != null) {
                        for (com.example.absensitm.data.model.HistoryResponse.AttendanceRecord raw : rawData) {
                            String dateStr = raw.getTimestamp(); // Ideally format this ISO string to "Senin, 01 Ags"
                            String timeStr = raw.getTimestamp(); // format this to "HH:mm"
                            
                            try {
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                                java.util.Date date = sdf.parse(raw.getTimestamp());
                                
                                java.text.SimpleDateFormat dateOut = new java.text.SimpleDateFormat("EEEE, dd MMM yyyy", new java.util.Locale("id", "ID"));
                                java.text.SimpleDateFormat timeOut = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                                
                                dateStr = dateOut.format(date);
                                timeStr = timeOut.format(date);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String status = "Hadir";
                            if (Boolean.TRUE.equals(raw.getIsLate())) {
                                status = "Terlambat";
                            } else if (raw.getEventType() != null) {
                                status = raw.getEventType(); 
                            }
                            
                            uiList.add(new AttendanceRecord(
                                raw.getId(),
                                dateStr,
                                timeStr,
                                "-",
                                status
                            ));
                        }
                    }
                    historyList.setValue(uiList);
                } else {
                    // Handle error if needed
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.absensitm.data.model.HistoryResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }
}
