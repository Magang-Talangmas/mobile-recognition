package com.example.javatraining.ui.main.izin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.javatraining.R;
import com.example.javatraining.databinding.FragmentIzinBinding;
import com.example.javatraining.data.remote.ApiClient;
import com.example.javatraining.data.remote.ApiService;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IzinFragment extends Fragment {

    private FragmentIzinBinding binding;
    private java.io.File currentPhotoFile;
    private boolean hasPhoto = false;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    try {
                        java.io.File cachePath = new java.io.File(getContext().getCacheDir(), "images");
                        cachePath.mkdirs();
                        currentPhotoFile = new java.io.File(cachePath, "izin_selfie_" + System.currentTimeMillis() + ".jpg");
                        java.io.FileOutputStream stream = new java.io.FileOutputStream(currentPhotoFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        stream.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }

                    binding.ivPreviewLampiran.setImageBitmap(imageBitmap);
                    binding.ivPreviewLampiran.setVisibility(View.VISIBLE);
                    binding.btnRemovePhoto.setVisibility(View.VISIBLE);
                    binding.llUploadPlaceholder.setVisibility(View.GONE);
                    binding.btnUpload.setBackgroundResource(0);
                    hasPhoto = true;
                }
            });

    private final ActivityResultLauncher<String> requestCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission required for selfie", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentIzinBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupDropdown();
        setupDatePickers();
        
        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnUpload.setOnClickListener(v -> {
            if (!hasPhoto) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    requestCameraLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });

        binding.btnRemovePhoto.setOnClickListener(v -> {
            hasPhoto = false;
            binding.ivPreviewLampiran.setVisibility(View.GONE);
            binding.btnRemovePhoto.setVisibility(View.GONE);
            binding.llUploadPlaceholder.setVisibility(View.VISIBLE);
            binding.btnUpload.setBackgroundResource(R.drawable.bg_photo_upload);
            currentPhotoFile = null;
        });
        
        binding.btnSubmitIzin.setOnClickListener(v -> {
            String type = binding.spinnerJenisIzin.getText().toString();
            String date = binding.etTanggalIzin.getText().toString();
            String reason = binding.etKeterangan.getText().toString();

            if (type.isEmpty() || date.isEmpty() || reason.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!hasPhoto) {
                Toast.makeText(requireContext(), "Silakan ambil foto selfie untuk lampiran", Toast.LENGTH_SHORT).show();
                return;
            }

            android.app.ProgressDialog progress = new android.app.ProgressDialog(requireContext());
            progress.setMessage("Mengirim pengajuan izin...");
            progress.setCancelable(false);
            progress.show();

            com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(requireContext());
            String employeeId = sessionManager.getUser() != null ? sessionManager.getUser().getId() : "";

            if (currentPhotoFile != null && currentPhotoFile.exists()) {
                String filename = "izin/" + employeeId + "/selfie_" + System.currentTimeMillis() + ".jpg";
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(currentPhotoFile, okhttp3.MediaType.parse("image/jpeg"));
                ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
                apiService.uploadStorageObject("recognition", filename, requestFile).enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            String publicUrl = com.example.javatraining.BuildConfig.SUPABASE_URL + "storage/v1/object/public/recognition/" + filename;
                            submitIzinWithUrl(employeeId, date, type, reason, publicUrl, progress);
                        } else {
                            progress.dismiss();
                            Toast.makeText(requireContext(), "Gagal upload foto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(requireContext(), "Error upload foto: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                submitIzinWithUrl(employeeId, date, type, reason, null, progress);
            }
        });
    }

    private void submitIzinWithUrl(String employeeId, String date, String type, String reason, String url, android.app.ProgressDialog progress) {
        com.example.javatraining.data.remote.request.LeaveRequest req = new com.example.javatraining.data.remote.request.LeaveRequest(
                employeeId, date, type, reason, url, "PENDING"
        );
        com.example.javatraining.data.repository.AbsensiTMRepository repo = new com.example.javatraining.data.repository.AbsensiTMRepository(requireActivity().getApplication());
        repo.submitLeaveRequest(req).observe(getViewLifecycleOwner(), success -> {
            progress.dismiss();
            if (success != null && success) {
                Toast.makeText(requireContext(), "Pengajuan Izin berhasil dikirim", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(requireContext(), "Gagal mengirim pengajuan izin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private void setupDropdown() {
        String[] jenisIzin = new String[]{"Sakit", "Cuti", "Izin Pribadi", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, jenisIzin);
        binding.spinnerJenisIzin.setAdapter(adapter);
    }

    private void setupDatePickers() {
        binding.etTanggalIzin.setOnClickListener(v -> showDatePicker(date -> {
            binding.etTanggalIzin.setText(date);
        }));
    }

    private void showDatePicker(OnDateSelectedListener listener) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(new Date(selection));
            listener.onSelected(date);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    interface OnDateSelectedListener {
        void onSelected(String date);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
