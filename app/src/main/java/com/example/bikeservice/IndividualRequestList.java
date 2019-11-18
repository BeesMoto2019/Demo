package com.example.bikeservice;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndividualRequestList extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;
    int intreqid;
    String strlandmark,strpin,strreqid,stremailid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_request_list);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userdetaillist= new ArrayList<>();
        datalist=findViewById(R.id.listview);
        datalist.setDivider(null);
        data();
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_INDIVIDUALREQ, params, CODE_POST_REQUEST);
        request.execute();

    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(IndividualRequestList.this, R.layout.raw_individualreq, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_individualreq, null, true);

            TextView tvreqid=listViewItem.findViewById(R.id.requestid);
            TextView tvemail=listViewItem.findViewById(R.id.usremail);
            TextView tvname=listViewItem.findViewById(R.id.name);
            TextView tvcontact=listViewItem.findViewById(R.id.contact);
            TextView tvbike=listViewItem.findViewById(R.id.bike);
            TextView tvservice=listViewItem.findViewById(R.id.service);
            final TextView tvlandmark=listViewItem.findViewById(R.id.landmark);
            TextView tvcity=listViewItem.findViewById(R.id.city);
            final TextView tvpin=listViewItem.findViewById(R.id.pincode);
            TextView tvdate=listViewItem.findViewById(R.id.appdate);
            TextView tvapptime=listViewItem.findViewById(R.id.apptime);
            final Button request=listViewItem.findViewById(R.id.mechanic);

            final User reqid = Userlist.get(position);
            final User usremail=Userlist.get(position);
            final User name=Userlist.get(position);
            final User contact = Userlist.get(position);
            final User bike = Userlist.get(position);
            final User service = Userlist.get(position);
            final User landmark = Userlist.get(position);
            final User city = Userlist.get(position);
            final User pin = Userlist.get(position);
            final User apdate = Userlist.get(position);
            final User apptime=Userlist.get(position);

            intreqid = reqid.getId();
            strreqid = Integer.toString(intreqid);
            tvreqid.setText(strreqid);
            tvemail.setText("User Email : "+usremail.getName());
            tvname.setText("Name : "+name.getEmail());
            tvcontact.setText("Contact : "+contact.getPhone());
            tvbike.setText("BikeModel : "+bike.getAddress());
            tvservice.setText("Service Type : "+service.getState());
            tvlandmark.setText("Landmark : "+landmark.getCity());
            tvcity.setText("City : "+city.getLandmark());
            tvpin.setText("Pincode : "+pin.getPincode());
            tvdate.setText("Appointment Date : "+apdate.getPassword());
            tvapptime.setText("Appointment Time : "+apptime.getStatus());

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intreqid = reqid.getId();
                    strreqid = Integer.toString(intreqid);
                    strlandmark=landmark.getCity().trim();
                    strpin=pin.getPincode().trim();
                    stremailid=usremail.getName().trim();
                    SharedPreferences sp =getSharedPreferences("User_Key", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("landmark",strlandmark);
                    editor.putString("pincode",strpin);
                    editor.putString("reqid",strreqid);
                    editor.putString("email",stremailid);
                    editor.commit();
                    //Toast.makeText(getApplicationContext(),"saved",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IndividualRequestList.this,MechReqList.class);
                    startActivity(intent);
                    finish();
                    // Network Perform Code
                }
            });

            return listViewItem;
        }
    }

    private void refreshIndividualList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            for (int k = 1; k <= obj.length() / 11; k++) {
                userdetaillist.add(new User(
                        obj.getInt("req_id"+k),
                        obj.getString("req_email"+k),
                        obj.getString("name"+k),
                        obj.getString("contact"+k),
                        obj.getString("bikemodel"+k),
                        obj.getString("service"+k),
                        obj.getString("landmark"+k),
                        obj.getString("city"+k),
                        obj.getString("pincode"+k),
                        obj.getString("appointment_date"+k),
                        obj.getString("appointment_time"+k)
                ));
            }
        }
        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        datalist.setAdapter(adapter);
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
            progressDialog=new ProgressDialog(IndividualRequestList.this);
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
                    refreshIndividualList(object.getJSONArray("IndividualReq"));

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
        Intent back=new Intent(IndividualRequestList.this,AdminHome.class);
        startActivity(back);
        finish();
    }
}
