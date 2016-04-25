package com.example.shivadeviah.enhancedgooglemaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {

    public static boolean isLoggedIn = false;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

    }

    public boolean isUserPartOfGroup()
    {
        // TODO: complete this
        return false;
    }

    @Override
    protected void onResume()
    {
        // Change elements on the UI depending on whether the user is logged in or not
        // TODO: check if this works
        super.onResume();

        String username = getIntent().getStringExtra("username");


        //onCreateOptionsMenu(menu);
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
            finish();
            intent = new Intent(DashboardActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        }

        else if (DashboardActivity.isLoggedIn && isUserPartOfGroup()){
            finish();
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
