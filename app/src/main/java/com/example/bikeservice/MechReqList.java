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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MechReqList extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;
    String strlandmark,strpincode,reqemail;
    User user;
    int globalInc = 0;
    String selectreqid,strgarage;
    CheckBox checkBox;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    ArrayList<String> selectedEmail = new ArrayList<String>();

    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_req_list);

        userdetaillist= new ArrayList<>();
        datalist=findViewById(R.id.listview);
        datalist.setDivider(null);
        Toolbar reqlist = (Toolbar)findViewById(R.id.mechtool);
        setSupportActionBar(reqlist);
        getSupportActionBar().setTitle(" Mechanic List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sp = getSharedPreferences("User_Key", Context.MODE_PRIVATE);
        strlandmark =sp.getString("landmark","");
        Log.d("landmark",strlandmark);
        strpincode = sp.getString("pincode","");
        Log.d("pincode",strpincode);
        selectreqid = sp.getString("reqid","");
        Log.d("reqid",selectreqid);
        reqemail = sp.getString("email","");
        Log.d("email",reqemail);
        Button request=(Button)findViewById(R.id.REQUEST);

        data();

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(globalInc>=1) {

                    for (int i=0; i<selectedEmail.size();i++) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("mech_email", selectedEmail.get(i));
                        params.put("mech_garage",selectedStrings.get(i));
                        params.put("landmark",strlandmark);
                        params.put("pincode",strpincode);
                        params.put("req_id",selectreqid);
                        //Toast.makeText(MechReqList.this, "The Value is:" + selectedEmail.get(i), Toast.LENGTH_SHORT).show();

                        System.out.println(selectedEmail.get(i));
                        // Network Perform Code

                        PerformNetworkRequestResponse request = new PerformNetworkRequestResponse(Apiurl.URL_ADMSELMECH, params, CODE_POST_REQUEST);
                        request.execute();

                        //for mech
                        HashMap<String, String> params2 = new HashMap<>();
                        params2.put("email", selectedEmail.get(i));
                        params2.put("title", "New Request");
                        params2.put("message", "You Have a New Request from " + reqemail);
                        params2.put("type","LatestRequest");

                        // Network Perform Code

                        PerformNetworkNotificationMech notifymech = new PerformNetworkNotificationMech(Apiurl.URL_NOTIFICATIONMECH, params2, CODE_POST_REQUEST);
                        notifymech.execute();

                        sweetAlertDialog=new SweetAlertDialog(MechReqList.this,SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("Request Send Successfull")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent req=new Intent(MechReqList.this,AdminHome.class);
                                        startActivity(req);
                                        finish();
                                    }
                                });

                    }

                }
                else {
                    Toast.makeText(MechReqList.this, "Please Select Any Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void data(){
        HashMap<String, String> params = new HashMap<>();
        params.put("landmark",strlandmark.trim().toLowerCase());
        params.put("pincode",strpincode.trim());
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_REQMECHANIC, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {

            super(MechReqList.this, R.layout.raw_mechreq, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            final View listViewItem = inflater.inflate(R.layout.raw_mechreq, null, true);

            TextView tvgarage=(TextView) listViewItem.findViewById(R.id.GARAGENAME);
            TextView tvemail=(TextView) listViewItem.findViewById(R.id.EMAILID);
            TextView tvcontact=(TextView) listViewItem.findViewById(R.id.CONTACT);
            TextView tvcity=(TextView) listViewItem.findViewById(R.id.CITY);
            TextView tvlandmark=(TextView) listViewItem.findViewById(R.id.LANDMARK);
            TextView tvpin=(TextView) listViewItem.findViewById(R.id.PINCODE);
            RatingBar rate=(RatingBar)listViewItem.findViewById(R.id.mechrate);
            checkBox=(CheckBox)listViewItem.findViewById(R.id.select);


            final User garage = Userlist.get(position);
            final User email = Userlist.get(position);
            final User contact = Userlist.get(position);
            final User city = Userlist.get(position);
            final User landmark = Userlist.get(position);
            final User pin = Userlist.get(position);
            final User mechrating=Userlist.get(position);
            final  User check=Userlist.get(position);

            tvgarage.setText("Garage name : "+garage.getName());
            tvemail.setText("Email : "+email.getEmail());
            tvcontact.setText("contact : "+contact.getPhone());
            tvcity.setText("City :"+city.getLandmark());
            tvlandmark.setText("Landmark :"+landmark.getCity());
            tvpin.setText("Pincode :"+pin.getPincode());
            float rating=Float.valueOf(mechrating.getService());
            rate.setRating(rating);
            checkBox.setChecked(check.getSelected());


            strgarage=garage.getName();
            user=getItem(position);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    user = (User) buttonView.getTag();

                    if(isChecked)
                    {
                        globalInc++;
                        //strname += garage.getName()+",";
                        selectedStrings.add(garage.getName());
                        selectedEmail.add(email.getEmail());
                        Userlist.get(position).setSelected(true);

                    }
                    else if(!isChecked)
                    {
                        globalInc--;
                        selectedStrings.remove(garage.getName());
                        selectedEmail.remove(email.getEmail());
                        Userlist.get(position).setSelected(false);

                    }
                    if(globalInc >= 4)// it will allow 3 checkboxes only
                    {
                        Toast.makeText(getContext(), "You Have Already Max User Selected", Toast.LENGTH_LONG).show();
                        buttonView.setChecked(false);
                        globalInc--;
                        selectedStrings.remove(garage.getName());
                        selectedEmail.remove(email.getEmail());
                        Userlist.get(position).setSelected(false);
                    }
                    else
                    {
                        user.setSelected(isChecked);

                    }
                    System.out.println(" ---------------    "+globalInc);
                }
            });

            checkBox.setTag(user);
            checkBox.setChecked(user.isSelected());

            return listViewItem;
        }
    }

    private void refreshApprovedreqlist(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
                userdetaillist.add(new User(
                        obj.getString("garage_name"),
                        obj.getString("mech_email"),
                        obj.getString("mech_contact"),
                        obj.getString("mech_city"),
                        obj.getString("mech_landmark"),
                        obj.getString("mech_pincode"),
                        obj.getString("rating")
                ));
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
            progressDialog=new ProgressDialog(MechReqList.this);
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
                    refreshApprovedreqlist(object.getJSONArray("service_request"));
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


    //perform network for request


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
            progressDialog=new ProgressDialog(MechReqList.this);
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
                    //System.out.println(object.getJSONArray("sendRequestToMech"));

                    //for user

                    HashMap<String, String> params1 = new HashMap<>();
                    params1.put("email", reqemail);
                    params1.put("title", "Request Status");
                    params1.put("message", "Your Request Is Approved by Admin");
                    params1.put("type","UserRequest");

                    // Network Perform Code

                    PerformNetworkNotification notify = new PerformNetworkNotification(Apiurl.URL_NOTIFICATION, params1, CODE_POST_REQUEST);
                    notify.execute();


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

    class PerformNetworkNotification extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkNotification(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    Log.e("notification","sent");

                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();
                    Log.e("notification","failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    class PerformNetworkNotificationMech extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkNotificationMech(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    Log.e("notification","sent");
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();
                    Log.e("notification","failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        Intent pass = new Intent(MechReqList.this,AdminHome.class);
        startActivity(pass);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }
}
