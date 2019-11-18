package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserCurrentOrders extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView currentlist;
    List<User> userdetaillist;
    String stremailid;
    int intid;
    String strid,strcode;
    ImageView imgclose;
    BottomSheetDialog dialog;
    TextView tvid,tvcode,tvresemail,tvresgarage,tvcont,tvdate,tvbikemodel,tvservicetype;
    String stremail,strgarage,strcont,strdate,strbm,strservice;
    String status;
    TextView usrcrnt;
    List<User> approvedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_current_orders);
        Toolbar toolbar=(Toolbar)findViewById(R.id.currenttool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Current Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        usrcrnt=(TextView)findViewById(R.id.textusrcrnt);
        userdetaillist= new ArrayList<>();
        approvedata=new ArrayList<>();
        currentlist=findViewById(R.id.currentlist);
        currentlist.setDivider(null);

        SharedPreferences sharedpreferences= getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);
        currentreq();
    }

    private void currentreq() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", stremailid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_CURRENTORDERS, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void approvereq(){
        HashMap<String, String> params = new HashMap<>();
        params.put("req_id",strid);
        PerformNetworkApproveRequest request = new PerformNetworkApproveRequest(Apiurl.URL_USRAPPROVEREQ, params, CODE_POST_REQUEST);
        request.execute();

    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(UserCurrentOrders.this, R.layout.raw_usercurrent_orderlist, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_usercurrent_orderlist, null, true);

            TextView tvreqid=listViewItem.findViewById(R.id.reqid);

            TextView tvsdate=listViewItem.findViewById(R.id.serdate);
            TextView tvservice=listViewItem.findViewById(R.id.stype);
            TextView tvbike=listViewItem.findViewById(R.id.bike);
            TextView statuss=listViewItem.findViewById(R.id.status);
            ImageView info=listViewItem.findViewById(R.id.moreinfo);

            final User reqid = Userlist.get(position);

            final User date = Userlist.get(position);
            final User service= Userlist.get(position);
            final User bike= Userlist.get(position);
            final User usersstatus=Userlist.get(position);

            intid=reqid.getId();
            strid=Integer.toString(intid);
            tvreqid.setText(strid);

            tvsdate.setText("Service Date : "+date.getEmail());
            tvservice.setText("Service : "+service.getService());
            tvbike.setText("BikeModel : "+bike.getName());
            String st = usersstatus.getStatus();
            //Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            // statuss.setText(usersstatus.getStatus());
            int st1 = Integer.parseInt(st);

            if(st1 == 1)
            {
                status="Pending";
                statuss.setTextColor(Color.parseColor("#FF0000"));
            }
            else if(st1==2)
            {
                status="Approve";
                statuss.setTextColor(Color.parseColor("#FFAE42"));
            }
            else if(st1==3)
            {
                statuss.setVisibility(View.GONE);
                info.setVisibility(View.VISIBLE);
            }
            statuss.setText(status);

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intid=reqid.getId();
                    strid=Integer.toString(intid);
                    approvereq();

                    dialog=new BottomSheetDialog(UserCurrentOrders.this);
                    LayoutInflater layoutInflater=getLayoutInflater();
                    View view1 = layoutInflater.inflate(R.layout.raw_user_approved_request, null);
                    dialog.setContentView(view1);

                    imgclose=(ImageView)view1.findViewById(R.id.closewindow);

                    tvid=view1.findViewById(R.id.rid);
                    tvresgarage=view1.findViewById(R.id.mechgarage);
                    tvresemail=view1.findViewById(R.id.mechemail);
                    tvcont=view1.findViewById(R.id.mechcontact);


                    tvcode=view1.findViewById(R.id.code);
                    tvbikemodel=view1.findViewById(R.id.model);
                    tvservicetype=view1.findViewById(R.id.service);
                    tvdate=view1.findViewById(R.id.date);

                    imgclose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                    dialog.show();
                }
            });

            return listViewItem;

        }
    }
    private void refreshcurrentlist(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(obj.getInt("req_id"),
                    obj.getString("appointment_date"),
                    obj.getString("Service"),
                    obj.getString("BikeModel"),
                    obj.getString("Status")
            ));
        }

        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        currentlist.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(UserCurrentOrders.this);
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
                    refreshcurrentlist(object.getJSONArray("USRCurrentOrders"));
                }

                else {
                    usrcrnt.setVisibility(View.VISIBLE);
                    usrcrnt.setText("YOU HAVE NO CURRENT ORDERS YET!!!");
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

    private void refreshApprovelist(JSONArray helptips) throws JSONException {
        approvedata.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            approvedata.add(new User(
                    obj.getInt("req_id"),
                    strcode = obj.getString("code"),
                    stremail = obj.getString("Res_email"),
                    strgarage = obj.getString("Garage_name"),
                    strcont = obj.getString("contact"),
                    strdate = obj.getString("appointment_date"),
                    strbm = obj.getString("BikeModel"),
                    strservice = obj.getString("Service")
            ));
        }

        tvcode.setText(strcode);
        tvresemail.setText(stremail);
        tvresgarage.setText(strgarage);
        tvcont.setText(strcont);
        tvdate.setText(strdate);
        tvbikemodel.setText(strbm);
        tvservicetype.setText(strservice);
    }


    private class PerformNetworkApproveRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;
        PerformNetworkApproveRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(UserCurrentOrders.this);
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshApprovelist(object.getJSONArray("user_approve_request"));
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
}
