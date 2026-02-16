package com.example.djsortapp;

public class DJ {
    private String name;
    private String genre;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private String email;
    private String address; // New field for human-readable location

    public DJ() {
        // Required for Firebase
    }

    public DJ(String name, String genre, String imageUrl, String email, double latitude, double longitude, String address) {
        this.name = name;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
    
    // Convenience constructor for Mock Data
    public DJ(String name, String genre, String imageUrl) {
        this.name = name;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
    public String getEmail() { return email; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getAddress() { return address; }
}
