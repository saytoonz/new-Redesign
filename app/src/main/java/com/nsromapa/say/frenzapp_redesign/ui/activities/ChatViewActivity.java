package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.gifpack.giphy.GiphyGifProvider;
import com.nsromapa.say.emogifstickerkeyboard.EmoticonGIFKeyboardFragment;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.Emoticon;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.EmoticonSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.gifs.Gif;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.asyncs.MessageInsertion;
import com.nsromapa.say.frenzapp_redesign.asyncs.UpdateOnlineStatus;
import com.nsromapa.say.frenzapp_redesign.helpers.PicassoEngine;
import com.nsromapa.say.frenzapp_redesign.models.AudioChannel;
import com.nsromapa.say.frenzapp_redesign.models.AudioSampleRate;
import com.nsromapa.say.frenzapp_redesign.models.AudioSource;
import com.nsromapa.say.frenzapp_redesign.models.Message;
import com.nsromapa.say.frenzapp_redesign.services.ChatViewActivityServices;
import com.nsromapa.say.frenzapp_redesign.ui.widget.ChatView;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import net.sqlcipher.database.SQLiteDatabase;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;
import omrecorder.AudioChunk;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter.pauseMediaPlayer;
import static com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter.resumeMediaPlayer;
import static com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter.stopMediaPlayer;
import static com.nsromapa.say.frenzapp_redesign.databases.MessageReaderDbHelper.DATABASE_LOCATION;
import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.setUserOnlineStatus;
import static com.nsromapa.say.frenzapp_redesign.ui.widget.ChatView.messageAdapter;
import static com.nsromapa.say.frenzapp_redesign.utils.PermissionUtils.checkMicrophonePermission;
import static com.nsromapa.say.frenzapp_redesign.utils.PermissionUtils.checkStoragePermission;
import static com.nsromapa.say.frenzapp_redesign.utils.Utils.downloadSoundAudio;

public class ChatViewActivity extends AppCompatActivity
        implements ChatView.RecordingListener, PullTransport.OnAudioChunkPulledListener {

    private static final String TAG = "ChatViewActivity";
    public static boolean message_isSelectionMode = false;

    private EmoticonGIFKeyboardFragment mEmoticonGIFKeyboardFragment;
    private static InputMethodManager inputMethodManager;
    private MediaPlayer mMediaPlayer;

    public static int imagePickerRequestCode = 10;
    public static int SELECT_VIDEO = 11;
    public static int CAMERA_REQUEST = 12;
    public static int SELECT_AUDIO = 13;
    private static ChatView chatView;
    private static TextView statusTV;
    List<String> mSelected;
    List<String> mSelectedLocal;
    private Message replyMessage;

    private EmoticonEditText messageEditText;
    private MaterialRippleLayout emojiKeyboardToggler;
    private Recorder recorder;

    private String filePath;
    private AudioSource source;
    private AudioChannel channel;
    private AudioSampleRate sampleRate;
    private int recorderSecondsElapsed;
    private boolean isRecording;
    private boolean isRecordingPaused = false;
    private boolean isStillHold = false;
    private Timer timer;
    private static String thisUserId, thisUserJson, chatType;
    private boolean backToMain;
    View view;
    Application application;
    private static Context context;
    private static HorizontalScrollView selectionMenuHolder;
    private static FancyButton selectionCounter;
    private static FloatingActionButton replySelected, forwardSelected, copySelected, deleteSelected, translateSelections, closeSelection;

    public static void updateUserStatus(String grabStatus, String otherUid, String lastSeen) {
        if (otherUid.equals(thisUserId)) {
            statusTV.setVisibility(View.VISIBLE);
            if (!grabStatus.isEmpty()) {
                if (grabStatus.equals("Online"))
                    statusTV.setText("Online");
                else if (grabStatus.equals("typing_" + Utils.getUserUid(context))) {
                    statusTV.setText("Typing…");
                } else if (grabStatus.contains("typing_")) {
                    statusTV.setText("online");
                } else {
                    if (!TextUtils.isEmpty(lastSeen))
                        statusTV.setText(lastSeen.trim());
                    else
                        statusTV.setText("Offline");
                }
            } else {
                statusTV.setVisibility(View.GONE);
            }
        }
    }

    public static void addMessage(Message message, boolean scrollToBottom) {
        chatView.addMessage(message, scrollToBottom);
        if (statusTV.getText().equals("Loading…")) {
            statusTV.setVisibility(View.GONE);
        }
    }

    public static void updateMessageStatus(Message message) {
        if (chatView != null)
            chatView.updateMessageStatus(message);
    }


    public static void updateUserOnlineStatus(int what) {
        if (what == 1) {
            new UpdateOnlineStatus(context, "typing_" + thisUserId, Utils.getUserUid(context)).execute();
        } else {
            new UpdateOnlineStatus(context, "Online", Utils.getUserUid(context)).execute();
        }
    }


    public static void disableSelectionMode() {
        messageAdapter.disableSelection();
        setSelectionCount("0");
        chatView.getSendLL().setVisibility(View.VISIBLE);
        chatView.getRecordARL().setVisibility(View.VISIBLE);
        selectionMenuHolder.setVisibility(View.GONE);
    }


    public static void showMenuSelectionView() {
        selectionMenuHolder.setVisibility(View.VISIBLE);
        chatView.getSendLL().setVisibility(View.INVISIBLE);
        chatView.getRecordARL().setVisibility(View.GONE);
    }


    public static void showHideMenusOnSelection(){
        if (messageAdapter.getSelectedList().size() != 1){
            replySelected.hide();
        }else{
            replySelected.show();
        }
    }




    private void setReplyWithMessage(Message message) {
        replyMessage = message;
        chatView.getReplyView().setVisibility(View.VISIBLE);
        chatView.getReplyCloseImageView().setOnClickListener(v -> {
            replyMessage = null;
            chatView.getReplyView().setVisibility(View.GONE);
        });

        if (message.getUserName().equals(Utils.getUserName(context))){
            chatView.getReplySenderName().setText("You");
        }else{
            chatView.getReplySenderName().setText(message.getUserName());
        }

        if (message.getMessageType().equals(Message.MessageType.RightSimpleMessage) ||
                message.getMessageType().equals(Message.MessageType.LeftSimpleMessage)){
            chatView.getReplyImageView().setVisibility(View.GONE);
            chatView.getReplyText().setText(message.getBody());


        }else  if (message.getMessageType().equals(Message.MessageType.LeftSingleImage) ||
                message.getMessageType().equals(Message.MessageType.RightSingleImage)){
            chatView.getReplyImageView().setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(replyMessage.getBody())){
                chatView.getReplyText().setText("\uD83D\uDDBC️ Image");
            }else{
                chatView.getReplyText().setText("\uD83D\uDDBC️ "+replyMessage.getBody());
            }

            if (!TextUtils.isEmpty(message.getLocalLocation())){
                Glide.with(context)
                        .load(message.getLocalLocation())
                        .into(chatView.getReplyImageView());
            }
            Glide.with(context)
                    .load(message.getSingleUrl())
                    .into(chatView.getReplyImageView());




        }else  if (message.getMessageType().equals(Message.MessageType.LeftGIF) ||
                message.getMessageType().equals(Message.MessageType.RightGIF)){
            chatView.getReplyImageView().setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(replyMessage.getBody())){
                chatView.getReplyText().setText("\uD83D\uDDBC️ Gif");
            }else{
                chatView.getReplyText().setText("\uD83D\uDDBC️ "+replyMessage.getBody());
            }

            if (!TextUtils.isEmpty(message.getLocalLocation())){
                Glide.with(context)
                        .load(message.getLocalLocation())
                        .into(chatView.getReplyImageView());
            }
            Glide.with(context)
                    .load(message.getSingleUrl())
                    .into(chatView.getReplyImageView());




        }else  if (message.getMessageType().equals(Message.MessageType.LeftSound) ||
                message.getMessageType().equals(Message.MessageType.RightSound)){
            chatView.getReplyImageView().setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(replyMessage.getBody())){
                chatView.getReplyText().setText("\uD83D\uDD09Sound");
            }else{
                chatView.getReplyText().setText("\uD83D\uDD09️ "+replyMessage.getBody());
            }

            if (!TextUtils.isEmpty(message.getLocalLocation())){
                Glide.with(context)
                        .load(message.getLocalLocation())
                        .into(chatView.getReplyImageView());
            }
            Glide.with(context)
                    .load(message.getSingleUrl())
                    .into(chatView.getReplyImageView());




        }else  if (message.getMessageType().equals(Message.MessageType.LeftSticker) ||
                message.getMessageType().equals(Message.MessageType.RightSticker)){
            chatView.getReplyImageView().setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(replyMessage.getBody())){
                chatView.getReplyText().setText("\uD83D\uDDBC️ Sticker");
            }else{
                chatView.getReplyText().setText("\uD83D\uDDBC️ "+replyMessage.getBody());
            }

            if (!TextUtils.isEmpty(message.getLocalLocation())){
                Glide.with(context)
                        .load(message.getLocalLocation())
                        .into(chatView.getReplyImageView());
            }
            Glide.with(context)
                    .load(message.getSingleUrl())
                    .into(chatView.getReplyImageView());





        }else  if (message.getMessageType().equals(Message.MessageType.LeftVideo) ||
                message.getMessageType().equals(Message.MessageType.RightVideo)){
            chatView.getReplyImageView().setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(replyMessage.getBody())){
                chatView.getReplyText().setText("\uD83C\uDF9E️️ Video");
            }else{
                chatView.getReplyText().setText("\uD83C\uDF9E️ "+replyMessage.getBody());
            }

            if (!TextUtils.isEmpty(message.getLocalLocation())){
                Glide.with(context)
                        .load(message.getLocalLocation())
                        .into(chatView.getReplyImageView());
            }
            Glide.with(context)
                    .load(message.getSingleUrl())
                    .into(chatView.getReplyImageView());





        }else  if (message.getMessageType().equals(Message.MessageType.LeftAudio) ||
                message.getMessageType().equals(Message.MessageType.RightAudio)){
            chatView.getReplyImageView().setVisibility(View.GONE);
            if (TextUtils.isEmpty(replyMessage.getBody())){
                chatView.getReplyText().setText("\uD83C\uDFA7 Audio");
            }else{
                chatView.getReplyText().setText("\uD83C\uDFA7️ "+replyMessage.getBody());
            }

        }
    }

    public static void setSelectionCount(String count) {
        selectionCounter.setText(count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        if (getIntent() != null) {
            if (getIntent().hasExtra("chatType") &&
                    getIntent().hasExtra("thisUserId") && getIntent().hasExtra("thisUserJson")) {
                chatType = getIntent().getStringExtra("chatType");
                thisUserId = getIntent().getStringExtra("thisUserId");
                thisUserJson = getIntent().getStringExtra("thisUserJson");
                backToMain = getIntent().getBooleanExtra("backToMain", false);
            } else {
                finish();
            }
        } else {
            finish();
        }

        if (checkStoragePermission(getApplicationContext())) {
            if (!(new File(DATABASE_LOCATION).exists()))
                new File(DATABASE_LOCATION).mkdirs();
        } else {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toasty.error(getApplicationContext(), "Storage Permission needed!", Toasty.LENGTH_LONG).show();
                            finish();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();

        }

        SQLiteDatabase.loadLibs(this);

        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        chatView = findViewById(R.id.chatView);
        statusTV = findViewById(R.id.statusTV);

        selectionMenuHolder = findViewById(R.id.selectionMenuHolder);
        selectionCounter = findViewById(R.id.selectionCounter);
        replySelected = findViewById(R.id.replySelected);
        deleteSelected = findViewById(R.id.deleteSelected);
        forwardSelected = findViewById(R.id.forwardSelected);
        copySelected = findViewById(R.id.copySelected);
        translateSelections = findViewById(R.id.translateSelections);
        closeSelection = findViewById(R.id.closeSelection);

        chatView.requestFocus();
        mSelected = new ArrayList<>();
        mSelectedLocal = new ArrayList<>();
        MaterialRippleLayout sendBtn = chatView.getSendMRL();
        messageEditText = chatView.getMessageET();
        emojiKeyboardToggler = chatView.getEmojiToggle();

        context = this;
        chatView.setRecordingListener(this);
        initializeEmojiGifStickerKeyBoard();
        chatView.getPauseResumeARL().setOnClickListener(v -> toggleRecording());
        source = AudioSource.MIC;
        channel = AudioChannel.STEREO;
        sampleRate = AudioSampleRate.HZ_16000;


        closeSelection.setOnClickListener(v -> disableSelectionMode());
        copySelected.setOnClickListener(v -> {
            StringBuilder textToCopy = new StringBuilder();
            List<String> selectedList = messageAdapter.getSelectedList();
            for (int i = 0; i < selectedList.size(); i++) {
               String messageText =  messageAdapter.getMessageText(Integer.parseInt(selectedList.get(i)));
               if (!TextUtils.isEmpty(messageText)){
                   textToCopy.append("\n").append(messageText);
               }
            }
            disableSelectionMode();
            if (!TextUtils.isEmpty(textToCopy)){
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FrenzApp", textToCopy);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
            }else{
                Toasty.warning(this, "No text found....").show();
            }
        });



        replySelected.setOnClickListener(v -> {
            List<String> selectedList = messageAdapter.getSelectedList();
            for (int i  = 0; i < selectedList.size(); i++){
                Message message = messageAdapter.getMessageItem(Integer.parseInt(selectedList.get(i)));
                if (message != null){
                    setReplyWithMessage(message);
                }
            }
            disableSelectionMode();
        });




        sendBtn.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(Objects.requireNonNull(messageEditText.getText()).toString().trim())) {
                String message = messageEditText.getText().toString().trim();
                messageEditText.setText("");
                sendMessageText(message);
            }
        });

        //Gallery button click listener
        chatView.setOnClickGalleryButtonListener(() -> Matisse.from(ChatViewActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(9)
                .theme(R.style.Matisse_Dracula)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(imagePickerRequestCode));

        //Video button click listener
        chatView.setOnClickVideoButtonListener(() -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            i.setType("video/*");
            startActivityForResult(i, SELECT_VIDEO);
        });

        //Camera button click listener
        chatView.setOnClickCameraButtonListener(() -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
            file.delete();
            File file1 = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");

            Uri uri = FileProvider.getUriForFile(ChatViewActivity.this,
                    getApplicationContext().getPackageName() + ".provider", file1);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });


        chatView.setOnClickAudioButtonListener(() -> {
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, SELECT_AUDIO);
        });

    }


    private void insertSthToDb(Message message) {
        if (replyMessage != null){
            message.setReplyMessage(message);
            replyMessage = null;
        }
        chatView.getReplyView().setVisibility(View.GONE);
        new MessageInsertion(this, chatView, message, chatType).execute(thisUserJson);
    }

    private void sendMessageText(String body) {
        Message message = new Message();
        message.setBody(body);
        message.setMessageType(Message.MessageType.RightSimpleMessage);
        message.setTime(getTime());
        message.setUserName(Utils.getUserName(context));
        message.setUserIcon(Utils.getUserImage(context));
        insertSthToDb(message);

    }

    private void sendNewSound(File soundImage) {
        Log.e(TAG, "sendNewSound: " + soundImage);
        String realNameUrl = soundImage.getAbsolutePath().replace(".mp3", ".gif");
        String filename = realNameUrl.substring(realNameUrl.lastIndexOf("/") + 1);
        Log.e(TAG, "sendNewSound: " + filename);
        String localPath = Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/sounds/SoundImages/" + filename;

        Message message = new Message();
        message.setBody(filename);
        message.setMessageType(Message.MessageType.RightSound);
        message.setTime(getTime());
        message.setUserName(Utils.getUserName(context));
        message.setLocalLocation(localPath);
        message.setSingleUrl("");
        message.setUserIcon(Utils.getUserImage(context));
        insertSthToDb(message);
    }


    private void sendNewGIF(Gif gif) {
        String realNameUrl = gif.getGifUrl().replace("/giphy.gif", ".gif");
        String filename = realNameUrl.substring(realNameUrl.lastIndexOf("/") + 1);
        Log.e(TAG, "sendNewGIF: " + filename);
        String localPath = Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/gifs/" + filename;

        Message message = new Message();
        message.setBody(filename);
        message.setMessageType(Message.MessageType.RightGIF);
        message.setTime(getTime());
        message.setUserName(Utils.getUserName(context));
        message.setLocalLocation(localPath);
        message.setSingleUrl(gif.getGifUrl());
        message.setUserIcon(Utils.getUserImage(context));
//            chatView.addMessage(message);
        insertSthToDb(message);
    }


    private void sendNewSticker(File sticker) {
        Message message = new Message();
        message.setBody(sticker.getName());
        message.setMessageType(Message.MessageType.RightSticker);
        message.setTime(getTime());
        message.setUserName(Utils.getUserName(context));
        message.setLocalLocation(sticker.getAbsolutePath());
        message.setSingleUrl(sticker.getAbsolutePath());
        message.setUserIcon(Utils.getUserImage(context));
        insertSthToDb(message);
    }

    public String getTime() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String time = mdformat.format(calendar.getTime());
        return time;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10: {

                //Image Selection result
                if (resultCode == RESULT_OK) {
                    List<Uri> list = Matisse.obtainResult(data);
                    mSelectedLocal.clear();
                    mSelected.clear();
                    for (int i = 0; i < list.size(); i++) {
                        File file = new File(getPathImage(list.get(i)));
                        mSelected.add(file.getAbsolutePath());
                        mSelectedLocal.add(file.getName());
                    }

                    if (mSelected.size() == 1) {
                        Message message = new Message();
                        message.setBody("");
                        message.setMessageType(Message.MessageType.RightSingleImage);
                        message.setTime(getTime());
                        message.setUserName(Utils.getUserName(context));
                        message.setSingleUrl(mSelected.get(0));
                        message.setLocalLocation(mSelected.get(0));
                        message.setUserIcon(Utils.getUserImage(context));
                        insertSthToDb(message);
                    } else {

                        Message message = new Message();
                        message.setBody("");
                        message.setMessageType(Message.MessageType.RightMultipleImages);
                        message.setTime(getTime());
                        message.setUserName(Utils.getUserName(context));
                        message.setImageList(mSelected);
                        message.setImageListNames(mSelectedLocal);
                        message.setUserIcon(Utils.getUserImage(context));
                        insertSthToDb(message);
                    }

                }
                break;
            }
            case 11: {

                //Video Selection Result
                if (resultCode == RESULT_OK) {
                    Message message = new Message();
                    message.setMessageType(Message.MessageType.RightVideo);
                    message.setTime(getTime());
                    message.setUserName(Utils.getUserName(context));
                    message.setSingleUrl(getPathVideo(Objects.requireNonNull(data.getData())));
                    message.setUserIcon(Utils.getUserImage(context));
                    insertSthToDb(message);
                }
                break;
            }
            case 12: {

                //Image Capture result

                if (resultCode == RESULT_OK) {
                    Message message = new Message();
                    message.setMessageType(Message.MessageType.RightSingleImage);
                    message.setTime(getTime());
                    message.setUserName(Utils.getUserName(context));
                    File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                    message.setSingleUrl(file.getAbsolutePath());
                    message.setUserIcon(Utils.getUserImage(context));
                    insertSthToDb(message);
                }
                break;
            }
            case 13: {
                if (resultCode == RESULT_OK) {
                    sendAudio(Objects.requireNonNull(data.getData()).toString(), getPathAudio(data.getData()));
                }
                break;
            }
        }

    }


    private void sendAudio(String urlPath, String localPath) {
        if (urlPath == null || TextUtils.isEmpty(urlPath)) return;
        Message message = new Message();
        message.setMessageType(Message.MessageType.RightAudio);
        message.setTime(getTime());
        message.setUserName(Utils.getUserName(context));
        message.setSingleUrl(urlPath);
        message.setLocalLocation(localPath);
        message.setUserIcon(Utils.getUserImage(context));
        insertSthToDb(message);
    }


    public String getPathVideo(Uri uri) {
        System.out.println("getpath " + uri.toString());
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    public String getPathAudio(Uri uri) {
        System.out.println("getpath " + uri.toString());
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        int columnIndex = cursor.getColumnIndex(projection[0]);
        cursor.moveToFirst();
        if (cursor != null) {
            return cursor.getString(columnIndex);
        } else return null;
    }

    public String getPathImage(Uri uri) {
        System.out.println("getpath " + uri.toString());
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        int columnIndex = cursor.getColumnIndex(projection[0]);
        cursor.moveToFirst();
        if (cursor != null) {
            return cursor.getString(columnIndex);
        } else return null;
    }


    @SuppressLint("SetTextI18n")
    private void playAndSendAudio(final File file, boolean showDownload) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.item_sound_option_dialog);

        TextView name = dialog.findViewById(R.id.name);
        ImageView image = dialog.findViewById(R.id.goProDialogImage);
        ImageView sendSound = dialog.findViewById(R.id.sendSound);
        ImageView playSound = dialog.findViewById(R.id.playSound);
        ImageView download = dialog.findViewById(R.id.downloadSound);
        LinearLayout downloadLayout = dialog.findViewById(R.id.downloadLayout);
        final File soundImage = new File(file
                .getAbsolutePath().replace("SoundAudios", "SoundImages")
                .replace(".mp3", ".gif"));

        name.setText("                                                       " +
                "                                                              ");


        dialog.setOnCancelListener(dialog1 -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });

        sendSound.setOnClickListener(v -> {
            if (mMediaPlayer != null) {
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            dialog.dismiss();
            sendNewSound(file);
        });


        Glide.with(this)
                .asGif()
                .load(soundImage)
                .into(image);
        dialog.show();


        if (showDownload) {
            playSound.setVisibility(View.GONE);
            downloadLayout.setVisibility(View.VISIBLE);
            download.setOnClickListener(v -> downloadSoundAudio(ChatViewActivity.this, file.getName()));
        } else {
            downloadLayout.setVisibility(View.GONE);
            playSound.setVisibility(View.VISIBLE);
            playSound.setOnClickListener(v -> mMediaPlayer.start());

            mMediaPlayer = MediaPlayer.create(ChatViewActivity.this, Uri.fromFile(file));
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.start();
        }
    }


    private void resumeRecording() {
        if (!checkMicrophonePermission(getApplicationContext())) {
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.RECORD_AUDIO)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toasty.error(getApplicationContext(), "Microphone Permission needed!", Toasty.LENGTH_LONG).show();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
            return;
        }

        if (!isRecordingPaused) {
            String location = Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/Audios/Sent/Rec/";
            File dir = new File(location);
            if (!dir.exists())
                dir.mkdirs();
            filePath = location + "/FRZMGS" + System.currentTimeMillis() + ".wav";
        }
        isRecording = true;
        ImageView playPauseIcon = chatView.getPauseResumeARL().findViewById(R.id.pause_resume_imageView);
        playPauseIcon.setImageResource(R.drawable.pause_microphone_100);

        if (recorder == null) {
            chatView.getTimeText().setText("00:00");

            recorder = OmRecorder.wav(
                    new PullTransport.Default(Utils.getMic(source, channel, sampleRate), this),
                    new File(filePath));
        }
        isRecordingPaused = false;
        recorder.resumeRecording();

        startTimer();
    }


    private void pauseRecording() {
        isRecording = false;
        isRecordingPaused = true;

        ImageView playPauseIcon = chatView.getPauseResumeARL().findViewById(R.id.pause_resume_imageView);
        playPauseIcon.setImageResource(R.drawable.play_microphone_100);

        if (recorder != null) {
            recorder.pauseRecording();
        }

        stopTimer();
    }

    private void stopRecording() {
        isRecordingPaused = false;
        recorderSecondsElapsed = 0;
        if (recorder != null) {
            recorder.stopRecording();
            recorder = null;
        }
        stopTimer();
    }

    public void toggleRecording() {
        final Handler HANDLER = new Handler();
        Utils.wait(100, () -> {
            if (isRecording) {
                pauseRecording();
            } else {
                resumeRecording();
            }
        }, HANDLER);
    }


    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void updateTimer() {
        runOnUiThread(() -> {
            if (isRecording) {
                recorderSecondsElapsed++;
                chatView.getTimeText().setText(Utils.formatSeconds(recorderSecondsElapsed));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeEmojiGifStickerKeyBoard() {
        EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                .setEmoticonProvider(SamsungEmoticonProvider.create())
                .setEmoticonSelectListener(new EmoticonSelectListener() {
                    @Override
                    public void emoticonSelected(Emoticon emoticon) {
                        Log.d(TAG, "emoticonSelected: " + emoticon.getUnicode());
                        messageEditText.append(emoticon.getUnicode(),
                                messageEditText.getSelectionStart(),
                                messageEditText.getSelectionEnd());
                        if (!messageEditText.hasFocus())
                            messageEditText.requestFocus();
                    }

                    @Override
                    public void onBackSpace() {
                    }
                });


        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment
                .GIFConfig(GiphyGifProvider.create(this, "564ce7370bf347f2b7c0e4746593c179"))
                .setGifSelectListener(gif -> {
                    Log.d(TAG, "onGifSelected: " + gif.getGifUrl());
                    sendNewGIF(gif);
                });

        EmoticonGIFKeyboardFragment.STICKERConfig stickerConfig = new EmoticonGIFKeyboardFragment.STICKERConfig()
                .setStickerSelectedListener(sticker -> {
                    Log.d(TAG, "stickerSelected: " + sticker);
                    sendNewSticker(sticker);
                });


        EmoticonGIFKeyboardFragment.SoundConfig soundConfig = new EmoticonGIFKeyboardFragment.SoundConfig()
                .setSoundImageSelectedListener(soundImage -> {
                    Log.d(TAG, "soundImage Selected: " + soundImage.getName());

                    String soundName = soundImage.getName().replace(".png", ".mp3").replace(".gif", ".mp3");
                    File file = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/FrenzApp/Media/sounds/SoundAudios/" + soundName);
                    if (!(file.exists()) || (!file.isFile())) {
                        playAndSendAudio(file, true);
                    } else if (file.exists() && file.isFile()) {
                        playAndSendAudio(file, false);
                    } else {
                        Toast.makeText(ChatViewActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                    }

                });

        mEmoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(findViewById(R.id.keyboard_container), emoticonConfig, gifConfig, stickerConfig, soundConfig);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.keyboard_container, mEmoticonGIFKeyboardFragment)
                .commit();
        mEmoticonGIFKeyboardFragment.hideKeyboard();

        emojiKeyboardToggler.setOnClickListener(view -> {
            if (mEmoticonGIFKeyboardFragment.isOpen()) {

                mEmoticonGIFKeyboardFragment.toggle();
                ImageView imageView = emojiKeyboardToggler.findViewById(R.id.emoji_keyboad_iv);
                imageView.setImageResource(R.drawable.ic_smiley);

                if (inputMethodManager != null) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            } else {
                //Check if keyboard is open and close it if it is
                if (inputMethodManager.isAcceptingText()) {
                    inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
                }

                ImageView imageView = emojiKeyboardToggler.findViewById(R.id.emoji_keyboad_iv);
                imageView.setImageResource(R.drawable.sp_ic_keyboard);
                mEmoticonGIFKeyboardFragment.toggle();
            }
        });

        messageEditText.setOnTouchListener((v, event) -> {
            mEmoticonGIFKeyboardFragment.hideKeyboard();
            return false;
        });
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onAudioChunkPulled(AudioChunk audioChunk) {
        float amplitude = isRecording ? (float) audioChunk.maxAmplitude() : 0f;
//        visualizverHandler.onDataReceived(amplitude);
    }


    @Override
    public void onRecordingStarted() {
        pauseMediaPlayer();
        isStillHold = true;
        Log.e(TAG, "onRecordingStarted");
        new Handler().postDelayed(() -> {
            if (isStillHold) {
                resumeRecording();
            }
        }, 1500);

    }

    @Override
    public void onRecordingLocked() {
        Log.e(TAG, "onRecordingLocked");
    }

    @Override
    public void onRecordingCompleted() {
        final Handler HANDLER = new Handler();
        isStillHold = false;
        isRecordingPaused = false;
        Log.e(TAG, "onRecordingCompleted");
        if (recorderSecondsElapsed > 1) {
            stopRecording();
            Utils.wait(1000, () -> sendAudio(filePath, filePath), HANDLER);

        }

        resumeMediaPlayer();
    }

    @Override
    public void onRecordingCanceled() {
        isStillHold = false;
        isRecordingPaused = false;
        resumeMediaPlayer();
        Log.e(TAG, "onRecordingCanceled");
    }


    @Override
    protected void onStart() {
        super.onStart();
        mEmoticonGIFKeyboardFragment.hideKeyboard();
        Intent intent1 = new Intent(this, ChatViewActivityServices.class);
        intent1.putExtra("thisUserId", thisUserId);
        startService(intent1);
    }


    @Override
    public void onResume() {
        super.onResume();
        setUserOnlineStatus(this, getResources().getString(R.string.online), Utils.getUserUid(context));
    }

    @Override
    protected void onPause() {
        stopMediaPlayer();
        setUserOnlineStatus(this, getResources().getString(R.string.offline), Utils.getUserUid(context));
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        stopMediaPlayer();
        stopService(new Intent(this, ChatViewActivityServices.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mEmoticonGIFKeyboardFragment == null || !mEmoticonGIFKeyboardFragment.handleBackPressed()) {
            if (backToMain && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isMainActivity", false)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (message_isSelectionMode) {
                disableSelectionMode();
            } else {
                super.onBackPressed();
            }
        }

    }
}

