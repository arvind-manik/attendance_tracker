package com.comeze.artuno.attendancetracker;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Artuno on 4/22/2017.
 */

public class Faculty extends Fragment {
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState){
        View viewObj = layoutInflater.inflate(R.layout.fac_tab,container,false);
        ImageView im = (ImageView)viewObj.findViewById(R.id.fac);
        makeItBlack(im);

        final EditText uname =(EditText)viewObj.findViewById(R.id.ed1);
        final EditText pword =(EditText)viewObj.findViewById(R.id.ed2);
        final String URL = "http://atrack.comeze.com/php/Login2.php";
        Button login=(Button)viewObj.findViewById(R.id.b1);

        login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final String username = uname.getText().toString();
                final String password = pword.getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                //String name = jsonResponse.getString("username");

                                Intent intent = new Intent(getActivity(), ReportsTab.class);
                                intent.putExtra("uname", username);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(username, password, URL, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(loginRequest);
            }

        });

        return viewObj;
    }

    private void makeItBlack(ImageView im) {
        ColorMatrix mat = new ColorMatrix();
        mat.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(mat);
        im.setColorFilter(cf);
    }
}
