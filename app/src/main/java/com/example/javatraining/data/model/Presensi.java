package com.example.javatraining.data.model;

import java.util.Date;

public class Presensi {
    private String id;
    private String karyawanId;
    private Date tanggal;
    private Date checkInTime;
    private Date checkOutTime;
    private Date waktuTerdeteksi;
    private LogType tipeLog;
    private String fotoSnapshot;

    public Presensi(String id, String karyawanId, Date tanggal, Date checkInTime, Date checkOutTime, Date waktuTerdeteksi, LogType tipeLog, String fotoSnapshot) {
        this.id = id;
        this.karyawanId = karyawanId;
        this.tanggal = tanggal;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.waktuTerdeteksi = waktuTerdeteksi;
        this.tipeLog = tipeLog;
        this.fotoSnapshot = fotoSnapshot;
    }

    public String getId() { return id; }
    public String getKaryawanId() { return karyawanId; }
    public Date getTanggal() { return tanggal; }
    public Date getCheckInTime() { return checkInTime; }
    public Date getCheckOutTime() { return checkOutTime; }
    public Date getWaktuTerdeteksi() { return waktuTerdeteksi; }
    public LogType getTipeLog() { return tipeLog; }
    public String getFotoSnapshot() { return fotoSnapshot; }
    
    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    
    public void setTipeLog(LogType tipeLog) {
        this.tipeLog = tipeLog;
    }
}
