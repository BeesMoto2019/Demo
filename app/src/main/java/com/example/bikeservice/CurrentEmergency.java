package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class CurrentEmergency extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    ListView emergency;
    List<User> emrglist;
    int intid;
    String strid,stremailid,strusrlat,strusrlon;
    TextView txtusremrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_emergency);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolcrntemerg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtusremrg=(TextView)findViewById(R.id.textusrcrntemrg);

        emergency=(ListView)findViewById(R.id.emergencylist);
        emergency.setDivider(null);
        emrglist=new ArrayList<>();

        SharedPreferences sharedpreferences= getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);

        currentreq();
    }

    private void currentreq(){
        HashMap<String, String> params = new HashMap<>();
        params.put("Email",stremailid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_USEREMERGENCYRCRD, params, CODE_POST_REQUEST);
        request.execute();

    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(CurrentEmergency.this, R.layout.raw_crnt_emergency_user, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_crnt_emergency_user, null, true);

            TextView tveid=listViewItem.findViewById(R.id.eid);
            TextView tvgrgname=listViewItem.findViewById(R.id.name);
            TextView tvresemail=listViewItem.findViewById(R.id.email);
            TextView tvrescontact=listViewItem.findViewById(R.id.contact);
            TextView tvsrvc=listViewItem.findViewById(R.id.srvc);
            TextView tvcode=listViewItem.findViewById(R.id.codeemrg);
            Button track=listViewItem.findViewById(R.id.brntrack);

            final User eid = Userlist.get(position);
            final User gragename = Userlist.get(position);
            final User email= Userlist.get(position);
            final User contact= Userlist.get(position);
            final User service=Userlist.get(position);
            final User usrlat=Userlist.get(position);
            final User usrlon=Userlist.get(position);
            final User code=Userlist.get(position);

            intid=eid.getId();
            strid=Integer.toString(intid);
            tveid.setText(strid);

            tvgrgname.setText("Garage Name : "+gragename.getName());
            tvresemail.setText("Mech Email : "+email.getGarage());
            tvrescontact.setText("Mech Contact : "+contact.getEmail());
            tvsrvc.setText("Service : "+service.getPhone());
            tvcode.setText("Unique Code : "+code.getPincode());

            strusrlat=usrlat.getCity();
            strusrlon=usrlon.getLandmark();

            //Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            // statuss.setText(usersstatus.getStatus());

            track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intid=eid.getId();
                    strid=Integer.toString(intid);

                    strusrlat=usrlat.getCity();
                    strusrlon=usrlon.getLandmark();

                    SharedPreferences getid= getSharedPreferences("location",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=getid.edit();
                    editor.putString("resid",strid);
                    editor.putString("usrlat",strusrlat);
                    editor.putString("usrlon",strusrlon);
                    editor.commit();

                    Intent track=new Intent(CurrentEmergency.this,UserTrackMech.class);
                    startActivity(track);
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
            emrglist.add(new User(obj.getInt("res_id"),
                    obj.getString("res_garage"),
                    obj.getString("res_email"),
                    obj.getString("res_contact"),
                    obj.getString("Service"),
                    obj.getString("Latitute"),
                    obj.getString("Longitute"),
                    obj.getString("code")
            ));
        }

        UserListAdapter adapter = new UserListAdapter(emrglist);
        emergency.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(CurrentEmergency.this);
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
                    refreshcurrentlist(object.getJSONArray("usershow_emerg"));

                }
                else {
                    txtusremrg.setVisibility(View.VISIBLE);
                    txtusremrg.setText("YOU HAVE NO CURRENT ORDERS YET!!!");
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
        Intent back=new Intent(CurrentEmergency.this,UserNavigation.class);
        startActivity(back);
        finish();
    }
}
