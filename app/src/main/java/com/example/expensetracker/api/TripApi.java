package com.example.expensetracker.api;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
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

public class TripApi {

    public static BehaviorSubject<List<Trip>> getAllTrips() {
        String url = BaseApp.serverUrl + "/trips/allTrips";

        final BehaviorSubject<List<Trip>> behaviorSubject = BehaviorSubject.create();

        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    List<Trip> trips = new ArrayList<>();
                    try {
                        for (int index = 0; index < response.length(); index++) {
                            Trip trip = gson.fromJson(response.getJSONObject(index).toString(), Trip.class);
                            trips.add(trip);
                        }
                        behaviorSubject.onNext(trips);
                        Timber.d("Trips retrieved successfully");

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

    public static BehaviorSubject<Trip> getTripByTripId(Long tripId) {
        String url = BaseApp.serverUrl + "/trips/" + tripId;

        final BehaviorSubject<Trip> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    Trip trip = gson.fromJson(response.toString(), Trip.class);
                    behaviorSubject.onNext(trip);
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

    public static BehaviorSubject<Boolean> createTrip(CreateTripRequest createTripRequest) throws JSONException {
        String url = BaseApp.serverUrl + "/trips/saveTrip";

        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(new Gson().toJson(createTripRequest, CreateTripRequest.class)),
                response -> behaviorSubject.onNext(true),
                behaviorSubject::onError) {
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

    public static BehaviorSubject<Trip> updateTrip(Long tripId, UpdateTripRequest updateTripRequest) throws JSONException {
        String url = BaseApp.serverUrl + "/trips/updateTrip/" + tripId;

        final BehaviorSubject<Trip> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                new JSONObject(
                        new Gson().toJson(updateTripRequest, UpdateTripRequest.class)
                ),
                response -> {
                    Gson gson = new Gson();
                    Trip trip = gson.fromJson(response.toString(), Trip.class);
                    behaviorSubject.onNext(trip);
                },
                behaviorSubject::onError
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

    public static BehaviorSubject<Boolean> deleteTripById(Long tripId) {
        String url = BaseApp.serverUrl + "/trips/deleteTrip/" + tripId;

        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
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

    public static BehaviorSubject<Boolean> createExpense(Long tripId, Expense expense) throws JSONException {
        String url = BaseApp.serverUrl + "/trips/addExpense/" + tripId;

        final BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(new Gson().toJson(expense, Expense.class)),
                response -> behaviorSubject.onNext(true),
                behaviorSubject::onError
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
