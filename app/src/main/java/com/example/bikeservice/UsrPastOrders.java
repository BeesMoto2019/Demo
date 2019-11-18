package com.example.bikeservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UsrPastOrders extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView pastlist;
    List<User> userdetaillist;
    List<User> feedbacklist;
    String strusremail,strreqid,streid,requesttype;
    int intreqid,inteid;
    ImageView imgclose;
    TextView tvfeedback;
    RatingBar rateapp,ratemech;
    String mechrate,apprate;
    SweetAlertDialog sweetAlertDialog;
    AlertDialog dialog;
    String mrate,Arate,fb;
    BottomSheetDialog bsdialog;
    TextView usrpast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usr_past_orders);
        Toolbar toolbar=(Toolbar)findViewById(R.id.usrpast);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Past Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        usrpast=(TextView)findViewById(R.id.textusrpast);
        userdetaillist= new ArrayList<>();
        feedbacklist = new ArrayList<>();
        pastlist=findViewById(R.id.usrpastlist);
        pastlist.setDivider(null);


        SharedPreferences sharedpreferences = getSharedPreferences("userlogin",
                Context.MODE_PRIVATE);
        strusremail = sharedpreferences.getString("email","");
        Log.d("Useremail",strusremail);
        pastorder();
    }

    private void pastorder(){
        HashMap<String, String> params = new HashMap<>();
        params.put("usr_email",strusremail);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_USRPAST, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(UsrPastOrders.this, R.layout.raw_past_orders, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_past_orders, null, true);

            TextView tvumemail = listViewItem.findViewById(R.id.umemail);
            TextView tvcontact = listViewItem.findViewById(R.id.contgarage);
            TextView tvbike = listViewItem.findViewById(R.id.bmodel);
            TextView tvservice = listViewItem.findViewById(R.id.bservice);
            TextView tvtype=listViewItem.findViewById(R.id.requesttype);
            TextView tvapdate = listViewItem.findViewById(R.id.adate);
            TextView tvtime = listViewItem.findViewById(R.id.wtime);
            Button btnfeedback = listViewItem.findViewById(R.id.feedback);
            Button btnview=listViewItem.findViewById(R.id.viewfeedback);

            final User reqid = Userlist.get(position);
            final User umemail = Userlist.get(position);
            final User contact = Userlist.get(position);
            final User bike = Userlist.get(position);
            final User service = Userlist.get(position);
            final User date = Userlist.get(position);
            final User time = Userlist.get(position);
            final User status = Userlist.get(position);
            final User type=Userlist.get(position);

            intreqid = reqid.getId();
            strreqid = Integer.toString(intreqid);

            //System.out.println(strreqid);
            tvumemail.setText("Mechanic Email: " + umemail.getName());
            tvcontact.setText("Mechanic garage : " + contact.getEmail());
            tvbike.setText("BikeModel : " + bike.getPhone());
            tvservice.setText("Service :" + service.getAddress());
            tvapdate.setText("Appointment date : " + date.getState());
            tvtime.setText("Work Time :" + time.getCity());
            tvtype.setText("Request Type : " +type.getPincode());

            String wrkstatus = status.getLandmark();
            int wrkst = Integer.parseInt(wrkstatus);

            if (wrkst == 0) {
                btnfeedback.setVisibility(View.INVISIBLE);
                btnview.setVisibility(View.VISIBLE);

                btnview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        intreqid = reqid.getId();
                        strreqid = Integer.toString(intreqid);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("req_id",strreqid);
                        params.put("req_type",type.getPincode());
                        PerformNetworkViewFeedback request = new PerformNetworkViewFeedback(Apiurl.URL_VIEWFEEDBACK, params, CODE_POST_REQUEST);
                        request.execute();

                        bsdialog=new BottomSheetDialog(UsrPastOrders.this);
                        LayoutInflater layoutInflater=getLayoutInflater();
                        View view1 = layoutInflater.inflate(R.layout.viewfeedback, null);
                        bsdialog.setContentView(view1);

                        imgclose=(ImageView)view1.findViewById(R.id.closewindow);

                        rateapp=view1.findViewById(R.id.apprate);
                        ratemech=view1.findViewById(R.id.mechrate);
                        tvfeedback=view1.findViewById(R.id.feed);

                        imgclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bsdialog.dismiss();
                            }
                        });

                        bsdialog.show();

                    }
                });
            }
            else {

            btnfeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intreqid = reqid.getId();
                    strreqid = Integer.toString(intreqid);
                    System.out.println(strreqid);

                    AlertDialog.Builder builder = new AlertDialog.Builder(UsrPastOrders.this);
                    View view1 = getLayoutInflater().inflate(R.layout.activity_feedback, null);
                    builder.setView(view1);

                    final RatingBar mechrating = (RatingBar) view1.findViewById(R.id.mechrating);
                    final RatingBar apprating = (RatingBar) view1.findViewById(R.id.apprating);
                    final EditText edtfeedback = (EditText) view1.findViewById(R.id.msg);

                    mechrating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                                rating = ratingBar.getRating();
                                mechrate = String.valueOf(rating);
                        }
                    });

                    apprating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                            rating = ratingBar.getRating();
                            apprate = String.valueOf(rating);
                        }
                    });

                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String strfeedback = edtfeedback.getText().toString().trim();

                            if(mechrating.getRating()==0.0)
                            {
                                mechrate="0.5";
                            }
                            if(apprating.getRating()==0.0)
                            {
                                apprate="0.0";
                            }
                            intreqid = reqid.getId();
                            strreqid = Integer.toString(intreqid);
                            System.out.println(strreqid);
                                HashMap<String, String> params = new HashMap<>();
                                params.put("req_id", strreqid);
                                params.put("Usr_email", strusremail);
                                params.put("Mech_email", umemail.getName());
                                params.put("Garage_name", contact.getEmail());
                                params.put("BikeModel", bike.getPhone());
                                params.put("Bikeservice", service.getAddress());
                                params.put("MechRating", mechrate);
                                params.put("AppRating", apprate);
                                params.put("req_type",type.getPincode());
                                params.put("feedback", strfeedback);

                                System.out.println(type.getPincode());

                                PerformNetworkFeedback feedback = new PerformNetworkFeedback(Apiurl.URL_USRFEEDBACK, params, CODE_POST_REQUEST);
                                feedback.execute();

                                HashMap<String, String> params1 = new HashMap<>();
                                params1.put("Mech_email", umemail.getName());

                                PerformNetworkRating rating = new PerformNetworkRating(Apiurl.URL_MECHRATING, params1, CODE_POST_REQUEST);
                                rating.execute();

                                sweetAlertDialog = new SweetAlertDialog(UsrPastOrders.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setContentText("Feedback Successfull")
                                        .setConfirmText("Ok")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                Intent req = new Intent(UsrPastOrders.this, UserNavigation.class);
                                                startActivity(req);
                                                finish();
                                            }
                                        });
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.feddback_design);
                    Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(Color.BLACK);
                    Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(Color.BLACK);
                    dialog.setCanceledOnTouchOutside(false);

                }

            });
        }
            return listViewItem;
        }
    }
    private void refreshcurrentlist(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            userdetaillist.add(new User(
                    obj.getInt("req_id"),
                    obj.getString("mech_email"),
                    obj.getString("mech_garage"),
                    obj.getString("usr_bikemodel"),
                    obj.getString("usr_service"),
                    obj.getString("appointment_date"),
                    obj.getString("mech_time"),
                    obj.getString("Status"),
                    obj.getString("req_type")
            ));
        }

        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        pastlist.setAdapter(adapter);
    }

    private void refreshFeedbacklist(JSONArray helptips) throws JSONException {
        feedbacklist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            feedbacklist.add(new User(
                   mrate = obj.getString("MechRating"),
                   Arate = obj.getString("AppRating"),
                   fb = obj.getString("feedback")
            ));
        }

        float mechrate = Float.parseFloat(mrate);
        float apprate = Float.parseFloat(Arate);
        ratemech.setRating(mechrate);
        rateapp.setRating(apprate);
        tvfeedback.setText(fb);
    }


    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
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
            progressDialog=new ProgressDialog(UsrPastOrders.this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please Wait...");
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshcurrentlist(object.getJSONArray("usr_past_order"));

                }
                else {
                    usrpast.setVisibility(View.VISIBLE);
                    usrpast.setText("YOU HAVE NO PAST ORDERS YET!!!");
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

    class PerformNetworkFeedback extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;

        PerformNetworkFeedback(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(UsrPastOrders.this);
            progressDialog1.setMessage("Please Wait...");
            progressDialog1.setCancelable(false);
            progressDialog1.setIndeterminate(true);
            progressDialog1.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog1.show();
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
            progressDialog1.dismiss();
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


    private class PerformNetworkRating extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        PerformNetworkRating(String url, HashMap<String, String> params, int requestCode) {
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshcurrentlist(object.getJSONArray("mechrating"));
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

    private class PerformNetworkViewFeedback extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;
        PerformNetworkViewFeedback(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(UsrPastOrders.this);
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshFeedbacklist(object.getJSONArray("View_Feedback"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
        else if(sweetAlertDialog!=null && sweetAlertDialog.isShowing())
        {
            sweetAlertDialog.dismiss();
        }
    }
}
