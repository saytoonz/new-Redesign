package com.nsromapa.say.frenzapp_redesign.models;

public class ChatList {
    private String sender, receiver, message, online_status,
            userImage, username, timestamp, message_status, chat_type,
            message_type, notification_count_sender, notification_count_receiver, friendJson;

    public ChatList(String sender, String receiver, String message,
                    String userImage, String username,
                    String timestamp, String message_status, String message_type,
                    String notification_count_sender, String notification_count_receiver,
                    String online_status, String chat_type,String friendJson) {
        this.sender = sender;
        this.receiver = receiver;
        this.message_status = message_status;
        this.message_type = message_type;
        this.message = message;
        this.userImage = userImage;
        this.username = username;
        this.timestamp = timestamp;
        this.notification_count_sender  = notification_count_sender ;
        this.notification_count_receiver = notification_count_receiver;
        this.online_status = online_status;
        this.chat_type = chat_type;
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

    public String getMessage_status() {
        return message_status;
    }

    public void setMessage_status(String message_status) {
        this.message_status = message_status;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getNotification_count_sender() {
        return notification_count_sender;
    }

    public void setNotification_count_sender(String notification_count_sender) {
        this.notification_count_sender = notification_count_sender;
    }

    public String getNotification_count_receiver() {
        return notification_count_receiver;
    }

    public void setNotification_count_receiver(String notification_count_receiver) {
        this.notification_count_receiver = notification_count_receiver;
    }

    public String getFriendJson() {
        return friendJson;
    }

    public void setFriendJson(String friendJson) {
        this.friendJson = friendJson;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getChat_type() {
        return chat_type;
    }

    public void setChat_type(String chat_type) {
        this.chat_type = chat_type;
    }
}
