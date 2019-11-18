package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DashboardAdmin extends Fragment {


    Button btnuserlist,btnuserreqlist,btnreqhistory,btnmechlist,btnmechapprove;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.activity_dashboard_admin,container,false);

        Toolbar toolbar=(Toolbar)v.findViewById(R.id.titleadmin);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).setTitle("Dashboard");
        btnuserlist=(Button) v.findViewById(R.id.btnuserreg);
        btnuserreqlist=(Button) v.findViewById(R.id.btnuserreq);
        btnreqhistory=(Button)v.findViewById(R.id.btnhistory);
        btnmechlist= (Button)v.findViewById(R.id.btnmechreg);
        btnmechapprove=(Button)v.findViewById(R.id.btnmechapprove);

        btnuserlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), UserList.class);
                startActivity(i);

            }
        });

        btnuserreqlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(getActivity(),IndividualRequestList.class);
                startActivity(in);
            }
        });

        btnreqhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent history=new Intent(getActivity(),AdminRequestHistory.class);
                startActivity(history);

            }
        });

        btnmechlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mechanic=new Intent(getActivity(),MechanicList.class);
                startActivity(mechanic);
            }
        });

        btnmechapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mech=new Intent(getActivity(),AdminApproveRequest.class);
                startActivity(mech);

            }
        });


        return v;
    }
}
