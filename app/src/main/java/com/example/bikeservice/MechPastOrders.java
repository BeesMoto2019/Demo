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

public class MechPastOrders extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView pastlist;
    List<User> userdetaillist;
    String strmechemail,strreqid;
    TextView txtpast;
    int intreqid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_past_orders);
        Toolbar toolbar=(Toolbar)findViewById(R.id.past);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Past Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtpast=(TextView)findViewById(R.id.textmechpast);
        userdetaillist= new ArrayList<>();
        pastlist=findViewById(R.id.pastorder);
        pastlist.setDivider(null);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        strmechemail = sp.getString("Email", "");
        Log.d("Email", strmechemail);
        pastorder();
    }

    private void pastorder(){
        HashMap<String, String> params = new HashMap<>();
        params.put("mech_email",strmechemail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_MECHPAST, params, CODE_POST_REQUEST);
        request.execute();

    }



    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(MechPastOrders.this, R.layout.raw_past_orders, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_mech_past_order, null, true);

            TextView tvumemail=listViewItem.findViewById(R.id.umemail);
            TextView tvcontact=listViewItem.findViewById(R.id.contgarage);
            TextView tvbike=listViewItem.findViewById(R.id.bmodel);
            TextView tvservice=listViewItem.findViewById(R.id.bservice);
            TextView tvtype=listViewItem.findViewById(R.id.request_type);
            TextView tvapdate=listViewItem.findViewById(R.id.adate);
            TextView tvtime=listViewItem.findViewById(R.id.wtime);

            final User reqid=Userlist.get(position);
            final User umemail = Userlist.get(position);
            final User contact= Userlist.get(position);
            final User bike= Userlist.get(position);
            final User service= Userlist.get(position);
            final User type=Userlist.get(position);
            final User date = Userlist.get(position);
            final User time=Userlist.get(position);

            intreqid = reqid.getId();
            strreqid = Integer.toString(intreqid);

            tvumemail.setText("Requester Email: "+umemail.getName());
            tvcontact.setText("Requester contact : "+contact.getGarage());
            tvbike.setText("BikeModel : "+bike.getEmail());
            tvservice.setText("Service :"+service.getPhone());
            tvtype.setText("Request Type :"+type.getCity());
            tvapdate.setText("Appointment date : "+date.getLandmark());
            tvtime.setText("Work Time :"+time.getPincode());


            return listViewItem;

        }
    }
    private void refreshcurrentlist(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    obj.getInt("req_id"),
                    obj.getString("usr_email"),
                    obj.getString("usr_contact"),
                    obj.getString("usr_bikemodel"),
                    obj.getString("usr_service"),
                    obj.getString("req_type"),
                    obj.getString("appointment_date"),
                    obj.getString("mech_time")

            ));
        }

        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        pastlist.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(MechPastOrders.this);
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
                    refreshcurrentlist(object.getJSONArray("Mech_past_order"));
                }
                else {
                    txtpast.setVisibility(View.VISIBLE);
                    txtpast.setText("YOU HAVE NO PAST ORDERS YET!!!");
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
        Intent past=new Intent(MechPastOrders.this,NavigationDrawer.class);
        startActivity(past);
        finish();
    }
}
