package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class RequesterType extends AppCompatActivity {

    Button btnind,btncor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_type);
        Toolbar toolbar = findViewById(R.id.toolbarsel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Request Type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnind=(Button)findViewById(R.id.indvidual);
        btncor=(Button)findViewById(R.id.corp);

        btnind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);
                Intent ind=new Intent(RequesterType.this,UserService.class);
                startActivity(ind);

            }
        });

        btncor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);
                Intent corp=new Intent(RequesterType.this,CorporateServices.class);
                startActivity(corp);

            }
        });
    }

    public void hideKeyboard(View view) {

        InputMethodManager inm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public void onBackPressed() {

        Intent back=new Intent(RequesterType.this,UserNavigation.class);
        startActivity(back);
        finish();
    }
}
