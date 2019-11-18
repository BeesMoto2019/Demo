package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDashbord extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<User> data;
    String stremailid;
    Button btnbooking,btnemergency,btncurrent,btnpast,btnemerg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.activity_user_dashbord,container,false);

        btnbooking=(Button)v.findViewById(R.id.btnuserbook);
        btnemergency=(Button)v.findViewById(R.id.btnemergency);
        btncurrent=(Button)v.findViewById(R.id.btncorder);
        btnpast=(Button)v.findViewById(R.id.btnporder);
        btnemerg=(Button)v.findViewById(R.id.btnemerg) ;
        data=new ArrayList<>();
        SharedPreferences sharedpreferences=getActivity().getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);

        btnbooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent book=new Intent(getActivity(),RequesterType.class);
                startActivity(book);
            }
        });

        btncurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent current=new Intent(getActivity(),UserCurrentOrders.class);
                startActivity(current);
            }
        });


        btnpast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent past=new Intent(getActivity(),UsrPastOrders.class);
                startActivity(past);
            }
        });

        btnemergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emergency=new Intent(getActivity(),EmergencyRequest.class);
                startActivity(emergency);
            }
        });

        btnemerg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emerg=new Intent(getActivity(),CurrentEmergency.class);
                startActivity(emerg);
            }
        });

        return v;
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        params.put("email",stremailid);
        PerformNetworkRequestFetch request = new PerformNetworkRequestFetch(Apiurl.URL_USERDETAILS, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void refreshuserdata(JSONArray helptips) throws JSONException {
        data.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            data.add(new User(
                    obj.getString("contact"),
                    obj.getString("city"),
                    obj.getString("landmark"),
                    obj.getString("pincode"),
                    obj.getString("image_url")
            ));
        }
    }

    private class PerformNetworkRequestFetch extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        PerformNetworkRequestFetch(String url, HashMap<String, String> params, int requestCode) {
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

            // Log.d(TAG, "Helptips Response: " + s.toString());
            //progressBar.setVisibility(GONE);
            try {
                Log.d("listview", s);
                JSONObject object = new JSONObject(s);
                Log.d("listview", String.valueOf(object));
                if (!object.getBoolean("error")) {
                    refreshuserdata(object.getJSONArray("userdetails"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
}
