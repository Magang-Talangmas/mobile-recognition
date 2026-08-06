package com.example.absensitm.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.absensitm.R;
import com.example.absensitm.data.local.SessionManager;
import com.example.absensitm.data.network.ApiClient;
import com.example.absensitm.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.setApiService(ApiClient.getApiService(requireContext()));

        setupObservers();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            viewModel.login(email, password);
        });

        binding.tvForgotPassword.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_forgot_password);
        });
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
            binding.btnLogin.setEnabled(!isLoading);
            if (isLoading) {
                binding.btnLogin.setText("");
            } else {
                binding.btnLogin.setText("Sign In");
            }
        });

        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), token -> {
            sessionManager.saveToken(token);
            Toast.makeText(requireContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();
            // Navigate to Dashboard
            Navigation.findNavController(requireView()).navigate(R.id.action_login_to_dashboard);
        });

        viewModel.getLoginError().observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
