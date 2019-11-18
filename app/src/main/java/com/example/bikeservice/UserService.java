package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserService extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView searchlist;
    List<User> userdetaillist;
    List<User> combolist;
    List<User> comboinfo;
    UserService.UserListAdapter adapter;
    SearchView searchView;
    String Bikemodel,srvcname;
    String strsrvcname,strsrvcinfo;
    Spinner combo;
    TextView tvsrvcname,tvsrvcinfo;
    LinearLayout datavalue;
    String strvalue,strmodel;
    Button btnnxt;
    RelativeLayout spin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_service);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarcor);
        combo=(Spinner)findViewById(R.id.spinnercombo);
        btnnxt=(Button)findViewById(R.id.nxt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchView=(SearchView)findViewById(R.id.searchbike);
        datavalue=(LinearLayout)findViewById(R.id.textvalue);
        tvsrvcname=(TextView)findViewById(R.id.servcname);
        tvsrvcinfo=(TextView)findViewById(R.id.servcinfo);
        userdetaillist= new ArrayList<>();
        combolist= new ArrayList<>();
        comboinfo= new ArrayList<>();
        searchlist=findViewById(R.id.searchbar);
        spin = (RelativeLayout)findViewById(R.id.spin_back);
        searchlist.setDivider(null);
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
                combo.setVisibility(View.VISIBLE);
                spin.setVisibility(View.VISIBLE);
            }
        });

        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(combo.getSelectedItem()=="Please Select Your Service Here!!")
                {
                    datavalue.setVisibility(View.GONE);
                }
                else {
                    srvcname=combo.getSelectedItem().toString().trim();
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
                    Toast.makeText(UserService.this,"Please Select Model",Toast.LENGTH_LONG).show();
                    searchView.requestFocus();

                }
                else if(combo.getSelectedItem()=="Please Select Your Service Here!!")
                {
                    Toast.makeText(UserService.this,"Please Select Your Service",Toast.LENGTH_LONG).show();
                    combo.requestFocus();
                }
                else {
                    SharedPreferences crpt = getSharedPreferences("crpt", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = crpt.edit();
                    editor.putString("model", strmodel);
                    editor.putString("srvc", srvcname);
                    editor.commit();
                    Intent req = new Intent(UserService.this, User_Request.class);
                    startActivity(req);
                    finish();
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

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    class UserListAdapter extends ArrayAdapter<User> implements Filterable {
        List<User> Userlist;
        List<User> filterdlist;

        public UserListAdapter(List<User> userlistt) {

            super(UserService.this, R.layout.raw_bike, userlistt);
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
        combo.setAdapter(spinnerAdapter);
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
            progressDialog=new ProgressDialog(UserService.this);
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
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
        int requestCode;
        ProgressDialog progressDialog1;
        PerformNetworkCombo(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(UserService.this);
            progressDialog1.setCancelable(false);
            progressDialog1.setIndeterminate(true);
            progressDialog1.setMessage("Please Wait...");
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
            progressDialog2=new ProgressDialog(UserService.this);
            progressDialog2.setCancelable(false);
            progressDialog2.setIndeterminate(true);
            progressDialog2.setMessage("Please Wait...");
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

                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
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

    public void hideKeyboard(View view) {

        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {

        Intent back=new Intent(UserService.this,RequesterType.class);
        startActivity(back);
    }
}
