package com.roshtune;

public class Song {
    private String id;
    private String musicName;
    private String artist;
    private String musicLink;
    private String bannerLink;

    public Song() {
        // Default constructor required for Firebase
    }

    public Song(String musicName, String artist, String musicLink, String bannerLink) {

        this.musicName = musicName;
        this.artist = artist;
        this.musicLink = musicLink;
        this.bannerLink = bannerLink;
    }

    // Getter methods for all fields
    public String getId() {
        return id;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getArtist() {
        return artist;
    }

    public String getMusicLink() {
        return musicLink;
    }

    public String getBannerLink() {
        return bannerLink;
    }
}
