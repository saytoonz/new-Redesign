package com.nsromapa.say.frenzapp_redesign.models;

import java.io.Serializable;

public class DiscoveryComment implements Serializable {
    private boolean isLoading = false;
    private String commenter_id;
    private String commenter_name;
    private String commenter_image;
    private String commenter_JObj;
    private String comment_id;
    private String comment_time;
    private String comment;
    private String comment_likes;
    private String comment_dislikes;
    private String comment_or_description;

    public DiscoveryComment(){

    }

    public DiscoveryComment(String commenter_id,
                            String commenter_name,
                            String commenter_image,
                            String commenter_JObj,
                            String comment_id,
                            String comment_time,
                            String comment,
                            String comment_likes,
                            String comment_dislikes,
                            String comment_or_description) {
        this.commenter_id = commenter_id;
        this.commenter_name = commenter_name;
        this.commenter_image = commenter_image;
        this.commenter_JObj = commenter_JObj;
        this.comment_id = comment_id;
        this.comment_time = comment_time;
        this.comment = comment;
        this.comment_likes = comment_likes;
        this.comment_dislikes = comment_dislikes;
        this.comment_or_description = comment_or_description;
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

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_likes() {
        return comment_likes;
    }

    public void setComment_likes(String comment_likes) {
        this.comment_likes = comment_likes;
    }

    public String getComment_dislikes() {
        return comment_dislikes;
    }

    public void setComment_dislikes(String comment_dislikes) {
        this.comment_dislikes = comment_dislikes;
    }

    public String getComment_or_description() {
        return comment_or_description;
    }

    public void setComment_or_description(String comment_or_description) {
        this.comment_or_description = comment_or_description;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
