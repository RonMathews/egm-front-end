package com.example.shivadeviah.enhancedgooglemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FreestyleResultsActivity extends AppCompatActivity {

    private String source = null;
    private String destination = null;
    private String json_dict=null;
    ArrayList<LatLng> bounds = new ArrayList<LatLng>();

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    ArrayList<LatLng> locationPoints;
    private LatLng boundSouthWest;
    private LatLng boundNorthEast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freestyle_results);
        Bundle bundle = getIntent().getExtras();
        if ( (getIntent().getStringExtra("json_dict") != null)){
            json_dict = bundle.getString("json_dict");
        }
        Toast.makeText(FreestyleResultsActivity.this, json_dict, Toast.LENGTH_LONG).show();
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        locationPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting reference to Button


        // Getting Map for the SupportMapFragment
        map = fm.getMap();


        // Enable MyLocation Button in the Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        new ParserTask().execute(json_dict);
    }



    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            JSONArray jRoutes = null;
            List<List<HashMap<String, String>>> routes = null;


            try{
                jObject = new JSONObject(jsonData[0]);
                jRoutes = jObject.getJSONArray("route");
                JSONObject jsonBound = ((JSONObject)jRoutes.get(0)).getJSONObject("bounds");
                JSONObject jsonSouthWest = jsonBound.getJSONObject("southwest");
                JSONObject jsonNorthEast = jsonBound.getJSONObject("northeast");
                boundSouthWest = new LatLng(jsonSouthWest.getDouble("lat"),jsonSouthWest.getDouble("lng"));
                boundNorthEast = new LatLng(jsonNorthEast.getDouble("lat"),jsonNorthEast.getDouble("lng"));

                map.addMarker(new MarkerOptions()
                        .position(boundSouthWest)
                        .title("Source")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                map.addMarker(new MarkerOptions()
                        .position(boundNorthEast)
                        .title("DESTINATION")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                //Log.i
                DirectionJSONParser parser = new DirectionJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
                Log.i("ERROR", "ERRORORORO");
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            // builder.include(new LatLng(maxLat, maxLon));

            builder.include(boundNorthEast);
            builder.include(boundSouthWest);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_anonymous, menu);
        return true;
    }
}

class DirectionJSONParser {

    public List<List<HashMap<String,String>>> parse(JSONObject jsonObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jsonObject.getJSONArray("route");
            // jRoutes = (JSONArray)jsonObject;

            /** Traversing all routes */

            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("PARSER", "-PARPER--------");
        }catch (Exception e){
            Log.i("_PARSEEE---------------","PASSSSPL0XXXXO");
        }

        return routes;
    }
    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
