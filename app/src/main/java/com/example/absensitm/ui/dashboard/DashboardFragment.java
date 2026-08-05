package com.example.absensitm.ui.dashboard;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.absensitm.R;
import com.example.absensitm.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Apply Layout Transition animation to the root for smooth changes
        binding.getRoot().setLayoutTransition(new LayoutTransition());

        // Simple enter animation for card
        binding.cardAttendanceStatus.setAlpha(0f);
        binding.cardAttendanceStatus.setTranslationY(50f);
        binding.cardAttendanceStatus.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(100)
                .start();

        // Setup Button listener
        binding.btnDoAttendance.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_dashboard_to_camera);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
