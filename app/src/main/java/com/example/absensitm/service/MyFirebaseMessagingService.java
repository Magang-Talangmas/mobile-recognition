package com.example.absensitm.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.absensitm.data.local.SessionManager;
import com.example.absensitm.data.model.TokenRequest;
import com.example.absensitm.data.network.ApiClient;
import com.example.absensitm.data.network.ApiService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d(TAG, "From: " + message.getFrom());

        // Notifikasi standar akan otomatis ditangani oleh sistem saat app di background.
        // Jika app di foreground, kita bisa memunculkan Toast atau notifikasi custom.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
            // Show custom notification if needed
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            sendRegistrationToServer(token);
        }
    }

    private void sendRegistrationToServer(String token) {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        apiService.updateDeviceToken(new TokenRequest(token)).enqueue(new Callback<com.example.absensitm.data.model.BaseResponse>() {
            @Override
            public void onResponse(Call<com.example.absensitm.data.model.BaseResponse> call, Response<com.example.absensitm.data.model.BaseResponse> response) {
                Log.d(TAG, "Token updated to server");
            }

            @Override
            public void onFailure(Call<com.example.absensitm.data.model.BaseResponse> call, Throwable t) {
                Log.e(TAG, "Failed to update token: " + t.getMessage());
            }
        });
    }
}
