package com.example.javatraining.ui.main.izin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.javatraining.R;
import com.example.javatraining.databinding.FragmentIzinBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IzinFragment extends Fragment {

    private FragmentIzinBinding binding;

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
        
        binding.btnSubmitIzin.setOnClickListener(v -> {
            String type = binding.spinnerJenisIzin.getText().toString();
            String date = binding.etTanggalIzin.getText().toString();
            String reason = binding.etKeterangan.getText().toString();

            if (type.isEmpty() || date.isEmpty() || reason.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Semua kolom harus diisi", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            android.app.ProgressDialog progress = new android.app.ProgressDialog(requireContext());
            progress.setMessage("Mengirim pengajuan izin...");
            progress.setCancelable(false);
            progress.show();

            com.example.javatraining.data.local.SessionManager sessionManager = new com.example.javatraining.data.local.SessionManager(requireContext());
            String employeeId = sessionManager.getUser() != null ? sessionManager.getUser().getId() : "";

            // Create timestamp for createdAt and updatedAt
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String now = sdf.format(new java.util.Date());

            com.example.javatraining.data.remote.request.LeaveRequest req = new com.example.javatraining.data.remote.request.LeaveRequest(
                employeeId, date, type, reason, null, "PENDING"
            );

            com.example.javatraining.data.repository.AbsensiTMRepository repo = new com.example.javatraining.data.repository.AbsensiTMRepository(requireActivity().getApplication());
            repo.submitLeaveRequest(req).observe(getViewLifecycleOwner(), success -> {
                progress.dismiss();
                if (success != null && success) {
                    android.widget.Toast.makeText(requireContext(), "Pengajuan Izin berhasil dikirim", android.widget.Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    android.widget.Toast.makeText(requireContext(), "Gagal mengirim pengajuan izin", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
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
