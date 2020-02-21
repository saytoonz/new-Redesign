package com.nsromapa.say.frenzapp_redesign.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.R;
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
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import es.dmoral.toasty.Toasty;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.CHATS;
import static com.nsromapa.say.frenzapp_redesign.utils.PermissionUtils.checkStoragePermission;

public class BackServices extends Service {
    private RequestQueue requestQueue;
    private RequestQueue requestQueue1;
    private SQLiteDatabase db;
    private String TAG = "BackServices";

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
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_STICKY;
    }

    private void handleStart(Intent intent, int startId) {
        Log.e(TAG, "handleStart: " + TAG);
        String doWhat = Objects.requireNonNull(intent.getExtras()).getString("doWhat");
        String friendId = Objects.requireNonNull(intent.getExtras()).getString("friendId");
        assert doWhat != null;
        if (doWhat.equals("mark_all_as_read")) {

            Log.e(TAG, "handleStart: mark_all_as_read");
            if (!TextUtils.isEmpty(friendId)) {
                setAllMessagesSeen(friendId);
                Log.e(TAG, "handleStart: friendId " + friendId);

            }
        } else if (doWhat.equals("delete_chat")) {
            if (checkStoragePermission(getApplicationContext())) {
                db = MessageReaderDbHelper.getInstance(getApplicationContext())
                        .getWritableDatabase("somePass");
            }
            String deleteType = Objects.requireNonNull(intent.getExtras()).getString("deleteType");
            String friendName = Objects.requireNonNull(intent.getExtras()).getString("friendName");
            String friendImage = Objects.requireNonNull(intent.getExtras()).getString("friendImage");
            deleteChat(friendId, deleteType, friendName);
        }


    }


    private void deleteChat(String UserId, String deleteType, String friendName) {
        NotificationManager mNotifyManager;
        NotificationCompat.Builder mBuilder;
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContentTitle(friendName)
                .setContentText("Deleting chat...")
                .setSmallIcon(R.mipmap.ic_launcher_round);

        // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
        mBuilder.setProgress(100, 30, true);
        // Displays the progress bar for the first time.
        assert mNotifyManager != null;
        mNotifyManager.notify(Integer.parseInt(UserId), mBuilder.build());
        // Sleeps the thread, simulating an operation
        switch (deleteType) {
            case "both":
                deleteMessagesFromLocalDb(UserId);
                deleteChatList(UserId);
                break;
            case "messages_only":
                deleteMessagesFromLocalDb(UserId);
                break;
            case "chatList_only":
                deleteChatList(UserId);
                break;
            default:
                mBuilder.setContentText("Failed to delete chat...")
                        .setProgress(0, 0, false);
                mNotifyManager.notify(Integer.parseInt(UserId), mBuilder.build());
                break;
        }

        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, CHATS,
                response -> {
                    Log.e(TAG, "deleteChat: " + response);
//                    if (response != null && !response.isEmpty()) {
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            JSONArray jsonArray = object.getJSONArray("DeleteResponse");
//                            JSONObject deleteResponse = jsonArray.getJSONObject(0);
//
//                            if (deleteResponse.getString("response").equals("success")) {
//                                String message = deleteResponse.getString("message");
//
//
//                            } else if (deleteResponse.getString("response").equals("error")) {
//                                String message = deleteResponse.getString("message");
//                                if (message.equals("Unknown User")) {
//                                    Utils.checkUserExistence(getApplicationContext());
//                                }
//                                mBuilder.setContentText("Failed to delete chat...")
//                                        .setProgress(0, 0, false);
//                                mNotifyManager.notify(Integer.parseInt(UserId), mBuilder.build());
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    getApplicationContext().stopService(new Intent(getApplicationContext(), BackServices.class));
                },
                error -> {
//                    Log.e(TAG, "deleteChat: " + error);
//                    mBuilder.setContentText("Failed to delete chat...")
//                            .setProgress(0, 0, false);
//                    mNotifyManager.notify(Integer.parseInt(UserId), mBuilder.build());
                    getApplicationContext().stopService(new Intent(getApplicationContext(), BackServices.class));
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid(getApplicationContext()));
                post.put("other_user", UserId);
                post.put("delete_chat", "");
                post.put("deleteType", deleteType);
                return post;
            }
        };
        if (requestQueue1 == null) {
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue1.add(stringRequest1);


//        // Start a the operation in a background thread
//        new Thread(() -> {
//            int incr;
//            // Do the "lengthy" operation 20 times
//            for (incr = 0; incr <= 100; incr += 5) {
//                // Sets the progress indicator to a max value, the current completion percentage and "determinate" state
//                mBuilder.setProgress(100, 30, true);
//                // Displays the progress bar for the first time.
//                mNotifyManager.notify(Integer.parseInt(UserId), mBuilder.build());
//                // Sleeps the thread, simulating an operation
//                try {
//                    // Sleep for 1 second
//                    Thread.sleep(1 * 1000);
//                } catch (InterruptedException e) {
//                    Log.d("TAG", "sleep failure");
//                }
//            }
//            // When the loop is finished, updates the notification
//            mBuilder.setContentText("Download completed")
//                    // Removes the progress bar
//                    .setProgress(0, 0, false);
//            mNotifyManager.notify(Integer.parseInt(UserId), mBuilder.build());
//        }
//                // Starts the thread by calling the run() method in its Runnable
//        ).start();

    }

    private void deleteChatList(String userId) {
    }

    private void deleteMessagesFromLocalDb(String userId) {
        if (db != null) {
            Cursor cursor = db.rawQuery("DELETE FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME +
                    "' WHERE (" + MessagesReaderContract.MessageEntry.MESSAGE_SENDER_ID + " = '" + userId
                    + "' OR  " + MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_ID + " = '" + userId + "' )", null);
            while (cursor.moveToNext()) {}
            cursor.close();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancel(Integer.parseInt(userId));
        } else {
            if (checkStoragePermission(getApplicationContext()))
                db = MessageReaderDbHelper.getInstance(getApplicationContext()).getReadableDatabase("somePass");
            else
                Toasty.error(getApplicationContext(), "Enable your storage permission to continue...", Toasty.LENGTH_LONG, true).show();
        }
    }


    private void setAllMessagesSeen(String UserId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHATS,
                response -> getApplicationContext().stopService(new Intent(getApplicationContext(), BackServices.class)),
                error -> setAllMessagesSeen(UserId)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> post = new HashMap<>();
                post.put("user_id", Utils.getUserUid(getApplicationContext()));
                post.put("other_user", UserId);
                post.put("setAllMessagesSeen", "");
                return post;
            }
        };
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue.add(stringRequest);
    }
}
