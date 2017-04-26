package com.comeze.artuno.attendancetracker;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Artuno on 4/24/2017.
 */

public class EnrollStudent extends AppCompatActivity {
    /*@Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState){
        View viewObj = layoutInflater.inflate(R.layout.stud_reg,container,false);
        return viewObj;
    }*/
    String successMsg = "Registration successful";

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stud_reg);
        final EditText username = (EditText) findViewById(R.id.uname);
        final EditText emailid = (EditText) findViewById(R.id.eid);
        final EditText password = (EditText) findViewById(R.id.pword);
        //final EditText cpword = (EditText) findViewById(R.id.cp);
        final Button regbtn = (Button) findViewById(R.id.register);
        //final String pword = password.getText().toString();


        regbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(final View v) {
                final String uname=username.getText().toString();
                final String mailId=emailid.getText().toString();
                final String pword = password.getText().toString();

                Response.Listener<String> resListener=new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            Boolean success = jsonResponse.getBoolean("success");

                            if(success){
                                /*Runnable r = new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(v, "Registration successful", Snackbar.LENGTH_LONG);
                                    }
                                };
                                Handler h = new Handler();
                                h.postDelayed(r,3000);*/
                                showMyToast(successMsg);

                                finish();
                            }
                            else{
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EnrollStudent.this);
                                alertDialog.setMessage("Registration failed")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(uname,mailId,pword,resListener);
                RequestQueue registerQueue = Volley.newRequestQueue(EnrollStudent.this);
                registerQueue.add(registerRequest);

            }

        });
    }

    private void showMyToast(String msg) {

            LayoutInflater inflater = getLayoutInflater();

            View layout = inflater.inflate(R.layout.snack_toast,(ViewGroup) findViewById(R.id.toast_layout));

            TextView text = (TextView) layout.findViewById(R.id.toast_text);


            text.setText(msg);

            Toast toast = new Toast(getApplicationContext());

            toast.setGravity(Gravity.BOTTOM | Gravity.LEFT | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

    }

}
