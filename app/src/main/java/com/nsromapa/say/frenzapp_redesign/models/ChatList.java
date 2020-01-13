package com.nsromapa.say.frenzapp_redesign.models;

public class ChatList {
    private String sender, receiver, message,
            userImage, username, timestamp, status,
            message_type, notification_count, friendJson;

    public ChatList(String sender, String receiver, String message,
                    String userImage, String username,
                    String timestamp, String status, String message_type,
                    String notification_count, String friendJson) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.message_type = message_type;
        this.message = message;
        this.userImage = userImage;
        this.username = username;
        this.timestamp = timestamp;
        this.notification_count = notification_count;
        this.friendJson = friendJson;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getNotification_count() {
        return notification_count;
    }

    public void setNotification_count(String notification_count) {
        this.notification_count = notification_count;
    }

    public String getFriendJson() {
        return friendJson;
    }

    public void setFriendJson(String friendJson) {
        this.friendJson = friendJson;
    }
}
