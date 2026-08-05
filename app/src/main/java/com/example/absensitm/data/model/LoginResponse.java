package com.example.absensitm.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    
    @SerializedName("token")
    private String token;

    @SerializedName("message")
    private String message;

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}
