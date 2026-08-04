package com.example.javatraining.data.repository;

import com.example.javatraining.data.model.AttendanceEvent;
import com.example.javatraining.data.model.EventDirection;
import com.example.javatraining.data.model.EventStatus;
import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.StatusTracking;
import com.example.javatraining.data.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MockDatabase {
    private static MockDatabase instance;

    private List<User> users;
    private List<Karyawan> karyawans;
    private List<AttendanceEvent> events;
    
    private User currentUser;
    private AtomicLong eventIdCounter = new AtomicLong(1);

    private MockDatabase() {
        users = new ArrayList<>();
        karyawans = new ArrayList<>();
        events = new ArrayList<>();
        
        initDummyData();
    }

    public static synchronized MockDatabase getInstance() {
        if (instance == null) {
            instance = new MockDatabase();
        }
        return instance;
    }

    private void initDummyData() {
        // Users
        User admin = new User("u1", "admin", "admin123", "ADMIN");
        User emp1 = new User("u2", "sarah", "sarah123", "EMPLOYEE");
        User emp2 = new User("u3", "marcus", "marcus123", "EMPLOYEE");
        User emp3 = new User("u4", "elena", "elena123", "EMPLOYEE");
        User emp4 = new User("u5", "david", "david123", "EMPLOYEE");
        User emp5 = new User("u6", "james", "james123", "EMPLOYEE");
        
        users.add(admin);
        users.add(emp1);
        users.add(emp2);
        users.add(emp3);
        users.add(emp4);
        users.add(emp5);

        // Current User (simulate login)
        currentUser = emp1;

        // Karyawan
        karyawans.add(new Karyawan("E001", "u2", "Sarah Jenkins", "Lead Designer", "enc1", StatusTracking.TRACKING_RUNNING));
        karyawans.add(new Karyawan("E002", "u3", "Marcus Chen", "Senior Engineer", "enc2", StatusTracking.TRACKING_RUNNING));
        karyawans.add(new Karyawan("E003", "u4", "Elena Rodriguez", "Product Manager", "enc3", StatusTracking.BREAK_DI_AREA));
        karyawans.add(new Karyawan("E004", "u5", "David Kim", "Data Analyst", "enc4", StatusTracking.TRACKING_PAUSE));
        karyawans.add(new Karyawan("E005", "u6", "James Wilson", "Marketing Specialist", "enc5", StatusTracking.BREAK));
        
        // Initial history: populate with varied events based on backend schema
        long now = System.currentTimeMillis();
        long oneDayAgo = now - 86400000L;
        
        long oneHourAgo = now - 3600000L;
        long twoHoursAgo = now - 7200000L;
        long threeHoursAgo = now - 10800000L;
        long fourHoursAgo = now - 14400000L;
        
        // TODAY EVENTS
        // Sarah (E001) checked in 4 hours ago
        events.add(new AttendanceEvent(eventIdCounter.getAndIncrement(), "cam_front", 101, "E001", "E001", 
            EventDirection.IN, LogType.CHECK_IN, 0.98, EventStatus.CONFIRMED, "CONFIRM", 
            new Date(fourHoursAgo), new Date(fourHoursAgo + 5000), null));

        // Marcus (E002) checked in 3 hours ago
        events.add(new AttendanceEvent(eventIdCounter.getAndIncrement(), "cam_front", 102, "E002", "E002", 
            EventDirection.IN, LogType.CHECK_IN, 0.95, EventStatus.CONFIRMED, "CONFIRM", 
            new Date(threeHoursAgo), new Date(threeHoursAgo + 5000), null));

        // Unknown Person detected 1 hour ago
        events.add(new AttendanceEvent(eventIdCounter.getAndIncrement(), "cam_front", 103, null, null, 
            EventDirection.IN, LogType.UNKNOWN_DETECTED, 0.20, EventStatus.UNRESOLVED_RECOGNITION, null, 
            new Date(oneHourAgo), null, "snapshot_unknown_01.jpg"));

        // YESTERDAY EVENTS
        // Sarah check in
        events.add(new AttendanceEvent(eventIdCounter.getAndIncrement(), "cam_front", 90, "E001", "E001", 
            EventDirection.IN, LogType.CHECK_IN, 0.97, EventStatus.CONFIRMED, "CONFIRM", 
            new Date(oneDayAgo - 14400000L), new Date(oneDayAgo - 14400000L + 5000), null));
            
        // Sarah check out
        events.add(new AttendanceEvent(eventIdCounter.getAndIncrement(), "cam_front", 91, "E001", "E001", 
            EventDirection.OUT, LogType.CHECK_OUT, 0.96, EventStatus.CONFIRMED, "CONFIRM", 
            new Date(oneDayAgo + 3600000L), new Date(oneDayAgo + 3600000L + 5000), null));
    }
    
    public User getCurrentUser() {
        return currentUser;
    }

    public Karyawan getCurrentKaryawan() {
        for (Karyawan k : karyawans) {
            if (k.getUserId().equals(currentUser.getId())) {
                return k;
            }
        }
        return null;
    }

    public List<Karyawan> getAllKaryawan() {
        return karyawans;
    }

    public List<AttendanceEvent> getAttendanceHistory() {
        return events;
    }

    // Check if the latest event today for the employee is CHECK_IN
    public boolean isCheckedIn(String karyawanId) {
        AttendanceEvent latest = getLatestEvent(karyawanId);
        if (latest != null && latest.getEventType() == LogType.CHECK_IN) {
            return true;
        }
        return false;
    }

    private AttendanceEvent getLatestEvent(String karyawanId) {
        AttendanceEvent latest = null;
        for (AttendanceEvent e : events) {
            if (karyawanId.equals(e.getEmployeeId())) {
                if (latest == null || e.getDetectedAt().after(latest.getDetectedAt())) {
                    latest = e;
                }
            }
        }
        return latest;
    }

    public void checkIn(String karyawanId) {
        if (!isCheckedIn(karyawanId)) {
            AttendanceEvent event = new AttendanceEvent(eventIdCounter.getAndIncrement(), "manual", -1, karyawanId, karyawanId,
                EventDirection.IN, LogType.CHECK_IN, 1.0, EventStatus.CONFIRMED, "CONFIRM", 
                new Date(), new Date(), null);
            events.add(0, event); // Add to top for descending order assumption (or sort later)
            updateKaryawanStatus(karyawanId, StatusTracking.TRACKING_RUNNING);
        }
    }

    public void checkOut(String karyawanId) {
        if (isCheckedIn(karyawanId)) {
            AttendanceEvent event = new AttendanceEvent(eventIdCounter.getAndIncrement(), "manual", -1, karyawanId, karyawanId,
                EventDirection.OUT, LogType.CHECK_OUT, 1.0, EventStatus.CONFIRMED, "CONFIRM", 
                new Date(), new Date(), null);
            events.add(0, event);
            updateKaryawanStatus(karyawanId, StatusTracking.TRACKING_PAUSE);
        }
    }
    
    private void updateKaryawanStatus(String karyawanId, StatusTracking status) {
        for (Karyawan k : karyawans) {
            if (k.getId().equals(karyawanId)) {
                k.setStatusTracking(status);
                break;
            }
        }
    }

    public void addUnknownLog() {
        AttendanceEvent event = new AttendanceEvent(eventIdCounter.getAndIncrement(), "manual", -1, null, null,
            EventDirection.IN, LogType.UNKNOWN_DETECTED, 0.0, EventStatus.UNRESOLVED_RECOGNITION, null, 
            new Date(), null, "dummy_url");
        events.add(0, event);
    }
}
