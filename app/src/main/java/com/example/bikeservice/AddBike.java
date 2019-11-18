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

public class AddBike extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    EditText edtbikename;
    Button btnadd;
    String strmodel;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bike);
        Toolbar toolbar = findViewById(R.id.toolbarbike);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Bike Model");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtbikename=(EditText)findViewById(R.id.bikename);
        btnadd=(Button)findViewById(R.id.btbikeadd);

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);

                strmodel = edtbikename.getText().toString().trim();

                if (TextUtils.isEmpty(strmodel)) {
                    edtbikename.setError("Please Enter Bike Model");
                    edtbikename.requestFocus();
                } else {

                    HashMap<String, String> params = new HashMap<>();
                    params.put("model", strmodel);

                    PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_ADMADDBIKE, params, CODE_POST_REQUEST);
                    request.execute();

                    sweetAlertDialog=new SweetAlertDialog(AddBike.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setContentText("Add Successfully")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    edtbikename.setText("");
                                    Intent req=new Intent(AddBike.this,AdminHome.class);
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
            progressDialog=new ProgressDialog(AddBike.this);
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

        Intent back = new Intent(getApplicationContext(),AdminHome.class);
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
