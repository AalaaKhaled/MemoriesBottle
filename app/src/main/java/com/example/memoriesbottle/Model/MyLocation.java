package com.example.memoriesbottle.Model;

public class MyLocation {
    double Latitude;
    double Longitude;
    String locatinId;
    String publisher;
    String postid;
    String description;
    String CountyName;
    String gov;

    public MyLocation(double latitude, double longitude, String locatinId, String publisher, String postid, String description, String countyName, String gov) {
        Latitude = latitude;
        Longitude = longitude;
        this.locatinId = locatinId;
        this.publisher = publisher;
        this.postid = postid;
        this.description = description;
        CountyName = countyName;
        this.gov = gov;
    }

    public MyLocation() {
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

    public String getLocatinId() {
        return locatinId;
    }

    public void setLocatinId(String locatinId) {
        this.locatinId = locatinId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public String getGov() {
        return gov;
    }

    public void setGov(String gov) {
        this.gov = gov;
    }
}
