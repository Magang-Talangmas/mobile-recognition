package com.example.javatraining.data.repository;

import com.example.javatraining.data.model.Karyawan;
import com.example.javatraining.data.model.LogType;
import com.example.javatraining.data.model.Presensi;
import com.example.javatraining.data.model.StatusTracking;
import com.example.javatraining.data.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MockDatabase {
    private static MockDatabase instance;

    private List<User> users;
    private List<Karyawan> karyawans;
    private List<Presensi> presensis;
    
    private User currentUser;

    private MockDatabase() {
        users = new ArrayList<>();
        karyawans = new ArrayList<>();
        presensis = new ArrayList<>();
        
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
        karyawans.add(new Karyawan("k1", "u2", "Sarah Jenkins", "Lead Designer", "enc1", StatusTracking.TRACKING_RUNNING));
        karyawans.add(new Karyawan("k2", "u3", "Marcus Chen", "Senior Engineer", "enc2", StatusTracking.TRACKING_RUNNING));
        karyawans.add(new Karyawan("k3", "u4", "Elena Rodriguez", "Product Manager", "enc3", StatusTracking.BREAK_DI_AREA));
        karyawans.add(new Karyawan("k4", "u5", "David Kim", "Data Analyst", "enc4", StatusTracking.TRACKING_PAUSE));
        karyawans.add(new Karyawan("k5", "u6", "James Wilson", "Marketing Specialist", "enc5", StatusTracking.BREAK));
        // Initial history: populate with varied logs
        long now = System.currentTimeMillis();
        long oneHourAgo = now - 3600000;
        long twoHoursAgo = now - 7200000;
        long threeHoursAgo = now - 10800000;
        long fourHoursAgo = now - 14400000;
        
        // Sarah Check In
        presensis.add(new Presensi(UUID.randomUUID().toString(), "k1", new Date(), new Date(fourHoursAgo), null, new Date(fourHoursAgo), LogType.CHECK_IN, null));
        // Marcus Check In
        presensis.add(new Presensi(UUID.randomUUID().toString(), "k2", new Date(), new Date(threeHoursAgo), null, new Date(threeHoursAgo), LogType.CHECK_IN, null));
        // Elena Face Detected
        presensis.add(new Presensi(UUID.randomUUID().toString(), "k3", new Date(), null, null, new Date(twoHoursAgo), LogType.FACE_DETECTED, null));
        // Unknown Person Detected
        presensis.add(new Presensi(UUID.randomUUID().toString(), null, new Date(), null, null, new Date(oneHourAgo), LogType.UNKNOWN_DETECTED, "snapshot_unknown_01.jpg"));
        // James Tracking Running
        presensis.add(new Presensi(UUID.randomUUID().toString(), "k5", new Date(), new Date(twoHoursAgo), null, new Date(twoHoursAgo), LogType.TRACKING_RUNNING, null));
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

    public List<Presensi> getAttendanceHistory() {
        return presensis;
    }

    public boolean isCheckedIn(String karyawanId) {
        for (Presensi p : presensis) {
            if (p.getKaryawanId() != null && p.getKaryawanId().equals(karyawanId) && p.getCheckOutTime() == null) {
                return true;
            }
        }
        return false;
    }

    public void checkIn(String karyawanId) {
        if (!isCheckedIn(karyawanId)) {
            Presensi p = new Presensi(UUID.randomUUID().toString(), karyawanId, new Date(), new Date(), null, new Date(), LogType.CHECK_IN, null);
            presensis.add(0, p); // add at top
            
            // Update tracking status
            updateKaryawanStatus(karyawanId, StatusTracking.TRACKING_RUNNING);
        }
    }

    public void checkOut(String karyawanId) {
        for (Presensi p : presensis) {
            if (p.getKaryawanId() != null && p.getKaryawanId().equals(karyawanId) && p.getCheckOutTime() == null) {
                p.setCheckOutTime(new Date());
                p.setTipeLog(LogType.CHECK_OUT);
                break;
            }
        }
        updateKaryawanStatus(karyawanId, StatusTracking.TRACKING_PAUSE);
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
        Presensi p = new Presensi(UUID.randomUUID().toString(), null, new Date(), null, null, new Date(), LogType.UNKNOWN_DETECTED, "dummy_url");
        presensis.add(0, p);
    }
}
