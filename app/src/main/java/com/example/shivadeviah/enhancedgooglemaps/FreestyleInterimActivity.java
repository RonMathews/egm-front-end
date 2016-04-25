package com.example.shivadeviah.enhancedgooglemaps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Iterator;

/**
 * Created by ksameersrk on 5/3/16.
 */
public class FreestyleInterimActivity extends Activity implements View.OnClickListener {
    /*
    There is bug if the list is greater then the height of the view
     */
    private ProgressDialog progress=null;

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
    private JSONObject selected_places_json = null;
    private JSONObject original_results = null;
    private ArrayList<Integer> selectedIds;

    /*JSONObject obj = new JSONObject();
                obj.put("op", 1);
                obj.put("phone", phoneNumber);
                obj.put("password", password);*/

    private String source = null;
    private String destination = null;
    private String json_str = null;
    Button click;
    Bundle b = new Bundle();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anonymous, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_login:
                intent = new Intent(FreestyleInterimActivity.this, LoginActivity.class);

                startActivity(intent);
                return true;

            case R.id.action_help:
                // TODO

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void callLogin(MenuItem item){
        if(!DashboardActivity.isLoggedIn)
        {
            startActivity(new Intent(FreestyleInterimActivity.this, LoginActivity.class));
        }
        else
        {
            startActivity(new Intent(FreestyleInterimActivity.this, DisplayProfileActivity.class));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freestyle_interim);

        //set header
        final TextView t1 = (TextView) findViewById(R.id.show_source);
        final TextView t2 = (TextView) findViewById(R.id.show_dest);
        Bundle bundle = getIntent().getExtras();
        source = bundle.getString("source");
        destination = bundle.getString("destination");
        json_str = bundle.getString("json_str");
        progress = new ProgressDialog(this);
        progress.setMessage("Just a moment...");
        progress.setCancelable(false);
        try
        {
            original_results = new JSONObject(json_str);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Toast.makeText(ListOfPlaces.this, json_str, Toast.LENGTH_LONG).show();
        t1.setText(source);
        t2.setText(destination);
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
        // TODO - change parse function
        JSONObject json = null;
        ArrayList<String> al = new ArrayList<String>();
        try {
            json = new JSONObject(json_str);
            Iterator<String> iter = json.keys();
            while (iter.hasNext())
            {
                String key = iter.next();

                al.add(key);
            }

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
                try {
                    Log.v("Current", original_results.getString(my_sel_items));
                }
                catch(Exception e){}
                //Log.v("First : ", mainListView.getFirstVisiblePosition() + "");
                //Log.v("Last  : ", mainListView.getLastVisiblePosition() + "");
                Log.v("list", selected_places.toString());
                //Log.v("list", selectedIds.toString());
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
                Log.i("SOURCE", source);
                Log.i("DEST", destination);
                Log.i("SEL PLACES SIZE", selected_places == null? "EMPTY" : selected_places.size()+"");
                String output = "";

                try {
                    selected_places_json = new JSONObject();

                    if(selected_places != null && selected_places.size() > 0)
                    {
                        for (String s : selected_places)
                        {
                            selected_places_json.put(s, new JSONObject(original_results.getString(s)));
                        }

                        output = source + "::" + destination + "::" + selected_places_json.toString();
                    }
                    else
                        output = source + "::" + destination ;
                }
                catch(Exception e){
                    e.printStackTrace();
                }



//                selected_places.add(0,source);
//                selected_places.add(1,destination);
//                new sendData().execute(a.toString(),selected_places.toString());
                  new sendData().execute(a.toString(), output);
                break;
            default:
                break;

        }

    }

    class sendData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

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
            progress.dismiss();
            Intent i = new Intent(FreestyleInterimActivity.this, FreestyleResultsActivity.class);
            JSONObject res = null;
            try{
               res = new JSONObject(result);
            }
            catch(Exception e){}
            Log.d("LENGTH(HELLO)", result.length()+"");
            Log.d("LENGTH(JM)", res.toString().length()+"");
            b.putString("json_dict",res.toString());
            i.putExtras(b);
            startActivity(i);

        }


    }
}