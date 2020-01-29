package com.nsromapa.say.frenzapp_redesign.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.databases.MessageReaderDbHelper;
import com.nsromapa.say.frenzapp_redesign.databases.MessagesReaderContract;
import com.nsromapa.say.frenzapp_redesign.models.Message;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity.addMessage;
import static com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity.updateUserStatus;
import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.updateChatList;
import static com.nsromapa.say.frenzapp_redesign.utils.Constants.STATUS;


public class ChatViewActivityServices extends Service {
    private RequestQueue requestQueue;
    private RequestQueue requestQueue1;
    private Handler handler;
    private Runnable runnable;
    private String UserId;
    private int offset = 0;
    private SQLiteDatabase db;
    private int delayTime = 50;

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
        db = MessageReaderDbHelper.getInstance(getApplicationContext())
                .getReadableDatabase("somePass");
//        getContacts();
//        getMessages();
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                runThisFunction1();
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, delayTime);
        delayTime = 4000;
    }


    protected void runThisFunction1() {
        getMessages();
        getStatusUpdate();
        setAllMessagesSeen();
    }


    private void getStatusUpdate() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATUS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONArray jsonArray = object.getJSONArray("StatusInfo");

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject chatListObj = jsonArray.getJSONObject(i);
                        updateUserStatus(chatListObj.getString("online_status"), UserId, chatListObj.getString("0"));                  }
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
    private void getMessages() {
        Cursor cursor = db.rawQuery("SELECT * FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME +
                "' WHERE "+MessagesReaderContract.MessageEntry.MESSAGE_FOR+"='"+ Utils.getUserUid() +
                "' AND (("+MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_ID+"='"+ UserId +
                "') OR ("+MessagesReaderContract.MessageEntry.MESSAGE_SENDER_ID+"='"+ UserId +
                "')) ORDER BY "+MessagesReaderContract.MessageEntry.MESSAGE_ID+" ASC LIMIT "+offset+",~0;", null);

        while (cursor.moveToNext()) {
            Message message = new Message();

            String imagesString = cursor.getString(12);
            List<String> imageList = new ArrayList<>();
            if (imagesString != null && !TextUtils.isEmpty(imagesString)){
                String[] images = imagesString.split(",,");
                for (String image : images) {
                    if (!image.isEmpty())
                        imageList.add(image);
                }
            }

            String nameString = cursor.getString(13);
            List<String> imageNamesList = new ArrayList<>();
            if (nameString != null && !TextUtils.isEmpty(nameString)){
                String[] names = nameString.split(",,");
                for (String name : names) {
                    if (!name.isEmpty())
                        imageNamesList.add(name);
                }
            }


            message.setLocal_id(String.valueOf(cursor.getInt(0)));
            message.setId(cursor.getString(1));
            message.setMessageType(Message.MessageType.valueOf(cursor.getString(2)));
            message.setUserName(cursor.getString(4));
            message.setUserIcon(cursor.getString(5));
            message.setTime(cursor.getString(9));
            message.setStatus(cursor.getString(10));
            message.setBody(cursor.getString(11));
            message.setImageList(imageList);
            message.setImageListNames(imageNamesList);
            message.setSingleUrl(cursor.getString(14));
            message.setLocalLocation(cursor.getString(15));

            addMessage(message, true);
            offset++;
        }

    }
    private void setAllMessagesSeen(){
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, STATUS, response -> {

        }, error -> {

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid());
                post.put("thatUser", UserId);
                post.put("setAllMessagesSeen", "");
                return post;
            }
        };
        if (requestQueue1 == null) {
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue1.add(stringRequest1);
    }
}
