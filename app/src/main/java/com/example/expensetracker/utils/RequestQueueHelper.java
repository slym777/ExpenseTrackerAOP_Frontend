package com.example.expensetracker.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueHelper {
    private static RequestQueueHelper requestQueueHelperInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private RequestQueueHelper(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueHelper getRequestQueueHelperInstance(Context context) {
        if (requestQueueHelperInstance == null)
            requestQueueHelperInstance = new RequestQueueHelper(context);
        return requestQueueHelperInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

}