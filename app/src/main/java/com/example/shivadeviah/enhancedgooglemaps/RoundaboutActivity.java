package com.example.shivadeviah.enhancedgooglemaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import org.w3c.dom.Text;


public class RoundaboutActivity extends AppCompatActivity {

    Bundle b = new Bundle();
    Button getPlaces;
    String response;
    TextView seekBarValue;
    EditText location;
    SeekBar seekBar;
    String user_location;
    String data;
    String radius;
    String localhost = getString(R.string.ip) + "f2";
    List<AutoCompleteTextView> allEds = new ArrayList<>();
    private ProgressDialog progress=null;
    private LinearLayout mLayout;
    private ScrollView mScroll;
    private AutoCompleteTextView mEditText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roundabout);
        mScroll = (ScrollView) findViewById(R.id.scrollRound);
        mLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mEditText = (AutoCompleteTextView) findViewById(R.id.user_location);
        mEditText.setAdapter(new AutoCompleteAdapter(this));
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(onClick());
        progress = new ProgressDialog(this);
        progress.setMessage("Just a moment...");
        progress.setCancelable(false);
        location = (EditText) findViewById(R.id.user_location);



        TextView temp = (TextView) findViewById(R.id.textView);
        temp.setGravity(Gravity.CENTER_HORIZONTAL);


        getPlaces = (Button) findViewById(R.id.getPlaces);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBarValue = (TextView)findViewById(R.id.radius);
        seekBarValue.setText(String.valueOf(seekBar.getProgress()) + "km");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress) + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        getPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allValuesEntered = true;
                user_location = location.getText().toString();
                if(user_location.equals("")){
                    allValuesEntered = false;
                }
                radius = seekBar == null ? "50" : seekBarValue.getText().toString();
                data = user_location+"::"+radius;

                for(int i=0; i < allEds.size(); i++){
                    data += "::" + allEds.get(i).getText().toString();
                    if(allEds.get(i).getText().toString().equals("")){
                        allValuesEntered = false;
                    }
                }
                if(allValuesEntered){
                    Log.i("--DEBUG--", "---------------------------------WORKS------------------------\n" + data);
                    new postData().execute();
                }
                else{
                    Toast.makeText(RoundaboutActivity.this, "PLEASE ENTER ALL LOCATIONS", Toast.LENGTH_LONG).show();
                }


            }
        });

    }
    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearLayout temp1 = (LinearLayout) findViewById(R.id.linearLayoutToKill1);
                LinearLayout temp2 = (LinearLayout) findViewById(R.id.linearLayoutToKill2);
                if(temp1 != null && temp2 != null)
                {
                    temp1.removeAllViews();
                    temp2.removeAllViews();
                }
                int count = mLayout.getChildCount();
                mLayout.addView(createNewEditTextView(), count-1);


            }
        };
    }

    private AutoCompleteTextView createNewEditTextView() {
        final AutoCompleteTextView editText = new AutoCompleteTextView(this);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        editText.requestFocus();
        int padding_in_dp = 20;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        editText.setPadding(0, 0, 0, padding_in_px);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editText.setHint("Enter another location");
        editText.setAdapter(new AutoCompleteAdapter(this));
        allEds.add(editText);
        mScroll.post(new Runnable() {

            @Override
            public void run() {
                // mScroll.fullScroll(ScrollView.FOCUS_DOWN);
                mScroll.scrollTo(0, mScroll.getBottom());
            }
        });
        return editText;
    }


    class postData extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            String post=null;
            try {
                URL url = new URL(localhost);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                String send = data;
                os.write(send.getBytes());
                os.flush();
                os.close();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb  = new StringBuilder();
                String line = "";
                while( ( line = br.readLine())  != null){
                    sb.append(line);
                }
                response = sb.toString();
                br.close();
                //int response = connection.getResponseCode();
                return response;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            b.putString("json_places", s);
            Intent i = new Intent(RoundaboutActivity.this, RoundaboutResultsActivity.class);
            i.putExtras(b);
            startActivity(i);
        }
    }
    private class AutoCompleteAdapter extends ArrayAdapter<Address> implements Filterable {

        private LayoutInflater mInflater;
        private Geocoder mGeocoder;
        private StringBuilder mSb = new StringBuilder();

        public AutoCompleteAdapter(final Context context) {
            super(context, -1);
            mInflater = LayoutInflater.from(context);
            //mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mGeocoder = new Geocoder(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            //TextView tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

            TextView tv;
            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            tv.setText(createFormattedAddressFromAddress(getItem(position)));
            return tv;
        }

        private String createFormattedAddressFromAddress(final Address address) {
            mSb.setLength(0);
            Log.i("SHARON AUTOCOMPLETE1", address.toString());
            final int addressLineSize = address.getMaxAddressLineIndex();
            for (int i = 0; i < addressLineSize; i++) {
                mSb.append(address.getAddressLine(i));
                if (i != addressLineSize - 1) {
                    mSb.append(", ");
                }
            }
            Log.i("SHARON AUTOCOMPLETE2", mSb.toString());
            return mSb.toString();
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    List<Address> addressList = null;
                    if (constraint != null) {
                        try {
                            addressList = mGeocoder.getFromLocationName((String) constraint, 5);
                        } catch (IOException e) {
                        }
                    }
                    if (addressList == null) {
                        addressList = new ArrayList<Address>();
                    }

                    final FilterResults filterResults = new FilterResults();
                    filterResults.values = addressList;
                    filterResults.count = addressList.size();

                    return filterResults;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(final CharSequence contraint, final Filter.FilterResults results) {
                    clear();
                    for (Address address : (List<Address>) results.values) {
                        add(address);
                    }
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    if(resultValue == null){
                        return "";
                    }
                    else {
                        Address addr = (Address) resultValue;
                        int size = addr.getMaxAddressLineIndex();
                        String text = "";
                        for (int i = 0; i < size; i++) {
                            text += (addr.getAddressLine(i));
                            if (i != size - 1) {
                                text += ", ";
                            }
                        }
                        // return resultValue == null ? "" : ((Address) resultValue).getAddressLine(0);
                        return text;
                    }
                }
            };
            return myFilter;
        }
    }

}