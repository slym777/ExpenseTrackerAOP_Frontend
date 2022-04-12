package com.example.expensetracker.api;

import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.RequestQueueHelper;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class AuthApi {

    public static BehaviorSubject<Boolean> signUp(User user) throws JSONException {
        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        String url = BaseApp.serverUrl + "/users/signUp";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(new Gson().toJson(user, User.class)),
                response -> behaviorSubject.onNext(true),
                error -> behaviorSubject.onError(error)
        );

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
        return behaviorSubject;
    }

    public static BehaviorSubject<User> login(String userEmail, String password) {
        String url = BaseApp.serverUrl + "/users/login/userEmail=" + userEmail;
        final BehaviorSubject<User> behaviorSubject = BehaviorSubject.create();

        String auth = userEmail + ":" + password;
        byte[] data = auth.getBytes(StandardCharsets.UTF_8);
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        String authHeader = "Basic " + base64;

        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
                response -> {
                    Gson gson = new Gson();
                    User user = gson.fromJson(response.toString(), User.class);
                    behaviorSubject.onNext(user);
                    Timber.d("User retrieved successfully");
                },
                behaviorSubject::onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", authHeader);
                Timber.d("retrieved token is " + SharedPreferencesUtils.getIdToken());
                return params;
            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
        return behaviorSubject;
    }
}
