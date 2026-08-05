package com.example.javatraining.ui.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.javatraining.data.model.User;
import com.example.javatraining.data.repository.AbsensiTMRepository;

public class LoginViewModel extends AndroidViewModel {
    private AbsensiTMRepository repository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        repository = new AbsensiTMRepository(application);
    }

    public LiveData<User> login(String email, String password) {
        return repository.login(email, password);
    }
}
