package com.example.bikeservice;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class User_FindMech<var, radius, fillColor> extends FragmentActivity implements OnMapReadyCallback {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<User> maplist;
    int number;
    private GoogleMap mMap;
    String name,email,contact,latitute,longitute;
    Button btnreq;
    SweetAlertDialog sweetAlertDialog;
    AlertDialog dialog;
    Double lati,longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__find_mech);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        maplist=new ArrayList<>();
        btnreq=(Button)findViewById(R.id.btnrequest);
        SharedPreferences sharedpreferences=getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        email=sharedpreferences.getString("email","");
        Log.d("email",email);

        SharedPreferences gps=getSharedPreferences("latlng", Context.MODE_PRIVATE);
        latitute=gps.getString("lat","");
        longitute=gps.getString("lng","");
        Log.d("lat",latitute);
        Log.d("long",longitute);

        lati=Double.parseDouble(latitute);
        longi=Double.parseDouble(longitute);

        SharedPreferences details = getSharedPreferences("value", Context.MODE_PRIVATE);
        name=details.getString("name","");
        contact=details.getString("contact","");
        Log.d("name",name);
        Log.d("contact",contact);

        data();

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    // Construct data
                    String apiKey = "apikey=" + "Va9cDk0PXpE-cHQStVYiuYpkHmNLre4QEpW7BAEkUt";
                    Random random = new Random();
                    number = random.nextInt(999999);
                    String message = "&message=" + "hey " + name + " your otp is" + number;
                    String sender = "&sender=" + "TXTLCL";
                    String numbers = "&numbers=" + contact;

                    // Send data
                    HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                    String data = apiKey + numbers + message + sender;
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                    conn.getOutputStream().write(data.getBytes("UTF-8"));
                    final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        stringBuffer.append(line);
                    }
                    rd.close();

                    // return stringBuffer.toString();
                    Toast.makeText(getApplicationContext(), "otp send", Toast.LENGTH_LONG).show();
                    System.out.println(number);
                } catch (Exception e) {
                    // System.out.println("Error SMS "+e);
                    //return "Error "+e;
                    Toast.makeText(getApplicationContext(), "error sms" + e, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "error" + e, Toast.LENGTH_LONG).show();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(User_FindMech.this);
                View view1 = getLayoutInflater().inflate(R.layout.custom_dialogue, null);
                final EditText otp = (EditText) view1.findViewById(R.id.otp);
                Button verify = (Button) view1.findViewById(R.id.verify);

                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!otp.getText().toString().isEmpty() && number==Integer.valueOf(otp.getText().toString())) {

                            HashMap<String, String> params = new HashMap<>();
                            params.put("Name",name);
                            params.put("Email", email);
                            params.put("Contact", contact);
                            params.put("Latitute", latitute);
                            params.put("Longitute", longitute);
                            params.put("Service", "Puncher");

                            PerformNetworkEmergency request = new PerformNetworkEmergency(Apiurl.URL_USREMERGENCY, params, CODE_POST_REQUEST);
                            request.execute();

                            //Toast.makeText(getApplicationContext(), "Registration successfull", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please Enter Correct OTP", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder.setView(view1);
                dialog = builder.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                sweetAlertDialog=new SweetAlertDialog(User_FindMech.this,SweetAlertDialog.SUCCESS_TYPE)
                        .setContentText("Requested Successfull")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent req=new Intent(User_FindMech.this,UserNavigation.class);
                                startActivity(req);
                                finish();
                            }
                        });
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
        CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(lati, longi));//latlong via shared
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);
        drawCircle(new LatLng(lati, longi));
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
        params.put("lat",latitute);//lat long via shared
        params.put("long",longitute);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_USRFINDGARAGE, params, CODE_POST_REQUEST);
        request.execute();
    }

    void createMarkersFromJson(JSONArray json) throws JSONException {
        // De-serialize the JSON string into an array of city objects
        //JSONArray jsonArray = new JSONArray(json);
        maplist.clear();
        for (int i = 0; i < json.length(); i++) {
            // Create a marker for each city in the JSON data.
            JSONObject jsonObj = json.getJSONObject(i);
            Log.d("listview", String.valueOf(jsonObj));
            maplist.add(new User(jsonObj.getString("garage_name"),
                    jsonObj.getString("mech_email"),
                    jsonObj.getDouble("latitute"),
                    jsonObj.getDouble("longitute")
            ));

        }
        for(int i = 0 ; i < maplist.size() ; i++) {

            createMarker(maplist.get(i).getLatitute(), maplist.get(i).getLongitute(), maplist.get(i).getCity());
        }
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(User_FindMech.this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog.show();
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    createMarkersFromJson(object.getJSONArray("Findlocation"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
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
        ProgressDialog progressDialog1;

        PerformNetworkEmergency(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(User_FindMech.this);
            progressDialog1.setMessage("Please Wait...");
            progressDialog1.setCancelable(false);
            progressDialog1.setIndeterminate(true);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog1.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);

                    for(int i = 0 ; i < maplist.size() ; i++) {

                        HashMap<String, String> params1 = new HashMap<>();
                        params1.put("email",maplist.get(i).getEmail());
                        params1.put("title", "New Request:Emergency ");
                        params1.put("message", "New Request from "+email);
                        params1.put("type","EmergencyRequest");

                        // Network Perform Code

                        PerformNetworkNotification notify = new PerformNetworkNotification(Apiurl.URL_NOTIFICATIONMECH, params1, CODE_POST_REQUEST);
                        notify.execute();

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog1.dismiss();
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
    protected void onDestroy()
    {
        super.onDestroy();
        if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
        else if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
