package com.example.javatraining.data.remote.request;

import com.google.gson.annotations.SerializedName;

public class FcmTokenRequest {
    @SerializedName("fcmToken")
    private String fcmToken;

    public FcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
