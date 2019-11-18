package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminAddServices extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    EditText edtbikesrvc,edtsrvcinfo;
    String strsrvc,strsrvcinfo;
    Button btnadd;
    ListView bikelist;
    int globalInc = 0;
    ArrayList<String> selectedbike = new ArrayList<String>();
    List<User> bikemodels;
    CheckBox checkBox,selectall;
    SweetAlertDialog sweetAlertDialog;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_services);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbarsrvc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Services");
        bikemodels=new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtbikesrvc=(EditText)findViewById(R.id.srvcname);
        edtsrvcinfo=(EditText)findViewById(R.id.srvcinfo);
        bikelist=(ListView)findViewById(R.id.bikes);
        selectall=(CheckBox)findViewById(R.id.selecalltbike);
        btnadd=(Button)findViewById(R.id.btadd);

        data();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strsrvc=edtbikesrvc.getText().toString().trim();
                strsrvcinfo=edtsrvcinfo.getText().toString().trim();

                if(TextUtils.isEmpty(strsrvc))
                {
                    edtbikesrvc.setError("Please Enter the Service Name");
                    edtbikesrvc.requestFocus();
                }
                else if(TextUtils.isEmpty(strsrvcinfo))
                {
                    edtsrvcinfo.setError("Please Enter the Service Information");
                    edtsrvcinfo.requestFocus();
                }
                else {
                    if(globalInc>=1)
                    {
                        for(int i=0;i<selectedbike.size();i++)
                        {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("bike_name", selectedbike.get(i));
                            params.put("srvc_name",strsrvc);
                            params.put("srvc_info",strsrvcinfo);

                            PerformNetworkRequestResponse request = new PerformNetworkRequestResponse(Apiurl.URL_ADDSERVICE, params, CODE_POST_REQUEST);
                            request.execute();

                            sweetAlertDialog=new SweetAlertDialog(AdminAddServices.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Add Service Successfully")
                                    .setConfirmText("Ok")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Intent req=new Intent(AdminAddServices.this,AdminHome.class);
                                            startActivity(req);
                                            finish();
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_BIKELIST, params, CODE_POST_REQUEST);
        request.execute();

    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {

            super(AdminAddServices.this, R.layout.raw_mechreq, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            final View listViewItem = inflater.inflate(R.layout.raw_bikes, null, true);

            TextView tvbike=(TextView) listViewItem.findViewById(R.id.bikemodel);
            checkBox=(CheckBox)listViewItem.findViewById(R.id.selectbike);

            final User bike = Userlist.get(position);
            final  User check=Userlist.get(position);

            tvbike.setText(bike.getName());
            checkBox.setChecked(check.getSelected());

            user=getItem(position);

            selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){

                            for (int j = 0; j < bikelist.getAdapter().getCount(); j++) {

                                View view = getViewByPosition(j,bikelist);
                                checkBox = view.findViewById(R.id.selectbike);
                                Userlist.get(j).setSelected(true);
                                checkBox.setChecked(true);
                                //bikelist.setItemChecked(j,true);
                        }
                    }
                    else if(!selectall.isChecked()){
                        for(int i=0;i<bikelist.getAdapter().getCount();i++)
                        {
                            View view = getViewByPosition(i,bikelist);
                            checkBox = view.findViewById(R.id.selectbike);
                            Userlist.get(i).setSelected(false);
                            checkBox.setChecked(false);
                        }
                    }
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    user = (User) buttonView.getTag();

                    if(isChecked)
                    {
                        globalInc++;
                        selectedbike.add(bike.getName());
                        Userlist.get(position).setSelected(true);
                    }
                    else if(!isChecked)
                    {
                        globalInc--;
                        selectedbike.remove(bike.getName());
                        Userlist.get(position).setSelected(false);
                    }
                    else
                    {
                        user.setSelected(isChecked);
                    }
                    System.out.println(" ---------------    "+globalInc);
                }
            });

            checkBox.setTag(user);
            checkBox.setChecked(user.isSelected());

            return listViewItem;
        }
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void refreshApprovedreqlist(JSONArray helptips) throws JSONException {
        bikemodels.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            bikemodels.add(new User(
                    obj.getString("model")
            ));
        }
        final UserListAdapter adapter = new UserListAdapter(bikemodels);
        bikelist.setAdapter(adapter);
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

            progressDialog=new ProgressDialog(AdminAddServices.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog.show();
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
                    refreshApprovedreqlist(object.getJSONArray("getBike"));
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

    class PerformNetworkRequestResponse extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        ProgressDialog progressDialog1;

        PerformNetworkRequestResponse(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            progressDialog1=new ProgressDialog(AdminAddServices.this);
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
                    //Toast.makeText(getApplicationContext(), "Sorry", Toast.LENGTH_SHORT).show();

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(AdminAddServices.this,AdminHome.class);
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
