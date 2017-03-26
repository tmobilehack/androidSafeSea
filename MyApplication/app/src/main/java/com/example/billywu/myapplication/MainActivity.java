package com.example.billywu.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import com.goebl.david.*;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    EditText textField;
    Button searchButton, myLocationButton;
    Context context;
    LocationManager locationManager;
    List<Address> addresses;
    private ProgressBar spinner;
    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    int duration = Toast.LENGTH_SHORT;
    Intent intent;
    Double targetLongitude, targetLatitude, targetScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = (Button) findViewById(R.id.search);
        myLocationButton = (Button) findViewById(R.id.myloc);
        textField = (EditText) findViewById(R.id.location_search);
        context = getApplicationContext();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        intent = new Intent(getBaseContext(), MapsActivity.class);


        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocationName(textField.getText().toString(), 1);


                        } catch (IOException e) {
                            Toast toast = Toast.makeText(context, "GEOCODER EXCEPTION", duration);
                            toast.show();
                        }

                        double latitude = 0.0;
                        double longitude = 0.0;
                        if (addresses != null && !addresses.isEmpty()) {
                            if (addresses.size() > 0) {
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                            }

                            if (latitude == 0.0 && longitude == 0.0) {
                                Toast toast = Toast.makeText(context, "Could not find location", duration);
                                toast.show();
                            }

                            Toast toast = Toast.makeText(context, "Location found: " + latitude + " " + longitude, duration);
                            toast.show();

                            spinner.setVisibility(View.VISIBLE);
                            targetLongitude = longitude;
                            targetLatitude = latitude;
                            new TaskTask().execute(new LocationPoint(longitude, latitude));

                        }
                    }
                });

        myLocationButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        String locationProvider = LocationManager.NETWORK_PROVIDER;
// Or use LocationManager.GPS_PROVIDER
                        try {
                            requestPermissions(LOCATION_PERMS, LOCATION_REQUEST);

                            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, lastKnownLocation.getLongitude() + " " +
                                    lastKnownLocation.getLatitude(), duration);
                            toast.show();

                            spinner.setVisibility(View.VISIBLE);
                            targetLongitude = lastKnownLocation.getLongitude();
                            targetLatitude = lastKnownLocation.getLatitude();
                            new TaskTask().execute(new LocationPoint(targetLongitude, targetLatitude));
                        } catch (SecurityException e) {
                            Toast toast = Toast.makeText(context, "you don't have permissions", duration);
                            toast.show();
                        }
                    }
                });
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private class TaskTask extends AsyncTask<LocationPoint, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(LocationPoint... lp) {

            URL url;
            HttpURLConnection urlConnection;
            try {
                Webb webb = com.goebl.david.Webb.create();

                Response<JSONObject> response =
                webb.get("http://ec2-34-208-245-91.us-west-2.compute.amazonaws.com:5000/api")
                        .param("longitude", Double.toString(lp[0].getLongitude()))
                        .param("latitude", Double.toString(lp[0].getLatitude()))
                        .ensureSuccess()
                        .asJsonObject();

                JSONObject apiResult = response.getBody();

                return apiResult;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            spinner.setVisibility(View.GONE);
            if (result != null) {


                intent.putExtra("json_result", result.toString());
                intent.putExtra("target_longitude", targetLongitude.toString());
                intent.putExtra("target_latitude", targetLatitude.toString());
                Log.d("RESULT ", result.toString());
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(context, "WE GOT NOTHING BACK FROM THE SERVER", duration);
                toast.show();
            }
        }
    }
}
