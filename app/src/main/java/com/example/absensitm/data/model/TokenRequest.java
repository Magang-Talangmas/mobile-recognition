package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("fcmToken")
    private String fcmToken;

    public TokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
