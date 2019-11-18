package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class User_Request extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText edtcity,edtlandmark,edtpincode,edtcontact,edtappoinmentdate,edtnotes;
    Button btnrequest;
    Calendar date;
    Spinner spntime;
    String strnotes,strtime,strcurrenttime;
    String strbikemodel,strurl,strusrname,strcity,strlandmark,strpincode,strcontact,strservice,stremailid,strappoinment,strappointmentdate,strcurrentdate;
    List<User> data;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__request);

        data=new ArrayList<>();
        Toolbar toolbar=(Toolbar)findViewById(R.id.reqtool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Individual Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtcity=(EditText)findViewById(R.id.userCity);
        edtlandmark=(EditText)findViewById(R.id.userlandmark);
        edtpincode=(EditText)findViewById(R.id.userpincode);
        edtcontact=(EditText)findViewById(R.id.usercontno);
        edtappoinmentdate=(EditText)findViewById(R.id.appoinmentdate);
        spntime=(Spinner)findViewById(R.id.spinnertime);
        edtnotes=(EditText)findViewById(R.id.notes);
        btnrequest=(Button)findViewById(R.id.userrequest);

        SharedPreferences crpt=getSharedPreferences("crpt", Context.MODE_PRIVATE);
        strbikemodel=crpt.getString("model","");
        Log.d("model",strbikemodel);
        strservice=crpt.getString("srvc","");
        Log.d("srvc",strservice);

        SharedPreferences sharedpreferences=getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);

        data();
        getDateTime();
        getTime();

        edtappoinmentdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(hasFocus) {
                    showDateTimePicker();
                }
            }
        });

        edtappoinmentdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        btnrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strcity=edtcity.getText().toString().trim();
                strlandmark=edtlandmark.getText().toString().trim();
                strpincode=edtpincode.getText().toString().trim();
                strcontact=edtcontact.getText().toString().trim();
                strappointmentdate=edtappoinmentdate.getText().toString().trim();
                strtime=spntime.getSelectedItem().toString().trim();
                strcurrentdate=getDateTime().trim();
                strnotes=edtnotes.getText().toString().trim();

                if(TextUtils.isEmpty(strcity))
                {
                    edtcity.setError("Please Enter Your City");
                    edtcity.requestFocus();
                }
                else if(TextUtils.isEmpty(strlandmark))
                {
                    edtlandmark.setError("Please Enter Your Landmark");
                    edtlandmark.requestFocus();
                }
                else if(!isValidpincode(strpincode))
                {
                    edtpincode.setError("Please Enter The Valid Pincode");
                    edtpincode.requestFocus();
                }
                else if(!isValidContact(strcontact))
                {
                    edtcontact.setError("Please Enter Valid Mobile Number");
                    edtcontact.requestFocus();
                }
                else if(TextUtils.isEmpty(strappointmentdate))
                {
                    Toast.makeText(User_Request.this,"Please select your appointment Date",Toast.LENGTH_LONG).show();
                    edtappoinmentdate.requestFocus();
                }
                else if(strappointmentdate.equals(strcurrentdate))
                {
                    Toast.makeText(User_Request.this,"Do not Select Today Date",Toast.LENGTH_LONG).show();
                    edtappoinmentdate.requestFocus();
                    showDateTimePicker();
                }
                else if(strtime.equals("Please Select Your Appointment Time")){
                    Toast.makeText(User_Request.this,"Please select your appointment Time",Toast.LENGTH_LONG).show();
                    spntime.requestFocus();
                }
                else {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("req_email", stremailid);
                    params.put("Name",strusrname);
                    params.put("BikeModel", strbikemodel);
                    params.put("City", strcity);
                    params.put("Landmark", strlandmark);
                    params.put("Pincode", strpincode);
                    params.put("Contact", strcontact);
                    params.put("Service", strservice);
                    params.put("appointment_date", strappointmentdate);
                    params.put("appointment_time",strtime);
                    params.put("req_date",getDateTime());
                    params.put("req_time",getTime());
                    params.put("req_type","Individual");
                    params.put("req_notes",strnotes);

                    PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_USERREQUEST, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog=new SweetAlertDialog(User_Request.this,SweetAlertDialog.SUCCESS_TYPE)
                    .setContentText("Requested Successfull")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent req=new Intent(User_Request.this,UserNavigation.class);
                                    startActivity(req);
                                    finish();
                                }
                            });
                }
            }
        });
    }
       private void data(){
        HashMap<String, String> params = new HashMap<>();
        params.put("email",stremailid);
        PerformNetworkRequestFetch request = new PerformNetworkRequestFetch(Apiurl.URL_USERDETAILS, params, CODE_POST_REQUEST);
        request.execute();
    }

    private String getDateTime()
    {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date=new Date();
        strcurrentdate=simpleDateFormat.format(date);
        return strcurrentdate;
    }

    private String getTime()
    {
        Date time=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        strcurrenttime=simpleDateFormat.format(time);
        return strcurrenttime;
    }
//for select appointment date

    public void showDateTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                monthOfYear=monthOfYear+1;
                strappoinment=dayOfMonth+"/"+monthOfYear+"/"+year;
                Date selecteddate=date.getTime();
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                strappoinment=dateFormat.format(selecteddate);
                edtappoinmentdate.setText(strappoinment);

            }
        };
        DatePickerDialog datePickerDialog = new  DatePickerDialog(User_Request.this,R.style.DialogTheme, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),   currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        date.add(Calendar.DAY_OF_MONTH,14);
        datePickerDialog.getDatePicker().setMaxDate(date.getTimeInMillis());
        datePickerDialog.show();
    }

    private void refreshuserdata(JSONArray helptips) throws JSONException {
        data.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            data.add(new User(
                    strusrname=obj.getString("name"),
                    strcontact=obj.getString("contact"),
                    strcity=obj.getString("city"),
                    strlandmark=obj.getString("landmark"),
                    strpincode=obj.getString("pincode"),
                    strurl=obj.getString("image_url")
            ));
        }
        edtcontact.setText(strcontact);
        edtcity.setText(strcity);
        edtlandmark.setText(strlandmark);
        edtpincode.setText(strpincode);
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private class PerformNetworkRequestFetch extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;
        PerformNetworkRequestFetch(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(User_Request.this);
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
                    refreshuserdata(object.getJSONArray("userdetails"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
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

    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
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
            progressDialog1=new ProgressDialog(User_Request.this);
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
                    edtcity.setText("");
                    edtlandmark.setText("");
                    edtpincode.setText("");
                    edtcontact.setText("");
                    edtappoinmentdate.setText("");

                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("email","admin@gmail.com" );
                    params1.put("title", "New Request:Individual ");
                    params1.put("message", "New Request from "+stremailid + " for service "+strservice);
                    params1.put("type","NewRequest");

                    // Network Perform Code

                    PerformNetworkNotification notify = new PerformNetworkNotification(Apiurl.URL_NOTIFYADMIN, params1, CODE_POST_REQUEST);
                    notify.execute();

                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);

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


    //phone validation
    private boolean isValidContact(String vcontact) {
        String Contact_PATTERN = "\\d{10}";

        Pattern pattern = Pattern.compile(Contact_PATTERN);
        Matcher matcher = pattern.matcher(vcontact);
        return matcher.matches();
    }

    private boolean isValidpincode(String pincode) {
        if (pincode != null && pincode.length()==6) {
            return true;
        }
        return false;
    }

    private boolean isValidName(String name) {
        if (name != null && name.length() >=3 ) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),RequesterType.class);
        startActivity(i);
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
