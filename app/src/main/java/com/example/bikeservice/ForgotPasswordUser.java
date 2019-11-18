package com.example.bikeservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPasswordUser extends AppCompatActivity {

    EditText edtemail;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ArrayList<String> emailid = new ArrayList<String>();
    private TextView editTextSubject;
    private TextView editTextMessage;
    SweetAlertDialog sweetAlertDialog;
    Button btnfrgt;
    List<User> data;
    String pass,stremail;
    Integer Rnumber;
    AlertDialog alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_user);
        Toolbar toolbar=(Toolbar)findViewById(R.id.forgotusrttool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextSubject = (TextView)findViewById(R.id.subject);
        editTextMessage = (TextView)findViewById(R.id.Message);
        edtemail=(EditText)findViewById(R.id.emailid);
        btnfrgt=(Button)findViewById(R.id.frgtpwd);
        data=new ArrayList<>();
        details();

        btnfrgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stremail = edtemail.getText().toString().trim();
                Random Number = new Random();
                Rnumber = Number.nextInt(99999);

                if (TextUtils.isEmpty(stremail)) {
                    edtemail.setError("Please Enter Your Email");
                    edtemail.requestFocus();
                } else if(!isEmailexists(stremail))
                {
                    edtemail.setError("Email is not Exist");
                    edtemail.requestFocus();
                } else {
                    sendEmail();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordUser.this);
                    builder.setTitle("Change Password");
                    View view1 = getLayoutInflater().inflate(R.layout.usr_change_pwd, null);
                    final EditText edtcode = (EditText) view1.findViewById(R.id.usrcode);
                    final EditText edtpwd = (EditText) view1.findViewById(R.id.changepwd);
                    final EditText edtcnf = (EditText) view1.findViewById(R.id.cpassword);
                    Button btnchange = (Button) view1.findViewById(R.id.Change);

                    btnchange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String code=edtcode.getText().toString().trim();
                            String strpwd=edtpwd.getText().toString().trim();
                            String strcnf=edtcnf.getText().toString().trim();
                            String strcode=String.valueOf(Rnumber);
                            if(TextUtils.isEmpty(code)) {
                                edtcode.setError("Please Enter The code");
                                edtcode.requestFocus();
                            } else if(!isValidPassword(strpwd)) {
                                edtpwd.setError("Please Enter Password 'hozU@123'");
                                edtpwd.requestFocus();
                            } else if(!code.equals(strcode)) {
                                edtcode.setError("Enter the Valid Code");
                                edtcode.requestFocus();
                            } else if(!strpwd.equals(strcnf)){
                                edtcnf.setError("Password Does Not Match");
                                edtcnf.requestFocus();
                            } else{
                                HashMap<String, String> params = new HashMap<>();
                                params.put("email", stremail);
                                params.put("password", strpwd);

                                PerformNetworkRequestUpdate request = new PerformNetworkRequestUpdate(Apiurl.URL_CHANGEPASSUSR, params, CODE_POST_REQUEST);
                                request.execute();
                            }
                        }
                    });
                    builder.setView(view1);
                    alert = builder.create();
                    alert.show();
                    alert.setCanceledOnTouchOutside(false);
                    //sweetAlertDialog.show();

                    sweetAlertDialog=new SweetAlertDialog(ForgotPasswordUser.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Successfull")
                            .setContentText("Changed Password Successfully")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent=new Intent(ForgotPasswordUser.this,UserLogin.class);
                                    startActivity(intent);
                                }
                            });
                }

            }
        });

    }

    private void sendEmail() {


        String subject = editTextSubject.getText().toString().trim();

        pass = String.valueOf(Rnumber);

        String messg = "Hello... " +stremail+ " ,\nYour Code is : "+Rnumber+ "\nYou can change the password Using this code. \nThank You...!!!";
        editTextMessage.setText(messg);

        String message = editTextMessage.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(ForgotPasswordUser.this, stremail, subject, message);

        //Executing sendmail to send email
        sm.execute();

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    class PerformNetworkRequestUpdate extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog;

        PerformNetworkRequestUpdate(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog=new ProgressDialog(ForgotPasswordUser.this);
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
                    alert.dismiss();
                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                }
                else {
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

    private void details(){
        HashMap<String, String> params = new HashMap<>();
       PerformNetworkRequestdetails request = new PerformNetworkRequestdetails(Apiurl.URL_USEREXISTS, params, CODE_POST_REQUEST);
        request.execute();

    }

    private void refreshprofilelist(JSONArray helptips) throws JSONException {
        data.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            data.add(new User(
                    obj.getString("email"),
                    obj.getString("contact")
            ));
        }
        for(int j=0;j<data.size();j++)
        {
            emailid.add(data.get(j).getName());
        }
        System.out.println(emailid);
    }

    private class PerformNetworkRequestdetails extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        PerformNetworkRequestdetails(String url, HashMap<String, String> params, int requestCode) {
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
                    refreshprofilelist(object.getJSONArray("exist"));
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

    private boolean isValidPassword(String vpassword) {
        String PASSWORD_PATTERN = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(vpassword);
        return matcher.matches();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (alert!=null && alert.isShowing()){
            alert.dismiss();
        }
        else if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back=new Intent(ForgotPasswordUser.this,UserLogin.class);
        startActivity(back);
        finish();
    }

    private boolean isEmailexists(String stremailid)
    {
        for (int i=0;i<emailid.size();i++)
        {
            if(stremailid.equals(emailid.get(i)))
            {
                System.out.println(emailid.get(i));
                return true;
            }
        }
        return false;
    }


}
