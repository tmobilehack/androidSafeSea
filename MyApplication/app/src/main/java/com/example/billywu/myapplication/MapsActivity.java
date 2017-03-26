package com.example.billywu.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Intent receivedIntent;
    int duration = Toast.LENGTH_SHORT;
    Context context;
    List<LocationPoint> locations;
    Double targetLongitude, targetLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locations = new ArrayList<LocationPoint>();

        receivedIntent = getIntent();
        Bundle bundle = getIntent().getExtras();
        String jsonResult = bundle.getString("json_result");

        Log.d("LATITUDE ",bundle.getString("target_latitude"));
        Log.d("LONGITUDE ",bundle.getString("target_longitude"));
        targetLatitude = Double.parseDouble(bundle.getString("target_latitude"));
        targetLongitude = Double.parseDouble(bundle.getString("target_longitude"));


        if (jsonResult != null) {
            Toast toast = Toast.makeText(context, jsonResult, duration);
            toast.show();
        }
        JSONArray arr;
        try {
            JSONObject realJSON = new JSONObject(jsonResult);
            JSONArray crimes = realJSON.getJSONArray("crimes");
            for (int i = 0; i < crimes.length(); i++) {
                //locations.add(new LocationPoint((Double) (new JSONObject(crimes.get(i).toString())).get("longitude"),(Double) (new JSONObject(crimes.get(i).toString())).get("latitude")));
                JSONObject crime = (JSONObject) crimes.get(i);
                Double longitude = Double.parseDouble(crime.getString("longitude"));
                Double latitude = Double.parseDouble(crime.getString("latitude"));
                String crimeTitle = crime.getString("offense_type");
                locations.add(new LocationPoint(longitude, latitude, crimeTitle));

            }

            for (int i = 0; i < locations.size(); i++) {
                Log.d("DATA! ", locations.get(i).toString());
            }

        } catch (Throwable t) {
            Log.e("WOW ","something's wrong with the JSON");
        }
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

        for (int i = 0; i < locations.size(); i++) {
            LocationPoint point = locations.get(i);
            LatLng location = new LatLng(point.getLatitude(), point.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(point.getCrimeTitle()));
        }

        LatLng targetLocation = new LatLng(targetLatitude, targetLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15.0f));



    }
}
