package com.example.absensitm.ui.main;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.absensitm.data.model.BaseResponse;
import com.example.absensitm.data.model.ConfirmRequest;
import com.example.absensitm.data.network.ApiClient;
import com.example.absensitm.data.network.ApiService;
import com.example.absensitm.databinding.ActivityConfirmationBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmationActivity extends AppCompatActivity {

    private ActivityConfirmationBinding binding;
    private String attendanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            attendanceId = getIntent().getStringExtra("attendanceId");
            String timestamp = getIntent().getStringExtra("timestamp");
            if (timestamp != null) {
                binding.tvTimestamp.setText("Detected: " + timestamp);
            }
        }

        binding.btnYes.setOnClickListener(v -> confirmAttendance("CONFIRMED"));
        binding.btnNo.setOnClickListener(v -> confirmAttendance("REJECTED"));
    }

    private void confirmAttendance(String status) {
        if (attendanceId == null) {
            Toast.makeText(this, "Error: Attendance ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.btnYes.setEnabled(false);
        binding.btnNo.setEnabled(false);

        ApiService apiService = ApiClient.getApiService(this);
        apiService.confirmAttendance(attendanceId, new ConfirmRequest(status)).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ConfirmationActivity.this, "Berhasil disubmit", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ConfirmationActivity.this, "Gagal mensubmit", Toast.LENGTH_SHORT).show();
                    binding.btnYes.setEnabled(true);
                    binding.btnNo.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(ConfirmationActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                binding.btnYes.setEnabled(true);
                binding.btnNo.setEnabled(true);
            }
        });
    }
}
