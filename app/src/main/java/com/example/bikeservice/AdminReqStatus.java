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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminReqStatus extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView mechlist;
    List<User> userdetaillist;
    int intreqid,intstatus;
    String strreqid,strcode,strstatus,reqstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_req_status);
        Toolbar toolbar=(Toolbar)findViewById(R.id.selecttool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Selected Mechanic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences reqid = getSharedPreferences("req_id", Context.MODE_PRIVATE);
        strreqid = reqid.getString("reqid","");
        Log.d("reqid",strreqid);

        userdetaillist= new ArrayList<>();
        mechlist=findViewById(R.id.mechstatus);
        mechlist.setDivider(null);

        data();
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        params.put("req_id",strreqid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_ADMSREQSTATUS, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(AdminReqStatus.this, R.layout.raw_adminreq_status, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_adminreq_status, null, true);

            TextView tvemail=listViewItem.findViewById(R.id.mechemailid);
            TextView tvgname=listViewItem.findViewById(R.id.mechgrg);
            TextView tvstatus=listViewItem.findViewById(R.id.req_st);
            TextView tvreqid = listViewItem.findViewById(R.id.reqid);
            TextView tvmechcont=listViewItem.findViewById(R.id.mechcontact);

            final User reqid = Userlist.get(position);
            final User name = Userlist.get(position);
            final User gname = Userlist.get(position);
            final User Contact=Userlist.get(position);
            final User status = Userlist.get(position);

            intreqid = reqid.getId();
            strreqid = Integer.toString(intreqid);
            tvreqid.setText(strreqid);
            tvemail.setText("Mechanic Name : "+name.getEmail());
            tvgname.setText("Mechanic Garage : "+gname.getService());
            tvmechcont.setText("Mechanic Contact : "+Contact.getName());
            strstatus = status.getStatus();

            intstatus = Integer.parseInt(strstatus);

            if(intstatus == 1)
            {
                reqstatus="Pending";
                tvstatus.setTextColor(Color.parseColor("#FF0000"));
            }
            else if(intstatus==0)
            {
                reqstatus="Approved";
                tvstatus.setTextColor(Color.parseColor("#00FF00"));
            }
            tvstatus.setText(reqstatus);

            return listViewItem;
        }
    }
    private void refreshIndividualList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            {
                userdetaillist.add(new User(
                        obj.getInt("req_id"),
                        obj.getString("mech_name"),
                        obj.getString("mech_garage"),
                        obj.getString("mech_contact"),
                        obj.getString("Status")

                ));
            }
        }
        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        mechlist.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(AdminReqStatus.this);
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
                    refreshIndividualList(object.getJSONArray("AdminReqStatus"));

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
        Intent back = new Intent(getApplicationContext(),AdminRequestHistory.class);
        startActivity(back);
        finish();
    }
}
