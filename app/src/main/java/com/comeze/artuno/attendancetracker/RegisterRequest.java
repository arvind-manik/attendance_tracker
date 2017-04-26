package com.comeze.artuno.attendancetracker;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Artuno on 3/19/2017.
 */

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_URL = "http://atrack.comeze.com/php/Register.php";
    Map<String, String> params = null;


    public RegisterRequest(String username, String email,String password,Response.Listener<String> listener){
        super(Method.POST,REGISTER_URL,listener,null);
        params = new HashMap<>();

        params.put("username", username);
        params.put("email", email);
        params.put("password", password);

    }

    @Override
    public Map<String,String> getParams(){

        return params;

    }


    /*public RegisterRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public RegisterRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }*/


}
