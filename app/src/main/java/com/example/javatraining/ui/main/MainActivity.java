package com.example.javatraining.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.javatraining.R;
import com.example.javatraining.databinding.ActivityMainBinding;
import com.example.javatraining.ui.main.history.HistoryFragment;
import com.example.javatraining.ui.main.home.HomeFragment;
import com.example.javatraining.ui.main.employees.EmployeesFragment;
import com.example.javatraining.ui.main.manual.ManualFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.navigation_employees) {
                selectedFragment = new EmployeesFragment();
            } else if (itemId == R.id.navigation_manual) {
                selectedFragment = new ManualFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        binding.fabScan.setOnClickListener(v -> {
            android.widget.Toast.makeText(MainActivity.this, "Buka kamera untuk Face Recognition...", android.widget.Toast.LENGTH_SHORT).show();
        });

        // Set initial fragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        }
    }
}
