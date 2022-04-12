package com.example.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.expensetracker.R;
import com.example.expensetracker.model.User;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;

public class SharedPreferencesUtils {
    private final static Context context = BaseApp.context;
    private final static android.content.SharedPreferences sharedPref =
            context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);

    public static Long getUserId() {
        return sharedPref.getLong(context.getString(R.string.user_id_shared_pref), -1L);
    }

    public static String getIdToken(){
        return sharedPref.getString(context.getResources().getString(R.string.id_token_shared_pref), "No token");
    }

    public static String getFullName(){
        return sharedPref.getString(context.getResources().getString(R.string.fullname_shared_pref), "");
    }

    public static String getUsername(){
        return sharedPref.getString(context.getResources().getString(R.string.username_shared_pref), "");
    }

    public static String getPhoneNumber(){
        return sharedPref.getString(context.getResources().getString(R.string.phonenumber_shared_pref), "");
    }

    public static String getEmail(){
        return sharedPref.getString(context.getResources().getString(R.string.email_shared_pref), "");
    }

    public static Boolean getIsEnabledAccount(){
        return sharedPref.getBoolean(context.getResources().getString(R.string.is_account_enabled_shared_pref), false);
    }

    public static String getNotifToken() {
        return sharedPref.getString(context.getString(R.string.notif_token_shared_pref), "");
    }

    public static Boolean getIsAuthRemembered() {
        return sharedPref.getBoolean(context.getResources().getString(R.string.is_auth_remembered_shared_pref), false);
    }

    public static void setEnabledAccount(Boolean enabled){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getResources().getString(R.string.is_account_enabled_shared_pref), enabled);
        editor.apply();

        Timber.d("IsEnabledAccount has been written to shared preferences");
    }

    public static void setToken(String token, Boolean isAuthRemembered){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getResources().getString(R.string.id_token_shared_pref), token);
        editor.putBoolean(context.getResources().getString(R.string.is_auth_remembered_shared_pref), isAuthRemembered);
        editor.apply();

        Timber.d("Token has been written to shared preferences");
    }

    public static void setNotificationToken(String token) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.notif_token_shared_pref), token);
        editor.apply();
    }


    public static void setProfileDetails(Long userId, String fullName, String email, String phoneNumber, String avatarUri, String idToken) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.user_id_shared_pref), userId);
        editor.putString(context.getString(R.string.fullname_shared_pref), fullName);
        editor.putString(context.getString(R.string.email_shared_pref), email);
        editor.putString(context.getString(R.string.phonenumber_shared_pref), phoneNumber);
        editor.putString(context.getString(R.string.avatar_uri_shared_pref), avatarUri);
        editor.putString(context.getString(R.string.id_token_shared_pref), idToken);
        editor.putBoolean(context.getResources().getString(R.string.is_auth_remembered_shared_pref), true);

        editor.apply();
        Timber.d("Profile details has been written to shared preferences");
    }

    public static User getProfileDetails() {
        User user = new User();
        user.setId(sharedPref.getLong(context.getString(R.string.user_id_shared_pref), -1L));
        user.setEmail(sharedPref.getString(context.getString(R.string.email_shared_pref), ""));
        user.setFullName(sharedPref.getString(context.getString(R.string.fullname_shared_pref), ""));
        user.setPhoneNumber(sharedPref.getString(context.getString(R.string.phonenumber_shared_pref), ""));
        user.setAvatarUri(sharedPref.getString(context.getString(R.string.avatar_uri_shared_pref), ""));

        Timber.d("Profile details have been retrieved.");
        return user;
    }

    public static void clearProfileDetails() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(context.getString(R.string.user_id_shared_pref));
        editor.remove(context.getString(R.string.email_shared_pref));
        editor.remove(context.getString(R.string.fullname_shared_pref));
        editor.remove(context.getString(R.string.username_shared_pref));
        editor.remove(context.getString(R.string.id_token_shared_pref));
        editor.remove(context.getString(R.string.is_auth_remembered_shared_pref));
        editor.remove(context.getString(R.string.is_account_enabled_shared_pref));

        editor.apply();
        Timber.d("Profile details has been removed from shared preferences.");
    }

    public static void setFirstAccess(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getResources().getString(R.string.firstAccess_sharedPref), false);
        editor.commit();
    }

    public static Boolean getFirstAccess(){
        boolean firstAccess = sharedPref.getBoolean(context.getResources().getString(R.string.firstAccess_sharedPref), true);
        return firstAccess;
    }
}
