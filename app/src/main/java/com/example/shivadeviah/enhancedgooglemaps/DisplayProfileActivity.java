package com.example.shivadeviah.enhancedgooglemaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayProfileActivity extends AppCompatActivity {

    private ProgressDialog progress=null;
    public static final String PREF_FILE = "PrefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_profile);

        progress = new ProgressDialog(this);
        progress.setMessage("Just a moment...");
        progress.setCancelable(false);

        TextView t1 = (TextView) findViewById(R.id.show_name);
        TextView t2 = (TextView) findViewById(R.id.show_phone_number);
        TextView t3 = (TextView) findViewById(R.id.show_age);

        t1.setText(getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Username", ""));
        t2.setText(getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Phone Number", ""));
        t3.setText(getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Join Date", ""));

    }

    public void callSignOut(View v)
    {
        String phoneNumber = getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Phone Number", "");

        new UserLogoutTask(phoneNumber).execute();
    }

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {

        private final String phoneNumber;

        UserLogoutTask(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection connection = null;
            Log.i("inback logout", "came here");
            try {
                JSONObject obj = new JSONObject();
                obj.put("op", 2);
                obj.put("phone", phoneNumber);
                URL url = new URL("http://192.168.1.117:8000/login");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = obj.toString();
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

                Log.i("info", data);


                if (data.equals("Signed Out"))
                {
                    DashboardActivity.isLoggedIn = false;
                    getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                            .edit()
                            .putString("Phone Number", "")
                            .putString("Username", "")
                            .putString("Join Date", "")
                            .commit();
                    return true;
                }
                else
                {
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progress.dismiss();
            finish();

            Toast.makeText(DisplayProfileActivity.this, "Successfully logged out. Come back soon!", Toast.LENGTH_LONG).show();
        }
    }
}

