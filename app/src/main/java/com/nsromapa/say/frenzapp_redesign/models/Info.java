package com.nsromapa.say.frenzapp_redesign.models;

import java.io.Serializable;



public abstract class Info implements Serializable {
    private String urlPhoto;

    public Info() {
    }

    public Info(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }
}
