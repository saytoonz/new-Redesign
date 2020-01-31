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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.CHATS;

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







    protected void runThisFunction1(){
          SendMessage();
        //GetMessages
        //getCalls
        //  setNotifications
    }

    private void SendMessage() {
        Cursor cursor = db.rawQuery("SELECT * FROM '" + MessagesReaderContract.MessageEntry.TABLE_NAME +
                "' WHERE "+MessagesReaderContract.MessageEntry.MESSAGE_FOR+"='"+ Utils.getUserUid() +
                "' AND "+MessagesReaderContract.MessageEntry.MESSAGE_ID +" IS NULL ORDER BY "+
                MessagesReaderContract.MessageEntry._ID+" ASC", null);

        while (cursor.moveToNext()) {
            Message message = new Message();
            final  Map<String, String> post = new HashMap<>();

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



            post.put("local_id",String.valueOf(cursor.getInt(0)));
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

//            SendMessage();
            Log.e("BootCompleteService", "SendMessage: "+ message.getTime() );

        }

        cursor.close();
    }
    private void pushMessage(Map<String, String> post) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHATS, response -> {

        }, error -> {

        }){
            @Override
            protected Map<String, String> getParams(){
               return post;
            }
        };


        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        requestQueue.add(stringRequest);
    }

}
