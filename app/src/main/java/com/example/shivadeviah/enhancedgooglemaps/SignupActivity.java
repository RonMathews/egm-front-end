package com.example.shivadeviah.enhancedgooglemaps;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via phone_number/password.
 */
public class SignupActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_NAME = "name";
    private static final String PREF_PASSWORD = "password";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ProgressDialog progress=null;

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mPhoneNumberView;
    private EditText mPasswordView1;
    private EditText mPasswordView2;
    private EditText mUserName;
    private EditText mUserAge;
    private View mProgressView;
    private View mLoginFormView;

    private String age;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Set up the login form.
        mPhoneNumberView = (AutoCompleteTextView) findViewById(R.id.phone_number);
        populateAutoComplete();

        progress = new ProgressDialog(this);
        progress.setMessage("Just a moment...");
        progress.setCancelable(false);

        mPasswordView1 = (EditText) findViewById(R.id.password1);
        mPasswordView2 = (EditText) findViewById(R.id.password2);
        mUserName = (EditText) findViewById(R.id.registration_name);
        mUserAge = (EditText) findViewById(R.id.registration_age);
        mPasswordView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignUpButton = (Button) findViewById(R.id.sign_in_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mPhoneNumberView, R.string.registration_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid phone_number, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPhoneNumberView.setError(null);
        mPasswordView1.setError(null);
        mPasswordView2.setError(null);
        mUserName.setError(null);
        mUserAge.setError(null);

        // Store values at the time of the login attempt.
        String phone_number = mPhoneNumberView.getText().toString();
        String password1 = mPasswordView1.getText().toString();
        String password2 = mPasswordView2.getText().toString();
        name = mUserName.getText().toString();
        age = mUserAge.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password1, if the user entered one.
        if (!isPasswordValid(password1)) {
            mPasswordView1.setError(getString(R.string.registration_error_invalid_password));
            focusView = mPasswordView1;
            cancel = true;
        }
        else if (!isPasswordValid(password2)){
            mPasswordView2.setError(getString(R.string.registration_error_invalid_password));
            focusView = mPasswordView2;
            cancel = true;
        }

        // Check for a valid phone_number.
        if (TextUtils.isEmpty(phone_number)) {
            mPhoneNumberView.setError(getString(R.string.registration_error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        } else if (!isPhoneNumberValid(phone_number)) {
            mPhoneNumberView.setError(getString(R.string.registration_error_invalid_phone_number));
            focusView = mPhoneNumberView;
            cancel = true;
        }
        if (TextUtils.isEmpty(name)) {
            mUserName.setError(getString(R.string.registration_error_field_required));
            focusView = mUserName;
            cancel = true;
        }
        if (TextUtils.isEmpty(age)) {
            mUserAge.setError(getString(R.string.registration_error_field_required));
            focusView = mUserAge;
            cancel = true;
        }

        if(! arePasswordsSame(password1, password2))
        {
            Toast.makeText(SignupActivity.this, password1+":"+password2, Toast.LENGTH_LONG).show();
            mPasswordView2.setError("Passwords do not match");
            cancel = true;
            focusView = mPasswordView2;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //TODO
            //showProgress(true);
            mAuthTask = new UserLoginTask(name, phone_number, password1, age);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        //TODO: Replace this with your own logic
        return phoneNumber.length() == 10;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean arePasswordsSame(String password1, String password2)
    {
        return password1.equals(password2);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only phone_number addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Phone
                .CONTENT_ITEM_TYPE},

                // Show primary phone_number addresses first. Note that there won't be
                // a primary phone_number if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> phoneNumbers = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            phoneNumbers.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addPhoneNumbersToAutoComplete(phoneNumbers);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addPhoneNumbersToAutoComplete(List<String> phoneNumbersCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, phoneNumbersCollection);

        mPhoneNumberView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String name;
        private final String phoneNumber;
        private final String password;
        private final String age;
        private  String reg_id;
        private int what;

        UserLoginTask(String name, String phoneNumber, String password, String age) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.password = password;
            this.age = age;
            this.reg_id = DashboardActivity.regid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

                We have phoneNumber
                        password
                        name
                        age
            */
            HttpURLConnection connection = null;
            try{
                JSONObject obj = new JSONObject();
                obj.put("op", 0);
                obj.put("phone", phoneNumber);
                obj.put("name", name);
                obj.put("password", password);
                obj.put("regid",reg_id);
                //obj.put("age", age);
                // TODO: 20/4/16 Send data

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

                String info = data.trim();
                Log.i("info", data);
                what = 0;
                if(info.equals("User created"))
                {
                    getSharedPreferences(PREF_FILE, MODE_PRIVATE)
                            .edit()
                            .putString(PREF_NAME, name)
                            .commit();
                    return true;
                }
                else if(info.equals("User Already Exists"))
                {
                    what = 1;
                    return false;
                }


            }
            catch (Exception e)
            {
                Log.v("DB", "doInBackground") ;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //TODO showProgress(false);
            progress.dismiss();
            if (success) {

                finish();
                Toast.makeText(SignupActivity.this, "Successfully signed up! Log in with your info to get started.", Toast.LENGTH_LONG).show();

            }
            else if(what == 1)
            {
                Toast.makeText(SignupActivity.this, "It seems you've already signed up. Try logging in?", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(SignupActivity.this, "Sorry, we had a little trouble processing your request. Please try again.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
            mAuthTask = null;
            //TODO showProgress(false);
        }
    }

}