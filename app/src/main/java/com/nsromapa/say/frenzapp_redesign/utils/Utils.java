package com.nsromapa.say.frenzapp_redesign.utils;

public class Utils {
    private String myStories;

    public Utils() {
    }

    public Utils(String myStories) {
        this.myStories = myStories;
    }

    public static String getUserUid(){
        return "2";
    }

    public static String getUserImage(){
        return "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLAyxdUJ8KLw1V8EHfAcSi6X94x13WHxQrgSIlve16-SFeVZYIGg&s";
    }

    public static String getUserName(){
        return "Saytoonz";
    }

    public static String getMyFollowings() {
        return "1,2,3";
    }

    public static String getMyFollower() {
        return "1,3";
    }

    public String getMyStories() {
        return myStories;
    }

}
