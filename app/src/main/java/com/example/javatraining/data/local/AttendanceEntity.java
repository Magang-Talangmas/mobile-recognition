package com.example.javatraining.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendance_history")
public class AttendanceEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String date; // Format: YYYY-MM-DD
    public String checkInTime; // Format: HH:MM:SS
    public String checkOutTime; // Format: HH:MM:SS
    public String status; // Present, Late, Absent, Leave
    public String snapshotUrl;
    
    public AttendanceEntity(String date, String checkInTime, String checkOutTime, String status, String snapshotUrl) {
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
        this.snapshotUrl = snapshotUrl;
    }
}
