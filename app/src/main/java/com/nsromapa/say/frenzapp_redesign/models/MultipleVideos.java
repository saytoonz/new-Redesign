package com.nsromapa.say.frenzapp_redesign.models;


public class MultipleVideos {
    private String urlVideo;
    private String urlVideoLocal;
    private int seekTo;

    public MultipleVideos(String urlVideo, int seekTo) {
        this.urlVideo = urlVideo;
        this.seekTo = seekTo;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getUrlVideoLocal() {
        return urlVideoLocal;
    }

    public void setUrlVideoLocal(String urlVideoLocal) {
        this.urlVideoLocal = urlVideoLocal;
    }

    public int getSeekTo() {
        return seekTo;
    }

    public void setSeekTo(int seekTo) {
        this.seekTo = seekTo;
    }
}
