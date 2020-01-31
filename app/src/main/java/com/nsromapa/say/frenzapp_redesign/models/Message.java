package com.nsromapa.say.frenzapp_redesign.models;


import java.util.ArrayList;
import java.util.List;

/**
 * * Created by say on 16/01/20.
 */


public class Message {
    protected String local_id="";
    protected  String id="";
    protected MessageType messageType = MessageType.RightSimpleMessage;
    protected String body="";
    protected String time ="";
    protected String status="";
    protected List<String> imageList = new ArrayList<>();
    protected List<String> imageListNames = new ArrayList<>();
    protected String userName = "";
    protected String userIcon = "";
    private String singleUrl = "";
    private String localLocation = "";
    protected int indexPosition;

    public String getSingleUrl() {
        return singleUrl;
    }

    public void setSingleUrl(String singleUrl) {
        this.singleUrl = singleUrl;
    }

    public enum MessageType{
        LeftSimpleMessage,
        RightSimpleMessage,
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
    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    public String getLocal_id() {
        return local_id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
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

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }


    public String getLocalLocation() {
        return localLocation;
    }

    public void setLocalLocation(String localLocation) {
        this.localLocation = localLocation;
    }
}


