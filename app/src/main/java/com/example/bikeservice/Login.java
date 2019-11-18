package com.example.bikeservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login extends AppCompatActivity {

    EditText email,password;
    TextView fpwd,signup;
    Button btnLogin;
    String stremail,strpass,dbstrtype;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    List<User> userdetaillist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userdetaillist=new ArrayList<>();
        email =(EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        btnLogin =(Button)findViewById(R.id.login);
        fpwd = (TextView)findViewById(R.id.forgotpwd);
        signup=(TextView) findViewById(R.id.register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);

                if(!isNetworkConnected()){
                    Toast.makeText(getApplicationContext(),"can't connect to the network",Toast.LENGTH_LONG).show();
                }
                else {

                    stremail = email.getText().toString().trim();
                    strpass = password.getText().toString().trim();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("Email", stremail);
                    params.put("Password", strpass);

                    PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_LOGIN, params, CODE_POST_REQUEST);
                    request.execute();
                }
            }
        });


        fpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //IntentUtils.fireIntent(Login.this,Forgot_Password.class,false);
                Intent pass = new Intent(Login.this,Forgot_Password.class);
                startActivity(pass);
                //finish();

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IntentUtils.fireIntent(Login.this,MainActivity.class,false);
                Intent i=new Intent(Login.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void hideKeyboard(View view) {

        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void loginlist(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    stremail=    obj.getString("email"),
                    dbstrtype=    obj.getString("flag")
            ));
        }


    }

    class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
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
            progressDialog=new ProgressDialog(Login.this);
            progressDialog.setTitle("Processing");
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
                    loginlist(object.getJSONArray("login"));
                    if(dbstrtype.equals("mechanic")) {
                        //IntentUtils.fireIntent(Login.this,NavigationDrawer.class,false);
                        Intent pass = new Intent(Login.this,NavigationDrawer.class);
                        pass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        pass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(pass);

                        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Email", stremail);
                        editor.putString("type", dbstrtype);
                        editor.commit();
                    }
                    else
                    {
                        //IntentUtils.fireIntent(Login.this,DashboardAdmin.class,false);
                        Intent admin = new Intent(Login.this,AdminHome.class);
                        admin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        admin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(admin);

                        SharedPreferences sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("Email", stremail);
                        editor.putString("type", dbstrtype);
                        editor.commit();

                        String strtoken=SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
                        System.out.println(strtoken);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", stremail);
                        params.put("Token", strtoken);

                        PerformNetworkRequestToken request = new PerformNetworkRequestToken(Apiurl.URL_ADMINTOKEN, params, CODE_POST_REQUEST);
                        request.execute();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
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

    class PerformNetworkRequestToken extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequestToken(String url, HashMap<String, String> params, int requestCode) {
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
                }
                else {
                    //Toast.makeText(getApplicationContext(),"message", Toast.LENGTH_SHORT).show();

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

    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()!= null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected())
        {
            return true;
        }
        return false;
    }
    public void onBackPressed()
    {
        //IntentUtils.fireIntent(Login.this,SelectType.class,false);
        Intent i=new Intent(Login.this, SelectType.class);
        startActivity(i);
        finish();
    }
}
