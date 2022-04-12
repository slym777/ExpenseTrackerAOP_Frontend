package com.example.expensetracker.ui.profile;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.SharedPreferencesUtils;

public class ProfileViewModel extends ViewModel {
    public String fullName;
    public String newEmail;
    public String newPhoneNumber;
    public String newAvatarUri;

    public void buildUser(User user) {
        this.fullName = user.getFullName();
        this.newEmail = user.getEmail();
        this.newPhoneNumber = user.getPhoneNumber();
        this.newAvatarUri = user.getAvatarUri();
    }

    public void setFullName(String firstName, String lastName){
        this.fullName = firstName + " " + lastName;
    }

    public void setNewEmail(String email){
        this.newEmail = email;
    }


    public void setNewPhoneNumber(String phoneNumber) {
        this.newPhoneNumber = phoneNumber;
    }

    public void setNewAvatarUri(Uri uri){
        this.newAvatarUri = uri.toString();
    }

    public void updateProfile() {
        Long id = SharedPreferencesUtils.getUserId();
        User user = new User(id, fullName, newEmail, newPhoneNumber, newAvatarUri);

    }
}