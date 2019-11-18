package com.example.bikeservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserRegistration extends AppCompatActivity {
    EditText edtname,edtemail,edtcontact,edtaddress,edtstate,edtcity,edtlandmark,edtpincode,edtpassword,edtcnfpassword;
    String strname,stremail,strcontact,stradd,strstate,strcity,strlandmark,strpin,strpassword,strcnfpassword,stremailaddress,strcontactno;
    Button btnsubmit;
    ArrayList<String> emailid = new ArrayList<String>();
    ArrayList<String> contactno = new ArrayList<String>();
    List<User> data;
    String token;
    private TextView editTextSubject;
    private TextView editTextMessage;
    int number;
    boolean result;
    AlertDialog dialog;
    SweetAlertDialog sweetAlertDialog;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        editTextSubject = (TextView)findViewById(R.id.subject);
        editTextMessage = (TextView)findViewById(R.id.Message);
        edtname = (EditText)findViewById(R.id.Name);
        edtemail = (EditText)findViewById(R.id.Email);
        edtcontact = (EditText)findViewById(R.id.phone);
        edtaddress = (EditText)findViewById(R.id.address);
        edtstate = (EditText)findViewById(R.id.states);
        edtcity = (EditText)findViewById(R.id.City);
        edtlandmark = (EditText)findViewById(R.id.landmark);
        edtpincode = (EditText)findViewById(R.id.Pincode);
        edtpassword = (EditText)findViewById(R.id.pwd);
        edtcnfpassword=(EditText)findViewById(R.id.cnfpwd);
        btnsubmit = (Button)findViewById(R.id.submit);
        data=new ArrayList<>();

        token=SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
        System.out.println(token);

        details();

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strname = edtname.getText().toString().trim();
                stremail = edtemail.getText().toString().trim();
                strcontact = edtcontact.getText().toString().trim();
                stradd = edtaddress.getText().toString().trim();
                strstate = edtstate.getText().toString().trim();
                strcity = edtcity.getText().toString().trim();
                strlandmark = edtlandmark.getText().toString().trim();
                strpin = edtpincode.getText().toString().trim();
                strpassword = edtpassword.getText().toString().trim();
                strcnfpassword = edtcnfpassword.getText().toString().trim();

                if (!isValidName(strname)) {
                    edtname.setError("Please Enter Your Name");
                    edtname.requestFocus();
                } else if (!isValidEmail(stremail)) {
                    edtemail.setError("Please Enter Email 'xyz@gmail.com'");
                    edtemail.requestFocus();
                } else if (isEmailexists(stremail)){
                    edtemail.setError("Email is Already Taken");
                    edtemail.requestFocus();
                } else if (!isValidContact(strcontact)) {
                    edtcontact.setError("Enter the 10 Digit Number");
                    edtcontact.requestFocus();
                } else if (isexists(strcontact)){
                    edtcontact.setError("Contact is Already Taken");
                    edtcontact.requestFocus();
                } else if (TextUtils.isEmpty(stradd)) {
                    edtaddress.setError("Please Enter Your Address");
                    edtaddress.requestFocus();
                } else if (TextUtils.isEmpty(strstate)) {
                    edtstate.setError("Please Enter Your State");
                    edtstate.requestFocus();
                } else if (TextUtils.isEmpty(strcity)) {
                    edtcity.setError("Please Enter Your City");
                    edtcity.requestFocus();
                } else if (TextUtils.isEmpty(strlandmark)) {
                    edtlandmark.setError("Plaese Enter Your Landmark");
                    edtlandmark.requestFocus();
                } else if (!isValidpincode(strpin)) {
                    edtpincode.setError("Please Enter 6 Digit Pincode");
                    edtpincode.requestFocus();
                } else if (!isValidPassword(strpassword)) {
                    edtpassword.setError("Please Enter Password 'hozU@123'");
                    edtpassword.requestFocus();
                } else if (!strpassword.equals(strcnfpassword)) {
                    edtcnfpassword.setError("Password is Not Match");
                    edtcnfpassword.requestFocus();
                } else {
                    try {
                        // Construct data
                        String apiKey = "apikey=" + "Va9cDk0PXpE-cHQStVYiuYpkHmNLre4QEpW7BAEkUt";
                        Random random = new Random();
                        number = random.nextInt(999999);
                        String message = "&message=" + "hey " + strname + "your otp is " + number;
                        String sender = "&sender=" + "TXTLCL";
                        String numbers = "&numbers=" + strcontact;
                        // Send data
                        HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                        String data = apiKey + numbers + message + sender;
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                        conn.getOutputStream().write(data.getBytes("UTF-8"));
                        final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        final StringBuffer stringBuffer = new StringBuffer();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                        rd.close();
                        // return stringBuffer.toString();
                        Toast.makeText(getApplicationContext(), "otp send", Toast.LENGTH_LONG).show();
                        System.out.println(number);
                    } catch (Exception e) {
                        // System.out.println("Error SMS "+e);
                        //return "Error "+e;
                        Toast.makeText(getApplicationContext(), "error sms" + e, Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "error" + e, Toast.LENGTH_LONG).show();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegistration.this);
                    View view1 = getLayoutInflater().inflate(R.layout.custom_dialogue, null);
                    final EditText otp = (EditText) view1.findViewById(R.id.otp);
                    Button verify = (Button) view1.findViewById(R.id.verify);

                    verify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!otp.getText().toString().isEmpty() && number == Integer.valueOf(otp.getText().toString())) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("Name", strname);
                                params.put("Email", stremail);
                                params.put("Contact", strcontact);
                                params.put("Address", stradd);
                                params.put("State", strstate);
                                params.put("City", strcity);
                                params.put("Landmark", strlandmark);
                                params.put("Pincode", strpin);
                                params.put("Password", strpassword);
                                params.put("Reg_datetime",getDateTime());
                                params.put("Token",token);

                                PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_USERREGISTRATION, params, CODE_POST_REQUEST);
                                request.execute();
                            } else {
                                Toast.makeText(UserRegistration.this, "Wrong Otp", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setView(view1);
                    dialog = builder.create();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);

                    sweetAlertDialog=new SweetAlertDialog(UserRegistration.this,SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Successfull")
                            .setContentText("Registration Successfull")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent=new Intent(UserRegistration.this,UserLogin.class);
                                    startActivity(intent);
                                    sendEmail();
                                }
                            });
                }
            }
        });
    }

    //store current DateTime in database
    private String getDateTime(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        return simpleDateFormat.format(date);

    }

    private void sendEmail() {

        String subject = editTextSubject.getText().toString().trim();

        String messg = "Hello... " +strname+ " ,\nYour Email is : "+stremail+ " ,\nYour Password is : "+strpassword+"\nYou has Successfully Register in our App. \nThank You...!!!";
        editTextMessage.setText(messg);

        String message = editTextMessage.getText().toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(UserRegistration.this, stremail, subject, message);

        //Executing sendmail to send email
        sm.execute();

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
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
            progressDialog=new ProgressDialog(UserRegistration.this);
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
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    sweetAlertDialog.show();
                    sweetAlertDialog.setCanceledOnTouchOutside(false);
                }
                else {
                    Toast.makeText(getApplicationContext(),"message", Toast.LENGTH_SHORT).show();

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
            contactno.add(data.get(j).getEmail());
        }
        System.out.println(emailid);
        System.out.println(contactno);
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

    //emailid validation
    private boolean isValidEmail(String vemail) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(vemail);
        return matcher.matches();
    }

    //password validation
    private boolean isValidPassword(String vpassword) {
        String PASSWORD_PATTERN = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(vpassword);
        return matcher.matches();
    }

    private boolean isValidName(String name) {
        if (name != null && name.length() >=3 ) {
            return true;
        }
        return false;
    }

    //phone validation
    private boolean isValidContact(String vcontact) {
        String Contact_PATTERN = "\\d{10}";

        Pattern pattern = Pattern.compile(Contact_PATTERN);
        Matcher matcher = pattern.matcher(vcontact);
        return matcher.matches();
    }

    private boolean isexists(String strcontact)
    {
        for (int i=0;i<contactno.size();i++)
        {
            if(strcontact.equals(contactno.get(i)))
            {
                System.out.println(contactno.get(i));
                return true;
            }
        }
        return false;
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

    private boolean isValidpincode(String pincode) {
        if (pincode != null && pincode.length()==6) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
        else if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(UserRegistration.this,UserLogin.class);
        startActivity(back);
        finish();
    }
}
