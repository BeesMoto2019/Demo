package com.example.bikeservice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class AdminApproveRequest extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;
    List<User> orderdata;
    String strreqid,strcode,strreqstatus;
    String usrid;
    int intid,requestid;
    ImageView imgclose;
    String strmechname,strusrname,strmechgarage,strbikemodel,strusrcontact,strcontact,strbikeservice,strapprating,strmechrating,strrating,strland,strpincode,strreqdate,strappdate,strtime,strreqtype,strreqtime,strappointmenttime;
    UserListAdapter adapter;
    TextView tvmechname,tvmechgarage,tvlandmark,tvpincode,tvmechcontact,tvusrcontact,tvusername,tvuserbike,tvusrsrvc,tvreqdate,tvreqtype,tvappdate,tvtime,tvreqtime,tvapptime;
    RatingBar mechrating,apprating,rating;
    BottomSheetDialog dialog;
    CircleImageView download;
    SweetAlertDialog sweetAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve_request);
        Toolbar toolbar=(Toolbar)findViewById(R.id.approvetool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Request History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userdetaillist= new ArrayList<>();
        orderdata=new ArrayList<>();
        datalist=findViewById(R.id.approvelist);
        datalist.setDivider(null);
        download = (CircleImageView)findViewById(R.id.icons);


        data();
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result=Utility.checkPermission(AdminApproveRequest.this);
                if(result) {

                    sweetAlertDialog=new SweetAlertDialog(AdminApproveRequest.this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Create Excel")
                            .setContentText("Do You Want to Create Excel File?")
                            .setConfirmText("Yes")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    createExcelSheet();
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                            sweetAlertDialog.show();
                }
            }
        });
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        //params.put("req_id",strreqid);
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_MECHRESPONSE, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(AdminApproveRequest.this, R.layout.raw_responseadmin, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_responseadmin, null, true);

            TextView tvid=(TextView)listViewItem.findViewById(R.id.userid);
            TextView tvresemail=(TextView) listViewItem.findViewById(R.id.resemail);
            final TextView tvgname=(TextView) listViewItem.findViewById(R.id.grgnme);
            TextView tvusremail=(TextView) listViewItem.findViewById(R.id.reqemail);
            TextView tvusrbike=(TextView) listViewItem.findViewById(R.id.reqbike);
            ImageView imginfo=(ImageView) listViewItem.findViewById(R.id.moreinfo);

            final User id=Userlist.get(position);
            final User email = Userlist.get(position);
            final User gname = Userlist.get(position);
            final User usremail = Userlist.get(position);
            final User usrbike = Userlist.get(position);

            intid=id.getId();
            usrid=String.valueOf(intid);
            tvid.setText(usrid);
            tvresemail.setText("Mechanic Email : "+email.getName());
            tvgname.setText("Mechanic Garage : "+gname.getEmail());
            tvusremail.setText("User Email : "+usremail.getCity());
            tvusrbike.setText("User BikeModel : "+usrbike.getLandmark());

            imginfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intid=id.getId();
                    usrid=String.valueOf(intid);

                    details();
                    //AlertDialog.Builder builder = new AlertDialog.Builder(AdminApproveRequest.this);
                    dialog=new BottomSheetDialog(AdminApproveRequest.this);
                    LayoutInflater layoutInflater=getLayoutInflater();
                    View view1 = layoutInflater.inflate(R.layout.approve_details, null);
                    dialog.setContentView(view1);

                    imgclose=(ImageView)view1.findViewById(R.id.closewindow);

                    //user
                    tvusername=(TextView)view1.findViewById(R.id.username);
                    tvusrcontact=(TextView)view1.findViewById(R.id.usercontact);
                    mechrating=(RatingBar)view1.findViewById(R.id.mechrate);
                    apprating=(RatingBar)view1.findViewById(R.id.apprate);

                    //mech
                    tvmechname=(TextView)view1.findViewById(R.id.mechname);
                    tvmechgarage=(TextView)view1.findViewById(R.id.mechgarage);
                    tvmechcontact=(TextView)view1.findViewById(R.id.mechcontact);
                    rating=(RatingBar)view1.findViewById(R.id.rate);

                    //order
                    tvreqtype=(TextView)view1.findViewById(R.id.reqtype);
                    tvuserbike=(TextView)view1.findViewById(R.id.usrbike);
                    tvusrsrvc=(TextView)view1.findViewById(R.id.usrsrvc);
                    tvreqdate=(TextView)view1.findViewById(R.id.reqdate);
                    tvreqtime=(TextView)view1.findViewById(R.id.reqtime);
                    tvappdate=(TextView)view1.findViewById(R.id.appointment);
                    tvapptime=(TextView)view1.findViewById(R.id.appointmenttime);
                    tvlandmark=(TextView)view1.findViewById(R.id.land);
                    tvpincode=(TextView)view1.findViewById(R.id.Pin);
                    tvtime=(TextView)view1.findViewById(R.id.wrktime);

                    imgclose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            return listViewItem;
        }
    }
    private void refreshIndividualList(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            {
                userdetaillist.add(new User(
                        obj.getInt("req_id"),
                        obj.getString("Res_email"),
                        obj.getString("Garage_name"),
                        obj.getString("Landmark"),
                        obj.getString("pincode"),
                        obj.getString("contact"),
                        obj.getString("req_email"),
                        obj.getString("BikeModel"),
                        obj.getString("Service"),
                        obj.getString("appointment_date"),
                        obj.getString("mech_time"),
                        obj.getString("MechRating"),
                        obj.getString("AppRating")
                ));
            }
        }
        adapter = new UserListAdapter(userdetaillist);
        datalist.setAdapter(adapter);
    }

    private void details(){

        usrid=String.valueOf(intid);
        System.out.println(usrid);
        HashMap<String, String> params = new HashMap<>();
        params.put("req_id",usrid);
        PerformNetworkDetails request = new PerformNetworkDetails(Apiurl.URL_APPROVEDETAILS, params, CODE_POST_REQUEST);
        request.execute();
    }
    private void refreshdetails(JSONArray helptips) throws JSONException {
        orderdata.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            {
                orderdata.add(new User(
                        requestid = obj.getInt("req_id"),
                        strusrname = obj.getString("Name"),
                        strreqdate = obj.getString("req_date"),
                        strusrcontact = obj.getString("Contact"),
                        strrating = obj.getString("Rating"),
                        strmechname = obj.getString("mech_name"),
                        strreqtype = obj.getString("req_type"),
                        strmechgarage = obj.getString("Garage_name"),
                        strland = obj.getString("Landmark"),
                        strpincode = obj.getString("pincode"),
                        strcontact = obj.getString("contact"),
                        strbikemodel = obj.getString("BikeModel"),
                        strbikeservice = obj.getString("Service"),
                        strappdate = obj.getString("appointment_date"),
                        strtime = obj.getString("mech_time"),
                        strmechrating = obj.getString("MechRating"),
                        strapprating = obj.getString("AppRating"),
                        strreqtime=obj.getString("appointment_time"),
                        strappointmenttime=obj.getString("req_time")
                ));
            }
        }

        if(strmechrating.equals("null") || strapprating.equals("null")) {

            tvusername.setText(strusrname);
            tvusrcontact.setText(strusrcontact);
            mechrating.setRating((float)0.0);
            apprating.setRating((float)0.0);
            tvmechname.setText(strmechname);
            tvmechgarage.setText(strmechgarage);
            tvmechcontact.setText(strcontact);
            float ratingmech=Float.valueOf(strrating);
            rating.setRating(ratingmech);
            tvreqtype.setText(strreqtype);
            tvuserbike.setText(strbikemodel);
            tvusrsrvc.setText(strbikeservice);
            tvreqdate.setText(strreqdate);
            tvappdate.setText(strappdate);
            tvlandmark.setText(strland);
            tvpincode.setText(strpincode);
            tvtime.setText(strtime);
            tvreqtime.setText(strreqtime);
            tvapptime.setText(strappointmenttime);
        }
        else
        {
            tvusername.setText(strusrname);
            tvusrcontact.setText(strusrcontact);
            float mechanic = Float.valueOf(strmechrating);
            mechrating.setRating(mechanic);
            float application = Float.valueOf(strapprating);
            apprating.setRating(application);
            tvmechname.setText(strmechname);
            tvmechgarage.setText(strmechgarage);
            tvmechcontact.setText(strcontact);
            float ratingmech=Float.valueOf(strrating);
            rating.setRating(ratingmech);
            tvreqtype.setText(strreqtype);
            tvuserbike.setText(strbikemodel);
            tvusrsrvc.setText(strbikeservice);
            tvreqdate.setText(strreqdate);
            tvappdate.setText(strappdate);
            tvlandmark.setText(strland);
            tvpincode.setText(strpincode);
            tvtime.setText(strtime);
            tvreqtime.setText(strreqtime);
            tvapptime.setText(strappointmenttime);
        }
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
            progressDialog=new ProgressDialog(AdminApproveRequest.this);
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

                    //Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshIndividualList(object.getJSONArray("approve_admdeatil"));


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

    private class PerformNetworkDetails extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;
        PerformNetworkDetails(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(AdminApproveRequest.this);
            progressDialog1.setMessage("Please Wait...");
            progressDialog1.setCancelable(false);
            progressDialog1.setIndeterminate(true);
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
                    refreshdetails(object.getJSONArray("individual_response"));

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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    //code for deny
                }
                break;
        }
    }

    private void createExcelSheet() {
        System.out.println("button clicked");
        //File destination =  new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() + ".xls");
        String Fnamexls = "approvereq" + System.currentTimeMillis() + ".xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath()+"/download");
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;

        try {

            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableFont writablefont = new WritableFont(WritableFont.TIMES,10,WritableFont.BOLD,false);
            WritableFont fieldfont = new WritableFont(WritableFont.TIMES,10,WritableFont.NO_BOLD,false);
            WritableCellFormat titleformat = new WritableCellFormat(writablefont);
            WritableCellFormat dataformat = new WritableCellFormat(fieldfont);
            //workbook.createSheet("Report", 0);
            WritableSheet sheet = workbook.createSheet("Approve Request", 0);
            Label label0 = new Label(0, 0, "Responser Email",titleformat);
            Label label1 = new Label(1, 0, "Garage Name",titleformat);
            Label label2 = new Label(2, 0, "Landmark",titleformat);
            Label label3 = new Label(3, 0, "Pincode",titleformat);
            Label label4 = new Label(4, 0, "Responser Contact",titleformat);
            Label label5 = new Label(5, 0, "Requester Email",titleformat);
            Label label6 = new Label(6, 0, "Bike Model",titleformat);
            Label label7 = new Label(7, 0, "Bike Service",titleformat);
            Label label8 = new Label(8, 0, "Appointment Date",titleformat);
            Label label9 = new Label(9, 0, "Work Time",titleformat);
            Label label10 = new Label(10, 0, "Mechanic Rating",titleformat);
            Label label11 = new Label(11, 0, "Application Rating",titleformat);


            try {
                sheet.addCell(label0);
                sheet.setColumnView(0,30);
                sheet.addCell(label1);
                sheet.setColumnView(1,30);
                sheet.addCell(label2);
                sheet.setColumnView(2,30);
                sheet.addCell(label3);
                sheet.setColumnView(3,30);
                sheet.addCell(label4);
                sheet.setColumnView(4,30);
                sheet.addCell(label5);
                sheet.setColumnView(5,30);
                sheet.addCell(label6);
                sheet.setColumnView(6,30);
                sheet.addCell(label7);
                sheet.setColumnView(7,30);
                sheet.addCell(label8);
                sheet.setColumnView(8,30);
                sheet.addCell(label9);
                sheet.setColumnView(9,30);
                sheet.addCell(label10);
                sheet.setColumnView(10,30);
                sheet.addCell(label11);
                sheet.setColumnView(11,30);

            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for(int i=0;i<userdetaillist.size();i++) {

                Label label12 = new Label(0, i+1, userdetaillist.get(i).getName(),dataformat);
                Label label13 = new Label(1, i+1, userdetaillist.get(i).getEmail(),dataformat);
                Label label14 = new Label(2, i+1, userdetaillist.get(i).getPhone(),dataformat);
                Label label15 = new Label(3, i+1, userdetaillist.get(i).getAddress(),dataformat);
                Label label16 = new Label(4, i+1, userdetaillist.get(i).getState(),dataformat);
                Label label17 = new Label(5, i+1, userdetaillist.get(i).getCity(),dataformat);
                Label label18 = new Label(6, i+1, userdetaillist.get(i).getLandmark(),dataformat);
                Label label19 = new Label(7, i+1, userdetaillist.get(i).getPincode(),dataformat);
                Label label20 = new Label(8, i+1, userdetaillist.get(i).getPassword(),dataformat);
                Label label21 = new Label(9, i+1, userdetaillist.get(i).getStatus(),dataformat);
                Label label22 = new Label(10, i+1, userdetaillist.get(i).getGarage(),dataformat);
                Label label23 = new Label(11, i+1, userdetaillist.get(i).getService(),dataformat);

                System.out.println(userdetaillist.get(i).getName());
                System.out.println(userdetaillist.get(i).getEmail());
                System.out.println(userdetaillist.get(i).getPhone());
                System.out.println(userdetaillist.get(i).getAddress());
                System.out.println(userdetaillist.get(i).getState());
                System.out.println(userdetaillist.get(i).getCity());
                System.out.println(userdetaillist.get(i).getLandmark());
                System.out.println(userdetaillist.get(i).getPincode());
                System.out.println(userdetaillist.get(i).getPassword());
                System.out.println(userdetaillist.get(i).getStatus());
                System.out.println(userdetaillist.get(i).getGarage());
                System.out.println(userdetaillist.get(i).getService());

                try {
                    sheet.addCell(label12);
                    sheet.addCell(label13);
                    sheet.addCell(label14);
                    sheet.addCell(label15);
                    sheet.addCell(label16);
                    sheet.addCell(label17);
                    sheet.addCell(label18);
                    sheet.addCell(label19);
                    sheet.addCell(label20);
                    sheet.addCell(label21);
                    sheet.addCell(label22);
                    sheet.addCell(label23);
                } catch (RowsExceededException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (WriteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            workbook.write();
            try {
                workbook.close();
                Toast.makeText(getApplicationContext(),"file is created in " + file,Toast.LENGTH_LONG).show();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent back = new Intent(getApplicationContext(),AdminHome.class);
        startActivity(back);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
        else if (sweetAlertDialog!=null && sweetAlertDialog.isShowing()){
            sweetAlertDialog.dismiss();
        }
    }
}
