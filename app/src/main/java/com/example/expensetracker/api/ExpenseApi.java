package com.example.expensetracker.api;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.expensetracker.model.CreateTripRequest;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.model.UpdateTripRequest;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.RequestQueueHelper;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class ExpenseApi {

    public static BehaviorSubject<List<Expense>> getExpensesByTripId(Long tripId, boolean isGroup) {
        String url = BaseApp.serverUrl + "/expenses/getTripExpenses/" + tripId + "/" + isGroup;

        final BehaviorSubject<List<Expense>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    List<Expense> expenses = new ArrayList<>();
                    try {
                        for (int index = 0; index < response.length(); index++) {
                            Expense expense = gson.fromJson(response.getJSONObject(index).toString(), Expense.class);
                            expenses.add(expense);
                        }
                        behaviorSubject.onNext(expenses);
                        Timber.d("Expenses retrieved successfully");

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

    public static BehaviorSubject<Expense> getExpenseByExpenseId(Long expenseId){
        String url = BaseApp.serverUrl + "/expenses/getExpense/" + expenseId;

        final BehaviorSubject<Expense> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    Expense expense = gson.fromJson(response.toString(), Expense.class);
                    behaviorSubject.onNext(expense);
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

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
        return behaviorSubject;
    }

    public static BehaviorSubject<List<Expense>> getCreditorExpensesByUserId(Long userId) {
        String url = BaseApp.serverUrl + "/expenses/getCreditorExpenses/" + userId;

        final BehaviorSubject<List<Expense>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    List<Expense> expenses = new ArrayList<>();
                    try {
                        for (int index = 0; index < response.length(); index++) {
                            Expense expense = gson.fromJson(response.getJSONObject(index).toString(), Expense.class);
                            expenses.add(expense);
                        }
                        behaviorSubject.onNext(expenses);
                        Timber.d("Expenses retrieved successfully");

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

    public static BehaviorSubject<Expense> editExpense(Long expenseId, Expense expense) throws JSONException {
        String url = BaseApp.serverUrl + "/expenses/editExpense/" + expenseId;

        final BehaviorSubject<Expense> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                new JSONObject(
                        new Gson().toJson(expense, Expense.class)
                ),
                response -> {
                    Gson gson = new Gson();
                    Expense expenseResponse = gson.fromJson(response.toString(), Expense.class);
                    behaviorSubject.onNext(expenseResponse);
                },
                behaviorSubject::onError) {
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

    public static BehaviorSubject<Boolean> deleteExpenseInTrip(Long expenseId) throws JSONException {
        String url = BaseApp.serverUrl + "/expenses/delete/" + expenseId;

        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                new JSONObject(new Gson().toJson(new Expense(expenseId), Expense.class)),
                response -> behaviorSubject.onNext(true),
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

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String json = new String(
                            response.data,
                            "UTF-8"
                    );
                    if (json.length() == 0) {
                        return Response.success(
                                null,
                                HttpHeaderParser.parseCacheHeaders(response)
                        );
                    } else {
                        return super.parseNetworkResponse(response);
                    }
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        RequestQueueHelper.getRequestQueueHelperInstance(BaseApp.context).addToRequestQueue(jsonObjectRequest);
        return behaviorSubject;
    }
}
