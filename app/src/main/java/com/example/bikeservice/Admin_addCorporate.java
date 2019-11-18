package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Admin_addCorporate extends AppCompatActivity {

    EditText crptname,crptaddress,crptlandmark,crptpincode,cmpnycity;
    String cname,caddress,clandmark,cpincode,ccity;
    Button btnadd;
    SweetAlertDialog sweetAlertDialog;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_addcorporate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Corporate");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        crptname = (EditText)findViewById(R.id.crptname);
        crptaddress = (EditText)findViewById(R.id.crptaddress);
        crptlandmark = (EditText)findViewById(R.id.crptlandmark);
        crptpincode = (EditText)findViewById(R.id.crptpin);
        cmpnycity=(EditText)findViewById(R.id.crptcity);
        btnadd = (Button)findViewById(R.id.btadd);

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cname = crptname.getText().toString().trim();
                caddress = crptaddress.getText().toString().trim();
                clandmark = crptlandmark.getText().toString().trim();
                cpincode = crptpincode.getText().toString().trim();
                ccity=cmpnycity.getText().toString().trim();

                if (TextUtils.isEmpty(cname)) {
                    crptname.setError("Enter The Corporate Name");
                    crptname.requestFocus();
                } else if (TextUtils.isEmpty(caddress)) {
                    crptaddress.setError("Enter The Corporate Address");
                    crptaddress.requestFocus();
                }else if(TextUtils.isEmpty(ccity)) {
                    cmpnycity.setError("Enter The Corporate City");
                    cmpnycity.requestFocus();
                } else if (TextUtils.isEmpty(clandmark)) {
                    crptlandmark.setError("Enter The Corporate Landmark");
                    crptlandmark.requestFocus();
                } else if (TextUtils.isEmpty(cpincode)) {
                    crptpincode.setError("Enter The Corporate Pincode");
                    crptpincode.requestFocus();
                } else {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("crpt_name", cname);
                    params.put("crpt_address", caddress);
                    params.put("crpt_city",ccity);
                    params.put("landmark", clandmark);
                    params.put("pincode", cpincode);


                    PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_ADMADDCORPORATE, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog=new SweetAlertDialog(Admin_addCorporate.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Add Successfully")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    crptname.setText("");
                                    crptaddress.setText("");
                                    cmpnycity.setText("");
                                    crptlandmark.setText("");
                                    crptpincode.setText("");
                                    Intent req=new Intent(Admin_addCorporate.this,AdminHome.class);
                                    startActivity(req);
                                    finish();
                                }
                            });
                }
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
            progressDialog=new ProgressDialog(Admin_addCorporate.this);
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
                    //Toast.makeText(getApplicationContext(),"message", Toast.LENGTH_SHORT).show();
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

    public void onBackPressed() {
        super.onBackPressed();

        Intent back = new Intent(Admin_addCorporate.this,AdminHome.class);
        startActivity(back);
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
