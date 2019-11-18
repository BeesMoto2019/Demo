package com.example.bikeservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Corporate_userList extends Fragment {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    ListView datalist;
    List<User> userdetaillist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.activity_corporate_user_list,container,false);

        userdetaillist= new ArrayList<>();
        datalist=(ListView) v.findViewById(R.id.listview1);
        datalist.setDivider(null);

        data();
        return v;
    }

    private void data(){
        HashMap<String, String> params = new HashMap<>();
        PerformNetworkRequest request = new PerformNetworkRequest(Apiurl.URL_CORPORATELIST, params, CODE_POST_REQUEST);
        request.execute();
    }

    class UserListAdapter extends ArrayAdapter<User> {
        List<User> Userlist;

        public UserListAdapter(List<User> userlistt) {
            super(getActivity(), R.layout.raw_corporate_userlist, userlistt);
            this.Userlist = userlistt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.raw_corporate_userlist, null, true);

            TextView tvname=listViewItem.findViewById(R.id.name);
            TextView tvemail=listViewItem.findViewById(R.id.email);
            TextView tvphone=listViewItem.findViewById(R.id.phone);
            TextView tvlandmark=listViewItem.findViewById(R.id.landmark);
            final User name = Userlist.get(position);
            final User email = Userlist.get(position);
            final User phone = Userlist.get(position);
            final User landmark = Userlist.get(position);

            tvname.setText("Crpt_Name : "+name.getName());
            tvemail.setText("Crpt_Address : "+email.getPhone());
            tvphone.setText("Landmark : "+phone.getCity());
            tvlandmark.setText("Pincode :"+landmark.getStatus());

            return listViewItem;
        }
    }

    private void refreshApprovedreqlist(JSONArray helptips) throws JSONException {
        userdetaillist.clear();
        for (int i = 0; i < helptips.length(); i++) {
            JSONObject obj = helptips.getJSONObject(i);
            Log.d("listview", String.valueOf(obj));
            for (int k = 1; k <= obj.length() / 4; k++) {
                userdetaillist.add(new User(
                        obj.getString("crpt_name"+k),
                        obj.getString("crpt_address"+k),
                        obj.getString("landmark"+k),
                        obj.getString("pincode"+k)
                ));
            }
        }
        UserListAdapter adapter = new UserListAdapter(userdetaillist);
        datalist.setAdapter((ListAdapter) adapter);
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
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Log.d(TAG, "Helptips Response: " + s.toString());
            //progressBar.setVisibility(GONE);
            try {
                Log.d("listview", s);
                JSONObject object = new JSONObject(s);
                Log.d("listview", String.valueOf(object));
                if (!object.getBoolean("error")) {

                    //Toast.makeText(getActivity().getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    refreshApprovedreqlist(object.getJSONArray("CorporateList"));

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
}
