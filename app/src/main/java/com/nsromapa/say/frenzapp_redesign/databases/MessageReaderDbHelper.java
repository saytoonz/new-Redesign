package com.nsromapa.say.frenzapp_redesign.databases;

import android.content.Context;
import android.os.Environment;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

public class MessageReaderDbHelper extends SQLiteOpenHelper {
    private static MessageReaderDbHelper instance;

    private static final int DATABASE_VERSION =1;
    private static final String DATABASE_NAME =  Environment.getExternalStorageDirectory()
            + File.separator + "FrenzApp"
            + File.separator +"sayt.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MessagesReaderContract.MessageEntry.TABLE_NAME + " (" +
                    MessagesReaderContract.MessageEntry._ID + " INTEGER PRIMARY KEY," +
                    MessagesReaderContract.MessageEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + "," +
                    MessagesReaderContract.MessageEntry.COLUMN_NAME_TITLE + TEXT_TYPE + "," +
                    MessagesReaderContract.MessageEntry.COLUMN_NAME_SUBTITLE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MessagesReaderContract.MessageEntry.TABLE_NAME;



    public MessageReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static public synchronized MessageReaderDbHelper getInstance(Context context){
        if (instance == null)
            instance = new MessageReaderDbHelper(context);
        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
