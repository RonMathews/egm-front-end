package com.example.shivadeviah.enhancedgooglemaps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Random;

public class GroupTripChatWindowActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    static ArrayList<String> messages;
    private TextView mGroupName;
    private EditText mMsgText;
    public static final String PREF_FILE = "PrefFile";
    private static final String PREF_USERNAME = "Username";
    CustomizedAdapter mListAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_trip_chat_window);

        //set up chat window
        mGroupName = (TextView) findViewById(R.id.chat_group_name);
        mGroupName.setText(getGroupName());
        mMsgText = (EditText) findViewById(R.id.chat_edit_text);


        try{
            String phoneNumber = getUserName();
            JSONObject obj = new JSONObject();
            obj.put("phone_number", phoneNumber);
        }
        catch (Exception e)
        {
            Log.i("error", "get list of messages");
        }

        //get messages
        messages = getMessages();
        //messages = formatMessages(messages);
        display();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // TODO: if there are new notifications

    }


    public void display() {
        listView = (ListView) findViewById(R.id.chat_list_view);
        mListAdapter = new CustomizedAdapter(this, messages);
        listView.setAdapter(mListAdapter);
        updateFocus();
    }

    public void updateFocus()
    {
        listView.setSelection(listView.getAdapter().getCount()-1);
    }

    public String getGroupName()
    {
        //// TODO: 10/4/16 get group name from database
        return getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Group Name", "");
    }

    public ArrayList<String> getMessages()
    {
        // TODO: 10/4/16 get messages from database for this group
        String msg[] = {
        };
        return new ArrayList<String>(Arrays.asList(msg));
    }

    public String getUserName()
    {
        return getSharedPreferences(PREF_FILE, MODE_PRIVATE).getString("Username", null);
    }

    //add user text
    public void addMessage(View v)
    {
        if(noErrors())
        {

            try{
                URL a = new URL(getString(R.string.ip) + "chat");
                String phoneNumber = getUserName();
                JSONObject obj = new JSONObject();
                obj.put("phone", phoneNumber);
                obj.put("message", mMsgText.getText().toString());

                new SendData().execute(a.toString(), obj.toString());

            }
            catch (Exception e)
            {
                Log.i("error", "get list of messages");
            }

            messages.add("- : " + getUserName() + " : " + mMsgText.getText().toString());
            mMsgText.setText("");
            mListAdapter.notifyDataSetChanged();
            updateFocus();

        }
    }

    //methods to check the fields
    public boolean noErrors()
    {
        // Reset errors.
        mMsgText.setError(null);

        String msgText = mMsgText.getText().toString();

        boolean allCorrect = true;
        View focusView = null;

        if (TextUtils.isEmpty(msgText)) {
            mMsgText.setError("Please Enter some message");
            focusView = mGroupName;
            allCorrect = false;
        }

        return allCorrect;
    }

    class SendData extends AsyncTask<String, String, String> {
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

                // TODO: Update the list view
                JSONObject object = new JSONObject(result);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
    }
}


class CustomizedAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> data;
    private static LayoutInflater inflater = null;
    private HashMap<String, String> hm = new HashMap<>();

    public CustomizedAdapter(Context context, ArrayList<String> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.member_view_pane, null);

        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView text = (TextView) vi.findViewById(R.id.content);
        String details[] = data.get(position).trim().split(":");
        Boolean currentUser = details[0].trim().equals("-");
        Log.e("cuur", currentUser.toString());
        String user_name = details[1].trim();
        String user_text = details[2].trim();

        name.setText(user_name);
        text.setText(user_text);

        //set random color
        int r = 0;
        int g = 0;
        int b = 0;
        if(hm.containsKey(user_name))
        {
            String color[] = hm.get(user_name).split(":");
            r = Integer.parseInt(color[0]);
            g = Integer.parseInt(color[1]);
            b = Integer.parseInt(color[2]);
        }
        else
        {
            Random rand = new Random();
            r = rand.nextInt();
            g = rand.nextInt();
            b = rand.nextInt();
            hm.put(user_name, r+":"+g+":"+b);
        }
        name.setTextColor(Color.rgb(r, g, b));

        if(currentUser)
        {
            name.setGravity(Gravity.RIGHT);
            text.setGravity(Gravity.RIGHT);
        }
        else
        {
            name.setGravity(Gravity.START);
            text.setGravity(Gravity.START);
        }

        return vi;
    }
}
