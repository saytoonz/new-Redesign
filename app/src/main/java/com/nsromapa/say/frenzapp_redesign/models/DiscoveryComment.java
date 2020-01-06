package com.nsromapa.say.frenzapp_redesign.models;

public class DiscoveryComment {

    private String commenter_id;
    private String commenter_name;
    private String commenter_image;
    private String commenter_JObj;

    public DiscoveryComment(String commenter_id, String commenter_name, String commenter_image, String commenter_JObj) {
        this.commenter_id = commenter_id;
        this.commenter_name = commenter_name;
        this.commenter_image = commenter_image;
        this.commenter_JObj = commenter_JObj;
    }

    public String getCommenter_id() {
        return commenter_id;
    }

    public void setCommenter_id(String commenter_id) {
        this.commenter_id = commenter_id;
    }

    public String getCommenter_name() {
        return commenter_name;
    }

    public void setCommenter_name(String commenter_name) {
        this.commenter_name = commenter_name;
    }

    public String getCommenter_image() {
        return commenter_image;
    }

    public void setCommenter_image(String commenter_image) {
        this.commenter_image = commenter_image;
    }

    public String getCommenter_JObj() {
        return commenter_JObj;
    }

    public void setCommenter_JObj(String commenter_JObj) {
        this.commenter_JObj = commenter_JObj;
    }
}
