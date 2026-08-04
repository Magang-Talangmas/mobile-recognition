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
        dummyData.add(new EmployeeAdapter.Employee("Sarah Jenkins", "Engineering", "ENG-402", null, EmployeeAdapter.StatusType.IN_OFFICE));
        dummyData.add(new EmployeeAdapter.Employee("Marcus Chen", "Product", "PRD-118", null, EmployeeAdapter.StatusType.ON_BREAK));
        dummyData.add(new EmployeeAdapter.Employee("Elena Rodriguez", "Design", "DES-892", "EL", EmployeeAdapter.StatusType.REMOTE));
        dummyData.add(new EmployeeAdapter.Employee("David Kim", "Marketing", "MKT-204", null, EmployeeAdapter.StatusType.OFFLINE));

        EmployeeAdapter adapter = new EmployeeAdapter(dummyData);
        rvEmployees.setAdapter(adapter);

        return view;
    }
}
