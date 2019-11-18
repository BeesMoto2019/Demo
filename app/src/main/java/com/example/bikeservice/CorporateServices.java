package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CorporateServices extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<User> userdetaillist;
    EditText edtlandmark,edtpincode,edtdate,edtcity,edtnotes;
    Spinner spntime;
    Spinner corporate;
    String strnotes,strtime,strcurrenttime;
    String crptname,crptcity,strcontact,strlandmark,strpincode,strmodel,strservice,strcurrentdate,strappoinment,stremailid,land,pin,app_date,strcity;
    Calendar date;
    Button crptreq;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporate_services);
        userdetaillist= new ArrayList<>();
        Toolbar toolbar=(Toolbar)findViewById(R.id.crptsrvc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Corporate Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        corporate=(Spinner)findViewById(R.id.crpt);
        edtcity=(EditText)findViewById(R.id.city);
        edtcity.setEnabled(false);
        edtlandmark=(EditText)findViewById(R.id.landmark);
        edtlandmark.setEnabled(false);
        edtpincode=(EditText)findViewById(R.id.pincode);
        edtpincode.setEnabled(false);
        edtdate=(EditText)findViewById(R.id.date);
        spntime=(Spinner)findViewById(R.id.spinnertime);
        edtnotes=(EditText)findViewById(R.id.notes);
        crptreq=(Button)findViewById(R.id.crptrequest);

        SharedPreferences crpt=getSharedPreferences("crpt", Context.MODE_PRIVATE);
        strmodel=crpt.getString("model","");
        Log.d("model",strmodel);
        strservice=crpt.getString("srvc","");
        Log.d("srvc",strservice);

        SharedPreferences sharedpreferences=getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);

        getusrphone();
        getcrpt();
        corporate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(corporate.getSelectedItem()=="Please Select Your Company Name Here!!")
                {
                    //((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                    edtcity.setText("");
                    edtlandmark.setText("");
                    edtpincode.setText("");
                    edtdate.setText("");
                    edtnotes.setText("");
                }
                else {
                    //((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                    crptname=corporate.getSelectedItem().toString().trim();
                    getcrptdetails();
                    edtcity.setError(null);
                    edtcity.clearFocus();
                    edtlandmark.setError(null);
                    edtlandmark.clearFocus();
                    edtpincode.setError(null);
                    edtpincode.clearFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getDateTime();
        getTime();

        edtdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showDateTimePicker();
                }
            }
        });

        edtdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        crptreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crptcity=edtcity.getText().toString().trim();
                land=edtlandmark.getText().toString().trim();
                pin=edtpincode.getText().toString().trim();
                app_date=edtdate.getText().toString().trim();
                strtime=spntime.getSelectedItem().toString().trim();
                strcurrentdate=getDateTime().trim();
                strnotes=edtnotes.getText().toString().trim();

                if(TextUtils.isEmpty(crptcity))
                {
                    edtcity.setError("Please Select any Corporate");
                    edtcity.requestFocus();
                }
                else if(TextUtils.isEmpty(land))
                {
                    edtlandmark.setError("Please Select any Corporate");
                    edtlandmark.requestFocus();
                }
                else if(TextUtils.isEmpty(pin))
                {
                    edtpincode.setError("Please Select any Corporate");
                    edtpincode.requestFocus();
                }
                else if(TextUtils.isEmpty(app_date))
                {
                    Toast.makeText(CorporateServices.this,"Please select your appointment Date",Toast.LENGTH_LONG).show();
                    edtdate.requestFocus();
                }
                else if(app_date.equals(strcurrentdate))
                {
                    Toast.makeText(CorporateServices.this,"Do not Select Today Date",Toast.LENGTH_LONG).show();
                    edtdate.requestFocus();
                    showDateTimePicker();
                }
                else if(strtime.equals("Please Select Your Appointment Time")){
                    Toast.makeText(CorporateServices.this,"Please select your appointment Time",Toast.LENGTH_LONG).show();
                    spntime.requestFocus();
                }
                else {

                    SharedPreferences cmpnydetails=getSharedPreferences("cmpny",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=cmpnydetails.edit();
                    editor.putString("req_email", stremailid);
                    editor.putString("Name", crptname);
                    editor.putString("City",crptcity);
                    editor.putString("Landmark", land);
                    editor.putString("Pincode", pin);
                    editor.putString("Contact",strcontact);
                    editor.putString("appointment_date", app_date);
                    editor.putString("appointment_time",strtime);
                    editor.putString("req_date", getDateTime());
                    editor.putString("req_time",getTime());
                    editor.putString("req_notes",strnotes);
                    editor.commit();
                    Intent req = new Intent(CorporateServices.this, CorporateRequest.class);
                    startActivity(req);
                    finish();
                }
            }
        });
    }

    private void getcrpt() {

        HashMap<String, String> params = new HashMap<>();
        PerformNetworkdetail request = new PerformNetworkdetail(Apiurl.URL_CRPTLIST, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void getcrptdetails() {

        HashMap<String, String> params = new HashMap<>();
        params.put("crpt_name",crptname);
        PerformNetworkcrptdetail details = new PerformNetworkcrptdetail(Apiurl.URL_CRPTDETAILS, params, CODE_POST_REQUEST);
        details.execute();
    }

    private void getusrphone() {

        HashMap<String, String> params = new HashMap<>();
        params.put("email",stremailid);
        PerformNetworkusrdetail details = new PerformNetworkusrdetail(Apiurl.URL_USRPHONENO, params, CODE_POST_REQUEST);
        details.execute();
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
                edtdate.setText(strappoinment);

            }
        };
        DatePickerDialog datePickerDialog = new  DatePickerDialog(CorporateServices.this,R.style.DialogTheme, dateSetListener, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),   currentDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        date.add(Calendar.DAY_OF_MONTH,14);
        datePickerDialog.getDatePicker().setMaxDate(date.getTimeInMillis());
        datePickerDialog.show();
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void refreshdetails(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                     obj.getString("crpt_name")

            ));
        }
        List<String> lables = new ArrayList<String>();
        lables.add("Please Select Your Company Name Here!!");
        for (int i = 0; i < userdetaillist.size(); i++) {
            lables.add(userdetaillist.get(i).getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        corporate.setAdapter(spinnerAdapter);

    }

    class PerformNetworkdetail extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;

        PerformNetworkdetail(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(CorporateServices.this);
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
                    refreshdetails(object.getJSONArray("getcrpt"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                } else {
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

    private void refreshcrptdetails(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    strcity=obj.getString("crpt_city"),
                    strlandmark=obj.getString("landmark"),
                    strpincode=obj.getString("pincode")
            ));

        }
        edtcity.setText(strcity);
        edtlandmark.setText(strlandmark);
        edtpincode.setText(strpincode);
        //Auto refresh listview
    }

    private void refreshusrdetails(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    strcontact=obj.getString("contact")
            ));

        }
    }


    class PerformNetworkcrptdetail extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;

        PerformNetworkcrptdetail(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(CorporateServices.this);
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
                    refreshcrptdetails(object.getJSONArray("crptdetails"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

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

    class PerformNetworkusrdetail extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkusrdetail(String url, HashMap<String, String> params, int requestCode) {
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
                    refreshusrdetails(object.getJSONArray("usrphone"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

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
}
