package com.example.djsortapp;

import com.google.firebase.Timestamp;

public class Request {
    private String songName;
    private String artistName;
    private String requesterId;
    private String djName; // For filtering (e.g., "David Guetta")
    private String status; // "pending", "accepted"
    private Timestamp timestamp;

    public Request() { }

    public Request(String songName, String artistName, String requesterId, String djName, String status, Timestamp timestamp) {
        this.songName = songName;
        this.artistName = artistName;
        this.requesterId = requesterId;
        this.djName = djName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getSongName() { return songName; }
    public String getArtistName() { return artistName; }
    public String getRequesterId() { return requesterId; }
    public String getDjName() { return djName; }
    public String getStatus() { return status; }
    public Timestamp getTimestamp() { return timestamp; }
    
    public void setStatus(String status) { this.status = status; }
    public void setSongName(String songName) { this.songName = songName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }
    public void setDjName(String djName) { this.djName = djName; }
    private String albumArtUrl;
    private String previewUrl;

    public void setAlbumArtUrl(String albumArtUrl) { this.albumArtUrl = albumArtUrl; }
    public String getAlbumArtUrl() { return albumArtUrl; }
    
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public String getPreviewUrl() { return previewUrl; }

    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
