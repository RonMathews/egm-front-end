package com.example.shivadeviah.enhancedgooglemaps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;


public class HelpDisplayActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_display);
        TextView text = (TextView) findViewById(R.id.help);
        String helpString = "\t\tA way to enhance your travel experience!\t\t\n\n\n" +
                "Help us plan your trip by choosing either 'GO FREESTYLE' or 'PLAN B'.\n" +
                "Travel with us by choosing either 'GROUP TRIP' or 'TRAVEL DIARY\n\n" +
                "\t\t\t\tGO FREESTYLE\nEnter source and destination and choose the waypoints you'd like to see and we'll suggest an optimal route.\n\n" +
                "\t\t\t\tPLAN 'B'\nEnter your location and how far you're willing to drive and we'll suggest some great places to visit.\n" +
                "Or enter yours and your friends' locations and we'll suggest places where you can all conveniently meet at.\n\n" +
                "\t\t\t\tGROUP TRIP\nPlan a road trip with friends, track them and chat with them till you reach your destination.\n\n" +
                "\t\t\t\tTRAVEL DIARY\nWe'll help you keep track of your travel activities and generate a textual description of your trip which you can then post on social media.";
        text.setText(helpString);


    }

}

