package com.nsromapa.say.frenzapp_redesign.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.updateChatList;
import static com.nsromapa.say.frenzapp_redesign.utils.Constants.CHATS;


public class ChatListService extends Service {
    private RequestQueue requestQueue;
    private Handler handler;
    private Runnable runnable;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        getContacts();
        handleStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getContacts();
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }

    private void handleStart(Intent intent, int startId) {
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                runThisFunction1();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }


    protected void runThisFunction1(){
        getContacts();
    }


    private void getContacts() {
        String savedChatList =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("chatList","");
        StringRequest stringRequest  = new StringRequest(Request.Method.POST, CHATS, response -> {
            List<ChatList> chatListList = new ArrayList<>();

            if (!savedChatList.equals(response)){
                Log.e("ChatListService", response);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("chatList", response).apply();
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray=  object.getJSONArray("chatLists");

                if (jsonArray.length() >0){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject chatListObj = jsonArray.getJSONObject(i);
                        JSONObject poster_info = chatListObj.getJSONObject("1");

                        Log.e("PreferenceManager", chatListObj.toString() );

                        chatListList.add(new ChatList(
                                chatListObj.getString("sender_id"),
                                chatListObj.getString("receiver_id"),
                                chatListObj.getString("last_message"),
                                poster_info.getString("image"),
                                poster_info.getString("username"),
                                chatListObj.getString("0"),
                                chatListObj.getString("message_status"),
                                chatListObj.getString("message_type"),
                                chatListObj.getString("notification_count"),
                                poster_info.getString("online_status"),
                                poster_info.toString()
                        ));
                    }
                }
                updateChatList(chatListList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid());
                post.put("grabChatList","");
                return post;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
       requestQueue.add(stringRequest);
    }

}
