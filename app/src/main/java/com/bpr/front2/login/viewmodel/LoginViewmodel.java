package com.bpr.front2.login.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class LoginViewmodel extends AndroidViewModel {

    private final LoginRepo info;


    public LoginViewmodel(@NonNull Application application) {
        super(application);
        this.info = LoginRepo.getInstance(application);
    }

    public void setInfo(String email, String password) {
        info.setInfo(email, password);
    }
}
