package com.example.expensetracker.ui.auth;

import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.AuthApi;
import com.example.expensetracker.model.User;

import org.json.JSONException;

import java.util.LinkedList;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class SignUpViewModel extends ViewModel {
    public BehaviorSubject<Boolean> signUpState = BehaviorSubject.create();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();


    public String fullName;
    public String email;
    public String password;
    public String phoneNumber;
    public String avatarUri;

    public void saveUserDetails(String firstName, String lastName){
        this.fullName = firstName + " " + lastName;
    }

    public void saveDownloadUri(Uri uri){
        avatarUri = uri.toString();
    }

    public void signUp() {
        User user = new User(fullName, email, phoneNumber, avatarUri, password);
        try {
            disposableLinkedList.add(AuthApi.signUp(user).subscribe(bool -> {
                signUpState.onNext(true);
            }, err -> {
                signUpState.onError(err);
            }));
        } catch (JSONException e) {
            signUpState.onError(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
