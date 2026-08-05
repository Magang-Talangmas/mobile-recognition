package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryResponse extends BaseResponse {
    @SerializedName("data")
    private List<AttendanceRecord> data;

    public List<AttendanceRecord> getData() { return data; }

    public static class AttendanceRecord {
        @SerializedName("id")
        private String id;
        @SerializedName("timestamp")
        private String timestamp;
        @SerializedName("eventType")
        private String eventType;
        @SerializedName("status")
        private String status;
        @SerializedName("imageUrl")
        private String imageUrl;
        @SerializedName("isLate")
        private Boolean isLate;

        public String getId() { return id; }
        public String getTimestamp() { return timestamp; }
        public String getEventType() { return eventType; }
        public String getStatus() { return status; }
        public String getImageUrl() { return imageUrl; }
        public Boolean getIsLate() { return isLate; }
    }
}
