package com.example.expensetracker.utils;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class BaseApp extends Application {

    public static Context context;

    public static String serverUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        Timber.d("Application started !");
        context = this;
        serverUrl = "http://192.168.1.150:9091";
    }
}
