package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UserLogin extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText edtuseremail,edtuserpassword;
    Button btnuserlogin;
    TextView tvuserfrgtpwd,tvusersignup;
    String struseremail,struserpwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        edtuseremail=(EditText)findViewById(R.id.useremail);
        edtuserpassword=(EditText)findViewById(R.id.userpassword);
        btnuserlogin=(Button)findViewById(R.id.userlogin);
        tvusersignup=(TextView)findViewById(R.id.userregister);
        tvuserfrgtpwd=(TextView)findViewById(R.id.userforgotpwd);

        tvusersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IntentUtils.fireIntent(UserLogin.this,UserRegistration.class,false);
                Intent userreg=new Intent(UserLogin.this,UserRegistration.class);
                startActivity(userreg);
                finish();
            }
        });

        tvuserfrgtpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IntentUtils.fireIntent(UserLogin.this,ForgotPasswordUser.class,false);
                Intent frgt=new Intent(UserLogin.this,ForgotPasswordUser.class);
                startActivity(frgt);
                //finish();
            }
        });

        btnuserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);

                struseremail=edtuseremail.getText().toString().trim();
                struserpwd=edtuserpassword.getText().toString().trim();


                HashMap<String, String> params = new HashMap<>();
                params.put("email", struseremail);
                params.put("password", struserpwd);

                PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_USERLOGIN, params, CODE_POST_REQUEST);
                request.execute();
            }
        });
    }

    public void hideKeyboard(View view) {

        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
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
            progressDialog=new ProgressDialog(UserLogin.this);
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

                    Intent pass = new Intent(UserLogin.this,UserNavigation.class);
                    pass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    pass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(pass);
                    SharedPreferences sharedpreferences = getSharedPreferences("userlogin",
                            Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("email", struseremail);
                    //editor.putString("type", dbstrtype);
                    editor.commit();

                    SharedPreferences sp = getSharedPreferences("Logintype",
                            Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor1 = sp.edit();
                    editor1.putString("type", "user");
                    editor1.commit();
                }
                else
                {
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
        Intent usrlogin=new Intent(UserLogin.this,SelectType.class);
        startActivity(usrlogin);
        finish();
    }

}
