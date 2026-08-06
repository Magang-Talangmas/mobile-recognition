package com.example.absensitm.ui.profile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.absensitm.data.model.BaseResponse;
import com.example.absensitm.data.model.PasswordRequest;
import com.example.absensitm.data.network.ApiClient;
import com.example.absensitm.databinding.ActivityChangePasswordBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSavePassword.setOnClickListener(v -> {
            String oldPassword = binding.etOldPassword.getText().toString();
            String newPassword = binding.etNewPassword.getText().toString();
            String confirmPassword = binding.etConfirmPassword.getText().toString();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnSavePassword.setEnabled(false);
            binding.btnSavePassword.setText("Menyimpan...");

            ApiClient.getApiService(this).updatePassword(new PasswordRequest(oldPassword, newPassword))
                    .enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            binding.btnSavePassword.setEnabled(true);
                            binding.btnSavePassword.setText("Simpan Password");
                            
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(ChangePasswordActivity.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Password lama salah atau gagal mengubah", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            binding.btnSavePassword.setEnabled(true);
                            binding.btnSavePassword.setText("Simpan Password");
                            Toast.makeText(ChangePasswordActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
