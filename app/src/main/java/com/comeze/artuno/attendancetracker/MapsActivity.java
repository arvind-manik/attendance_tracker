package com.comeze.artuno.attendancetracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//QR code scanner library from journeyapps git for zxing

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String API_KEY = "AIzaSyBC3UzAMsTWRwjUluVrEFIgfXlpP0QoDZg";

    private GoogleMap mMap;
    GoogleApiClient myClient;
    Location myLastLocation;
    Marker myCurrLoc;
    LocationRequest myRequest;
    JSONObject jsonResponse;
    JSONObject proximity;
    String distance;
    IntentIntegrator qrScan;
    String qrResult;
    String coords;

    String username;

    //Map creation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        username = (String) getIntent().getExtras().get("uname");
        Log.v("myTag",username);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button checkIn = (Button)findViewById(R.id.cin);
        qrScan = new IntentIntegrator(this);

        //On Click listener for Check-in button
        checkIn.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){

               /*Intent get = new Intent(MapsActivity.this,ScanQrCode.class);
               startActivity(get);
               qrResult = get.getExtras().getString("parse");*/

               Intent scan = new Intent(MapsActivity.this,ScanQrCode.class);
               int resultCode = 2;
               startActivityForResult(scan,resultCode);

           }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.map_style);
        mMap.setMapStyle(style);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    //Interface Overridden methods for GoogleApiClient
    protected synchronized void buildGoogleApiClient() {
        myClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        myClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        myRequest = new LocationRequest();
        myRequest.setInterval(5000);
        myRequest.setFastestInterval(2500);
        myRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(myClient, myRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        myLastLocation = location;
        if (myCurrLoc != null) {
            myCurrLoc.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your'e here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        myCurrLoc = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (myClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(myClient, this);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (myClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    showMyToast("Permission denied");
                }
                return;
            }

        }
    }

    //Handles QrCode intent result
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(result.getContents());
                    qrResult = obj.getString("value");
                    Intent i = new Intent(MapsActivity.this,viewJSON.class);
                    i.putExtra("test", qrResult);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/


    //Have stacked onResponse events of several Volley requests within
    //this method gets result from QrCodeScan
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            qrResult = data.getStringExtra("MESSAGE");
            Log.v("myTag", qrResult);

        }

        String urlForLatLng = "http://atrack.comeze.com/php/latlng.php?val="+qrResult;
        //Request queue for Google Distance Matrix API results
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest laloRequest = new StringRequest(Request.Method.GET, urlForLatLng, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.v("myTag",jsonResponse.toString());
                    jsonResponse = jsonResponse.getJSONArray("result").getJSONObject(0);
                    coords = jsonResponse.get("lat")+","+jsonResponse.get("lon");
                    Log.v("myTag",coords);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String url_request="";

                try {
                    url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + myLastLocation.getLatitude() + ","
                            + myLastLocation.getLongitude() + "&destinations="+coords+"&key=" + API_KEY;
                    //url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + 12.978 + ","
                    //+ 80.241 + "&destinations=12.987831,80.251651&key=" + API_KEY;
                }catch (NullPointerException nep){
                    nep.printStackTrace();
                }

                RequestQueue queue1 = Volley.newRequestQueue(MapsActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url_request, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonResponse = new JSONObject(response);
                            try {
                                proximity = jsonResponse.getJSONArray("rows")
                                        .getJSONObject(0)
                                        .getJSONArray ("elements")
                                        .getJSONObject(0)
                                        .getJSONObject("distance");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                distance = proximity.get("text").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //QRcode scan to verify the class code
                            //qrScan.initiateScan();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        distance = distance.replaceAll("[^\\d.]", "");
                        Log.v("myTag",distance);
                        if(Float.parseFloat(distance)<0.5){
                            RequestQueue queue2 = Volley.newRequestQueue(MapsActivity.this);
                            String attUrl = "http://atrack.comeze.com/php/attUpdate.php?val="+username;

                            StringRequest attendanceReq = new StringRequest(Request.Method.GET, attUrl, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response){
                                    showMyToast("Request sent...");
                                }

                            },new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            });
                            queue2.add(attendanceReq);
                        }
                        else{
                            showMyToast("You are too far away");
                        }

                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

                queue1.add(stringRequest);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(laloRequest);


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