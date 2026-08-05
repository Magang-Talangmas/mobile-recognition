package com.example.absensitm.ui.splash;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.absensitm.R;
import com.example.absensitm.data.local.SessionManager;
import com.example.absensitm.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Simple Logo Animation (Scale & Fade)
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(binding.ivLogo, scaleX, scaleY, alpha);
        animator.setDuration(1000);
        animator.start();

        binding.tvAppName.setAlpha(0f);
        binding.tvAppName.animate().alpha(1f).setDuration(1000).setStartDelay(300).start();
        
        binding.tvAppSubtitle.setAlpha(0f);
        binding.tvAppSubtitle.animate().alpha(1f).setDuration(1000).setStartDelay(500).start();

        // Check login status after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getView() == null) return;
            
            SessionManager sessionManager = new SessionManager(requireContext());
            if (sessionManager.isLoggedIn()) {
                Navigation.findNavController(requireView()).navigate(R.id.action_splash_to_dashboard);
            } else {
                Navigation.findNavController(requireView()).navigate(R.id.action_splash_to_login);
            }
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
