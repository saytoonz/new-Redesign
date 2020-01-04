package com.nsromapa.say.frenzapp_redesign.models;

/**
 * Created by amsavarthan on 4/4/18.
 */

public class Post {
    private String userId, name, timestamp, likes, favourites, description, color,username,userimage;
    private String images_url, post_jsonString, post_type, postId;

    public Post(String postId, String userId, String timestamp, String likes,
                String favourites, String description, String color, String username,
                String userimage, String post_type, String images_url, String post_jsonString) {

        this.userId = userId;
        this.name = name;
        this.timestamp = timestamp;
        this.likes = likes;
        this.favourites = favourites;
        this.description = description;
        this.color = color;
        this.username = username;
        this.userimage = userimage;
        this.images_url = images_url;
        this.post_jsonString = post_jsonString;
        this.postId = postId;
        this.post_type = post_type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getFavourites() {
        return favourites;
    }

    public void setFavourites(String favourites) {
        this.favourites = favourites;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getImages_url() {
        return images_url;
    }

    public void setImages_url(String images_url) {
        this.images_url = images_url;
    }

    public String getPost_jsonString() {
        return post_jsonString;
    }

    public void setPost_jsonString(String post_jsonString) {
        this.post_jsonString = post_jsonString;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
