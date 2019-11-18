package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserNavigation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    Toolbar toolbar;
    Fragment fragment;
    String strimage;
    CircleImageView iv;
    TextView usremail;
    List<User> data;
    String stremailid,strcontact,strcity,strlandmark,strpincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_navigation);
        toolbar=(Toolbar)findViewById(R.id.usertool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
        data= new ArrayList<>();

        drawerLayout=(DrawerLayout)findViewById(R.id.userdrawer);

        SharedPreferences sharedpreferences=getSharedPreferences("userlogin", Context.MODE_PRIVATE);
        stremailid=sharedpreferences.getString("email","");
        Log.d("email",stremailid);


        fragment= new UserDashbord();
        if(fragment!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }

        drawer();
        data();
    }

    public void drawer()
    {
        NavigationView navigationView=(NavigationView) findViewById(R.id.usernavigationview);
        navigationView.setItemIconTintList(null);
        View headerview=navigationView.getHeaderView(0).findViewById(R.id.head);
        iv =(CircleImageView) headerview.findViewById(R.id.img);
        usremail = (TextView)headerview.findViewById(R.id.email);
        usremail.setText(stremailid);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {

                    case R.id.userdashboard:

                        fragment= new UserDashbord();
                        if(fragment!=null){
                            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                            //ft.replace(R.id.content_frame,fragment);
                            ft.commit();
                        }
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.userpast: {
                        Intent profile = new Intent(UserNavigation.this,UsrPastOrders.class );
                        startActivity(profile);
                    }
                    drawerLayout.closeDrawers();
                    break;

                    case R.id.userprofile:{
                        Intent profile=new Intent(UserNavigation.this,UserProfileSetting.class);
                        startActivity(profile);
                    }
                    drawerLayout.closeDrawers();
                    break;

//                    case R.id.userfeedback:{
//                        Intent feedback=new Intent(UserNavigation.this,Feedback.class);
//                        startActivity(feedback);
//                    }
//                    drawerLayout.closeDrawers();
//                    break;

                    case R.id.usercurrent:{
                        Intent history=new Intent(UserNavigation.this,UserCurrentOrders.class);
                        startActivity(history);
                    }
                    drawerLayout.closeDrawers();
                    break;

                    case R.id.userpay:{
                        Intent pay=new Intent(UserNavigation.this,Payment.class);
                        startActivity(pay);
                    }
                    drawerLayout.closeDrawers();
                    break;

                    case R.id.userlogout: {
                        SharedPreferences sharedpreferences = getSharedPreferences("userlogin", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.commit();
                        SharedPreferences sp = getSharedPreferences("Logintype", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = sp.edit();
                        editor1.clear();
                        editor1.commit();
                        Intent intent = new Intent(UserNavigation.this, SelectType.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
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
                    strcontact=obj.getString("contact"),
                    strcity=obj.getString("city"),
                    strlandmark=obj.getString("landmark"),
                    strpincode=obj.getString("pincode"),
                    strimage=obj.getString("image_url")
            ));
        }
        Glide.with(this).load(strimage).into(iv);
    }

    private class PerformNetworkRequestFetch extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;
        PerformNetworkRequestFetch(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(UserNavigation.this);
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
                    refreshuserdata(object.getJSONArray("userdetails"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
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
}
