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
            // TODO: Implement API logic to save leave request
            requireActivity().onBackPressed();
        });
    }

    private void setupDropdown() {
        String[] jenisIzin = new String[]{"Sakit", "Cuti", "Izin Pribadi", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, jenisIzin);
        binding.spinnerJenisIzin.setAdapter(adapter);
    }

    private void setupDatePickers() {
        binding.etTanggalMulai.setOnClickListener(v -> showDatePicker(date -> {
            binding.etTanggalMulai.setText(date);
        }));

        binding.etTanggalSelesai.setOnClickListener(v -> showDatePicker(date -> {
            binding.etTanggalSelesai.setText(date);
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
