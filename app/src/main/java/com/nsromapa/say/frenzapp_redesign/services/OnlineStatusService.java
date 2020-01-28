package com.nsromapa.say.frenzapp_redesign.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity.updateUserStatus;
import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.updateChatList;
import static com.nsromapa.say.frenzapp_redesign.utils.Constants.STATUS;


public class OnlineStatusService extends Service {
    private RequestQueue requestQueue;
    private Handler handler;
    private Runnable runnable;
    private String UserId;

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
        handleStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }

    private void handleStart(Intent intent, int startId) {
        UserId  = Objects.requireNonNull(intent.getExtras()).getString("thisUserId");

        getContacts();
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


    protected void runThisFunction1() {
        getContacts();
    }


    private void getContacts() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATUS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("StatusInfo");

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject chatListObj = jsonArray.getJSONObject(i);
                        updateUserStatus(chatListObj.getString("online_status"), UserId);                  }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid());
                post.put("other_user", UserId);
                post.put("grabStatusList", "");
                return post;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue.add(stringRequest);
    }

}
