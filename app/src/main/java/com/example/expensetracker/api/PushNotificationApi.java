package com.example.expensetracker.api;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.RequestQueueHelper;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class PushNotificationApi {

    public static void updateNotificationToken(String token, Long userId) {
        String url = BaseApp.serverUrl + "/pushNotification/updateToken/" + token + "/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
            Timber.d("Notification Token updated successfully");
        }, error -> Timber.e(error.getMessage())) {
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
    }

    public static void insertNotificationToken(String token, Long userId) {
        String url = BaseApp.serverUrl + "/pushNotification/insertToken/" + token + "/" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> Timber.d("Notification Token inserted successfully"),
                Timber::e) {

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
    }

}
