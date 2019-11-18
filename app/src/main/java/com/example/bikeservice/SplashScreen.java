package com.example.bikeservice;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashScreen extends AppCompatActivity {
    String strtype,stringtype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedpreferences = getSharedPreferences("Logintype",
                Context.MODE_PRIVATE);
        strtype=sharedpreferences.getString("type","");
        Log.d("logintype",strtype);

        final SharedPreferences sharedpreferences1 = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        stringtype=sharedpreferences1.getString("type","");
        Log.d("type",stringtype);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                // This method will be executed once the timer is over
                // Start your app main activity

                if(strtype.equals("user")){
                    Intent pass = new Intent(SplashScreen.this, UserNavigation.class);
                    startActivity(pass);
                    finish();
                }
                else if(stringtype.equals("mechanic")) {
                    Intent pass = new Intent(SplashScreen.this, NavigationDrawer.class);
                    startActivity(pass);
                    finish();

                }
                else if(stringtype.equals("admin")) {
                    Intent pass = new Intent(SplashScreen.this, AdminHome.class);
                    startActivity(pass);
                    finish();

                }
                else {
                    Intent pass = new Intent(SplashScreen.this, SelectType.class);
                    startActivity(pass);
                    finish();

                }

            }
        }, 3000);
    }
}
