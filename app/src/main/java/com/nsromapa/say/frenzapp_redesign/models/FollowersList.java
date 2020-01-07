package com.nsromapa.say.frenzapp_redesign.models;

public class FollowersList {
    private String image, username, fullname, id;

    public FollowersList(String image, String username, String fullname, String id) {
        this.image = image;
        this.username = username;
        this.fullname = fullname;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

