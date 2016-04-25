package com.example.shivadeviah.enhancedgooglemaps;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class DashboardActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "35755771265";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Demo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    static String regid;

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

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getGcmPreferences(context);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                Log.i(TAG,regid+"");
                return msg;

            }

        }.execute(null, null, null);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(DashboardActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    /*private void sendRegistrationIdToBackend() {
        // Your implementation here.

        new sendData().execute("/register");
    }*/


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
                Log.i("HELLO BRO", "GARP123");
                Log.i("GARP1", "WTF" + getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_GROUP_NAME, ""));
                Log.i("GARP2", "WTF2" + getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_GROUP_DEST, ""));
                groupName = pref.getString(PREF_GROUP_NAME, "");
            }
            if(pref != null && !getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString(PREF_GROUP_NAME, "").equals(""))
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

                    Log.i("OHNANAWHATMANAME", pref.getString("Phone Number", ""));
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
