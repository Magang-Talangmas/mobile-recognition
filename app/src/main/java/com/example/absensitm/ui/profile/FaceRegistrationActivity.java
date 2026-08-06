package com.example.absensitm.ui.profile;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.absensitm.data.model.BaseResponse;
import com.example.absensitm.data.network.ApiClient;
import com.example.absensitm.data.network.ApiService;
import com.example.absensitm.databinding.ActivityFaceRegistrationBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceRegistrationActivity extends AppCompatActivity {

    private ActivityFaceRegistrationBinding binding;
    private File photo1, photo2, photo3;
    private Uri currentPhotoUri;
    private int currentStep = 0; // 1, 2, or 3
    private ApiService apiService;

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    if (currentStep == 1) {
                        binding.imgPhoto1.setImageBitmap(BitmapFactory.decodeFile(photo1.getAbsolutePath()));
                        binding.btnTake1.setText("Ulang");
                    } else if (currentStep == 2) {
                        binding.imgPhoto2.setImageBitmap(BitmapFactory.decodeFile(photo2.getAbsolutePath()));
                        binding.btnTake2.setText("Ulang");
                    } else if (currentStep == 3) {
                        binding.imgPhoto3.setImageBitmap(BitmapFactory.decodeFile(photo3.getAbsolutePath()));
                        binding.btnTake3.setText("Ulang");
                    }
                    checkIfAllTaken();
                } else {
                    Toast.makeText(this, "Batal mengambil foto", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFaceRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getApiService(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnTake1.setOnClickListener(v -> launchCamera(1));
        binding.btnTake2.setOnClickListener(v -> launchCamera(2));
        binding.btnTake3.setOnClickListener(v -> launchCamera(3));

        binding.btnSubmit.setOnClickListener(v -> submitRegistration());
    }

    private void launchCamera(int step) {
        currentStep = step;
        try {
            File photoFile = new File(getCacheDir(), "face_reg_" + step + "_" + System.currentTimeMillis() + ".jpg");
            if (step == 1) photo1 = photoFile;
            else if (step == 2) photo2 = photoFile;
            else if (step == 3) photo3 = photoFile;

            currentPhotoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );
            takePictureLauncher.launch(currentPhotoUri);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuka kamera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfAllTaken() {
        if (photo1 != null && photo1.exists() && 
            photo2 != null && photo2.exists() && 
            photo3 != null && photo3.exists()) {
            binding.btnSubmit.setEnabled(true);
        }
    }

    private void submitRegistration() {
        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText("Mengirim...");

        List<MultipartBody.Part> parts = new ArrayList<>();
        parts.add(prepareFilePart("photos", photo1));
        parts.add(prepareFilePart("photos", photo2));
        parts.add(prepareFilePart("photos", photo3));

        apiService.registerFace(parts).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(FaceRegistrationActivity.this, "Wajah berhasil didaftarkan!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = "Gagal mendaftar wajah";
                    if (response.errorBody() != null) {
                        try {
                            org.json.JSONObject jObjError = new org.json.JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (Exception e) {}
                    }
                    Toast.makeText(FaceRegistrationActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    resetSubmitButton();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(FaceRegistrationActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                resetSubmitButton();
            }
        });
    }

    private void resetSubmitButton() {
        binding.btnSubmit.setEnabled(true);
        binding.btnSubmit.setText("Daftarkan Wajah");
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
