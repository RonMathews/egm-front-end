package com.example.shivadeviah.enhancedgooglemaps;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;


class LatJSONParser extends AppCompatActivity {

    public List<Double>  parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}

        //SET UP DS
        List<Double> lat = new ArrayList<>();

        JSONArray jLat = null;

        try {
            //get Lat
            jLat = jObject.getJSONArray("places_lat");
            for(int i = 0; i < jLat.length(); i++){
                lat.add((Double)jLat.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lat;
    }
}

class LonJSONParser extends AppCompatActivity {

    public List<Double>  parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}

        //SET UP DS
        List<Double> lon = new ArrayList<>();

        JSONArray jlon = null;

        try {
            //get lon
            jlon = jObject.getJSONArray("places_long");
            for(int i = 0; i < jlon.length(); i++){
                lon.add((Double)jlon.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lon;
    }
}


class CurLatJSONParser extends AppCompatActivity {

    public List<Double>  parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}

        //SET UP DS
        List<Double> curLat = new ArrayList<>();

        JSONArray jcurLat = null;

        try {
            //get lon
            jcurLat = jObject.getJSONArray("users_lat");
            for(int i = 0; i < jcurLat.length(); i++){
                curLat.add((Double)jcurLat.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curLat;
    }
}


class CurLonJSONParser extends AppCompatActivity {

    public List<Double>  parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}

        //SET UP DS
        List<Double> curLon = new ArrayList<>();

        JSONArray jcurLon = null;

        try {
            //get lon
            jcurLon = jObject.getJSONArray("users_long");
            for(int i = 0; i < jcurLon.length(); i++){
                curLon.add((Double)jcurLon.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curLon;
    }
}

class DesJSONParser extends AppCompatActivity {

    public List<List<Object>> parse(JSONObject jObject) {

        //SHOW INPUT
        Log.d("SHARON Parser", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}
        List<List<Object>> des = new ArrayList<>();
        List<Object> subArray = new ArrayList<>();
        JSONArray jDes = null;
        JSONArray jSubArray = null;

        try {

            jDes = jObject.getJSONArray("all_details");
            for(int i = 0; i < jDes.length(); i++){
                jSubArray = (JSONArray)jDes.get(i);
                subArray = new ArrayList<>();
                for(int j = 0; j < jSubArray.length(); j++){
                    //Log.d("SHARON Parser", "CHECK" + jSubArray);
                    subArray.add(jSubArray.get(j));
                }
                des.add(subArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return des;
    }
}

public class RoundaboutResultsActivity extends FragmentActivity implements OnMapReadyCallback
{

    String json_places;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    List<Double> lat = null;
    List<Double> lon = null;
    List<List<Object>> des = null;
    List<Double> currentLat = new ArrayList<>();
    List<Double> currentLon = new ArrayList<>();
    JSONObject jObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roundabout_results);
        Bundle b = getIntent().getExtras();
        json_places = b.getString("json_places");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        try{
            jObject = new JSONObject(json_places);
            // jObject = new JSONObject("{\"places_lat\": [1.1, 2.2, 3.3], \"places_long\": [4.4, 5.5, 6.6]}, \"users_lat\": [7.1, 8.2, 9.3], \"users_long\": [0.4, 1.5, 2.6]}, \"all_details\":[[\"ABC\", 4.4, \"abc, xyz, 123\", \"www.hello.com\"], [\"ABC\", 4.4, \"abc, xyz, 123\", \"www.hello.com\"]]");


            Log.d("onCreate", "CHECK" + jObject); //{"lat":[1.1,2.2,3.3],"long":[4.4,5.5,6.6]}

            LatJSONParser parser = new LatJSONParser();
            LonJSONParser parser2 = new LonJSONParser();
            CurLatJSONParser parser3 = new CurLatJSONParser();
            CurLonJSONParser parser4 = new CurLonJSONParser();
            DesJSONParser parser5 = new DesJSONParser();


            // Starts parsing data
            lat = parser.parse(jObject);
            lon = parser2.parse(jObject);

            //recreating JSON object as string too long - neednt do when getting from server
            //  jObject = new JSONObject("{\"users_lat\": [7.1, 8.2, 9.3], \"users_long\": [0.4, 1.5, 2.6]}, \"all_details\":[[\"ABC\", 4.4, \"abc, xyz, 123\", \"www.hello.com\"], [\"ABC\", 4.4, \"abc, xyz, 123\", \"www.hello.com\"]]");

            currentLat = parser3.parse(jObject);
            currentLon = parser4.parse(jObject);

            //recreating JSON object as string too long - neednt do when getting from server
            //  jObject = new JSONObject("{\"all_details\":[[\"ABC\", 4.4, \"abc, xyz, 123\", \"www.hello.com\"], [\"EFG\", 5.4, \"ijw, xyz, 123\", \"www.bye.com\"]]}");

            des = parser5.parse(jObject);

            Log.d("Latitude", "onCreate: " + lat);
            Log.d("Longitude", "onCreate: " + lon);
            Log.d("User Lat", "onCreate: " + currentLat);
            Log.d("User Lon", "onCreate: " + currentLon);

            Log.d("Descr", "onCreate: " + des);
            String name = (String)des.get(0).get(0);
            Double rating = (Double)des.get(0).get(1);
            String address = (String)des.get(0).get(2);
            String url = (String)des.get(0).get(3);
            Log.d("Check", "onCreate: " + name + ":" + rating + ":" + address + ":" + url);

            int i;
            float zoomLevel = 16;
            LatLng currentPos = new LatLng(currentLat.get(0), currentLon.get(0));
            for (i = 0; i < currentLat.size(); i++) {
                currentPos = new LatLng(currentLat.get(i), currentLon.get(i));
                mMap.addMarker(new MarkerOptions().position(currentPos).title("User " + (i+1)));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
            }
            //LatLng pos = new LatLng(lat, lon);
            //mMap.addMarker(new MarkerOptions().position(currentPos).title("User"));
            for (i = 0; i < lat.size(); i++) {
                LatLng pos = new LatLng(lat.get(i), lon.get(i));
                mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(des.get(i).get(0) + "\nRating: " + des.get(i).get(1))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, zoomLevel));//


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
