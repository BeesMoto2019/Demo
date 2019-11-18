package com.example.bikeservice;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MechViewLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    String latitute,longitute,reqid,emailid,strcurrentdate;
    Double late,longi;
    List<User> mechdetails;
    String loginemail;
    Button btnapprove;
    LatLng latLng;
    SweetAlertDialog sweetAlertDialog;
    String garagename,mechcontact,strlong,strlat,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_view_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnapprove=(Button)findViewById(R.id.btnapprve);
        mechdetails=new ArrayList<>();

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        loginemail = sp.getString("Email", "");
        Log.d("Email", loginemail);

        SharedPreferences latlng = getSharedPreferences("latlong", MODE_PRIVATE);
        reqid=latlng.getString("id","");
        username=latlng.getString("name","");
        emailid=latlng.getString("email","");
        latitute=latlng.getString("lat","");
        longitute=latlng.getString("longi","");
        Log.d("id",reqid);
        Log.d("name",username);
        Log.d("email",emailid);
        Log.d("lat",latitute);
        Log.d("longi",longitute);
        data();

        btnapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> params = new HashMap<>();
                params.put("res_email",loginemail);
                params.put("res_garage", garagename);
                params.put("res_contact", mechcontact);
                params.put("res_latitute", strlat);
                params.put("res_longitute", strlong);
                params.put("e_id",reqid);
                params.put("res_date",getDateTime());

                PerformNetworkEmergency request = new PerformNetworkEmergency(Apiurl.URL_MECHEMERGENCYRES, params, CODE_POST_REQUEST);
                request.execute();

                sweetAlertDialog=new SweetAlertDialog(MechViewLocation.this,SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("Requested Successfull")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent req=new Intent(MechViewLocation.this,NavigationDrawer.class);
                                startActivity(req);
                                finish();
                            }
                        });
            }
        });
    }

    private String getDateTime()
    {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date=new Date();
        strcurrentdate=simpleDateFormat.format(date);
        return strcurrentdate;
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        late = Double.parseDouble(latitute);
        longi = Double.parseDouble(longitute);
        createMarker(late, longi, username);
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(late, longi));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    protected Marker createMarker(double latitude, double longitude, String title) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title));
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        params.put("mech_email",loginemail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_MECHEMERGENCYDETAILS, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void mechdetails(JSONArray helptips) throws JSONException {
        mechdetails.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            mechdetails.add(new User(
                    garagename=obj.getString("garage_name"),
                    mechcontact=obj.getString("mech_contact"),
                    strlat=obj.getString("latitute"),
                    strlong=obj.getString("longitute")
            ));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latLng=new LatLng(location.getLatitude(),location.getLongitude());
        createMarker(location.getLatitude(),location.getLongitude(),username);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(MechViewLocation.this);
            progressDialog1.setMessage("Please Wait...");
            progressDialog1.setCancelable(false);
            progressDialog1.setIndeterminate(true);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog1.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Log.d(TAG, "Helptips Response: " + s.toString());
            //progressBar.setVisibility(GONE);
            try {
                Log.d("listview", s);
                JSONObject object = new JSONObject(s);
                Log.d("listview", String.valueOf(object));
                if (!object.getBoolean("error")) {
                    mechdetails(object.getJSONArray("mechemerg_details"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog1.dismiss();
        }
        @Override
        protected String doInBackground (Void...voids){
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    class PerformNetworkEmergency extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;

        PerformNetworkEmergency(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(MechViewLocation.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);

                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("email",emailid);
                    params1.put("title", "Request Status:Emergency ");
                    params1.put("message", "Your Request is Approved by "+loginemail);
                    params1.put("type","UserEmergencyRequest");

                    // Network Perform Code

                    PerformNetworkNotification notify = new PerformNetworkNotification(Apiurl.URL_NOTIFICATIONMECH, params1, CODE_POST_REQUEST);
                    notify.execute();

                }
                else {
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    class PerformNetworkNotification extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkNotification(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back=new Intent(MechViewLocation.this,MechEmergencyReq.class);
        startActivity(back);
        finish();
    }
}
