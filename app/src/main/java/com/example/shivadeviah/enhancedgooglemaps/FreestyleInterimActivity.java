package com.example.shivadeviah.enhancedgooglemaps;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
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
import java.util.Arrays;

/**
 * Created by ksameersrk on 5/3/16.
 */
public class FreestyleInterimActivity extends Activity implements View.OnClickListener {
    /*
    There is bug if the list is greater then the height of the view
     */
    String[] places;/*= {
            "Bekal Fort",
            "Bengaluru Palace",
            "Brindavan Gardens",
            "Ducati Bengaluru",
            "Eco Tourism Park",
            "Jawaharlal Nehru Planetarium",
            "Panambur Beach",
            "Bekal Fort",
            "Bengaluru Palace",
            "Brindavan Gardens",
            "Ducati Bengaluru",
            "Eco Tourism Park",
            "Jawaharlal Nehru Planetarium",
            "Panambur Beach",
            "Bekal Fort",
            "Bengaluru Palace",
            "Brindavan Gardens",
            "Ducati Bengaluru",
            "Eco Tourism Park",
            "Jawaharlal Nehru Planetarium",
            "Panambur Beach",
            "Tipu Sultan's Fort"
    };*/
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter;
    private String my_sel_items;
    private ArrayList<String> selected_places;
    private ArrayList<Integer> selectedIds;

    private String source = null;
    private String destination = null;
    private String json_str = null;
    Button click;
    Bundle b = new Bundle();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freestyle_interim);

        //set header
        final TextView t = (TextView) findViewById(R.id.header_text);
        Bundle bundle = getIntent().getExtras();
        source = bundle.getString("source");
        destination = bundle.getString("destination");
        json_str = bundle.getString("json_str");

        // Toast.makeText(ListOfPlaces.this, json_str, Toast.LENGTH_LONG).show();
        t.setText(source + " -> " + destination);
        click = (Button) findViewById(R.id.get_route);
        click.setOnClickListener(this);

        //initialize
        my_sel_items = "";
        try {
            parse(json_str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        display();
    }

    public void parse(String json_str) throws JSONException {
        ArrayList<String> al = new ArrayList<String>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json_str);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Object[] objectList = al.toArray();
        places = Arrays.copyOf(objectList, objectList.length, String[].class);
    }


    public void display() {
        mainListView = (ListView) findViewById(R.id.main_list_view);
        listAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                places
        ); // sw32
        //set Adapter
        mainListView.setAdapter(listAdapter);
        mainListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //set the listener for onclick
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);

                my_sel_items = "";
                SparseBooleanArray a = mainListView.getCheckedItemPositions();
                selected_places = new ArrayList<String>();
                selectedIds = new ArrayList<Integer>();
                for (int j = 0; j < a.size(); j++) {
                    Log.v("value size : ", a.size() + "");
                    if (a.valueAt(j)) {
                        my_sel_items = (String) mainListView.getAdapter().getItem(a.keyAt(j));
                        //Toast.makeText(ListOfPlaces.this,my_sel_items,Toast.LENGTH_LONG).show();
                        selected_places.add(my_sel_items);
                        selectedIds.add(new Integer(a.keyAt(j)));
                    } else {
                        int tmp = a.keyAt(j);
                        if (selectedIds.contains(tmp)) {
                            selectedIds.remove(new Integer(tmp));
                        }
                    }
                }

                Log.v("First : ", mainListView.getFirstVisiblePosition() + "");
                Log.v("Last  : ", mainListView.getLastVisiblePosition() + "");
                Log.v("list", selected_places.toString());
                Log.v("list", selectedIds.toString());
            }
        });

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.get_route:
                startActivity(new Intent(this, FreestyleResultsActivity.class));
                URL a = null;
                try {
                    a = new URL("http://192.168.1.117:8000/f1m2");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                selected_places.add(0,source);
                selected_places.add(1,destination);
                new sendData().execute(a.toString(),selected_places.toString());
                break;
            default:
                break;

        }

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
            Intent i = new Intent(FreestyleInterimActivity.this, FreestyleResultsActivity.class);
            b.putString("json_dict",result);
            i.putExtras(b);
            startActivity(i);

        }


    }
}