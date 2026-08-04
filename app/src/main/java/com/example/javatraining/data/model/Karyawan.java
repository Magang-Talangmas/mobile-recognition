package com.example.javatraining.data.model;

public class Karyawan {
    private String id;
    private String userId;
    private String namaLengkap;
    private String jabatan;
    private String faceEncoding; // mock string for now
    private StatusTracking statusTracking;

    public Karyawan(String id, String userId, String namaLengkap, String jabatan, String faceEncoding, StatusTracking statusTracking) {
        this.id = id;
        this.userId = userId;
        this.namaLengkap = namaLengkap;
        this.jabatan = jabatan;
        this.faceEncoding = faceEncoding;
        this.statusTracking = statusTracking;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getJabatan() { return jabatan; }
    public String getFaceEncoding() { return faceEncoding; }
    public StatusTracking getStatusTracking() { return statusTracking; }
    
    public void setStatusTracking(StatusTracking statusTracking) {
        this.statusTracking = statusTracking;
    }
}
