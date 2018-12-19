package com.example.tanso.fotogram.Controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tanso.fotogram.Model.Model;

import java.util.HashMap;
import java.util.Map;

public class FotogramAPI {

    private static String baseURL = "https://ewserver.di.unimi.it/mobicomp/fotogram/";

    public enum API {

        LOGIN, CREATE_POST, PICTURE_UPDATE, LOGOUT, USERS, FOLLOW, UNFOLLOW, PROFILE, WALL, FOLLOWED;

        @Override
        public String toString(){
            return this.name().toLowerCase();
        }
    }

    public static void makeAPICall(final API callType, Context context, final HashMap<String,String> params, final ResponseCode onResponse, final ErrorCode onError){

        RequestQueue rq = Model.getRequestQueue(context);
        String url = baseURL+callType;
        StringRequest sr = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(onResponse != null)
                            onResponse.run(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ajeje", callType+" error: "+error.toString());
                        if(onError != null)
                            onError.run(null);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        rq.add(sr);
    }
}

