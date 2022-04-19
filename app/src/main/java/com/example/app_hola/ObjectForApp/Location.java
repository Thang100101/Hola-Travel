package com.example.app_hola.ObjectForApp;

import java.io.Serializable;

public class Location implements Serializable {
    double Latitude,Longitude;
    String Name, ID;

    public Location() {
        Name = " ";
    }

    public Location(double latitude, double longitude, String name, String ID) {
        Latitude = latitude;
        Longitude = longitude;
        Name = name;
        this.ID = ID;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
