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
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.CHATS;
import static com.nsromapa.say.frenzapp_redesign.utils.PermissionUtils.checkStoragePermission;

public class BootCompleteService extends Service {
    private Handler handler;
    private Runnable runnable;
    private RequestQueue requestQueue;
    private SQLiteDatabase db;

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
        if (checkStoragePermission(getApplicationContext()))
            db = MessageReaderDbHelper.getInstance(getApplicationContext())
                    .getReadableDatabase("somePass");

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
        //GetMessages
        //getCalls
        //  setNotifications
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

        Log.e("pushMessage", "SendMessage: "+ post );

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

}
