package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MechTiming extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    TextView tvtimer;
    EditText edtverify;
    Button btnstart,btnstop;
    String value,verify,strphone,strusremail,strmodel,strservice,strmechemail,strgarage,strdate,strreqid;
    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    List<User> userdetaillist;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_timing);
        userdetaillist=new ArrayList<>();
        Toolbar toolbar=(Toolbar)findViewById(R.id.timetool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Working Time");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences code=getSharedPreferences("codevalue", Context.MODE_PRIVATE);
        edtverify=(EditText)findViewById(R.id.codeverify);
        tvtimer=(TextView)findViewById(R.id.time);
        btnstart=(Button)findViewById(R.id.start);
        btnstop=(Button)findViewById(R.id.pause);

        SharedPreferences work=getSharedPreferences("Work",Context.MODE_PRIVATE);
        strreqid=work.getString("req_id","");
        Log.d("req_id",strreqid);
        strservice=work.getString("service","");
        Log.d("service",strservice);
        strmodel=work.getString("model","");
        Log.d("model",strmodel);
        strusremail=work.getString("usremail","");
        Log.d("usremail",strusremail);
        strphone=work.getString("usrphone","");
        Log.d("usrphone",strphone);
        strdate = work.getString("date","");
        Log.d("date",strdate);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        strmechemail = sp.getString("Email", "");
        Log.d("Email", strmechemail);

        SharedPreferences garage=getSharedPreferences("Garage",Context.MODE_PRIVATE);
        strgarage=garage.getString("garage","");
        Log.d("garage",strgarage);

//        SharedPreferences date=getSharedPreferences("Date",Context.MODE_PRIVATE);
        code();

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verify=edtverify.getText().toString().trim();
                if(verify.equals(value)) {
                    startTime = SystemClock.uptimeMillis();
                    myHandler.postDelayed(updateTimerMethod, 0);
                    btnstart.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(MechTiming.this,"Enter Correct Code",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSwap += timeInMillies;
                myHandler.removeCallbacks(updateTimerMethod);

                verify=edtverify.getText().toString().trim();

                if(TextUtils.isEmpty(verify)) {
                    Toast.makeText(MechTiming.this,"Please Enter Your Code",Toast.LENGTH_LONG).show();
                } else if(tvtimer.getText().equals("00:00")) {
                    Toast.makeText(MechTiming.this,"Please Complete Your Work",Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("usr_email", strusremail);
                    params.put("usr_contact", strphone);
                    params.put("mech_email", strmechemail);
                    params.put("mech_garage", strgarage);
                    params.put("usr_bikemodel", strmodel);
                    params.put("usr_service", strservice);
                    params.put("appointment_date", strdate);
                    params.put("mech_time", tvtimer.getText().toString().trim());
                    params.put("req_id", strreqid);
                    params.put("req_type","Schedule");

                    PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_TIMER, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog = new SweetAlertDialog(MechTiming.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Work Completed")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    tvtimer.setText("00:00");
                                    Intent req = new Intent(MechTiming.this, NavigationDrawer.class);
                                    startActivity(req);
                                    finish();
                                }
                            });
                }
            }
        });
    }


    private Runnable updateTimerMethod=new Runnable() {
        @Override
        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            tvtimer.setText("" + String.format("%02d",minutes) + ":"
                    + String.format("%02d", seconds));
            myHandler.postDelayed(this, 0);
        }
    };


    private void code() {

        HashMap<String, String> params = new HashMap<>();
        params.put("req_id", strreqid);
        PerformNetworkcode request = new PerformNetworkcode(Apiurl.URL_USERCODE, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshdcode(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    value = obj.getString("code")
            ));
        }

        Log.d("value",value);
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    class PerformNetworkcode extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkcode(String url, HashMap<String, String> params, int requestCode) {
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
                    refreshdcode(object.getJSONArray("useruniquecode"));

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


    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
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
            progressDialog=new ProgressDialog(MechTiming.this);
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
                    params1.put("email", strusremail);
                    params1.put("title", "Request Status");
                    params1.put("message", "Your Order is Completed Thank You");
                    params1.put("type","OrderCmplt");

                    // Network Perform Code

                    PerformNetworkNotification notify = new PerformNetworkNotification(Apiurl.URL_NOTIFICATION, params1, CODE_POST_REQUEST);
                    notify.execute();
                }
                else {
                    Toast.makeText(getApplicationContext(),"message", Toast.LENGTH_SHORT).show();

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

                    Log.e("notification","sent");
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();
                    Log.e("notification","failed");
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
    public void onBackPressed() {
        Intent working=new Intent(MechTiming.this,NavigationDrawer.class);
        startActivity(working);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }
}
