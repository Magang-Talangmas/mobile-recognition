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
        dummyData.add(new EmployeeAdapter.Employee("Sarah Jenkins", "Lead Designer", "08:42 AM", null, EmployeeAdapter.StatusType.ACTIVE));
        dummyData.add(new EmployeeAdapter.Employee("Marcus Chen", "Senior Engineer", "09:15 AM", null, EmployeeAdapter.StatusType.ACTIVE));
        dummyData.add(new EmployeeAdapter.Employee("Elena Rodriguez", "Product Manager", "12:30 PM", "EL", EmployeeAdapter.StatusType.BREAK));
        dummyData.add(new EmployeeAdapter.Employee("David Kim", "Data Analyst", "Expected 9:00", null, EmployeeAdapter.StatusType.ABSENT));
        dummyData.add(new EmployeeAdapter.Employee("James Wilson", "Marketing Specialist", "08:55 AM", null, EmployeeAdapter.StatusType.ACTIVE));

        EmployeeAdapter adapter = new EmployeeAdapter(dummyData);
        rvEmployees.setAdapter(adapter);

        return view;
    }
}
