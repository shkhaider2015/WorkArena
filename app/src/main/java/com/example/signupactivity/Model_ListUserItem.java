package com.example.signupactivity;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.text.DecimalFormat;

public class Model_ListUserItem {


    private String uID;
    private String name;
    private String email;
    private Uri uri;
    private Location location;
    private Location currentUserLocation;

    public Model_ListUserItem()
    {
        location = new Location("otherUser");

    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getuID() {
        return uID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Uri getUri() {
        return uri;
    }

    public void setLocation(double latitude, double longitude, Location currentUserLocation)
    {
            this.currentUserLocation = currentUserLocation;
            location.setLatitude(latitude);
            location.setLongitude(longitude);



    }

    public String getDistance()
    {
        double speedOfCar = 60;
        double distance = Math.round(location.distanceTo(currentUserLocation) / speedOfCar);
        DecimalFormat decimalFormat = new DecimalFormat("0.#");
        

        return decimalFormat.format(distance);

    }
}
