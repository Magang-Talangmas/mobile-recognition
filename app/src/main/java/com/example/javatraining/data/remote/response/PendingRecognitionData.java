package com.example.javatraining.data.remote.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PendingRecognitionData {
    @SerializedName("items")
    private List<RecognitionEventData> items;
    
    @SerializedName("total")
    private int total;
    
    public List<RecognitionEventData> getItems() {
        return items;
    }
}
