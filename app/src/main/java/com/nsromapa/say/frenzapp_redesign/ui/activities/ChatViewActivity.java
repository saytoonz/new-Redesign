package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.gifpack.giphy.GiphyGifProvider;
import com.nsromapa.say.emogifstickerkeyboard.EmoticonGIFKeyboardFragment;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.Emoticon;
import com.nsromapa.say.emogifstickerkeyboard.emoticons.EmoticonSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.gifs.Gif;
import com.nsromapa.say.emogifstickerkeyboard.gifs.GifSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.internal.sound.SoundSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.internal.sticker.StickerSelectListener;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.helpers.PicassoEngine;
import com.nsromapa.say.frenzapp_redesign.models.AudioChannel;
import com.nsromapa.say.frenzapp_redesign.models.AudioSampleRate;
import com.nsromapa.say.frenzapp_redesign.models.AudioSource;
import com.nsromapa.say.frenzapp_redesign.models.Message;
import com.nsromapa.say.frenzapp_redesign.ui.widget.ChatView;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter.pauseMediaPlayer;
import static com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter.resumeMediaPlayer;
import static com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter.stopMediaPlayer;
import static com.nsromapa.say.frenzapp_redesign.utils.Utils.downloadSoundAudio;

public class ChatViewActivity extends AppCompatActivity implements ChatView.RecordingListener, PullTransport.OnAudioChunkPulledListener {

    private static final String TAG = "ChatViewActivity";
    private EmoticonGIFKeyboardFragment mEmoticonGIFKeyboardFragment;
    private static InputMethodManager inputMethodManager;
    private MediaPlayer mMediaPlayer;

    public static int imagePickerRequestCode = 10;
    public static int SELECT_VIDEO = 11;
    public static int CAMERA_REQUEST = 12;
    public static int SELECT_AUDIO = 13;
    private ChatView chatView;
    boolean switchbool = true;
    List<Uri> mSelected;
    List<String> mSelectedLocal;

    private MaterialRippleLayout sendBtn;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view_test);

        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        chatView = findViewById(R.id.chatView);
        chatView.requestFocus();
        mSelected = new ArrayList<>();
        mSelectedLocal = new ArrayList<>();
        sendBtn = chatView.getSendMRL();
        messageEditText = chatView.getMessageET();
        emojiKeyboardToggler = chatView.getEmojiToggle();


        chatView.setRecordingListener(this);
        initializeEmojiGifStickerKeyBoard();
        chatView.getPauseResumeARL().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });
        source = AudioSource.MIC;
        channel = AudioChannel.STEREO;
        sampleRate = AudioSampleRate.HZ_16000;


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Objects.requireNonNull(messageEditText.getText()).toString().trim())) {
                    String message = messageEditText.getText().toString().trim();
                    messageEditText.setText("");
                    sendMessageText(message);
                }
            }
        });


        //Gallery button click listener
        chatView.setOnClickGalleryButtonListener(new ChatView.OnClickGalleryButtonListener() {
            @Override
            public void onGalleryButtonClick() {
                Matisse.from(ChatViewActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(9)
                        .theme(R.style.Matisse_Dracula)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .forResult(imagePickerRequestCode);
            }
        });

        //Video button click listener
        chatView.setOnClickVideoButtonListener(new ChatView.OnClickVideoButtonListener() {
            @Override
            public void onVideoButtonClick() {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                i.setType("video/*");
                startActivityForResult(i, SELECT_VIDEO);
            }
        });

        //Camera button click listener
        chatView.setOnClickCameraButtonListener(new ChatView.OnClickCameraButtonListener() {
            @Override
            public void onCameraButtonClicked() {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                file.delete();
                File file1 = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");

                Uri uri = FileProvider.getUriForFile(ChatViewActivity.this, getApplicationContext().getPackageName() + ".provider", file1);
                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });


        chatView.setOnClickAudioButtonListener(new ChatView.OnClickAudioButtonListener() {
            @Override
            public void onAudioButtonClicked() {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, SELECT_AUDIO);
            }
        });


    }


    private void sendMessageText(String body) {
        if (switchbool) {
            Message message = new Message();
            message.setBody(body);
            message.setMessageType(Message.MessageType.RightSimpleImage);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);

            switchbool = false;
        } else {
            Message message1 = new Message();
            message1.setBody(body);
            message1.setMessageType(Message.MessageType.LeftSimpleMessage);
            message1.setTime(getTime());
            message1.setUserName("Hodor");
            message1.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message1);

            switchbool = true;
        }
    }

    private void resumeRecording() {
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
//        stopPlaying();
        Utils.wait(100, new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    pauseRecording();
                } else {
                    resumeRecording();
                }
            }
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    recorderSecondsElapsed++;
                    chatView.getTimeText().setText(Utils.formatSeconds(recorderSecondsElapsed));
                }
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
                    }

                    @Override
                    public void onBackSpace() {
                    }
                });


        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment
                .GIFConfig(GiphyGifProvider.create(this, "564ce7370bf347f2b7c0e4746593c179"))
                .setGifSelectListener(new GifSelectListener() {
                    @Override
                    public void onGifSelected(@NonNull Gif gif) {
                        Log.d(TAG, "onGifSelected: " + gif.getGifUrl());
                        sendNewGIF(gif);
                    }
                });

        EmoticonGIFKeyboardFragment.STICKERConfig stickerConfig = new EmoticonGIFKeyboardFragment.STICKERConfig()
                .setStickerSelectedListener(new StickerSelectListener() {
                    @Override
                    public void onStickerSelectListner(@NonNull File sticker) {
                        Log.d(TAG, "stickerSelected: " + sticker);
                        sendNewSticker(sticker);
                    }
                });


        EmoticonGIFKeyboardFragment.SoundConfig soundConfig = new EmoticonGIFKeyboardFragment.SoundConfig()
                .setSoundImageSelectedListener(new SoundSelectListener() {
                    @Override
                    public void onSoundSelectListner(@NonNull File soundImage) {
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

                    }
                });

        mEmoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(findViewById(R.id.keyboard_container), emoticonConfig, gifConfig, stickerConfig, soundConfig);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.keyboard_container, mEmoticonGIFKeyboardFragment)
                .commit();
        mEmoticonGIFKeyboardFragment.hideKeyboard();

        emojiKeyboardToggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        messageEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEmoticonGIFKeyboardFragment.hideKeyboard();
                return false;
            }
        });
    }

    private void sendNewSound(File soundImage) {
        Log.e(TAG, "sendNewSound: " + soundImage);
        String realNameUrl = soundImage.getAbsolutePath().replace(".mp3", ".gif");
        String filename = realNameUrl.substring(realNameUrl.lastIndexOf("/") + 1);
        Log.e(TAG, "sendNewSound: " + filename);
        String localPath = Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/sounds/SoundImages/" + filename;
        if (switchbool) {
            Message message = new Message();
            message.setBody(filename);
            message.setMessageType(Message.MessageType.RightSound);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setImageLocalLocation(localPath);
            message.setSingleUrl("");
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();
            message.setBody(filename);
            message.setMessageType(Message.MessageType.LeftSound);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setImageLocalLocation(localPath);
            message.setSingleUrl("");
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
    }


    private void sendNewGIF(Gif gif) {
        String realNameUrl = gif.getGifUrl().replace("/giphy.gif", ".gif");
        String filename = realNameUrl.substring(realNameUrl.lastIndexOf("/") + 1);
        Log.e(TAG, "sendNewGIF: " + filename);
        String localPath = Environment.getExternalStorageDirectory().getPath() + "/FrenzApp/Media/gifs/" + filename;
        if (switchbool) {
            Message message = new Message();
            message.setBody(filename);
            message.setMessageType(Message.MessageType.RightGIF);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setImageLocalLocation(localPath);
            message.setSingleUrl(gif.getGifUrl());
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();
            message.setBody(filename);
            message.setMessageType(Message.MessageType.LeftGIF);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setImageLocalLocation(localPath);
            message.setSingleUrl(gif.getGifUrl());
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
    }


    private void sendNewSticker(File sticker) {
        Log.e(TAG, "sendNewSticker: " + sticker.getName());

        mSelected.clear();
        mSelected.add(Uri.parse(sticker.getAbsolutePath()));

        if (switchbool) {
            Message message = new Message();
            message.setBody(sticker.getName());
            message.setMessageType(Message.MessageType.RightSticker);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setImageLocalLocation(sticker.getAbsolutePath());
            message.setImageList(mSelected);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();
            message.setBody(sticker.getName());
            message.setMessageType(Message.MessageType.LeftSticker);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setImageLocalLocation(sticker.getAbsolutePath());
            message.setImageList(mSelected);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
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
                    mSelected = Matisse.obtainResult(data);
                    mSelectedLocal.clear();
                    for (int i = 0; i < mSelected.size(); i++) {
                        mSelectedLocal.add("1579225193965.jpg");
                    }

                    if (switchbool) {
                        if (mSelected.size() == 1) {
                            Message message = new Message();
//                            message.setBody(messageET.getText().toString().trim());
                            message.setBody("");
                            message.setMessageType(Message.MessageType.RightSingleImage);
                            message.setTime(getTime());
                            message.setUserName("Groot");
                            message.setImageList(mSelected);
                            message.setImageLocalLocation("");
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                            chatView.addMessage(message);
                            switchbool = false;
                        } else {

                            Message message = new Message();
                            message.setBody("");
                            message.setMessageType(Message.MessageType.RightMultipleImages);
                            message.setTime(getTime());
                            message.setUserName("Groot");
                            message.setImageList(mSelected);
                            message.setImageListNames(mSelectedLocal);
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                            chatView.addMessage(message);
                            switchbool = false;
                        }
                    } else {

                        if (mSelected.size() == 1) {
                            Message message = new Message();
//                            message.setBody(messageET.getText().toString().trim());
                            message.setBody("");
                            message.setMessageType(Message.MessageType.LeftSingleImage);
                            message.setTime(getTime());
                            message.setUserName("Hodor");
                            message.setImageList(mSelected);
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                            chatView.addMessage(message);
                            switchbool = true;
                        } else {

                            Message message = new Message();
//                            message.setBody(messageET.getText().toString().trim());
                            message.setBody("");
                            message.setMessageType(Message.MessageType.LeftMultipleImages);
                            message.setTime(getTime());
                            message.setUserName("Hodor");
                            message.setImageList(mSelected);
                            message.setImageListNames(mSelectedLocal);
                            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                            chatView.addMessage(message);
                            switchbool = true;
                        }

                    }
                }
                break;
            }
            case 11: {

                //Video Selection Result
                if (resultCode == RESULT_OK) {
                    if (switchbool) {
                        Message message = new Message();
                        message.setMessageType(Message.MessageType.RightVideo);
                        message.setTime(getTime());
                        message.setUserName("Groot");
                        message.setVideoUri(Uri.parse(getPathVideo(data.getData())));
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                        chatView.addMessage(message);
                        switchbool = false;
                    } else {
                        Message message = new Message();

                        message.setMessageType(Message.MessageType.LeftVideo);
                        message.setTime(getTime());
                        message.setUserName("Hodor");
                        message.setVideoUri(Uri.parse(getPathVideo(data.getData())));
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                        chatView.addMessage(message);
                        switchbool = true;
                    }
                }
                break;
            }
            case 12: {

                //Image Capture result

                if (resultCode == RESULT_OK) {


                    if (switchbool) {
                        Message message = new Message();
                        message.setMessageType(Message.MessageType.RightSingleImage);
                        message.setTime(getTime());
                        message.setUserName("Groot");
                        mSelected.clear();
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        //Uri of camera image
                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                        mSelected.add(uri);
                        message.setImageList(mSelected);
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
                        chatView.addMessage(message);
                        switchbool = false;
                    } else {
                        Message message = new Message();

                        message.setMessageType(Message.MessageType.LeftSingleImage);
                        message.setTime(getTime());
                        message.setUserName("Hodor");
                        mSelected.clear();
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        //Uri of camera image
                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                        mSelected.add(uri);
                        message.setImageList(mSelected);
                        message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
                        chatView.addMessage(message);
                        switchbool = true;
                    }
                }
                break;
            }
            case 13: {
                if (resultCode == RESULT_OK) {
                    sendAudio(data.getData(), "");
                }
                break;
            }
        }

    }


    private void sendAudio(Uri uri, String localPath) {
        if (uri == null) return;

        if (switchbool) {
            Message message = new Message();
            message.setMessageType(Message.MessageType.RightAudio);
            message.setTime(getTime());
            message.setUserName("Groot");
            message.setAudioUri(uri);
            message.setAudioLocalLocation(localPath);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/groot"));
            chatView.addMessage(message);
            switchbool = false;
        } else {
            Message message = new Message();

            message.setMessageType(Message.MessageType.LeftAudio);
            message.setTime(getTime());
            message.setUserName("Hodor");
            message.setAudioUri(uri);
            message.setAudioLocalLocation(localPath);
            message.setUserIcon(Uri.parse("android.resource://com.sayt.chatview/drawable/hodor"));
            chatView.addMessage(message);
            switchbool = true;
        }
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



        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
               if (mMediaPlayer != null){
                   mMediaPlayer.setOnCompletionListener(null);
                   mMediaPlayer.stop();
                   mMediaPlayer.release();
               }
            }
        });

        sendSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (mMediaPlayer != null){
                   mMediaPlayer.setOnCompletionListener(null);
                   mMediaPlayer.stop();
                   mMediaPlayer.release();
               }
                dialog.dismiss();
                sendNewSound(file);
            }
        });


        Glide.with(this)
                .asGif()
                .load(soundImage)
                .into(image);
        dialog.show();


        if (showDownload) {
            playSound.setVisibility(View.GONE);
            downloadLayout.setVisibility(View.VISIBLE);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadSoundAudio(ChatViewActivity.this, file.getName());
                }
            });
        }else{
            downloadLayout.setVisibility(View.GONE);
            playSound.setVisibility(View.VISIBLE);
            playSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMediaPlayer.start();
                }
            });

            mMediaPlayer = MediaPlayer.create(ChatViewActivity.this, Uri.fromFile(file));
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.start();
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        mEmoticonGIFKeyboardFragment.hideKeyboard();
    }

    @Override
    protected void onPause() {
        stopMediaPlayer();
        super.onPause();
    }


    @Override
    public void onRecordingStarted() {
        pauseMediaPlayer();
        isStillHold = true;
        Log.e(TAG, "onRecordingStarted");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isStillHold) {
                    resumeRecording();
                }
            }
        }, 1500);

    }

    @Override
    public void onRecordingLocked() {
        Log.e(TAG, "onRecordingLocked");
    }

    @Override
    public void onRecordingCompleted() {
        isStillHold = false;
        isRecordingPaused = false;
        Log.e(TAG, "onRecordingCompleted");
        if (recorderSecondsElapsed > 1) {
            stopRecording();
            Utils.wait(2000, new Runnable() {
                @Override
                public void run() {
                    sendAudio(Uri.parse(filePath), filePath);
                }
            });

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
    protected void onDestroy() {
        stopMediaPlayer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mEmoticonGIFKeyboardFragment == null || !mEmoticonGIFKeyboardFragment.handleBackPressed())
            super.onBackPressed();
    }

    @Override
    public void onAudioChunkPulled(AudioChunk audioChunk) {
        float amplitude = isRecording ? (float) audioChunk.maxAmplitude() : 0f;
//        visualizverHandler.onDataReceived(amplitude);
    }
}

