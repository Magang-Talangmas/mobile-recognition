package com.example.absensitm.data.model;

public class StatusResponse {
    private boolean success;
    private String message;
    private Data data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        private String status;
        private String checkInTime;
        private boolean isCheckedOut;
        private String pendingAttendanceId;

        public String getStatus() { return status; }
        public String getCheckInTime() { return checkInTime; }
        public boolean isCheckedOut() { return isCheckedOut; }
        public String getPendingAttendanceId() { return pendingAttendanceId; }
    }
}
