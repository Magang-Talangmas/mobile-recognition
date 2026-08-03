package com.example.javatraining.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.javatraining.data.local.AttendanceEntity;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Query("SELECT * FROM attendance_history ORDER BY date DESC")
    LiveData<List<AttendanceEntity>> getAllAttendance();

    @Query("SELECT * FROM attendance_history WHERE date = :targetDate LIMIT 1")
    LiveData<AttendanceEntity> getAttendanceByDate(String targetDate);

    @Query("SELECT * FROM attendance_history WHERE date = :targetDate LIMIT 1")
    AttendanceEntity getAttendanceByDateSync(String targetDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AttendanceEntity attendance);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AttendanceEntity> attendanceList);
    
    @Query("DELETE FROM attendance_history")
    void clearAll();
}
