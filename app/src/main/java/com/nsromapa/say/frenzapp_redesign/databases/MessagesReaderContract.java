package com.nsromapa.say.frenzapp_redesign.databases;

public class MessagesReaderContract {
    public MessagesReaderContract(){}

    public static abstract class MessageEntry{
        public static final String _ID = "id";
        public static final String TABLE_NAME = "singlemessages";
        public static final String COLUMN_NAME_ENTRY_ID = "news_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }
}
