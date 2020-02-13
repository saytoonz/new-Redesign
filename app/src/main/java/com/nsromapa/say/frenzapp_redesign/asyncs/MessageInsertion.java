package com.nsromapa.say.frenzapp_redesign.asyncs;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nsromapa.say.frenzapp_redesign.databases.MessageReaderDbHelper;
import com.nsromapa.say.frenzapp_redesign.databases.MessagesReaderContract;
import com.nsromapa.say.frenzapp_redesign.models.Message;
import com.nsromapa.say.frenzapp_redesign.ui.widget.ChatView;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import net.sqlcipher.database.SQLiteDatabase;

public class MessageInsertion extends AsyncTask<String, String, String> {

    private SQLiteDatabase db;
    private ChatView chatView;
    private Message message;
    private String chatType;
    private String local_id;



    public MessageInsertion(Context context, ChatView chatView, Message message, String chatType) {
        this.db = MessageReaderDbHelper.getInstance(context).getWritableDatabase("somePass");
        this.chatView = chatView;
        this.message = message;
        this.chatType = chatType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.local_id = System.currentTimeMillis()+"_"+Utils.getUserUid();
        message.setStatus("0");
        message.setLocal_id(local_id);
        chatView.addMessage(message, true);

    }

    @Override
    protected String doInBackground(String... strings) {
        String thisUserJson = strings[0];
        String messageImageListsInString = "";
        String messageImageListNamesInString = "";
        if (message.getImageList() != null) {
            if (message.getImageList().size() > 0) {
                for (int i = 0; i < message.getImageList().size(); i++) {
                    messageImageListsInString = messageImageListsInString.concat(message.getImageList().get(i) + ",,");
                    messageImageListNamesInString = messageImageListNamesInString.concat(message.getImageList().get(i)
                            .substring(message.getImageList().get(i).lastIndexOf("/") + 1) + ",,");
                }
            }
        }

        ContentValues values = new ContentValues();
        values.put(MessagesReaderContract.MessageEntry.LOCAL_ID, local_id);
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_ID, "");
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_TYPE, message.getMessageType().toString());
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_ID, Utils.getUserUid());
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_NAME, Utils.getUserName());
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_SENDER_IMAGE, Utils.getUserImage());
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_ID, Utils.getUserInfoFromUserJSON(thisUserJson, "id"));
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_NAME, Utils.getUserInfoFromUserJSON(thisUserJson, "username"));
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_RECEIVER_IMAGE, Utils.getUserInfoFromUserJSON(thisUserJson, "image"));
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_TIME, String.valueOf(message.getTime()));
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_STATUS, String.valueOf(message.getStatus()));
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_BODY, String.valueOf(message.getBody()));
        values.put(MessagesReaderContract.MessageEntry.IMAGE_LIST, messageImageListsInString);
        values.put(MessagesReaderContract.MessageEntry.IMAGE_NAME_LIST, messageImageListNamesInString);
        values.put(MessagesReaderContract.MessageEntry.SINGLE_URL, String.valueOf(message.getSingleUrl()));
        values.put(MessagesReaderContract.MessageEntry.LOCAL_LOCATION, String.valueOf(message.getLocalLocation()));
        values.put(MessagesReaderContract.MessageEntry.MESSAGE_FOR, Utils.getUserUid());
        values.put(MessagesReaderContract.MessageEntry.CHAT_TYPE, String.valueOf(chatType));
        values.put(MessagesReaderContract.MessageEntry.REPLY_TO, String.valueOf(0));
        db.insert(MessagesReaderContract.MessageEntry.TABLE_NAME, null, values);


        Log.e("MessageInsertion", "doInBackground: messageImageListsInString  -------> " + messageImageListsInString);
        Log.e("MessageInsertion", "doInBackground: messageImageListNamesInString ----> " + messageImageListNamesInString);
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}
