package com.example.billywu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent receivedIntent;
    List<LocationPoint> propertyLocations, transLocations, pedLocations;
    Context context;
    Double targetLongitude, targetLatitude, targetScoreProperty, targetScoreTrans, targetScorePed;
    TextView scoreText;
    Button statsButton, propertyButton, transButton, pedButton;
    Intent intent;

    int duration = Toast.LENGTH_SHORT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //scoreText = (TextView) findViewById(R.id.score_text);
        statsButton = (Button) findViewById(R.id.stats_button);
        propertyButton = (Button) findViewById(R.id.property_button);
        transButton = (Button) findViewById(R.id.trans_button);
        pedButton = (Button) findViewById(R.id.ped_button);
        propertyLocations = new ArrayList<LocationPoint>();
        transLocations = new ArrayList<LocationPoint>();
        pedLocations = new ArrayList<LocationPoint>();

        receivedIntent = getIntent();
        Bundle bundle = getIntent().getExtras();
        String jsonResult = bundle.getString("json_result");

        intent = new Intent(getBaseContext(), DataActivity.class);


        Log.d("LATITUDE ", bundle.getString("target_latitude"));
        Log.d("LONGITUDE ", bundle.getString("target_longitude"));
        targetLatitude = Double.parseDouble(bundle.getString("target_latitude"));
        targetLongitude = Double.parseDouble(bundle.getString("target_longitude"));

        intent.putExtra("json_result", jsonResult.toString());
        intent.putExtra("target_longitude", targetLongitude.toString());
        intent.putExtra("target_latitude", targetLatitude.toString());


        JSONArray arr;
        try {
            JSONObject realJSON = new JSONObject(jsonResult);
            Log.d("realJSON", realJSON.toString());
            JSONObject crimes = (JSONObject) realJSON.get("crimes");

            Log.d("crimes", crimes.toString());


            JSONArray homeCrimes = crimes.getJSONArray("home");
            Log.d("homeCrimes", homeCrimes.toString());
            JSONArray transCrimes = crimes.getJSONArray("vehicle");
            Log.d("transCrimes", transCrimes.toString());
            JSONArray pedCrimes = crimes.getJSONArray("walk");
            Log.d("pedCrimes", pedCrimes.toString());

            JSONObject scores = (JSONObject) realJSON.get("scores");
            Log.d("scores", scores.toString());


            targetScoreProperty = (100.0 * Double.parseDouble(scores.getString("home").toString()));
            Log.d("rawPropertyScore", targetScoreProperty.toString());
            targetScoreTrans = (100.0 * Double.parseDouble(scores.getString("vehicle").toString()));
            Log.d("rawTransScore", targetScoreTrans.toString());
            targetScorePed = (100.0 * Double.parseDouble(scores.getString("walk").toString()));
            Log.d("rawPedScore", targetScorePed.toString());


            propertyButton.setText(
                    "Property\n" + Math.round(targetScoreProperty) + "");
            transButton.setText(
                    "Trans\n" + Math.round(targetScoreTrans) + "");
            pedButton.setText(
                    "Ped\n" + Math.round(targetScorePed) + "");


            for (int i = 0; i < homeCrimes.length(); i++) {
                //locations.add(new LocationPoint((Douwble) (new JSONObject(crimes.get(i).toString())).get("longitude"),(Double) (new JSONObject(crimes.get(i).toString())).get("latitude")));
                JSONObject crime = (JSONObject) homeCrimes.get(i);
                Double longitude = Double.parseDouble(crime.getString("longitude"));
                Double latitude = Double.parseDouble(crime.getString("latitude"));
                String crimeTitle = crime.getString("offense_description");
                propertyLocations.add(new LocationPoint(longitude, latitude, crimeTitle));

            }
            for (int i = 0; i < transCrimes.length(); i++) {
                //locations.add(new LocationPoint((Douwble) (new JSONObject(crimes.get(i).toString())).get("longitude"),(Double) (new JSONObject(crimes.get(i).toString())).get("latitude")));
                JSONObject crime = (JSONObject) transCrimes.get(i);
                Double longitude = Double.parseDouble(crime.getString("longitude"));
                Double latitude = Double.parseDouble(crime.getString("latitude"));
                String crimeTitle = crime.getString("offense_description");
                transLocations.add(new LocationPoint(longitude, latitude, crimeTitle));

            }
            for (int i = 0; i < pedCrimes.length(); i++) {
                //locations.add(new LocationPoint((Douwble) (new JSONObject(crimes.get(i).toString())).get("longitude"),(Double) (new JSONObject(crimes.get(i).toString())).get("latitude")));
                JSONObject crime = (JSONObject) pedCrimes.get(i);
                Double longitude = Double.parseDouble(crime.getString("longitude"));
                Double latitude = Double.parseDouble(crime.getString("latitude"));
                String crimeTitle = crime.getString("offense_description");
                pedLocations.add(new LocationPoint(longitude, latitude, crimeTitle));

            }

            Log.d("propertyArray", propertyLocations.toString());
            Log.d("transArray", transLocations.toString());
            Log.d("pedArray", pedLocations.toString());

            /*for (int i = 0; i < locations.size(); i++) {
                Log.d("DATA! ", locations.get(i).toString());
            }*/

        } catch (Throwable t) {
            Log.e("WOW ", "something's wrong with the JSON");
            Log.d("exception", t.getMessage());
        }

        statsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        startActivity(intent);
                    }
                });

        propertyButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mMap.clear();
                        //target location marker
                        LatLng targetLocation = new LatLng(targetLatitude, targetLongitude);
                        Marker targetMarker =
                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(targetLocation));
                        for (int i = 0; i < propertyLocations.size(); i++) {
                            LocationPoint point = propertyLocations.get(i);
                            LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(point.getCrimeTitle()));
                        }


                    }
                });

        transButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mMap.clear();
                        LatLng targetLocation = new LatLng(targetLatitude, targetLongitude);
                        Marker targetMarker =
                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(targetLocation));
                        for (int i = 0; i < transLocations.size(); i++) {
                            LocationPoint point = transLocations.get(i);
                            LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(point.getCrimeTitle()));
                        }


                    }
                });
        pedButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mMap.clear();
                        LatLng targetLocation = new LatLng(targetLatitude, targetLongitude);
                        Marker targetMarker =
                                mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(targetLocation));

                        for (int i = 0; i < pedLocations.size(); i++) {
                            LocationPoint point = pedLocations.get(i);
                            LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(point.getCrimeTitle()));
                        }


                    }
                });
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
        propertyButton.setVisibility(View.VISIBLE);
        transButton.setVisibility(View.VISIBLE);
        pedButton.setVisibility(View.VISIBLE);

        //target location marker
        LatLng targetLocation = new LatLng(targetLatitude, targetLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15.0f));
        Marker targetMarker =
               mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(targetLocation));


    }
}
