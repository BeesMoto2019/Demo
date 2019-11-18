package com.example.bikeservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

public class SelectType extends AppCompatActivity {
    TextView type;
    Button btnuser,btnmech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);
        type = (TextView)findViewById(R.id.type);
        btnuser = (Button)findViewById(R.id.user);
        btnmech = (Button)findViewById(R.id.mech);


        btnuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //IntentUtils.fireIntent(SelectType.this,UserLogin.class,false);
                Intent user = new Intent(getApplicationContext(),UserLogin.class);
                startActivity(user);
                finish();
            }
        });

        btnmech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //IntentUtils.fireIntent(SelectType.this,Login.class,false);
                Intent mech = new Intent(getApplicationContext(),Login.class);
                startActivity(mech);
                finish();
            }
        });
    }
}
