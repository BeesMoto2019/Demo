package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;

public class MechEmergencyActive extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    ListView emergactive;
    List<User> emrglist;
    String loginemail,strid,strlat,strlon,reslat,reslon,strresid;
    int intid,res_id;
    TextView emegactive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_emergency_active);

        Toolbar toolbar=(Toolbar)findViewById(R.id.emergactive);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Active Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emegactive=(TextView)findViewById(R.id.textmechemegactive);
        emergactive=(ListView)findViewById(R.id.emergactivelist);
        emrglist=new ArrayList<>();
        emergactive.setDivider(null);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        loginemail = sp.getString("Email", "");
        Log.d("Email", loginemail);

        emergactive();
    }

    private void emergactive(){
        HashMap<String, String> params = new HashMap<>();
        params.put("res_email",loginemail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_MECHEMERGACTIVE, params, CODE_POST_REQUEST);
        request.execute();

    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(MechEmergencyActive.this, R.layout.raw_emergency_active, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_emergency_active, null, true);

            TextView tvname=listViewItem.findViewById(R.id.uname);
            TextView tvemail=listViewItem.findViewById(R.id.uemail);
            TextView tvcontact=listViewItem.findViewById(R.id.ucontact);
            TextView tvsrvc=listViewItem.findViewById(R.id.uservc);
            Button track=listViewItem.findViewById(R.id.track);
            Button active=listViewItem.findViewById(R.id.aorder);

            final User eid = Userlist.get(position);
            final User resid=Userlist.get(position);
            final User name = Userlist.get(position);
            final User email= Userlist.get(position);
            final User contact= Userlist.get(position);
            final User service=Userlist.get(position);
            final User latitute=Userlist.get(position);
            final User longitute=Userlist.get(position);
            final User res_latitute=Userlist.get(position);
            final User res_longitute=Userlist.get(position);

            intid=eid.getId();
            strid=Integer.toString(intid);
            res_id=resid.getDate();
            strresid=Integer.toString(res_id);

            tvname.setText("Name : "+name.getName());
            tvemail.setText("Email : "+email.getEmail());
            tvcontact.setText("Contact : "+contact.getPhone());
            tvsrvc.setText("Service : "+service.getAddress());

            strlat = latitute.getState();
            strlon = longitute.getCity();
            reslat = res_latitute.getLandmark();
            reslon = res_longitute.getPincode();

            //Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            // statuss.setText(usersstatus.getStatus());

            track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intid=eid.getDate();
                    strid=Integer.toString(intid);
                    res_id=resid.getDate();
                    strresid=Integer.toString(res_id);

                    strlat = latitute.getState();
                    strlon = longitute.getCity();
                    reslat = res_latitute.getLandmark();
                    reslon = res_longitute.getPincode();

                    SharedPreferences getid= getSharedPreferences("id", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=getid.edit();
                    editor.putString("eid",strid);
                    editor.putString("resid",strresid);
                    editor.putString("latitute",strlat);
                    editor.putString("longitute",strlon);
                    editor.putString("res_lat",reslat);
                    editor.putString("res_lon",reslon);
                    editor.commit();

                    Intent track=new Intent(MechEmergencyActive.this,MechTrackUser.class);
                    startActivity(track);
                }
            });

            active.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intid=eid.getDate();
                    strid=Integer.toString(intid);

                    res_id=resid.getDate();
                    strresid=Integer.toString(res_id);

                    String stremail=email.getEmail();
                    String strcontact=contact.getPhone();
                    String strsrvc=service.getAddress();

                    SharedPreferences getdata= getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=getdata.edit();
                    editor.putString("eid",strid);
                    editor.putString("resid",strresid);
                    editor.putString("email",stremail);
                    editor.putString("contact",strcontact);
                    editor.putString("srvc",strsrvc);
                    editor.commit();

                    Intent work=new Intent(MechEmergencyActive.this,EmergencyWork.class);
                    startActivity(work);
                }
            });

            return listViewItem;
        }
    }

    private void refreshcurrentlist(JSONArray helptips) throws JSONException {
        emrglist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            emrglist.add(new User(obj.getInt("e_id"),
                    obj.getInt("res_id"),
                    obj.getString("Name"),
                    obj.getString("Email"),
                    obj.getString("Contact"),
                    obj.getString("Service"),
                    obj.getString("Latitute"),
                    obj.getString("Longitute"),
                    obj.getString("res_latitute"),
                    obj.getString("res_longitute")
            ));
        }

        UserListAdapter adapter = new UserListAdapter(emrglist);
        emergactive.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(MechEmergencyActive.this);
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
                    refreshcurrentlist(object.getJSONArray("emergency_active"));

                }
                else {
                    emegactive.setVisibility(View.VISIBLE);
                    emegactive.setText("YOU HAVE NO ACTIVE ORDERS YET!!!");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back=new Intent(MechEmergencyActive.this,NavigationDrawer.class);
        startActivity(back);
        finish();
    }
}
