package com.comeze.artuno.attendancetracker;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Artuno on 4/26/2017.
 */

public class CheckIn extends StringRequest {

    private Map<String, String> params;

    public CheckIn(String qr, String dist, String checkInUrl, Response.Listener<String> listener){
        super(Method.POST, checkInUrl, listener, null);
        params = new HashMap<>();
        params.put("qr",qr);
        params.put("dist",dist);
    }

    @Override
    public Map<String,String> getParams(){
        return params;
    }
}
