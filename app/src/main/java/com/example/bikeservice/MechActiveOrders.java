package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class MechActiveOrders extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;
    Integer id;
    String strid;
    String stremail;
    TextView txtactive;
    String userbikemodel,userbikeservice,useremail,usercontact,workdate;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_active_orders);
        Toolbar toolbar=(Toolbar)findViewById(R.id.active);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Active Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtactive=(TextView)findViewById(R.id.textactivemech);
        //order list approved by particular mechanic

        userdetaillist= new ArrayList<>();
        datalist=findViewById(R.id.activelist);
        datalist.setDivider(null);

        SharedPreferences preferences =getSharedPreferences("user", Context.MODE_PRIVATE);
        stremail = preferences.getString("Email", "");
        Log.d("Email",stremail);
        network();
        //data();

    }

    private void network(){
        if(!ConnectionUtils.isConnected(this)){
//            txtactive.setVisibility(View.VISIBLE);
//            txtactive.setText("Please Enable your internnet connection");
            setContentView(R.layout.internet_connection_error);
            Button refresh = (Button)findViewById(R.id.refresh);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }else{
            data();
        }
    }
    private void data(){
        HashMap<String, String> params = new HashMap<>();
        params.put("email",stremail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_ACTIVEORDERS, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(MechActiveOrders.this, R.layout.raw_active_order_list, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_active_order_list, null, true);

            TextView tvid=listViewItem.findViewById(R.id.id);
            TextView tvdate=listViewItem.findViewById(R.id.sdate);
            TextView tvservice=listViewItem.findViewById(R.id.Service);
            TextView tvmodel=listViewItem.findViewById(R.id.model);
            TextView tvlandmark=listViewItem.findViewById(R.id.landmark);
            TextView tvpincode=listViewItem.findViewById(R.id.pincode);
            TextView tvcontact=listViewItem.findViewById(R.id.phone);
            TextView tvreqemail=listViewItem.findViewById(R.id.reqemail);
            Button btnwork=listViewItem.findViewById(R.id.working);

            final User reqid=Userlist.get(position);
            final User date = Userlist.get(position);
            final User service = Userlist.get(position);
            final User bike = Userlist.get(position);
            final User landmark = Userlist.get(position);
            final User pin = Userlist.get(position);
            final User reqemail = Userlist.get(position);
            final User contact = Userlist.get(position);


//            intdate=date.getDate();
//            strdate=Integer.toString(intdate);
//            tvdate.setText(strdate);

            id=reqid.getId();
            strid=Integer.toString(id);
            tvid.setText(strid);
            tvdate.setText("Service Date : "+date.getName());
            tvservice.setText("Service Type : "+service.getGarage());
            tvmodel.setText("BikeModel : "+bike.getEmail());
            tvlandmark.setText("Landmark : "+landmark.getPhone());
            tvpincode.setText("Pincode : "+pin.getCity());
            tvreqemail.setText("Requester Email: "+reqemail.getLandmark());
            tvcontact.setText("Requester Contact : "+contact.getPincode());
            //tvcity.setText("City : "+city.getCity());

            btnwork.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    id=reqid.getId();
                    strid=Integer.toString(id);
                    workdate = date.getName();
                    userbikeservice=service.getGarage();
                    userbikemodel=bike.getEmail();
                    useremail=reqemail.getLandmark();
                    usercontact=contact.getPincode();

                    SharedPreferences work=getSharedPreferences("Work",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=work.edit();
                    editor.putString("req_id",strid);
                    editor.putString("date",workdate);
                    editor.putString("service",userbikeservice);
                    editor.putString("model",userbikemodel);
                    editor.putString("usremail",useremail);
                    editor.putString("usrphone",usercontact);
                    editor.commit();

                    Intent job=new Intent(MechActiveOrders.this,MechTiming.class);
                    startActivity(job);
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
                        obj.getString("appointment_date"),
                        obj.getString("Service"),
                        obj.getString("BikeModel"),
                        obj.getString("Landmark"),
                        obj.getString("pincode"),
                        obj.getString("req_email"),
                        obj.getString("Contact")

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
            progressDialog=new ProgressDialog(MechActiveOrders.this);
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
                    refreshIndividualList(object.getJSONArray("ActiveOrders"));
                }
                else {
                    txtactive.setVisibility(View.VISIBLE);
                    txtactive.setText("YOU HAVE NO ACTIVE ORDERS YET!!!");
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

//    private boolean isNetworkConnected(){
//        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(cm.getActiveNetworkInfo()!= null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected())
//        {
//            return true;
//        }
//        return false;
//    }

    @Override
    public void onBackPressed() {
        Intent active=new Intent(MechActiveOrders.this,NavigationDrawer.class);
        startActivity(active);
        finish();
    }
}
