package com.comeze.artuno.attendancetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Artuno on 4/26/2017.
 */

public class ReportsTab extends AppCompatActivity {

    String username;
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_report);
        tv = (TextView)findViewById(R.id.rep);
        username = (String) getIntent().getExtras().get("uname");
        EditText ed = (EditText)findViewById(R.id.classId);
        username = ed.getText().toString();

    }

    public void reportNow(View v){
        String url = "http://atrack.comeze.com/php/facReport.php?val="+username;

        RequestQueue q = Volley.newRequestQueue(this);

        StringRequest reportRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject newObj = new JSONObject(response);
                    Log.v("myTag",newObj.toString());
                    JSONArray result = newObj.getJSONArray("result");
                    String currRecord;
                    JSONObject data;
                    for(int i=0; i<result.length();i++){
                        data = result.getJSONObject(i);
                        currRecord = data.getString("username")+"\n"+data.getString("present");
                        tv.append(currRecord);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        q.add(reportRequest);

    }

}
