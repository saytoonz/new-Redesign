package com.nsromapa.say.frenzapp_redesign.models;

public class Followers {
    private String image, username, fullname, user_id, followers_table_id;

    public Followers(String image, String username, String fullname, String user_id, String followers_table_id) {
        this.image = image;
        this.username = username;
        this.fullname = fullname;
        this.user_id = user_id;
        this.followers_table_id = followers_table_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFollowers_table_id() {
        return followers_table_id;
    }

    public void setFollowers_table_id(String followers_table_id) {
        this.followers_table_id = followers_table_id;
    }
}

