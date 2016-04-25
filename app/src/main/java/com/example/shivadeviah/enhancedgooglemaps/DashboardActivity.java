package com.example.shivadeviah.enhancedgooglemaps;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    public static boolean isLoggedIn = false;

    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_GROUP_NAME = "Group Name";
    private static final String PREF_GROUP_DEST = "Group Destination";

    Boolean isPartOfGroup = false;
    private Menu menu;
    String groupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Phone Number", "").equals(""))
                isLoggedIn = true;

    }

    class sendData extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = params[1];
                os.write(send.getBytes());
                os.flush();
                os.close();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                String data = sb.toString();
                br.close();
                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try{
                JSONObject object = new JSONObject(result);
                String no = object.getString("status");
                Log.i("sw32","HARRO");

                Log.i("status", no+"");
                if(Integer.parseInt(no) == 1)

                {
                    String gname = object.getString("name");
                    String gdest = object.getString("dest");
                    getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                            .edit()
                            .putString(PREF_GROUP_DEST, gdest)
                            .putString(PREF_GROUP_NAME, gname)
                            .commit();
                    isPartOfGroup = true;
                }
                else
                {
                    isPartOfGroup = false;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Intent i;
            if(! isPartOfGroup)
            {
                i = new Intent(DashboardActivity.this, CreateGroupActivity.class);
            }
            else
            {
                //TODO : uncomment GroupTripMap.class and comment CreateGroup.class
                i = new Intent(DashboardActivity.this, GroupTripDashboardActivity.class);
                //i = new Intent(MainActivity.this, CreateGroup.class);
                // Toast.makeText(MainActivity.this, "Already part a group,"+
                //  " wait for our developers to create GroupActivity", Toast.LENGTH_LONG).show();
            }
            startActivity(i);
        }
    }


    public void getUserGroup()
        {
            SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
            if(pref != null)
            {
                groupName = pref.getString(PREF_GROUP_NAME, "");
            }
            if(pref != null && !groupName.equals(""))
            {
                Log.i("OHNANAWHATMANAME", groupName);
                startActivity(new Intent(DashboardActivity.this, GroupTripDashboardActivity.class));
            }
            else
            {
                URL a = null;
                JSONObject obj = new JSONObject();
                try {
                    a = new URL("http://192.168.1.117:8000/test");

                    obj.put("op", "4");
                    obj.put("phone", pref.getString("Phone Number", ""));
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Location location;
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Log.i("prelocation", "came here");
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    Log.i("location", latitude+":"+longitude);
                    obj.put("lat", latitude+"");
                    obj.put("lng", longitude+"");



                } catch (Exception e) {
                    e.printStackTrace();
                }
                new sendData().execute(a.toString(), obj.toString());
            }
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

        else
        {
            getUserGroup();
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
