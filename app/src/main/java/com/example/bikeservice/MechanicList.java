package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MechanicList extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    Toolbar mechlist;
    List<User> userdetaillist;
    private TextView editTextSubject;
    private TextView editTextMessage;
    int intmechid;
    String strmechid, stremail,strname,strlandmark, strpincode, strgaragename, stringcontact, stringcity,mechemail;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_list);
        userdetaillist = new ArrayList<>();
        datalist = findViewById(R.id.listview);
        editTextSubject = (TextView)findViewById(R.id.subject);
        editTextMessage = (TextView)findViewById(R.id.Message);
        mechlist = (Toolbar)findViewById(R.id.mechlist);
        setSupportActionBar(mechlist);
        getSupportActionBar().setTitle("Mechanic List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        datalist.setDivider(null);
        data();
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_MECHLIST, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(MechanicList.this, R.layout.raw_mechlist, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            final View listViewItem = inflater.inflate(R.layout.raw_mechlist, null, true);

            TextView tvreqid=listViewItem.findViewById(R.id.reqid);
            TextView tvfname=listViewItem.findViewById(R.id.fname);
            TextView tvgname=listViewItem.findViewById(R.id.gname);
            final TextView tvemail=listViewItem.findViewById(R.id.email);
            TextView tvphone=listViewItem.findViewById(R.id.cont);
            TextView tvcity=listViewItem.findViewById(R.id.city);
            TextView tvlandmark=listViewItem.findViewById(R.id.landmark);
            TextView tvpin=listViewItem.findViewById(R.id.pinc);
            Button btnapprove=listViewItem.findViewById(R.id.approve);

            final User reqid = Userlist.get(position);
            final User fname = Userlist.get(position);
            final User gname = Userlist.get(position);
            final User email = Userlist.get(position);
            final User phone = Userlist.get(position);
            final User city = Userlist.get(position);
            final User landmark = Userlist.get(position);
            final User pincode = Userlist.get(position);


            intmechid = reqid.getId();
            strmechid = Integer.toString(intmechid);
            tvreqid.setText(strmechid);
            tvfname.setText("Name : "+fname.getName());
            tvgname.setText("Garage : "+gname.getGarage());
            tvemail.setText("Email : "+email.getEmail());
            tvphone.setText("PhoneNo : "+phone.getPhone());
            tvcity.setText("City :"+city.getCity());
            tvlandmark.setText("Landmark :"+landmark.getLandmark());
            tvpin.setText("Pincode :"+pincode.getPincode());

            strname=fname.getName();
            mechemail=email.getEmail().trim();

            btnapprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    strname=fname.getName();
                    mechemail=email.getEmail().trim();
                    stremail = ((TextView)listViewItem.findViewById(R.id.email)).getText().toString().trim();
                    strmechid = ((TextView)listViewItem.findViewById(R.id.reqid)).getText().toString().trim();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("mech_id", strmechid);
                    PerformNetworkRequestResponse request = new PerformNetworkRequestResponse(Apiurl.URL_APPROVEMECH, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog=new SweetAlertDialog(MechanicList.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Approved Successfully")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent req=new Intent(MechanicList.this,AdminHome.class);
                                    startActivity(req);
                                    sendEmail();
                                }
                            });

                }
            });
            return listViewItem;

        }
    }

    private void sendEmail() {

        String subject = editTextSubject.getText().toString().trim();

        String messg = "Hello... " +strname+ " ,\nYour Email is : "+mechemail+ "\nYour Registration Request is Approved. \nThank You...!!!";
        editTextMessage.setText(messg);

        String message = editTextMessage.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(MechanicList.this, mechemail, subject, message);

        //Executing sendmail to send email
        sm.execute();

    }

    private void refreshIndividualList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
                userdetaillist.add(new User(
                        obj.getInt("mech_id"),
                        obj.getString("mech_name"),
                        obj.getString("garage_name"),
                        obj.getString("mech_email"),
                        obj.getString("mech_contact"),
                        obj.getString("mech_city"),
                        obj.getString("mech_landmark"),
                        obj.getString("mech_pincode")
                ));

        }
        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        datalist.setAdapter(adapter);
        //Auto refresh listview
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
            progressDialog=new ProgressDialog(MechanicList.this);
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

                    //Toast.makeText(getApplicationContext().getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshIndividualList(object.getJSONArray("Mech_List"));

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
            progressDialog=new ProgressDialog(MechanicList.this);
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
                    sweetAlertDialog.show();
                }
                else {
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

    @Override
    public void onBackPressed() {
        Intent pass = new Intent(MechanicList.this,AdminHome.class);
        startActivity(pass);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }
}
