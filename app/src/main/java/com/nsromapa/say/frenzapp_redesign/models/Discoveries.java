package com.nsromapa.say.frenzapp_redesign.models;

import java.io.Serializable;

public class Discoveries implements Serializable {
    private boolean isLoading = false;
    private String id;
    private String mediaUrl;
    private String mimeType;
    private String description;
    private String background;
    private String timeAgo;
    private String likes;
    private String watches;
    private String comments;
    private String i_liked = "no";
    private String posterJson;


    public Discoveries() {
    }

    public Discoveries(String id, String mediaUrl,
                       String mimeType, String description,
                       String background, String timeAgo, String
                               likes, String watches, String comments,
                       String  i_liked, String posterJson) {
        this.id = id;
        this.mediaUrl = mediaUrl;
        this.mimeType = mimeType;
        this.description = description;
        this.background = background;
        this.timeAgo = timeAgo;
        this.likes = likes;
        this.watches = watches;
        this.comments = comments;
        this.i_liked = i_liked;
        this.posterJson = posterJson;
    }


    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getWatches() {
        return watches;
    }

    public void setWatches(String watches) {
        this.watches = watches;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getI_liked() {
        return i_liked;
    }

    public void setI_liked(String i_liked) {
        this.i_liked = i_liked;
    }


    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterJson() {
        return posterJson;
    }

    public void setPosterJson(String posterJson) {
        this.posterJson = posterJson;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
