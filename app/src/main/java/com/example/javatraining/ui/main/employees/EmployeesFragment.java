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

    private List<EmployeeAdapter.Employee> allEmployeesData;
    private List<EmployeeAdapter.Employee> filteredData;
    private EmployeeAdapter adapter;
    private String currentFilter = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        RecyclerView rvEmployees = view.findViewById(R.id.rvEmployees);
        rvEmployees.setLayoutManager(new LinearLayoutManager(getContext()));

        allEmployeesData = getEmployeesFromDb();
        filteredData = new ArrayList<>(allEmployeesData);
        adapter = new EmployeeAdapter(filteredData);
        rvEmployees.setAdapter(adapter);

        // Search feature
        android.widget.EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString(), currentFilter);
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Filter Chips
        android.widget.TextView chipEngineering = view.findViewById(R.id.chipEngineering);
        android.widget.TextView chipDesign = view.findViewById(R.id.chipDesign);
        android.widget.TextView chipMarketing = view.findViewById(R.id.chipMarketing);

        View.OnClickListener chipListener = v -> {
            String selectedFilter = ((android.widget.TextView) v).getText().toString();
            if (currentFilter.equals(selectedFilter)) {
                currentFilter = ""; // deselect
                v.setBackgroundResource(R.drawable.bg_chip_inactive);
                ((android.widget.TextView)v).setTextColor(getResources().getColor(R.color.html_on_surface_variant));
            } else {
                currentFilter = selectedFilter;
                chipEngineering.setBackgroundResource(R.drawable.bg_chip_inactive);
                chipDesign.setBackgroundResource(R.drawable.bg_chip_inactive);
                chipMarketing.setBackgroundResource(R.drawable.bg_chip_inactive);
                chipEngineering.setTextColor(getResources().getColor(R.color.html_on_surface_variant));
                chipDesign.setTextColor(getResources().getColor(R.color.html_on_surface_variant));
                chipMarketing.setTextColor(getResources().getColor(R.color.html_on_surface_variant));

                v.setBackgroundResource(R.drawable.bg_chip_active);
                ((android.widget.TextView)v).setTextColor(getResources().getColor(R.color.html_on_primary));
            }
            filterList(etSearch.getText().toString(), currentFilter);
        };

        chipEngineering.setOnClickListener(chipListener);
        chipDesign.setOnClickListener(chipListener);
        chipMarketing.setOnClickListener(chipListener);

        return view;
    }

    private List<EmployeeAdapter.Employee> getEmployeesFromDb() {
        List<EmployeeAdapter.Employee> list = new ArrayList<>();
        List<com.example.javatraining.data.model.Karyawan> dbKaryawans = com.example.javatraining.data.repository.MockDatabase.getInstance().getAllKaryawan();
        for (com.example.javatraining.data.model.Karyawan k : dbKaryawans) {
            EmployeeAdapter.StatusType st = EmployeeAdapter.StatusType.valueOf(k.getStatusTracking().name());
            String initials = "";
            if (k.getNamaLengkap().contains(" ")) {
                String[] parts = k.getNamaLengkap().split(" ");
                initials = parts[0].substring(0,1) + parts[1].substring(0,1);
            } else {
                initials = k.getNamaLengkap().substring(0,2).toUpperCase();
            }
            list.add(new EmployeeAdapter.Employee(k.getNamaLengkap(), k.getJabatan(), "Updated Just Now", initials, st));
        }
        return list;
    }

    private void filterList(String query, String filterDept) {
        filteredData.clear();
        for (EmployeeAdapter.Employee emp : allEmployeesData) {
            boolean matchesQuery = emp.name.toLowerCase().contains(query.toLowerCase()) || emp.role.toLowerCase().contains(query.toLowerCase());
            boolean matchesDept = filterDept.isEmpty() || emp.role.contains(filterDept);
            if (matchesQuery && matchesDept) {
                filteredData.add(emp);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
