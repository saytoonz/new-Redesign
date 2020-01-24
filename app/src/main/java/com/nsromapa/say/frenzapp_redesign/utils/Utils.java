package com.nsromapa.say.frenzapp_redesign.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioFormat;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.nsromapa.say.frenzapp_redesign.models.AudioChannel;
import com.nsromapa.say.frenzapp_redesign.models.AudioSampleRate;
import com.nsromapa.say.frenzapp_redesign.models.AudioSource;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    private String myStories;
    private static final Handler HANDLER = new Handler();

    public Utils() {
    }


    public Utils(String myStories) {
        this.myStories = myStories;
    }

    public static String getUserUid() {
        return "2";
    }

    public static String getUserImage() {
        return "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLAyxdUJ8KLw1V8EHfAcSi6X94x13WHxQrgSIlve16-SFeVZYIGg&s";
    }

    public static String getUserName() {
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

    public static boolean isMeFollowing(String user_id) {
        return true;
    }

    public static boolean isFollowingMe(String user_id) {
        return true;
    }

    public static boolean areWeFriends(String user_id) {
        return isFollowingMe(user_id) && isMeFollowing(user_id);
    }


    public static String getUserInfoFromUserJSON(String userJson, String index) {
        try {
            JSONObject userObject = new JSONObject(userJson);
            return userObject.getString(index);
        } catch (JSONException e) {
            return "";
        }
    }


    public static boolean isFriendChatNotificationMuted(Context context, String friendId) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("chatNotification_" + friendId, false);
    }
    public static void setFriendChatNotificationMuted(Context context, String friendId) {
        if (isFriendChatNotificationMuted(context, friendId)){
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("chatNotification_" + friendId, false).apply();
        }else{
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("chatNotification_" + friendId, true).apply();
        }
    }


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "< 1 min";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " mins";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hrs";
        } else if (diff < 48 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hrs";
//            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days";
        }
    }




    public static void wait(int millis, Runnable callback){
        HANDLER.postDelayed(callback, millis);
    }

    public static omrecorder.AudioSource getMic(AudioSource source,
                                                AudioChannel channel,
                                                AudioSampleRate sampleRate) {
        return new omrecorder.AudioSource.Smart(
                source.getSource(),
                AudioFormat.ENCODING_PCM_16BIT,
                channel.getChannel(),
                sampleRate.getSampleRate());
    }

    public static boolean isBrightColor(int color) {
        if(android.R.color.transparent == color) {
            return true;
        }
        int [] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        int brightness = (int) Math.sqrt(
                rgb[0] * rgb[0] * 0.241 +
                        rgb[1] * rgb[1] * 0.691 +
                        rgb[2] * rgb[2] * 0.068);
        return brightness >= 200;
    }

    public static int getDarkerColor(int color) {
        float factor = 0.8f;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    public static String formatSeconds(int seconds) {
        return getTwoDecimalsValue(seconds / 3600) + ":"
                + getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }


    public static void downloadSoundAudio(Context context, String soundName) {
        Toast.makeText(context, "Download: " + soundName, Toast.LENGTH_SHORT).show();
    }

}
