package com.example.javatraining.ui.main.employees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javatraining.R;

import java.util.ArrayList;
import java.util.List;

public class EmployeesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        RecyclerView rvEmployees = view.findViewById(R.id.rvEmployees);
        rvEmployees.setLayoutManager(new LinearLayoutManager(getContext()));

        List<EmployeeAdapter.Employee> dummyData = new ArrayList<>();
        dummyData.add(new EmployeeAdapter.Employee("Budi Santoso", "Senior Developer", "Engineering", "BS", "Hadir", "#F59E0B"));
        dummyData.add(new EmployeeAdapter.Employee("Sari Dewi", "Marketing Manager", "Marketing", "SD", "Hadir", "#FB7185"));
        dummyData.add(new EmployeeAdapter.Employee("Andi Pratama", "Financial Analyst", "Finance", "AP", "Terlambat", "#34D399"));
        dummyData.add(new EmployeeAdapter.Employee("Rina Wahyu", "HR Specialist", "HR", "RW", "Hadir", "#38BDF8"));

        EmployeeAdapter adapter = new EmployeeAdapter(dummyData);
        rvEmployees.setAdapter(adapter);

        return view;
    }
}
