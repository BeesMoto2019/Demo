package com.example.bikeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AdminAddInfo extends Fragment {

    Button btnaddbike,btnaddcorporate,btnaddservices;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_admin_add_info,container,false);
        Toolbar toolbar=(Toolbar)v.findViewById(R.id.addadmin);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).setTitle("Add Information");

        btnaddcorporate=(Button)v.findViewById(R.id.btncorporate);
        btnaddbike=(Button)v.findViewById(R.id.btnbike);
        btnaddservices=(Button)v.findViewById(R.id.btnaddsrv);

        btnaddcorporate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent corporate=new Intent(getActivity(),Admin_addCorporate.class);
                startActivity(corporate);
            }
        });

        btnaddbike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bike=new Intent(getActivity(),AddBike.class);
                startActivity(bike);
            }
        });

        btnaddservices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent srvc=new Intent(getActivity(),AdminAddServices.class);
                startActivity(srvc);
            }
        });

        return v;
    }
}
