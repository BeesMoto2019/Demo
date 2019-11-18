package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MechLatestReq extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;
    String strurl;
    int intreqid;
    TextView tvcontactno, tvCity, tvgrgname,tvstatus,tvtext;
    String strreqid,loginemail,strlandmark, strpincode, strgaragename, stringcontact, stringcity,strmechstatus,strappontment,stremailid;
    String requestid;
    SweetAlertDialog sweetAlertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_latest_req);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Latest Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userdetaillist = new ArrayList<>();
        datalist = findViewById(R.id.listview);
        datalist.setDivider(null);
        tvtext =findViewById(R.id.text);
        tvcontactno = (TextView) findViewById(R.id.contact);
        tvCity = (TextView) findViewById(R.id.city);
        tvgrgname = (TextView) findViewById(R.id.grgname);
        tvstatus = (TextView) findViewById(R.id.status);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        loginemail = sp.getString("Email", "");
        Log.d("Email", loginemail);

        SharedPreferences sp1 = getSharedPreferences("User_Key", Context.MODE_PRIVATE);
        strlandmark =sp1.getString("landmark","");
        Log.d("landmark",strlandmark);
        strpincode = sp1.getString("pincode","");
        Log.d("pincode",strpincode);
        requestid = sp1.getString("reqid","");
        Log.d("reqid",requestid);

        getdetails();
    }

    private void getdetails() {

        HashMap<String, String> params = new HashMap<>();
        params.put("Emailid", loginemail);
        PerformNetworkdetail request = new PerformNetworkdetail(Apiurl.URL_MECHDETAIL, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void data() {
        HashMap<String, String> params = new HashMap<>();
        params.put("mech_email", loginemail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_SENDREQUEST, params, CODE_POST_REQUEST);
        request.execute();
    }


    private void refreshdetails(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    strgaragename = obj.getString("garage_name"),
                    stringcontact = obj.getString("mech_contact"),
                    stringcity = obj.getString("mech_city"),
                    strmechstatus = obj.getString("Status"),
                    strurl=obj.getString("image_url")

            ));
        }
        tvgrgname.setText(strgaragename);
        tvcontactno.setText(stringcontact);
        tvCity.setText(stringcity);
        tvstatus.setText(strmechstatus);

        SharedPreferences garage=getSharedPreferences("Garage",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=garage.edit();
        editor.putString("garage",strgaragename);
        editor.commit();

        int st1 = Integer.parseInt(strmechstatus);
        if(st1 == 0)
        {
            data();
        }
        else {
            tvtext.setText("Wait for admin's approval");
        }

    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(MechLatestReq.this, R.layout.raw_latest_req, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            final View listViewItem = inflater.inflate(R.layout.raw_latest_req, null, true);

            TextView tvemail=listViewItem.findViewById(R.id.reqemail);
            TextView tvname=listViewItem.findViewById(R.id.reqname);
            final TextView tvcontact = listViewItem.findViewById(R.id.contact);
            TextView tvbike = listViewItem.findViewById(R.id.bike);
            TextView tvservice = listViewItem.findViewById(R.id.service);
            final TextView tvlandmark = listViewItem.findViewById(R.id.landmark);
            final TextView tvcity = listViewItem.findViewById(R.id.city);
            final TextView tvpin = listViewItem.findViewById(R.id.pincode);
            final TextView tvreqid = listViewItem.findViewById(R.id.reqid);
            TextView tvappdate = listViewItem.findViewById(R.id.apmtdt);
            TextView tvapptime=listViewItem.findViewById(R.id.apmtime);
            TextView tvreqstatus = listViewItem.findViewById(R.id.reqstatus);
            Button request = listViewItem.findViewById(R.id.mechanic);

            final User reqid = Userlist.get(position);
            final User email=Userlist.get(position);
            final User name=Userlist.get(position);
            final User bike = Userlist.get(position);
            final User city = Userlist.get(position);
            final User pin = Userlist.get(position);
            final User landmark = Userlist.get(position);
            final User contact = Userlist.get(position);
            final User service = Userlist.get(position);
            final User date = Userlist.get(position);
            final User apptime=Userlist.get(position);
            final User reqstatus = Userlist.get(position);

            intreqid = reqid.getId();
            strreqid = Integer.toString(intreqid);
            tvreqid.setText(strreqid);
                tvemail.setText("User Email : "+email.getName());
                tvname.setText("Name : "+name.getEmail());
                tvcontact.setText("BikeModel : " + bike.getPhone());
                tvbike.setText("City : " + city.getAddress());
                tvservice.setText("Landmark : " + pin.getState());
                tvlandmark.setText("Pincode : " + landmark.getCity());
                tvcity.setText("Contact : " + contact.getLandmark());
                tvpin.setText("Service : " + service.getPincode());
                tvappdate.setText("Appointment Date : "+date.getPassword());
                tvapptime.setText("Appointment Time : "+apptime.getApptime());
                tvreqstatus.setText("reqstatus : "+reqstatus.getStatus());

                String strreqstatus = reqstatus.getStatus();
                int reqst = Integer.parseInt(strreqstatus);

                if(reqst == 0){
                    request.setClickable(false);
                    request.setTextColor(Color.WHITE);
                    Toast.makeText(getApplicationContext(),"This request has already approved by another mechanic",Toast.LENGTH_SHORT).show();
                }

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    stremailid=email.getName().trim();

                    strreqid = ((TextView) listViewItem.findViewById(R.id.reqid)).getText().toString();
                    strappontment=date.getPassword();

                    SharedPreferences preferences =getSharedPreferences("Req_id", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("Req_id",strreqid);
                    editor.commit();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("res_emailid", loginemail);
                    params.put("garage", tvgrgname.getText().toString());
                    params.put("landmark", strlandmark);
                    params.put("pincode", strpincode);
                    params.put("city", tvCity.getText().toString());
                    params.put("contact", tvcontactno.getText().toString());
                    params.put("req_id", strreqid);
                    params.put("appointment_date",strappontment);

                    // Network Perform Code

                    PerformNetworkRequestResponse request = new PerformNetworkRequestResponse(Apiurl.URL_INDIVIDUALRESPONSE, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog=new SweetAlertDialog(MechLatestReq.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Approved Successfull")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent req=new Intent(MechLatestReq.this,NavigationDrawer.class);
                                    startActivity(req);
                                    finish();
                                }
                            });
                }
            });

            return listViewItem;
        }

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
            progressDialog=new ProgressDialog(MechLatestReq.this);
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
                    refreshdetails(object.getJSONArray("mech_details"));

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

    private void refreshIndividualList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    obj.getInt("req_id"),
                    obj.getString("req_email"),
                    obj.getString("name"),
                    obj.getString("bikemodel"),
                    obj.getString("city"),
                    obj.getString("landmark"),
                    obj.getString("pincode"),
                    obj.getString("contact"),
                    obj.getString("service"),
                    obj.getString("appointment_date"),
                    obj.getString("appointment_time"),
                    obj.getString("status")
            ));
        }
        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        datalist.setAdapter(adapter);
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
            progressDialog1=new ProgressDialog(MechLatestReq.this);
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
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshIndividualList(object.getJSONArray("sendRequestToMech"));
                }
                else {
                    tvtext.setVisibility(View.VISIBLE);
                    tvtext.setText("YOU HAVE NO REQUEST YET!!!");
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

    class PerformNetworkRequestResponse extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;

        PerformNetworkRequestResponse(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(MechLatestReq.this);
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

                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("email", stremailid);
                    params1.put("title", "Request Status");
                    params1.put("message", "Your Request Is Approved by Mechanic "+loginemail);
                    params1.put("type","ApproveUser");

                    // Network Perform Code

                    PerformNetworkNotification notify = new PerformNetworkNotification(Apiurl.URL_NOTIFICATION, params1, CODE_POST_REQUEST);
                    notify.execute();

                    HashMap<String, String> params2 = new HashMap<>();
                    params2.put("email", "admin@gmail.com");
                    params2.put("title", "Request Status");
                    params2.put("message", "Request Is Approved by Mechanic "+loginemail+" for user "+stremailid);
                    params2.put("type","ApproveAdmin");

                    // Network Perform Code

                    PerformNetworkNotificationAdmin notifyadmin = new PerformNetworkNotificationAdmin(Apiurl.URL_NOTIFYADMIN, params2, CODE_POST_REQUEST);
                    notifyadmin.execute();

                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);


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

    class PerformNetworkNotificationAdmin extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkNotificationAdmin(String url, HashMap<String, String> params, int requestCode) {
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
        Intent back=new Intent(MechLatestReq.this,NavigationDrawer.class);
        startActivity(back);
        finish();
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