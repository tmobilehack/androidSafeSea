package com.example.billywu.myapplication;

/**
 * Created by billywu on 3/25/17.
 */

public class LocationPoint {
    private Double longitude;
    private Double latitude;
    private String crimeTitle;

    public LocationPoint(double lon, double lat) {
        longitude = lon;
        latitude = lat;
    }

    public LocationPoint(double lon, double lat, String crimeTitle) {
        longitude = lon;
        latitude = lat;
        this.crimeTitle = crimeTitle;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getCrimeTitle() { return this.crimeTitle; }

    public String toString() {
        return this.longitude + ", " + this.latitude;
    }
}
