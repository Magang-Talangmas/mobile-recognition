package com.example.absensitm.data.model;

public class ConfirmRequest {
    private String status;

    public ConfirmRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
