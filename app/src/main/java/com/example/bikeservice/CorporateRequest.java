package com.example.bikeservice;

import android.app.Activity;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CorporateRequest extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView searchlist;
    List<User> userdetaillist;
    List<User> combolist;
    List<User> comboinfo;
    UserListAdapter adapter;
    SearchView searchView;
    String Bikemodel;
    Spinner spncombo;
    LinearLayout datavalue;
    String srvcname;
    String strsrvcname,strsrvcinfo;
    String strnotes,strtime,strcurrenttime;
    String crptname,crptcity,strcontact,strlandmark,strcurrentdate,stremailid,land,pin,app_date;
    TextView tvsrvcname,tvsrvcinfo;
    String strmodel;
    Button btnnxt;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporate_request);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarcor);
        spncombo=(Spinner)findViewById(R.id.spinner);
        btnnxt=(Button)findViewById(R.id.nxt);
        datavalue=(LinearLayout)findViewById(R.id.textvalue);
        tvsrvcname=(TextView)findViewById(R.id.servcname);
        tvsrvcinfo=(TextView)findViewById(R.id.servcinfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Corporate Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView=(SearchView)findViewById(R.id.searchbike);

        SharedPreferences sharedpreferences=getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);

        SharedPreferences cmpnydetails=getSharedPreferences("cmpny",Context.MODE_PRIVATE);
        crptname=cmpnydetails.getString("Name","");
        crptcity=cmpnydetails.getString("City","");
        land=cmpnydetails.getString("Landmark","");
        pin=cmpnydetails.getString("Pincode","");
        strcontact=cmpnydetails.getString("Contact","");
        app_date=cmpnydetails.getString("appointment_date","");
        strtime=cmpnydetails.getString("appointment_time","");
        strcurrentdate=cmpnydetails.getString("req_date","");
        strcurrenttime=cmpnydetails.getString("req_time","");
        strnotes=cmpnydetails.getString("req_notes","");

        //edtsearch=(EditText)findViewById(R.id.Search);
        userdetaillist= new ArrayList<>();
        combolist= new ArrayList<>();
        comboinfo= new ArrayList<>();
        searchlist=findViewById(R.id.searchbar);
        data();

        searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(userdetaillist.get(position).getName(),false);
                strmodel=searchView.getQuery().toString().trim();

                HashMap<String, String> params = new HashMap<>();
                params.put("bike_name",strmodel);
                PerformNetworkCombo request = new PerformNetworkCombo(Apiurl.URL_FETCHSERVICECOMBO, params, CODE_POST_REQUEST);
                request.execute();
                spncombo.setVisibility(View.VISIBLE);
            }
        });

        spncombo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(spncombo.getSelectedItem()=="Please Select Your Service Here!!")
                {
                    datavalue.setVisibility(View.GONE);
                }
                else {
                    srvcname=spncombo.getSelectedItem().toString().trim();
                    getsrvc();
                    datavalue.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnnxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strmodel=searchView.getQuery().toString().trim();

                if (TextUtils.isEmpty(strmodel)) {
                    Toast.makeText(CorporateRequest.this,"Please Select Model",Toast.LENGTH_LONG).show();
                    searchView.requestFocus();

                }
                else if(spncombo.getSelectedItem()=="Please Select Your Service Here!!")
                {
                    Toast.makeText(CorporateRequest.this,"Please Select Your Service",Toast.LENGTH_LONG).show();
                    spncombo.requestFocus();
                }
                else {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("req_email", stremailid);
                    params.put("Name", crptname);
                    params.put("BikeModel", strmodel);
                    params.put("City",crptcity);
                    params.put("Landmark", land);
                    params.put("Pincode", pin);
                    params.put("Contact",strcontact);
                    params.put("Service",srvcname);
                    params.put("appointment_date", app_date);
                    params.put("appointment_time",strtime);
                    params.put("req_date", strcurrentdate);
                    params.put("req_time",strcurrenttime);
                    params.put("req_type","Corporate");
                    params.put("req_notes",strnotes);

                    PerformNetworkCorporateRequest request = new PerformNetworkCorporateRequest(Apiurl.URL_USERREQUEST, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog=new SweetAlertDialog(CorporateRequest.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Requested Successfull")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent req=new Intent(CorporateRequest.this,UserNavigation.class);
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
        //params.put("req_id",strreqid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_BIKELIST, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void getsrvc(){
        HashMap<String, String> params = new HashMap<>();
        params.put("srvc_name",srvcname);
        PerformNetworksrvc request = new PerformNetworksrvc(Apiurl.URL_FETCHSERVICE, params, CODE_POST_REQUEST);
        request.execute();

    }

    class UserListAdapter extends ArrayAdapter<User> implements Filterable {
        List<User> Userlist;
        List<User> filterdlist;

        public UserListAdapter(List<User> userlistt) {

            super(CorporateRequest.this, R.layout.raw_bike, userlistt);
            this.Userlist = userlistt;
            this.filterdlist=userlistt;
            this.filterdlist=new ArrayList<>();
            this.filterdlist.addAll(userlistt);
        }

        @Override
        public int getCount() {
            return userdetaillist.size();
        }

        @Override
        public User getItem(int position) {
            return userdetaillist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_bike, null, true);

            TextView tvbike = listViewItem.findViewById(R.id.bike);
            final User model = Userlist.get(position);

            tvbike.setText(model.getName());

            Bikemodel=model.getName();

            return listViewItem;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            userdetaillist.clear();
            if (charText.length() == 0) {
                userdetaillist.addAll(filterdlist);
            } else {
                for (User wp : filterdlist) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        userdetaillist.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }
        private void refreshIndividualList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            {
                userdetaillist.add(new User(
                        obj.getString("model")

                ));
            }
        }

        adapter = new UserListAdapter(userdetaillist);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if(TextUtils.isEmpty(newText))
                {
                    adapter.clear();
                }
                else {
                    adapter.filter(newText);
                    searchlist.setAdapter(adapter);
                }

                return false;
            }
        });
    }

    private void refreshcombo(JSONArray helptips) throws JSONException {
        combolist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            combolist.add(new User(
                    obj.getString("srvc_name")
            ));
        }
        List<String> lables = new ArrayList<String>();
        lables.add("Please Select Your Service Here!!");
        for (int i = 0; i < combolist.size(); i++) {
            lables.add(combolist.get(i).getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spncombo.setAdapter(spinnerAdapter);
    }

    private void refreshcrptsrvcinfo(JSONArray helptips) throws JSONException {
        comboinfo.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            comboinfo.add(new User(
                    strsrvcname=obj.getString("srvc_name"),
                    strsrvcinfo=obj.getString("srvc_info")
            ));
        }
        tvsrvcname.setText(strsrvcname);
        tvsrvcinfo.setText(strsrvcinfo);
        //Auto refresh listview
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
            progressDialog=new ProgressDialog(CorporateRequest.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
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
                    refreshIndividualList(object.getJSONArray("getBike"));

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

    private class PerformNetworkCombo extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        ProgressDialog progressDialog1;
        int requestCode;
        PerformNetworkCombo(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(CorporateRequest.this);
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                    refreshcombo(object.getJSONArray("fetchsrvcombo"));

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

    private class PerformNetworksrvc extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog2;
        PerformNetworksrvc(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog2=new ProgressDialog(CorporateRequest.this);
            progressDialog2.setMessage("Please Wait...");
            progressDialog2.setCancelable(false);
            progressDialog2.setIndeterminate(true);
            progressDialog2.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog2.show();
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                    refreshcrptsrvcinfo(object.getJSONArray("fetchsrvc"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog2.dismiss();
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

    class PerformNetworkCorporateRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog3;

        PerformNetworkCorporateRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog3=new ProgressDialog(CorporateRequest.this);
            progressDialog3.setMessage("Please Wait...");
            progressDialog3.setCancelable(false);
            progressDialog3.setIndeterminate(true);
            progressDialog3.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog3.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("email","admin@gmail.com" );
                    params1.put("title", "New Request:Corporate ");
                    params1.put("message", "New Request from "+stremailid + " and Corporate Name "+crptname + " for service "+srvcname);
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
            progressDialog3.dismiss();
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
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

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

    public void hideKeyboard(View view) {

        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {

        Intent back=new Intent(CorporateRequest.this,RequesterType.class);
        startActivity(back);
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
