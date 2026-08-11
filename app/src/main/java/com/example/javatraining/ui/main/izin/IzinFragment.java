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
    private android.net.Uri photoUri;
    private boolean hasPhoto = false;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    try {
                        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        android.graphics.BitmapFactory.decodeFile(currentPhotoFile.getAbsolutePath(), options);
                        
                        int reqWidth = 1080;
                        int reqHeight = 1920;
                        int inSampleSize = 1;
                        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                            final int halfHeight = options.outHeight / 2;
                            final int halfWidth = options.outWidth / 2;
                            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                                inSampleSize *= 2;
                            }
                        }
                        options.inJustDecodeBounds = false;
                        options.inSampleSize = inSampleSize;
                        
                        Bitmap imageBitmap = android.graphics.BitmapFactory.decodeFile(currentPhotoFile.getAbsolutePath(), options);

                        // Fix rotation using ExifInterface
                        if (imageBitmap != null) {
                            try {
                                android.media.ExifInterface exif = new android.media.ExifInterface(currentPhotoFile.getAbsolutePath());
                                int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_UNDEFINED);
                                int rotationDegrees = 0;
                                switch (orientation) {
                                    case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                        rotationDegrees = 90;
                                        break;
                                    case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                        rotationDegrees = 180;
                                        break;
                                    case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                                        rotationDegrees = 270;
                                        break;
                                }
                                if (rotationDegrees != 0) {
                                    android.graphics.Matrix matrix = new android.graphics.Matrix();
                                    matrix.postRotate(rotationDegrees);
                                    Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
                                    if (rotatedBitmap != imageBitmap) {
                                        imageBitmap.recycle();
                                        imageBitmap = rotatedBitmap;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (imageBitmap == null && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                imageBitmap = (Bitmap) extras.get("data");
                            }
                        }

                        if (imageBitmap != null) {
                            java.io.FileOutputStream stream = new java.io.FileOutputStream(currentPhotoFile);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                imageBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, stream);
                            } else {
                                @SuppressWarnings("deprecation")
                                Bitmap.CompressFormat webpFormat = Bitmap.CompressFormat.WEBP;
                                imageBitmap.compress(webpFormat, 100, stream);
                            }
                            stream.close();

                            binding.ivPreviewLampiran.setImageBitmap(imageBitmap);
                            binding.ivPreviewLampiran.setVisibility(View.VISIBLE);
                            binding.btnRemovePhoto.setVisibility(View.VISIBLE);
                            binding.llUploadPlaceholder.setVisibility(View.GONE);
                            binding.btnUpload.setBackgroundResource(0);
                            hasPhoto = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    private final ActivityResultLauncher<String> requestCameraLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Izin kamera diperlukan untuk foto selfie", Toast.LENGTH_SHORT).show();
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
        
        binding.btnBack.setOnClickListener(v -> {
            if (requireActivity() instanceof com.example.javatraining.ui.main.MainActivity) {
                ((com.example.javatraining.ui.main.MainActivity) requireActivity()).navigateToHome();
            } else {
                requireActivity().onBackPressed();
            }
        });

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
            String reason = binding.etKeterangan.getText().toString();

            if (type.isEmpty() || reason.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());

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
                String filename = "izin/" + employeeId + "/selfie_" + System.currentTimeMillis() + ".webp";
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(currentPhotoFile, okhttp3.MediaType.parse("image/webp"));
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
                if (requireActivity() instanceof com.example.javatraining.ui.main.MainActivity) {
                    ((com.example.javatraining.ui.main.MainActivity) requireActivity()).navigateToHome();
                } else {
                    requireActivity().onBackPressed();
                }
            } else {
                Toast.makeText(requireContext(), "Gagal mengirim pengajuan izin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                java.io.File cachePath = new java.io.File(getContext().getCacheDir(), "images");
                cachePath.mkdirs();
                currentPhotoFile = new java.io.File(cachePath, "izin_selfie_" + System.currentTimeMillis() + ".webp");
                photoUri = androidx.core.content.FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", currentPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupDropdown() {
        String[] jenisIzin = new String[]{"Sakit", "Cuti", "Izin Pribadi", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, jenisIzin);
        binding.spinnerJenisIzin.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchAttendanceStatus();
    }

    private void fetchAttendanceStatus() {
        binding.loadingState.setVisibility(View.VISIBLE);
        binding.formContainer.setVisibility(View.GONE);
        binding.successState.setVisibility(View.GONE);

        final boolean[] attendancesLoaded = {false};
        final boolean[] leavesLoaded = {false};
        final com.example.javatraining.data.remote.response.AttendanceData[] latestAttendance = {null};
        final com.example.javatraining.data.remote.response.LeaveData[] latestLeave = {null};

        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);
        com.example.javatraining.data.local.SessionManager sm = new com.example.javatraining.data.local.SessionManager(requireContext());
        String empId = sm.getUser() != null ? sm.getUser().getId() : "";

        apiService.getAttendances("eq." + empId, 1).enqueue(new Callback<java.util.List<com.example.javatraining.data.remote.response.AttendanceData>>() {
            @Override
            public void onResponse(Call<java.util.List<com.example.javatraining.data.remote.response.AttendanceData>> call, Response<java.util.List<com.example.javatraining.data.remote.response.AttendanceData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    latestAttendance[0] = response.body().get(0);
                }
                attendancesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
            @Override
            public void onFailure(Call<java.util.List<com.example.javatraining.data.remote.response.AttendanceData>> call, Throwable t) {
                attendancesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
        });

        apiService.getLeaveRequests("eq." + empId, 1).enqueue(new Callback<java.util.List<com.example.javatraining.data.remote.response.LeaveData>>() {
            @Override
            public void onResponse(Call<java.util.List<com.example.javatraining.data.remote.response.LeaveData>> call, Response<java.util.List<com.example.javatraining.data.remote.response.LeaveData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    latestLeave[0] = response.body().get(0);
                }
                leavesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
            @Override
            public void onFailure(Call<java.util.List<com.example.javatraining.data.remote.response.LeaveData>> call, Throwable t) {
                leavesLoaded[0] = true;
                checkStatus(attendancesLoaded, leavesLoaded, latestAttendance[0], latestLeave[0]);
            }
        });
    }

    private void checkStatus(boolean[] attendancesLoaded, boolean[] leavesLoaded, com.example.javatraining.data.remote.response.AttendanceData latestAtt, com.example.javatraining.data.remote.response.LeaveData latestLeave) {
        if (attendancesLoaded[0] && leavesLoaded[0]) {
            boolean hasLeaveToday = false;
            boolean hasAttToday = false;
            
            java.util.Calendar calToday = java.util.Calendar.getInstance();

            if (latestLeave != null) {
                String timeStr = latestLeave.getCreatedAt() != null ? latestLeave.getCreatedAt() : latestLeave.getDate();
                java.util.Date d = parseIsoDate(timeStr);
                if (d == null && latestLeave.getDate() != null) d = parseIsoDate(latestLeave.getDate() + "T00:00:00Z");
                if (d != null) {
                    java.util.Calendar calEvent = java.util.Calendar.getInstance();
                    calEvent.setTime(d);
                    if (calEvent.get(java.util.Calendar.YEAR) == calToday.get(java.util.Calendar.YEAR) &&
                        calEvent.get(java.util.Calendar.DAY_OF_YEAR) == calToday.get(java.util.Calendar.DAY_OF_YEAR)) {
                        hasLeaveToday = true;
                    }
                }
            }

            if (latestAtt != null && latestAtt.getTimestamp() != null) {
                java.util.Date d = parseIsoDate(latestAtt.getTimestamp());
                if (d != null) {
                    java.util.Calendar calEvent = java.util.Calendar.getInstance();
                    calEvent.setTime(d);
                    if (calEvent.get(java.util.Calendar.YEAR) == calToday.get(java.util.Calendar.YEAR) &&
                        calEvent.get(java.util.Calendar.DAY_OF_YEAR) == calToday.get(java.util.Calendar.DAY_OF_YEAR)) {
                        hasAttToday = true;
                    }
                }
            }

            if (hasLeaveToday) {
                setCompletedState("Anda sudah mengajukan izin hari ini.");
            } else if (hasAttToday) {
                setCompletedState("Anda sudah memiliki absensi hari ini. Tidak dapat mengajukan izin.");
            } else {
                setFormState();
            }
        }
    }

    private void setFormState() {
        binding.loadingState.setVisibility(View.GONE);
        binding.successState.setVisibility(View.GONE);
        binding.formContainer.setVisibility(View.VISIBLE);
    }

    private void setCompletedState(String message) {
        binding.loadingState.setVisibility(View.GONE);
        binding.formContainer.setVisibility(View.GONE);
        binding.successState.setVisibility(View.VISIBLE);

        binding.tvSuccessTitle.setText("Selesai untuk hari ini!");
        binding.tvSuccessSubtitle.setText(message);

        if (binding.ivSuccessAnim.getDrawable() instanceof android.graphics.drawable.Animatable) {
            ((android.graphics.drawable.Animatable) binding.ivSuccessAnim.getDrawable()).start();
        }
    }

    private java.util.Date parseIsoDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        String normalized = dateStr.trim().replace(" ", "T");

        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String f : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(f, java.util.Locale.getDefault());
                if (f.contains("XXX") || f.contains("Z")) {
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                }
                return sdf.parse(normalized);
            } catch (Exception ignored) { }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
