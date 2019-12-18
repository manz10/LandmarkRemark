package com.example.landmarkremark.model;

/*
This class holds the structure of Note model
 */
public class Note {

    private String message;         //holds the message or the info of the added notes
    private double latitude;
    private double longitude;
    private String address;

    public Note() {
    }

    public Note(String message, double latitude, double longitude, String address) {
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
