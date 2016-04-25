package com.example.shivadeviah.enhancedgooglemaps;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    public static boolean isLoggedIn = false;

    public static final String PREF_FILE = "PrefFile";

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Phone Number", "").equals(""))
                isLoggedIn = true;

    }

    public boolean isUserPartOfGroup()
    {
        getSharedPreferences("")
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_anonymous, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_login:
                if(isLoggedIn)
                {
                    intent = new Intent(DashboardActivity.this, LoginActivity.class);

                    startActivity(intent);


                }
                else
                {
                    intent = new Intent(DashboardActivity.this, DisplayProfileActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_help:
                intent = new Intent(DashboardActivity.this, HelpDisplayActivity.class);

                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void callFirstFeature(View v)
    {
        startActivity(new Intent(DashboardActivity.this, FreestyleActivity.class));
    }

    public void callSecondFeature(View v)
    {
        startActivity(new Intent(DashboardActivity.this, RoundaboutActivity.class));
    }

    public void callThirdFeature(View v)
    {
        Intent intent;
        Log.i("ISLOGGEDIN", (isLoggedIn? "True" : "False"));
        if(!DashboardActivity.isLoggedIn) {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        }

        if(DashboardActivity.isLoggedIn && !isUserPartOfGroup())
        {
            intent = new Intent(DashboardActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        }

        else if (DashboardActivity.isLoggedIn && isUserPartOfGroup()){
            intent = new Intent(DashboardActivity.this, GroupTripDashboardActivity.class);
            startActivity(intent);
        }
    }

    public void callFourthFeature(View v)
    {
        startActivity(new Intent(DashboardActivity.this, TravelDiaryActivity.class));
    }

    public void callLogin(MenuItem item){
        if(!DashboardActivity.isLoggedIn)
        {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        }
        else
        {
            startActivity(new Intent(DashboardActivity.this, DisplayProfileActivity.class));
        }
    }


}
