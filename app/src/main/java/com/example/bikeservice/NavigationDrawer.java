package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationDrawer extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    Fragment fragment;
    CircleImageView iv;
    TextView mechemail;
    String loginemail,strgaragename,stringcontact,stringcity,strmechstatus, strimage;
    List<User> userdetaillist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        toolbar=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
        userdetaillist=new ArrayList<>();

        drawerLayout=(DrawerLayout)findViewById(R.id.mdrawer);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        loginemail = sp.getString("Email", "");
        Log.d("Email", loginemail);

        fragment= new MechDashboard();
        if(fragment!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        drawer();
        getdetails();
    }

    public void drawer()
    {
        NavigationView navigationView=(NavigationView) findViewById(R.id.navigationview);
        navigationView.setItemIconTintList(null);
        View headerview = navigationView.getHeaderView(0).findViewById(R.id.head);
        //LinearLayout ln = (LinearLayout) headerview.findViewById(R.id.head);
        iv = (CircleImageView) headerview.findViewById(R.id.img);
        mechemail = (TextView)headerview.findViewById(R.id.email);
        mechemail.setText(loginemail);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {

                    case R.id.dashboard:

                        fragment= new MechDashboard();
                        if(fragment!=null){
                            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                            //ft.replace(R.id.content_frame,fragment);
                            ft.commit();
                        }
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.latestrequest: {
                        Intent latest = new Intent(NavigationDrawer.this, MechLatestReq.class);
                        startActivity(latest);
                    }
                    drawerLayout.closeDrawers();
                    break;

                    case R.id.request: {
                        Intent orders = new Intent(NavigationDrawer.this, MechActiveOrders.class);
                        startActivity(orders);
                    }
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.logout: {
                        SharedPreferences sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        SharedPreferences sp1 = getSharedPreferences("Logintype", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sp1.edit();
                        editor1.clear();
                        editor1.commit();
                        Intent intent = new Intent(NavigationDrawer.this, SelectType.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    drawerLayout.closeDrawers();
                    break;

                    case R.id.profile: {
                        Intent profile = new Intent(NavigationDrawer.this,ProfileSetting.class );
                        startActivity(profile);
                    }
                    drawerLayout.closeDrawers();
                    break;

                    case R.id.history: {
                        Intent pastorder = new Intent(NavigationDrawer.this,MechPastOrders.class );
                        startActivity(pastorder);
                    }
                    drawerLayout.closeDrawers();
                    break;
                }
                return true;
            }
        });

        drawerToggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerToggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
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
        Glide.with(this).load(strimage).into(iv);
    }

    class PerformNetworkdetail extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;

        PerformNetworkdetail(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(NavigationDrawer.this);
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
                    refreshdetails(object.getJSONArray("mech_details"));

                } else {
                    //Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

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
}
