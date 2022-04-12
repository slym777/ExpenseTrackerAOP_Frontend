package com.example.expensetracker.ui.auth;

import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.AuthApi;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class LoginViewModel extends ViewModel {
    public BehaviorSubject<Boolean> loginState = BehaviorSubject.create();

    public void login(String email, String password) {
        AuthApi.login(email, password).subscribe(user -> {
            SharedPreferencesUtils.setProfileDetails(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getAvatarUri(),
                    user.getPassword()
            );
            loginState.onNext(true);
        }, err -> loginState.onError(new Throwable("Unable to reach login service")));
    }
}
