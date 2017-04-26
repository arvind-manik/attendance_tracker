package com.comeze.artuno.attendancetracker;

/**
 * Created by Artuno on 4/26/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ScanQrCode extends AppCompatActivity {

    String code;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        IntentIntegrator qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                Intent databackIntent = new Intent();
                databackIntent.putExtra("MESSAGE","tihds709gf");
                setResult(2,databackIntent);
                finish();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    code = obj.getString("value");
                    Intent databackIntent = new Intent();
                    databackIntent.putExtra("MESSAGE",code);
                    setResult(2,databackIntent);
                    finish();
                    /*Intent i = new Intent(MapsActivity.this,viewJSON.class);
                    i.putExtra("test", qrResult);
                    startActivity(i);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
