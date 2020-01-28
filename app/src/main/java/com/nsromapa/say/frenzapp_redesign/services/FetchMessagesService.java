package com.nsromapa.say.frenzapp_redesign.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.RequestQueue;
import com.nsromapa.say.frenzapp_redesign.databases.MessageReaderDbHelper;
import com.nsromapa.say.frenzapp_redesign.databases.MessagesReaderContract;
import com.nsromapa.say.frenzapp_redesign.models.Message;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity.addMessage;


public class FetchMessagesService extends Service {
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

        getMessages();
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                getMessages();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }




    private void getMessages() {
        SQLiteDatabase db = MessageReaderDbHelper.getInstance(getApplicationContext())
                .getReadableDatabase("somePass");
        Cursor cursor = db.rawQuery("SELECT * FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME + "';", null);

            while (cursor.moveToNext()) {
                Message message = new Message();
                String imagesString = cursor.getString(11);
                List<String> imageList = new ArrayList<>();
                if (imagesString != null && !TextUtils.isEmpty(imagesString)){
                    String[] images = imagesString.split(",,");
                    imageList = new ArrayList<>(Arrays.asList(images));
                }

                String nameString = cursor.getString(12);
                List<String> imageNamesList = new ArrayList<>();
                if (nameString != null && !TextUtils.isEmpty(nameString)){
                    String[] names = nameString.split(",,");
                    imageNamesList = new ArrayList<>(Arrays.asList(names));
                }


                message.setLocal_id(String.valueOf(cursor.getInt(0)));
                message.setId(cursor.getString(1));
                message.setMessageType(Message.MessageType.valueOf(cursor.getString(2)));
                message.setUserName(cursor.getString(3));
                message.setUserIcon(cursor.getString(4));
                message.setTime(cursor.getString(8));
                message.setStatus(cursor.getString(9));
                message.setBody(cursor.getString(10));
                message.setImageList(imageList);
                message.setImageListNames(imageNamesList);
                message.setSingleUrl(cursor.getString(12));
                message.setLocalLocation(cursor.getString(13));

                addMessage(message, true);
            }

    }

}
