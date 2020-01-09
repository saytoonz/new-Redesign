package com.nsromapa.say.frenzapp_redesign.models;

import java.io.Serializable;

public class Viewers implements Serializable {
    private boolean isLoading;
    private String viewer_id;
    private String viewer_name;
    private String viewer_username;
    private String viewer_image;


    public Viewers() {
    }

    public Viewers(String viewer_id, String viewer_name, String viewer_username, String viewer_image) {
        this.viewer_id = viewer_id;
        this.viewer_name = viewer_name;
        this.viewer_username = viewer_username;
        this.viewer_image = viewer_image;
    }


    public String getViewer_id() {
        return viewer_id;
    }

    public void setViewer_id(String viewer_id) {
        this.viewer_id = viewer_id;
    }

    public String getViewer_name() {
        return viewer_name;
    }

    public void setViewer_name(String viewer_name) {
        this.viewer_name = viewer_name;
    }

    public String getViewer_username() {
        return viewer_username;
    }

    public void setViewer_username(String viewer_username) {
        this.viewer_username = viewer_username;
    }

    public String getViewer_image() {
        return viewer_image;
    }

    public void setViewer_image(String viewer_image) {
        this.viewer_image = viewer_image;
    }


    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
