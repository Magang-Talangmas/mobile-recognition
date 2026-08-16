package com.example.javatraining.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.javatraining.data.model.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "AbsensioSession";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_BACKEND_TOKEN = "backend_jwt_token";
    private static final String KEY_USER = "user_data";
    private static final String KEY_REMEMBER_ME = "is_remember_me";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    public void saveSession(String token, User user) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    public void setRememberMe(boolean isRememberMe) {
        editor.putBoolean(KEY_REMEMBER_ME, isRememberMe);
        editor.apply();
    }

    public boolean isRememberMe() {
        return prefs.getBoolean(KEY_REMEMBER_ME, false);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveBackendToken(String token) {
        editor.putString(KEY_BACKEND_TOKEN, token);
        editor.apply();
    }

    public String getBackendToken() {
        return prefs.getString(KEY_BACKEND_TOKEN, null);
    }

    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}
