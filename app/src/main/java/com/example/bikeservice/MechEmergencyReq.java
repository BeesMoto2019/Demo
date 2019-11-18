package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class MechEmergencyReq extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView emergencylist;
    List<User> emergency;
    String loginemail;
    String email,strid;
    TextView tvemerg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_emergency_req);
        Toolbar toolbar=(Toolbar)findViewById(R.id.mechtoolreq);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emergency=new ArrayList<>();
        emergencylist=(ListView)findViewById(R.id.emerglist);
        emergencylist.setDivider(null);
        tvemerg=(TextView)findViewById(R.id.textemergency);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        loginemail = sp.getString("Email", "");
        Log.d("Email", loginemail);

        data();
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        //params.put("req_id",strreqid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_MECHEMERGENCY, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(MechEmergencyReq.this, R.layout.raw_emergencymechlist, userlistt);
            this.Userlist = userlistt;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_emergencymechlist, null, true);

            CardView value=listViewItem.findViewById(R.id.cardvalue);

            final User mechemail=Userlist.get(position);

            email=mechemail.getCity();

            if(loginemail.equals(email)) {
                tvemerg.setVisibility(View.GONE);
                value.setVisibility(View.VISIBLE);
                TextView tvreqname = listViewItem.findViewById(R.id.name);
                TextView tvreqemail = listViewItem.findViewById(R.id.email);
                TextView tvcontact = listViewItem.findViewById(R.id.contact);
                TextView tvlat = listViewItem.findViewById(R.id.latitute);
                TextView tvlong = listViewItem.findViewById(R.id.longitute);
                TextView tvservice=listViewItem.findViewById(R.id.service);
                Button btnapprove = listViewItem.findViewById(R.id.btnapprove);

                final User reqid = Userlist.get(position);
                final User reqname = Userlist.get(position);
                final User reqemail = Userlist.get(position);
                final User contact = Userlist.get(position);
                final User lat = Userlist.get(position);
                final User longi = Userlist.get(position);
                final User service=Userlist.get(position);

                tvreqname.setText("Requester Name : " + reqname.getName());
                tvreqemail.setText("Requester Email : " + reqemail.getEmail());
                tvcontact.setText("Requester Contact : " + contact.getPhone());
                tvlat.setText("Latitute : " + lat.getLatitute());
                tvlong.setText("Longitute : " + longi.getLongitute());
                tvservice.setText("Service : "+service.getGarage());

                btnapprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id=reqid.getId();
                        strid=String.valueOf(id);
                        String strname=reqname.getName();
                        String stremail=reqemail.getEmail();
                        String latitude = String.valueOf(lat.getLatitute());
                        String longitude = String.valueOf(longi.getLongitute());

                        SharedPreferences latlng = getSharedPreferences("latlong", MODE_PRIVATE);
                        SharedPreferences.Editor editor = latlng.edit();
                        editor.putString("id",strid);
                        editor.putString("name",strname);
                        editor.putString("email",stremail);
                        editor.putString("lat", latitude);
                        editor.putString("longi", longitude);
                        editor.commit();

                        Intent pass = new Intent(MechEmergencyReq.this,MechViewLocation.class);
                        startActivity(pass);
                        finish();
                    }
                });
            }
            else {
                    value.setVisibility(View.GONE);

                }
            return listViewItem;
        }
    }

    private void refreshIndividualList(JSONArray helptips) throws JSONException {
        emergency.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            {
                emergency.add(new User(
                        obj.getInt("e_id"),
                        obj.getString("Name"),
                        obj.getString("mech_email"),
                        obj.getString("Email"),
                        obj.getString("Contact"),
                        obj.getDouble("Latitute"),
                        obj.getDouble("Longitute"),
                        obj.getString("Service")
                ));
            }
        }
        UserListAdapter adapter = new UserListAdapter(emergency);
        emergencylist.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(MechEmergencyReq.this);
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
                    refreshIndividualList(object.getJSONArray("MechEmergency"));

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
        Intent back=new Intent(MechEmergencyReq.this,NavigationDrawer.class);
        startActivity(back);
        finish();
    }
}
