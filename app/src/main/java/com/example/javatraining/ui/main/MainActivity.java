package com.example.javatraining.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.javatraining.R;
import com.example.javatraining.databinding.ActivityMainBinding;
import com.example.javatraining.ui.main.history.HistoryFragment;
import com.example.javatraining.ui.main.home.HomeFragment;
import com.example.javatraining.ui.main.manual.ManualFragment;
import com.example.javatraining.ui.main.profile.ProfileFragment;
import com.example.javatraining.ui.main.manual.ManualFragment;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import android.view.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import com.example.javatraining.ui.main.home.FaceScanActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startActivity(new Intent(this, FaceScanActivity.class));
                } else {
                    Toast.makeText(this, "Izin kamera dibutuhkan untuk Face Scan", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Force fully rounded corners on BottomAppBar
        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) binding.bottomAppBar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, 100f)
                        .build());

        setupNavigation();

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            selectNavTab(0);
        }
    }

    private void setupNavigation() {
        binding.navDashboard.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            selectNavTab(0);
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fabManual.setVisibility(View.VISIBLE);
        });

        binding.navHistory.setOnClickListener(v -> {
            loadFragment(new HistoryFragment());
            selectNavTab(1);
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fabManual.setVisibility(View.VISIBLE);
        });

        // FAB (Manual Entry) click listener
        binding.fabManual.setOnClickListener(v -> {
            loadFragment(new ManualFragment());
            selectNavTab(-1);
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fabManual.setVisibility(View.VISIBLE);
        });
    }

    public void selectNavTab(int index) {
        // Reset all colors
        int inactiveColor = getResources().getColor(R.color.nav_inactive_gray, getTheme());
        int activeColor = getResources().getColor(R.color.nav_active_icon, getTheme());

        binding.iconDashboard.setColorFilter(inactiveColor);
        binding.iconHistory.setColorFilter(inactiveColor);

        binding.dotDashboard.setVisibility(View.INVISIBLE);
        binding.dotHistory.setVisibility(View.INVISIBLE);

        // Highlight selected
        switch (index) {
            case 0:
                binding.iconDashboard.setColorFilter(activeColor);
                binding.dotDashboard.setVisibility(View.VISIBLE);
                break;
            case 1:
                binding.iconHistory.setColorFilter(activeColor);
                binding.dotHistory.setVisibility(View.VISIBLE);
                break;
            case -1:
                // No tab selected
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    
    // Public method to allow fragments to navigate programmatically
    public void switchToFragment(Fragment fragment) {
        loadFragment(fragment);
        selectNavTab(-1); // Deselect bottom nav if it's a hidden fragment like Manual
        
        if (fragment instanceof com.example.javatraining.ui.main.notifications.NotificationsFragment) {
            binding.bottomAppBar.setVisibility(View.GONE);
            binding.fabManual.setVisibility(View.GONE);
        } else {
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fabManual.setVisibility(View.VISIBLE);
        }
    }
}
