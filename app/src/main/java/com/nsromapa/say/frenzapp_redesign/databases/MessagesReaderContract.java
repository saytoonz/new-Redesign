package com.nsromapa.say.frenzapp_redesign.databases;

public class MessagesReaderContract {
    public MessagesReaderContract() {
    }

    public static abstract class MessageEntry {
        public static final String TABLE_NAME = "single_messages";
        public static final String _ID = "local_id";
        public static final String MESSAGE_ID = "message_id";
        public static final String MESSAGE_TYPE = "message_type";
        public static final String MESSAGE_SENDER_ID = "from_id";
        public static final String MESSAGE_SENDER_NAME = "from_name";
        public static final String MESSAGE_SENDER_IMAGE = "from_image";
        public static final String MESSAGE_RECEIVER_ID = "to_id";
        public static final String MESSAGE_RECEIVER_NAME = "to_name";
        public static final String MESSAGE_RECEIVER_IMAGE = "to_image";
        public static final String MESSAGE_TIME = "time";
        public static final String MESSAGE_STATUS = "status";
        public static final String MESSAGE_BODY = "body";
        public static final String IMAGE_LIST = "image_list";
        public static final String IMAGE_NAME_LIST = "image_list_names";
        public static final String SINGLE_URL = "single_url";
        public static final String LOCAL_LOCATION = "local_location";
        public static final String MESSAGE_FOR = "owner_id";
    }
}
