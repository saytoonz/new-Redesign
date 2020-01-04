package com.nsromapa.say.frenzapp_redesign.models;

public class Discoveries {
    private String id;
    private String mediaUrl;
    private boolean isLoading = false;


    public Discoveries() {
    }

    public Discoveries(String id, String mediaUrl) {
        this.id = id;
        this.mediaUrl = mediaUrl;
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

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
