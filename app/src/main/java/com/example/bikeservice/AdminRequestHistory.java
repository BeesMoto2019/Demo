package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;

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
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AdminRequestHistory extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;
    int intreqid,intstatus;
    String strreqid,strcode,strstatus,reqstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_request_history);
        Toolbar toolbar=(Toolbar)findViewById(R.id.historytool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pending Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userdetaillist= new ArrayList<>();
        datalist=findViewById(R.id.historylist);
        datalist.setDivider(null);

        data();
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        //params.put("req_id",strreqid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_ADMSREQHISTORY, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(AdminRequestHistory.this, R.layout.raw_admin_req_history, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_admin_req_history, null, true);

            TextView tvreqemail=listViewItem.findViewById(R.id.reqemail);
            TextView tvcontact=listViewItem.findViewById(R.id.ucontact);
            TextView tvservice=listViewItem.findViewById(R.id.uservice);
            TextView tvmodel=listViewItem.findViewById(R.id.ubmodel);
            TextView tvstatus=listViewItem.findViewById(R.id.requestst);
            ImageView check=listViewItem.findViewById(R.id.checkmech);
            final TextView tvreqid = listViewItem.findViewById(R.id.req_id);

            final User reqid = Userlist.get(position);
            final User reqemail=Userlist.get(position);
            final User contact = Userlist.get(position);
            final User service = Userlist.get(position);
            final User bike = Userlist.get(position);
            final User status = Userlist.get(position);


            intreqid = reqid.getDate();
            strreqid = Integer.toString(intreqid);
            tvreqid.setText(strreqid);
            tvreqemail.setText("Requester Email : "+reqemail.getEmail());
            tvcontact.setText("Requester Contact : "+contact.getPhone());
            tvservice.setText("Service : "+service.getLandmark());
            tvmodel.setText("BikeModel : "+bike.getCity());
            tvstatus.setText("Selected Mechanic : "+status.getPincode());


            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intreqid = reqid.getDate();
                    strreqid = Integer.toString(intreqid);
                    System.out.println(strreqid);
                    SharedPreferences reqid = getSharedPreferences("req_id",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = reqid.edit();
                    editor.putString("reqid",strreqid);
                    editor.commit();

                    Intent pass = new Intent(getApplicationContext(),AdminReqStatus.class);
                    startActivity(pass);
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
            {
                userdetaillist.add(new User(
                        obj.getInt("req_id"),
                        obj.getString("req_email"),
                        obj.getString("Contact"),
                        obj.getString("Service"),
                        obj.getString("BikeModel"),
                        obj.getString("mech_no")

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
            progressDialog=new ProgressDialog(AdminRequestHistory.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
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
                    refreshIndividualList(object.getJSONArray("AdminReqHistory"));

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

    public void onBackPressed() {
        super.onBackPressed();

        Intent back = new Intent(getApplicationContext(),AdminHome.class);
        startActivity(back);
        finish();
    }

}
