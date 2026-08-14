package com.example.javatraining.data.remote.response;

import com.example.javatraining.data.model.User;
import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName(value = "token", alternate = {"access_token"})
    private String token;

    @SerializedName(value = "employee", alternate = {"user"})
    private User employee;

    public String getToken() {
        return token;
    }

    public User getEmployee() {
        return employee;
    }
}
