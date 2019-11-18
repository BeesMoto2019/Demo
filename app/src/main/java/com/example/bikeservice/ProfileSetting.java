package com.example.bikeservice;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetting extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private int REQUEST_CAMERA = 0, PICK_IMAGE = 1;
    private String userChoosenTask;
    ArrayList<String> mechemailid = new ArrayList<String>();
    ArrayList<String> mechcontactno = new ArrayList<String>();
    SweetAlertDialog sweetAlertDialog;
    AlertDialog dialog1;
    ProgressDialog progressDialog;
    boolean check = true;
    EditText edtname,edtemail,edtcontact,edtaddress,edtstate,edtcity,edtlandmark,edtpincode,edtpassword;
    Button btnupdate;
    TextView textView;
    int number;
    List<User> data;
    int id;
    CircleImageView prpic;
    ImageView prcam;
    TextView tvchange;
    Bitmap bm=null;
    AlertDialog dialog;
    String strfilename,stremail;
    String strid,emailid,strname,strcontact,straddress,strstate,strcity,strlandmark,strpincode,strpassword,strimage,strphone;
    SweetAlertDialog selectphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        data=new ArrayList<>();
        Toolbar toolbar=(Toolbar)findViewById(R.id.profiletoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvchange=(TextView)findViewById(R.id.Changepassword);

        prpic = (CircleImageView) findViewById(R.id.prpic);
        prcam = (ImageView)findViewById(R.id.prcam);
        textView=(TextView)findViewById(R.id.regid);
        edtname=(EditText)findViewById(R.id.garagename);
        edtemail=(EditText)findViewById(R.id.email);
        edtcontact=(EditText)findViewById(R.id.contact);
        edtaddress=(EditText) findViewById(R.id.address);
        edtstate=(EditText)findViewById(R.id.state);
        edtcity=(EditText)findViewById(R.id.city);
        edtlandmark=(EditText)findViewById(R.id.landmark);
        edtpincode=(EditText)findViewById(R.id.pincode);
        btnupdate=(Button)findViewById(R.id.update);

        SharedPreferences sharedpreferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        emailid=sharedpreferences.getString("Email","");
        Log.d("Email",emailid);
        profile();


        //code for uploading profile pic

        prpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tvchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
                View view1 = getLayoutInflater().inflate(R.layout.change_password, null);
                builder.setView(view1);

                final EditText edtopass = (EditText) view1.findViewById(R.id.oldpassword);
                final EditText edtnpass = (EditText) view1.findViewById(R.id.npassword);
                final EditText edtcpass = (EditText) view1.findViewById(R.id.cpassword);
                final Button btnchange=(Button) view1.findViewById(R.id.chnage);

                btnchange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String stroldpass=edtopass.getText().toString().trim();
                        String strnewpass=edtnpass.getText().toString().trim();
                        String strcnfpass=edtcpass.getText().toString().trim();

                        if(TextUtils.isEmpty(stroldpass)) {
                            edtopass.setError("Please Enter Old Password");
                            edtopass.requestFocus();
                        } else if(!isValidPassword(strnewpass)) {
                            edtnpass.setError("Please Enter Password Like 'hozU@123'");
                            edtnpass.requestFocus();
                        } else if(!strcnfpass.equals(strnewpass)){
                            edtcpass.setError("Password is Not Match");
                            edtcpass.requestFocus();
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("mech_id", strid);
                            params.put("password", stroldpass);
                            params.put("newpass", strnewpass);
                            PerformNetworkChangepass pass = new PerformNetworkChangepass(Apiurl.URL_MECHCHANGEPWD, params, CODE_POST_REQUEST);
                            pass.execute();

                            sweetAlertDialog = new SweetAlertDialog(ProfileSetting.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Changed Password Successfull")
                                    .setConfirmText("Ok")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent req = new Intent(ProfileSetting.this, NavigationDrawer.class);
                                            startActivity(req);
                                            finish();
                                        }
                                    });
                        }
                    }
                });
                dialog1 = builder.create();
                dialog1.show();
                dialog1.setCanceledOnTouchOutside(true);
            }
        });


        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strid = textView.getText().toString().trim();
                strname = edtname.getText().toString().trim();
                strphone = edtcontact.getText().toString().trim();
                straddress = edtaddress.getText().toString().trim();
                strstate = edtstate.getText().toString().trim();
                strcity = edtcity.getText().toString().trim();
                strlandmark = edtlandmark.getText().toString().trim();
                strpincode = edtpincode.getText().toString().trim();

                if(!isValidName(strname)) {
                    edtname.setError("Please Enter Your Name");
                    edtname.requestFocus();
                } else if(!isValidContact(strphone)) {
                    edtcontact.setError("Enter the 10 Digit Number");
                    edtcontact.requestFocus();
                } else if(TextUtils.isEmpty(straddress)){
                    edtaddress.setError("Please Enter Your Address");
                    edtaddress.requestFocus();
                } else if(TextUtils.isEmpty(strstate)){
                    edtstate.setError("Please Enter Your State");
                    edtstate.requestFocus();
                } else if(TextUtils.isEmpty(strcity)){
                    edtcity.setError("Please Enter Your City");
                    edtcity.requestFocus();
                } else if(TextUtils.isEmpty(strlandmark)) {
                    edtlandmark.setError("Please Enter Your PIncode");
                    edtlandmark.requestFocus();
                } else if(!isValidpincode(strpincode)){
                    edtpincode.setError("Please Enter 6 Digit Pincode");
                    edtpincode.requestFocus();
                } else {
                    if (!edtcontact.getText().toString().equals(strcontact)) {
                        //System.out.println("contact is changed");
                        try {
                            // Construct data
                            String apiKey = "apikey=" + "Va9cDk0PXpE-cHQStVYiuYpkHmNLre4QEpW7BAEkUt";
                            Random random = new Random();
                            number = random.nextInt(999999);
                            String message = "&message=" + "hey " + strname + " your otp is " + number;
                            String sender = "&sender=" + "BeesMOTO";
                            String numbers = "&numbers=" + strphone;

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
                            System.out.println(e);
                            Toast.makeText(getApplicationContext(), "error sms" + e, Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), "error" + e, Toast.LENGTH_LONG).show();
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
                        View view1 = getLayoutInflater().inflate(R.layout.custom_dialogue, null);
                        final EditText otp = (EditText) view1.findViewById(R.id.otp);
                        Button verify = (Button) view1.findViewById(R.id.verify);


                        verify.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!otp.getText().toString().isEmpty() && number == Integer.valueOf(otp.getText().toString())) {
                                    if (bm == null) {
                                        update();
                                    } else {
                                        ImageUploadToServerFunction();

                                        sweetAlertDialog = new SweetAlertDialog(ProfileSetting.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setContentText("Changed Successfull")
                                                .setConfirmText("Ok")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        Intent req = new Intent(ProfileSetting.this, NavigationDrawer.class);
                                                        startActivity(req);
                                                        finish();
                                                    }
                                                });

                                        sweetAlertDialog.show();
                                    }
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Enter Correct OTP", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                        builder.setView(view1);
                        dialog = builder.create();
                        dialog.show();
                        dialog.setCanceledOnTouchOutside(false);
                    } else {
                        //System.out.println("contact is same");
                        if (bm == null) {
                            update();
                        } else {
                            ImageUploadToServerFunction();

                            sweetAlertDialog = new SweetAlertDialog(ProfileSetting.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Changed Successfull")
                                    .setConfirmText("Ok")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent req = new Intent(ProfileSetting.this, NavigationDrawer.class);
                                            startActivity(req);
                                            finish();
                                        }
                                    });

                            sweetAlertDialog.show();
                        }
                        ;
                    }
                }
            }
        });
    }

    private void update()
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("mech_id", strid);
        params.put("garage_name", strname);
        params.put("mech_contact", strphone);
        params.put("mech_address", straddress);
        params.put("mech_state", strstate);
        params.put("mech_city", strcity);
        params.put("mech_landmark", strlandmark);
        params.put("mech_pincode", strpincode);
        //params.put("image_url", "");

        PerformNetworkRequestUpdateProfile request = new PerformNetworkRequestUpdateProfile(Apiurl.URL_UPDATEINFO, params, CODE_POST_REQUEST);
        request.execute();

        sweetAlertDialog=new SweetAlertDialog(ProfileSetting.this,SweetAlertDialog.SUCCESS_TYPE)
                .setContentText("Changed Successfull")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent req=new Intent(ProfileSetting.this,NavigationDrawer.class);
                        startActivity(req);
                        finish();
                    }
                });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileSetting.this);
        builder1.setTitle("Add Photo!");
        builder1.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(ProfileSetting.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder1.show();
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }
    // select image from galary
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                Uri uri=data.getData();
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                Uri uri1 = Uri.fromFile(destination);
                strfilename = uri1.getLastPathSegment();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        prpic.setImageBitmap(bm);
    }
    // take picture from camera
    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        Uri uri = Uri.fromFile(destination);
        strfilename = uri.getLastPathSegment();

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prpic.setImageBitmap(bm);
    }

    private void profile(){
        HashMap<String, String> params = new HashMap<>();
        params.put("mech_email",emailid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_PROFILE, params, CODE_POST_REQUEST);
        request.execute();

    }

    private void refreshprofilelist(JSONArray helptips) throws JSONException {
        data.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            data.add(new User(
                    id=obj.getInt("mech_id"),
                    strname=obj.getString("garage_name"),
                    emailid=obj.getString("mech_email"),
                    strcontact=obj.getString("mech_contact"),
                    straddress=obj.getString("mech_address"),
                    strstate=obj.getString("mech_state"),
                    strcity=obj.getString("mech_city"),
                    strlandmark=obj.getString("mech_landmark"),
                    strpincode=obj.getString("mech_pincode"),
                    strimage=obj.getString("image_url")
            ));
        }
        strid=Integer.toString(id);
        textView.setText(strid);
        edtname.setText(strname);
        edtemail.setText(emailid);
        edtcontact.setText(strcontact);
        edtaddress.setText(straddress);
        edtstate.setText(strstate);
        edtcity.setText(strcity);
        edtlandmark.setText(strlandmark);
        edtpincode.setText(strpincode);
        //set profile image into imageview using imageurl
        if(!strimage.isEmpty()){
            Glide.with(ProfileSetting.this).load(strimage).into(prpic);
            //System.out.println(strimage);
        }

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(ProfileSetting.this);
            progressDialog1.setCancelable(false);
            progressDialog1.setIndeterminate(true);
            progressDialog1.setMessage("Please Wait...");
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog1.show();
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
                    refreshprofilelist(object.getJSONArray("profile"));
                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                    SharedPreferences sp1 = getSharedPreferences("Mechanic", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sp1.edit();
                    editor.putString("garage",edtname.getText().toString());
                    editor.putString("city",edtcity.getText().toString());
                    editor.putString("contact",edtcontact.getText().toString());
                    editor.putString("landmark",edtlandmark.getText().toString());
                    editor.putString("pincode",edtpincode.getText().toString());
                    editor.commit();

                    progressDialog1.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog1.dismiss();
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

    class PerformNetworkRequestUpdateProfile extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog2;

        PerformNetworkRequestUpdateProfile(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog2=new ProgressDialog(ProfileSetting.this);
            progressDialog2.setMessage("Please Wait...");
            progressDialog2.setCancelable(false);
            progressDialog2.setIndeterminate(true);
            progressDialog2.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog2.show();
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
                    Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog2.dismiss();
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

    public void ImageUploadToServerFunction(){

        ByteArrayOutputStream byteArrayOutputStreamObject ;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(ProfileSetting.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                //Printing uploading success message coming from server on android app.
                //Toast.makeText(ProfileSetting.this,string1,Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                //iv.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<>();
                HashMapParams.put("mech_id", strid);
                HashMapParams.put("garage_name", strname);
                HashMapParams.put("mech_contact", strphone);
                HashMapParams.put("mech_address", straddress);
                HashMapParams.put("mech_state", strstate);
                HashMapParams.put("mech_city", strcity);
                HashMapParams.put("mech_landmark", strlandmark);
                HashMapParams.put("mech_pincode", strpincode);
                HashMapParams.put("image_url", ConvertImage);
                HashMapParams.put("image_name", strfilename);
                String FinalData = imageProcessClass.ImageHttpRequest(Apiurl.URL_UPDATEPROFILE, HashMapParams);

                return FinalData;

            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }
    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }
    }

    private void details(){
        HashMap<String, String> params = new HashMap<>();
       PerformNetworkRequestdetails request = new PerformNetworkRequestdetails(Apiurl.URL_MECHEXISTS, params, CODE_POST_REQUEST);
        request.execute();

    }

    private void refreshlist(JSONArray helptips) throws JSONException {
        data.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            data.add(new User(
                    obj.getString("mech_email"),
                    obj.getString("mech_contact")
            ));
        }
        for(int j=0;j<data.size();j++)
        {
            mechemailid.add(data.get(j).getName());
            mechcontactno.add(data.get(j).getEmail());
        }
        System.out.println(mechemailid);
        System.out.println(mechcontactno);
    }

    private boolean isEmailexists(String stremailid)
    {
        for (int i=0;i<mechemailid.size();i++)
        {
            if(stremailid.equals(mechemailid.get(i)))
            {
                System.out.println(mechemailid.get(i));
                return true;
            }
        }
        return false;
    }

    private boolean isexists(String strcontact)
    {
        for (int i=0;i<mechcontactno.size();i++)
        {
            if(strcontact.equals(mechcontactno.get(i)))
            {
                System.out.println(mechcontactno.get(i));
                return true;
            }
        }
        return false;
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
                    refreshprofilelist(object.getJSONArray("Mechexist"));
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

    class PerformNetworkChangepass extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog3;

        PerformNetworkChangepass(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);

            progressDialog3=new ProgressDialog(ProfileSetting.this);
            progressDialog3.setMessage("Please Wait...");
            progressDialog3.setCancelable(false);
            progressDialog3.setIndeterminate(true);
            progressDialog3.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog3.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    sweetAlertDialog.show();
                    dialog1.dismiss();

                }
                else {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog3.dismiss();
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
        Intent intent=new Intent(ProfileSetting.this,NavigationDrawer.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
        else if(progressDialog!=null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        else if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
        else if(dialog1!=null && dialog1.isShowing())
        {
            dialog1.dismiss();
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

    private boolean isValidpincode(String pincode) {
        if (pincode != null && pincode.length()==6) {
            return true;
        }
        return false;
    }
}
