package com.example.javatraining.data.remote.response;

import com.example.javatraining.data.model.User;
import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("token")
    private String token;

    @SerializedName("employee")
    private User employee;

    public String getToken() {
        return token;
    }

    public User getEmployee() {
        return employee;
    }
}
