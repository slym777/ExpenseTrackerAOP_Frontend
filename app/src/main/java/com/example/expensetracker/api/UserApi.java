package com.example.expensetracker.api;

import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expensetracker.model.Notification;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.RequestQueueHelper;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class UserApi {

    public static BehaviorSubject<User> updateUser(User user) throws JSONException {
        final BehaviorSubject<User> behaviorSubject = BehaviorSubject.create();

        String url = BaseApp.serverUrl + "/users/edit/userId=" + user.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(new Gson().toJson(user, User.class)),
                response -> {
                    Gson gson = new Gson();
                    User updatedUser = gson.fromJson(response.toString(), User.class);
                    behaviorSubject.onNext(updatedUser);
                },
                error -> behaviorSubject.onError(error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + SharedPreferencesUtils.getIdToken());
                Timber.d("retrieved token is " + SharedPreferencesUtils.getIdToken());
                return params;
            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
        return behaviorSubject;
    }


    public static BehaviorSubject<List<User>> getListOfUsers() {
        String url = BaseApp.serverUrl + "/users/allUsers";

        final BehaviorSubject<List<User>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    List<User> users = new ArrayList<>();
                    try {
                        for (int index = 0; index < response.length(); index++) {
                            User User = gson.fromJson(response.getJSONObject(index).toString(), User.class);
                            users.add(User);
                        }
                        behaviorSubject.onNext(users);
                        Timber.d("Users retrieved successfully");

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                },
                error -> behaviorSubject.onError(error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + SharedPreferencesUtils.getIdToken());
                Timber.d("retrieved token is " + SharedPreferencesUtils.getIdToken());
                return params;
            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonArrayRequest);
        return behaviorSubject;
    }

    public static BehaviorSubject<List<Notification>> getListOfNotifications() {
        String url = BaseApp.serverUrl + "/users/" + SharedPreferencesUtils.getUserId() + "/getNotifications";

        final BehaviorSubject<List<Notification>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    List<Notification> notifications = new ArrayList<>();
                    try {
                        for (int index = 0; index < response.length(); index++) {
                            Notification notification = gson.fromJson(response.getJSONObject(index).toString(), Notification.class);
                            notifications.add(notification);
                        }
                        behaviorSubject.onNext(notifications);
                        Timber.d("Notifications retrieved successfully");

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                },
                error -> behaviorSubject.onError(error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + SharedPreferencesUtils.getIdToken());
                Timber.d("retrieved token is " + SharedPreferencesUtils.getIdToken());
                return params;
            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonArrayRequest);
        return behaviorSubject;
    }
}
