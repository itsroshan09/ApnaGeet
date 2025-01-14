package com.roshtune;

public class User {
    private String username;
    private String mobile;

    // Required default constructor for Firebase
    public User() {
    }

    public User(String username, String mobile) {
        this.username = username;
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
