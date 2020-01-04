package com.nsromapa.say.frenzapp_redesign.models;


import java.util.ArrayList;

public class StoryStatus {
    private String posterId;
    private String posterName;
    private String posterImage;
    private String lastStory;
    private ArrayList<StoriesData> stories;


    public StoryStatus(String posterId, String posterName,
                       String posterImage, String lastStory, ArrayList<StoriesData> mStoriesList) {
        this.posterId = posterId;
        this.posterName = posterName;
        this.posterImage = posterImage;
        this.stories = mStoriesList;
        this.lastStory = lastStory;
    }



    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getLastStory() {
        return lastStory;
    }

    public void setLastStory(String lastStory) {
        this.lastStory = lastStory;
    }

    public ArrayList<StoriesData> getStories() {
        return stories;
    }

    public void setStories(ArrayList<StoriesData> stories) {
        this.stories = stories;
    }
}
