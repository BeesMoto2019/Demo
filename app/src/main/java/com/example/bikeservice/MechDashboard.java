package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.DeniedByServerException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MechDashboard extends Fragment {


    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<User> userdetaillist;
    TextView newrequest,reqemail;
    ImageView image;
    Button latestreq,activeorder,pastorder,emergency,emergactive;
    String loginemail,stremail,strstatus;
    String strgaragename,stringcontact,strmechstatus,stringcity,strimage;
    int icount=0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.activity_mech_dashboard,container,false);

        userdetaillist=new ArrayList<>();
        newrequest=(TextView)v.findViewById(R.id.newreq);
        latestreq=(Button)v.findViewById(R.id.btnlatestreq);
        activeorder=(Button)v.findViewById(R.id.btnactiveorder);
        pastorder=(Button)v.findViewById(R.id.btnpastorder);
        emergency=(Button)v.findViewById(R.id.btnemergency);
        emergactive=(Button)v.findViewById(R.id.btnemergactive);
        reqemail=(TextView)v.findViewById(R.id.reqemail);


        SharedPreferences sp = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loginemail = sp.getString("Email", "");
        Log.d("Email", loginemail);
        getdetails();

        latestreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newrequest=new Intent(getActivity(),MechLatestReq.class);
                startActivity(newrequest);
            }
        });

        activeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent active=new Intent(getActivity(),MechActiveOrders.class);
                startActivity(active);
            }
        });

        pastorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent past=new Intent(getActivity(),MechPastOrders.class);
                startActivity(past);
            }
        });

        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emergency=new Intent(getActivity(),MechEmergencyReq.class);
                startActivity(emergency);
            }
        });

        emergactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eactive=new Intent(getActivity(),MechEmergencyActive.class);
                startActivity(eactive);
            }
        });

        return v;
    }

    private void getdetails() {

        HashMap<String, String> params = new HashMap<>();
        params.put("Emailid", loginemail);
        PerformNetworkdetail request = new PerformNetworkdetail(Apiurl.URL_MECHDETAIL, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshdetails(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    strgaragename = obj.getString("garage_name"),
                    stringcontact = obj.getString("mech_contact"),
                    stringcity = obj.getString("mech_city"),
                    strmechstatus = obj.getString("Status"),
                    strimage = obj.getString("image_url")
            ));

        }

        int st = Integer.parseInt(strmechstatus);
        if(st == 0)
        {
            data();
        }
        else {

            newrequest.setText("Please Wait for admin's approval for your account");
            //image.setVisibility(View.GONE);
            //latestreq.setClickable(false);
        }

    }
    //to show if new request is available
    private void data() {

        HashMap<String, String> params = new HashMap<>();
        params.put("mech_email", loginemail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_NEWREQ, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    stremail = obj.getString("req_email"),
                    strstatus = obj.getString("Status")

            ));
            icount++;
            System.out.println(icount);
        }

        String strcount = Integer.toString(icount);

        System.out.println(icount);

        if(strcount.equals("0"))
        {
            newrequest.setVisibility(View.GONE);
        }
        else {
            newrequest.setVisibility(View.VISIBLE);
            newrequest.setText(strcount);
        }
    }

    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
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
                    refreshList(object.getJSONArray("Mech_latestReq"));

                } else {

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

    class PerformNetworkdetail extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkdetail(String url, HashMap<String, String> params, int requestCode) {
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
                    //Toast.makeText(getActivity(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshdetails(object.getJSONArray("mech_details"));

                } else {
                    //Toast.makeText(getActivity(), "Sorry", Toast.LENGTH_SHORT).show();

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
}
