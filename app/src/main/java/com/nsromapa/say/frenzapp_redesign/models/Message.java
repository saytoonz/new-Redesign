package com.nsromapa.say.frenzapp_redesign.models;


import android.net.Uri;

import java.util.List;

/**
 * * Created by say on 16/01/20.
 */


public class Message {


    protected String LeftSimpleMessage = "LEFT";
    protected String RightSimpleMessage = "RIGHT";
    protected String LeftSingleImage = "LeftImage";
    protected String RightSingleImage = "RightImage";

    //Can hold upto 11 images.
    protected String LeftMultipleImages = "LeftImages";
    protected String RightMultipleImages = "RightImages";

    //Single Video
    protected String LeftVideo = "LeftVideo";
    protected String RightVideo = "RightVideo";

    protected String LeftAudio = "LeftAudio";
    protected String RightAudio = "RightAudio";

    protected long id;
    protected MessageType messageType;
    protected String type;
    protected String body;
    protected String time;
    protected String status;
    protected List<Uri> imageList;
    protected List<String> imageListNames;
    protected String userName;
    protected Uri userIcon;
    protected Uri videoUri;
    protected Uri audioUri;
    private String singleUrl;
    private String audioLocalLocation;
    private String videoLocalLocation;
    private String imageLocalLocation;
    protected int indexPosition;

    public String getSingleUrl() {
        return singleUrl;
    }

    public void setSingleUrl(String singleUrl) {
        this.singleUrl = singleUrl;
    }

    public enum MessageType{
        LeftSimpleMessage,
        RightSimpleImage,
        LeftSingleImage,
        RightSingleImage,
        LeftMultipleImages,
        RightMultipleImages,
        LeftVideo,
        RightVideo,
        LeftAudio,
        RightAudio,
        LeftSticker,
        RightSticker,
        LeftGIF,
        RightGIF,
        LeftSound,
        RightSound
    }

    public Message(){

    }

    public Uri getAudioUri() {
        return audioUri;
    }

    public void setAudioUri(Uri audioUri) {
        this.audioUri = audioUri;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getIndexPosition() {
        return indexPosition;
    }

    public void setIndexPosition(int indexPosition) {
        this.indexPosition = indexPosition;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Uri> getImageList() {
        return imageList;
    }

    public void setImageList(List<Uri> imageList) {
        this.imageList = imageList;
    }

    public List<String> getImageListNames() {
        return imageListNames;
    }

    public void setImageListNames(List<String> imageListNames) {
        this.imageListNames = imageListNames;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Uri getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(Uri userIcon) {
        this.userIcon = userIcon;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public String getAudioLocalLocation() {
        return audioLocalLocation;
    }

    public void setAudioLocalLocation(String audioLocalLocation) {
        this.audioLocalLocation = audioLocalLocation;
    }

    public String getVideoLocalLocation() {
        return videoLocalLocation;
    }

    public void setVideoLocalLocation(String videoLocalLocation) {
        this.videoLocalLocation = videoLocalLocation;
    }

    public String getImageLocalLocation() {
        return imageLocalLocation;
    }

    public void setImageLocalLocation(String imageLocalLocation) {
        this.imageLocalLocation = imageLocalLocation;
    }
}


