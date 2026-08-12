package com.example.javatraining.ui.main;

import android.os.Bundle;
import android.os.Build;
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
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.HapticFeedbackConstants;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.javatraining.ui.main.home.FaceScanActivity;
import com.example.javatraining.ui.main.home.HomeFragment;
import com.example.javatraining.data.remote.ApiClient;
import com.example.javatraining.data.remote.ApiService;
import com.example.javatraining.data.remote.request.FcmTokenRequest;
import com.example.javatraining.data.remote.response.BaseResponse;
import com.example.javatraining.data.remote.response.EmployeeData;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.javatraining.data.local.SessionManager;
import com.example.javatraining.data.model.User;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startActivity(new Intent(this, FaceScanActivity.class));
                } else {
                    Toast.makeText(this, "Izin kamera dibutuhkan untuk Face Scan", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<String> requestNotificationLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Izin notifikasi disarankan untuk info jadwal", Toast.LENGTH_SHORT).show();
                }
            });

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestNotificationPermission();

        // Force fully rounded corners on BottomAppBar
        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) binding.bottomAppBar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, 100f)
                        .build());
        
        // Add outline stroke so it stands out from the background
        float strokeWidth = getResources().getDisplayMetrics().density * 1.5f;
        bottomBarBackground.setStroke(strokeWidth, androidx.core.content.ContextCompat.getColor(this, R.color.ent_outline));
                        
        // Fix for concave shadow bug: force a custom convex outline so native shadow renders correctly
        binding.bottomAppBar.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(android.view.View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 100f);
            }
        });
        binding.bottomAppBar.setClipToOutline(true);

        setupNavigation();

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            selectNavTab(0);
        }

        // Ensure FCM Token is always registered on launch
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult();
                Log.d("MainActivity", "FCM Token: " + token);
                SessionManager sessionManager = new SessionManager(MainActivity.this);
                User user = sessionManager.getUser();
                if (user != null) {
                    ApiService apiService = ApiClient.getClient(MainActivity.this).create(ApiService.class);
                    apiService.updateFcmToken("eq." + user.getEmail(), new FcmTokenRequest(token))
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Log.d("MainActivity", "FCM Token registered on server for user: " + user.getId());
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("MainActivity", "FCM Token registration failed", t);
                                }
                            });
                } else {
                    Log.e("MainActivity", "User is null, cannot register FCM Token");
                }
            }
        });
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

        binding.navQuickIzin.setOnClickListener(v -> {
            loadFragment(new com.example.javatraining.ui.main.izin.IzinFragment());
            selectNavTab(2);
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fabManual.setVisibility(View.VISIBLE);
        });

        binding.navProfile.setOnClickListener(v -> {
            startActivity(new android.content.Intent(MainActivity.this, com.example.javatraining.ui.main.profile.ProfileActivity.class));
            // No tab change since Profile is an Activity
        });

        // FAB (Manual Entry / Clock In) click listener
        binding.fabManual.setOnClickListener(v -> {
            loadFragment(new ManualFragment());
            selectNavTab(-1);
            binding.bottomAppBar.setVisibility(View.VISIBLE);
            binding.fabManual.setVisibility(View.VISIBLE);
        });

        // FAB Micro-interactions (Breathing & Touch Scale)
        ObjectAnimator breathingAnim = ObjectAnimator.ofPropertyValuesHolder(
                binding.fabManual,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.03f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.03f, 1.0f));
        breathingAnim.setDuration(3000);
        breathingAnim.setRepeatCount(ObjectAnimator.INFINITE);
        breathingAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        breathingAnim.start();

        binding.fabManual.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(150)
                            .setInterpolator(new AccelerateDecelerateInterpolator()).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250)
                            .setInterpolator(new OvershootInterpolator(1.2f)).start();
                    break;
            }
            return false; // Let click listener handle the click event
        });
    }

    public void selectNavTab(int index) {
        // Reset all colors
        int inactiveColor = getResources().getColor(R.color.nav_inactive_gray, getTheme());
        int activeColor = getResources().getColor(R.color.nav_active_icon, getTheme());

        binding.iconDashboard.setColorFilter(inactiveColor);
        binding.iconHistory.setColorFilter(inactiveColor);
        binding.iconQuickIzin.setColorFilter(inactiveColor);
        binding.iconProfile.setColorFilter(inactiveColor);

        binding.dotDashboard.setVisibility(View.INVISIBLE);
        binding.dotHistory.setVisibility(View.INVISIBLE);
        binding.dotQuickIzin.setVisibility(View.INVISIBLE);
        binding.dotProfile.setVisibility(View.INVISIBLE);

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
            case 2:
                binding.iconQuickIzin.setColorFilter(activeColor);
                binding.dotQuickIzin.setVisibility(View.VISIBLE);
                break;
            case 3:
                binding.iconProfile.setColorFilter(activeColor);
                binding.dotProfile.setVisibility(View.VISIBLE);
                break;
            case -1:
                // No tab selected
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in,
                        android.R.anim.fade_out)
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

    public void navigateToHistory() {
        loadFragment(new HistoryFragment());
        selectNavTab(1);
        binding.bottomAppBar.setVisibility(View.VISIBLE);
        binding.fabManual.setVisibility(View.VISIBLE);
    }

    public void navigateToHome() {
        loadFragment(new HomeFragment());
        selectNavTab(0);
        binding.bottomAppBar.setVisibility(View.VISIBLE);
        binding.fabManual.setVisibility(View.VISIBLE);
    }
}
