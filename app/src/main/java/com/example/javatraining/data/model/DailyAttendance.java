package com.example.javatraining.data.model;

import java.util.Date;

public class DailyAttendance {
    private Date date;
    private AttendanceEvent checkInEvent;
    private AttendanceEvent checkOutEvent;
    private com.example.javatraining.data.remote.response.LeaveData leaveData;

    public DailyAttendance(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AttendanceEvent getCheckInEvent() {
        return checkInEvent;
    }

    public void setCheckInEvent(AttendanceEvent checkInEvent) {
        this.checkInEvent = checkInEvent;
    }

    public AttendanceEvent getCheckOutEvent() {
        return checkOutEvent;
    }

    public void setCheckOutEvent(AttendanceEvent checkOutEvent) {
        this.checkOutEvent = checkOutEvent;
    }

    public com.example.javatraining.data.remote.response.LeaveData getLeaveData() {
        return leaveData;
    }

    public void setLeaveData(com.example.javatraining.data.remote.response.LeaveData leaveData) {
        this.leaveData = leaveData;
    }
}
