package com.nsromapa.say.frenzapp_redesign.models;

import java.io.Serializable;

public class StoriesData implements Serializable {

    public StoriesData(String storyId, String mediaUrl, String mimeType, String description, String postedTime) {
        this.mediaUrl = mediaUrl;
        this.mimeType = mimeType;
        this.postedTime = postedTime;
        this.storyId = storyId;
        this.description = description;
    }

    public String mediaUrl;
    public String mimeType;
    public String postedTime;
    public String storyId;
    public String description;
}
