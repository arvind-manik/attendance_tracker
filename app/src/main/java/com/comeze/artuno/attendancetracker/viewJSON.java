package com.comeze.artuno.attendancetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Artuno on 4/26/2017.
 */

public class viewJSON extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_the_file);
        TextView tv = (TextView)findViewById(R.id.myText);
        tv.setText(getIntent().getExtras().getString("test"));
        TextView v2 = (TextView)findViewById(R.id.myQr);
        v2.setText(getIntent().getExtras().getString("v2"));
    }
}
