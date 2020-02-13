package com.nsromapa.say.frenzapp_redesign.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.util.Util;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.databases.MessageReaderDbHelper;
import com.nsromapa.say.frenzapp_redesign.databases.MessagesReaderContract;
import com.nsromapa.say.frenzapp_redesign.models.Message;
import com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

import static androidx.core.app.NotificationCompat.BADGE_ICON_SMALL;
import static com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity.updateMessageStatus;
import static com.nsromapa.say.frenzapp_redesign.utils.Constants.CHATS;
import static com.nsromapa.say.frenzapp_redesign.utils.PermissionUtils.checkStoragePermission;

public class BootCompleteService extends Service {
    private Handler handler;
    private Runnable runnable;
    private RequestQueue requestQueue;
    private RequestQueue requestQueue1;
    private SQLiteDatabase db;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteDatabase.loadLibs(this);
    }


    @Override
    public void onDestroy() {
        if (requestQueue != null)
            requestQueue.cancelAll(this);
        if (requestQueue1 != null)
            requestQueue1.cancelAll(this);
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_STICKY;
    }

    private void handleStart(Intent intent, int startId) {
        if (checkStoragePermission(getApplicationContext()))
            db = MessageReaderDbHelper.getInstance(getApplicationContext())
                    .getWritableDatabase("somePass");

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                runThisFunction1();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 8000);
    }


    protected void runThisFunction1() {
        SendMessage();
        GetMessages();
        SetNotificationBadges();
        //getCalls
    }
    private void SetNotificationBadges() {
        ShortcutBadger.applyCount(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Utils.getChatNotificationName(), 0));
    }


    private void setNotifications(JSONObject messageObj) throws JSONException {
        if (messageObj.getString("message_status").equals("1") ||
                messageObj.getString("message_status").equals("2")){

            JSONObject friendObj = messageObj.getJSONObject("2");
            int count = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Utils.getChatNotificationName(), 0);
            count++;
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(Utils.getChatNotificationName(), count).apply();

            int thisUserChatCount = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(Utils.getUserChatNotificationName()+"_"+friendObj.getString("id"), 0);

            thisUserChatCount++;
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(Utils.getUserChatNotificationName()+"_"+friendObj.getString("id"),thisUserChatCount).apply();

            Intent notificationIntent = new Intent(getApplicationContext(), ChatViewActivity.class);
            notificationIntent.putExtra("chatType", messageObj.getString("chat_type"));
            notificationIntent.putExtra("thisUserId", friendObj.getString("id"));
            notificationIntent.putExtra("thisUserJson", friendObj.toString());
            notificationIntent.putExtra("backToMain", true);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), default_notification_channel_id);

            mBuilder.setContentTitle(friendObj.getString("username"));
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setContentText(messageObj.getString("body"));


            // ICONS
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground);

            Bitmap large_icon_bmp = ((BitmapDrawable) getApplicationContext().getResources()
                    .getDrawable(R.mipmap.ic_launcher_round)).getBitmap();
            mBuilder.setLargeIcon(large_icon_bmp);

            mBuilder.setAutoCancel(true);
            mBuilder.setBadgeIconType(BADGE_ICON_SMALL);
            mBuilder.setVibrate(new long[]{500, 1500});
            mBuilder.setNumber(thisUserChatCount);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(Integer.parseInt(messageObj.getString("from_id")), mBuilder.build());

            updateReceivedMessageStatusOnline(messageObj.getString("id"), "2");
        }

    }

    private void updateReceivedMessageStatusOnline(String messageId, String settingStatus) {

    }

    private void SendMessage() {
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT * FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME +
                    "' WHERE " + MessagesReaderContract.MessageEntry.MESSAGE_FOR + "='" + Utils.getUserUid() +
                    "' AND " + MessagesReaderContract.MessageEntry.MESSAGE_SENDER_ID + "='" + Utils.getUserUid() +
                    "' AND " + MessagesReaderContract.MessageEntry.MESSAGE_ID + " = '' ORDER BY " +
                    MessagesReaderContract.MessageEntry._ID + " ASC", null);

            while (cursor.moveToNext()) {
                final Map<String, String> post = new HashMap<>();

                post.put("local_id", String.valueOf(cursor.getString(1)));
                post.put("message_type", String.valueOf(cursor.getString(3)));
                post.put("from_id", String.valueOf(cursor.getString(4)));
                post.put("to_id", String.valueOf(cursor.getString(7)));
                post.put("chat_type", String.valueOf(cursor.getString(18)));
                post.put("body", String.valueOf(cursor.getString(12)));
                post.put("image_list", String.valueOf(cursor.getString(13)));
                post.put("image_list_names", String.valueOf(cursor.getString(14)));
                post.put("single_url", String.valueOf(cursor.getString(15)));
                post.put("reply_to_messageid", cursor.getString(19));
                post.put("createNewMessaged", "true");
                pushMessage(post, String.valueOf(cursor.getString(1)));
            }

            cursor.close();

        } else {
            if (checkStoragePermission(getApplicationContext()))
                db = MessageReaderDbHelper.getInstance(getApplicationContext()).getReadableDatabase("somePass");
        }

    }

    private void pushMessage(Map<String, String> post, String loc_id) {

        Log.e("pushMessage", "SendMessage: " + post);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHATS, response -> {

            if (response != null && !response.isEmpty()) {

                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("MessageResponse");

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject messageObj = jsonArray.getJSONObject(i);
                            String local_id = messageObj.getString("local_id");
                            String messageId = messageObj.getString("messageId");
                            Message message = new Message();

                            if (local_id.equals(loc_id) && TextUtils.isDigitsOnly(messageId)) {
                                Log.e("PushMessage", "======================>     local_id: " + local_id + "&& messageId " + messageId);
                                if (db != null) {
                                    db.execSQL("UPDATE " + MessagesReaderContract.MessageEntry.TABLE_NAME + "\n" +
                                            "SET " + MessagesReaderContract.MessageEntry.MESSAGE_ID + " = '" + messageId + "' \n" +
                                            "WHERE " + MessagesReaderContract.MessageEntry.LOCAL_ID + " = '" + local_id + "' ;");

                                    db.execSQL("UPDATE " + MessagesReaderContract.MessageEntry.TABLE_NAME + "\n" +
                                            "SET " + MessagesReaderContract.MessageEntry.MESSAGE_STATUS + " = '1' \n" +
                                            "WHERE " + MessagesReaderContract.MessageEntry.LOCAL_ID + " = '" + local_id + "' " +
                                            "AND " + MessagesReaderContract.MessageEntry.MESSAGE_STATUS + " < '1' ;");

                                } else {
                                    if (checkStoragePermission(getApplicationContext()))
                                        db = MessageReaderDbHelper.getInstance(getApplicationContext()).getReadableDatabase("somePass");
                                }
                                message.setLocal_id(local_id);
                                message.setId(messageId);
                                message.setStatus("1");
                                updateMessageStatus(message);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, error -> {
        }) {
            @Override
            protected Map<String, String> getParams() {
                return post;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue.add(stringRequest);

    }


    private String getMessageLastId() {
        if (db != null) {
            String messageId;
            Cursor cursor = db.rawQuery("SELECT " + MessagesReaderContract.MessageEntry.MESSAGE_ID +
                    "  FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME +
                    "' ORDER BY " + MessagesReaderContract.MessageEntry._ID + " DESC", null);

            cursor.moveToFirst();
            if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
                messageId = "0";
            } else {
                messageId = String.valueOf(cursor.getString(cursor.getColumnIndex(MessagesReaderContract.MessageEntry.MESSAGE_ID)));
            }

            if (TextUtils.isEmpty(messageId))
                messageId = "0";
            Log.e("getMessageLastId", "getMessageLastId: " + messageId);

            cursor.close();
            return messageId;
        } else {
            if (checkStoragePermission(getApplicationContext()))
                db = MessageReaderDbHelper.getInstance(getApplicationContext()).getReadableDatabase("somePass");
            return "0";
        }

    }

    private void GetMessages() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHATS, response -> {

            if (response != null && !response.isEmpty()) {

                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("Messages");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject messageObj = jsonArray.getJSONObject(i);
                        insertIntoDB(messageObj, messageObj.getString("local_id"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid());
                post.put("lastgrabed_message", getMessageLastId());
                post.put("getMessages", "");
                return post;
            }
        };
        if (requestQueue1 == null) {
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue1.add(stringRequest);
    }

    private void insertIntoDB(JSONObject messageObj, String local_id) throws JSONException {
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT " + MessagesReaderContract.MessageEntry.LOCAL_ID +
                    "  FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME +
                    "' WHERE " + MessagesReaderContract.MessageEntry.LOCAL_ID + " = '" + local_id + "' LIMIT 1", null);

            JSONObject friendObj = messageObj.getJSONObject("2");

            if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {

                ContentValues values = new ContentValues();
                values.put(MessagesReaderContract.MessageEntry.LOCAL_ID, local_id);
                values.put(MessagesReaderContract.MessageEntry.MESSAGE_ID, messageObj.getString("id"));
                values.put(MessagesReaderContract.MessageEntry.MESSAGE_TYPE, messageObj.getString("0"));

                if (messageObj.getString("1").equals("yes")) {
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_ID, Utils.getUserUid());
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_NAME, Utils.getUserName());
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_IMAGE, Utils.getUserImage());
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_ID, friendObj.getString("id"));
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_NAME, friendObj.getString("username"));
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_IMAGE, friendObj.getString("image"));
                } else {
                    //If I am receiving message check if not received before
                    // and create notification if not seen
                    setNotifications(messageObj);

                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_ID, friendObj.getString("id"));
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_NAME, friendObj.getString("username"));
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_IMAGE, friendObj.getString("image"));
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_ID, Utils.getUserUid());
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_NAME, Utils.getUserName());
                    values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_IMAGE, Utils.getUserImage());
                }

                values.put(MessagesReaderContract.MessageEntry.MESSAGE_TIME, messageObj.getString("date_added"));
                values.put(MessagesReaderContract.MessageEntry.MESSAGE_STATUS, messageObj.getString("message_status"));
                values.put(MessagesReaderContract.MessageEntry.MESSAGE_BODY, messageObj.getString("body"));
                values.put(MessagesReaderContract.MessageEntry.IMAGE_LIST, messageObj.getString("image_list"));
                values.put(MessagesReaderContract.MessageEntry.IMAGE_NAME_LIST, messageObj.getString("image_list_names"));
                values.put(MessagesReaderContract.MessageEntry.SINGLE_URL, messageObj.getString("single_url"));
                values.put(MessagesReaderContract.MessageEntry.LOCAL_LOCATION, "");
                values.put(MessagesReaderContract.MessageEntry.MESSAGE_FOR, Utils.getUserUid());
                values.put(MessagesReaderContract.MessageEntry.CHAT_TYPE, messageObj.getString("chat_type"));
                values.put(MessagesReaderContract.MessageEntry.REPLY_TO, messageObj.getString("reply_to_messageid"));
                db.insert(MessagesReaderContract.MessageEntry.TABLE_NAME, null, values);
            }
            cursor.close();
        } else {
            if (checkStoragePermission(getApplicationContext()))
                db = MessageReaderDbHelper.getInstance(getApplicationContext()).getReadableDatabase("somePass");
        }
    }
}
