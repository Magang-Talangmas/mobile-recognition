package com.example.absensitm.data.model;

public class AttendanceRecord {
    private String id;
    private String date;
    private String timeIn;
    private String timeOut;
    private String status; // e.g. "Hadir", "Terlambat", "Izin"

    public AttendanceRecord(String id, String date, String timeIn, String timeOut, String status) {
        this.id = id;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getStatus() {
        return status;
    }
}
