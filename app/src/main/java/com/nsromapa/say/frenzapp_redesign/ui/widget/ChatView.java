package com.nsromapa.say.frenzapp_redesign.ui.widget;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.zagum.expandicon.ExpandIconView;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonEditText;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.MessageAdapter;
import com.nsromapa.say.frenzapp_redesign.models.Message;
import com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity.updateUserOnlineStatus;
import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.setUserOnlineStatus;

/**
 * * Created by say on 16/01/20.
 */

public class ChatView extends RelativeLayout {

    public static int Personal = 1;
    public static int Group = 2;

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    protected int mode = 1;
    protected boolean more = false;
    protected RelativeLayout mLayoutRoot, recordingLayout;
    protected FrameLayout keyboard_container;
    protected RecyclerView chatRV;
    protected LinearLayout sendLL;
    protected MaterialRippleLayout sendMRL, recordARL, stopARL, pauseResumeARL;
    protected HorizontalScrollView moreHSV;
    protected MaterialRippleLayout galleryMRL, videoMRL, cameraMRL, audioMRL, emojiToggle;
    protected ExpandIconView expandIconView;
    private CardView layoutLock;
    private ImageView imageViewLockArrow, imageViewLock, imageViewMic, dustin, dustin_cover;
    private View layoutDustin;
    private View layoutSlideCancel;
    private TextView timeText;
    private List<String> trackingLocalIds = new ArrayList<>();

    protected List<Message> messageList;
    protected static MessageAdapter messageAdapter;
    protected boolean showSenderLL = false;
    protected boolean showLeftBubbleIcon = true;
    protected boolean showRightBubbleIcon = true;
    protected boolean showSenderName = true;
    protected EmoticonEditText messageET;

    private int leftBubbleLayoutColor = R.color.colorAccent;
    private int rightBubbleLayoutColor = R.color.colorAccent1;
    private int leftBubbleTextColor = android.R.color.black;
    private int rightBubbleTextColor = android.R.color.white;
    private int chatViewBackgroundColor = android.R.color.white;
    private int timeTextColor = android.R.color.tab_indicator_text;
    private int senderNameTextColor = android.R.color.tab_indicator_text;
    private OnClickGalleryButtonListener onClickGalleryButtonListener;
    private OnClickVideoButtonListener onClickVideoButtonListener;
    private OnClickCameraButtonListener onClickCameraButtonListener;
    private OnClickAudioButtonListener onClickAudioButtonListener;

    public enum UserBehaviour {
        CANCELING,
        LOCKING,
        NONE
    }

    public enum RecordingBehaviour {
        CANCELED,
        LOCKED,
        LOCK_DONE,
        RELEASED
    }

    public interface RecordingListener {

        void onRecordingStarted();

        void onRecordingLocked();

        void onRecordingCompleted();

        void onRecordingCanceled();

    }


    private Animation animBlink, animJump, animJumpFast;

    private boolean isDeleting;
    private boolean stopTrackingAction;
    private Handler handler;

    private int audioTotalTime;
    private TimerTask timerTask;
    private Timer audioTimer;
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("m:ss", Locale.getDefault());

    private float lastX, lastY;
    private float firstX, firstY;

    private float directionOffset, cancelOffset, lockOffset;
    private float dp = 0;
    private boolean isLocked = false;

    private UserBehaviour userBehaviour = UserBehaviour.NONE;
    private RecordingListener recordingListener;


    public static void updater() {
        messageAdapter.notifyDataSetChanged();
    }


    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);


        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ChatView,
                0, 0);
        setAttributes(a);
        a.recycle();

    }


    public RecordingListener getRecordingListener() {
        return recordingListener;
    }

    public void setRecordingListener(RecordingListener recordingListener) {
        this.recordingListener = recordingListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRecording() {
        sendMRL.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateUserOnlineStatus(1);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    if (sendMRL.getVisibility() != View.GONE) {
                        sendMRL.setVisibility(View.GONE);
                        sendMRL.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                        recordARL.setVisibility(View.VISIBLE);
                        recordARL.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                    }
                } else {

                    updateUserOnlineStatus(2);
                    if (sendMRL.getVisibility() != View.VISIBLE && !isLocked) {
                        recordARL.setVisibility(View.GONE);
                        sendMRL.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                        sendMRL.setVisibility(View.VISIBLE);
                        sendMRL.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                    }
                }
            }
        });

        recordARL.setOnTouchListener((view, motionEvent) -> {

            if (isDeleting) {
                return true;
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                cancelOffset = (float) (recordARL.getX() / 2.8);
                lockOffset = (float) (recordARL.getX() / 2.5);

                if (firstX == 0) {
                    firstX = motionEvent.getRawX();
                }

                if (firstY == 0) {
                    firstY = motionEvent.getRawY();
                }

                startRecord();

            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                    || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording(RecordingBehaviour.RELEASED);
                }

            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

                if (stopTrackingAction) {
                    return true;
                }

                UserBehaviour direction = UserBehaviour.NONE;

                float motionX = Math.abs(firstX - motionEvent.getRawX());
                float motionY = Math.abs(firstY - motionEvent.getRawY());

                if (motionX > directionOffset &&
                        motionX > directionOffset &&
                        lastX < firstX && lastY < firstY) {

                    if (motionX > motionY && lastX < firstX) {
                        direction = UserBehaviour.CANCELING;

                    } else if (motionY > motionX && lastY < firstY) {
                        direction = UserBehaviour.LOCKING;
                    }

                } else if (motionX > motionY && motionX > directionOffset && lastX < firstX) {
                    direction = UserBehaviour.CANCELING;
                } else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                    direction = UserBehaviour.LOCKING;
                }

                if (direction == UserBehaviour.CANCELING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawY() + recordARL.getWidth() / 2 > firstY) {
                        userBehaviour = UserBehaviour.CANCELING;
                    }

                    if (userBehaviour == UserBehaviour.CANCELING) {
                        translateX(-(firstX - motionEvent.getRawX()));
                    }
                } else if (direction == UserBehaviour.LOCKING) {
                    if (userBehaviour == UserBehaviour.NONE || motionEvent.getRawX() + recordARL.getWidth() / 2 > firstX) {
                        userBehaviour = UserBehaviour.LOCKING;
                    }

                    if (userBehaviour == UserBehaviour.LOCKING) {
                        translateY(-(firstY - motionEvent.getRawY()));
                    }
                }

                lastX = motionEvent.getRawX();
                lastY = motionEvent.getRawY();
            }
            view.onTouchEvent(motionEvent);
            return true;
        });

        stopARL.setOnClickListener(v -> {
            isLocked = false;
            stopRecording(RecordingBehaviour.LOCK_DONE);
        });
    }

    private void translateY(float y) {
        if (y < -lockOffset) {
            locked();
            recordARL.setTranslationY(0);
            return;
        }

        if (layoutLock.getVisibility() != View.VISIBLE) {
            layoutLock.setVisibility(View.VISIBLE);
        }

        stopARL.setTranslationY(y);
        layoutLock.setTranslationY(y / 2);
        stopARL.setTranslationX(0);
    }

    private void translateX(float x) {
        if (x < -cancelOffset) {
            canceled();
            recordARL.setTranslationX(0);
            layoutSlideCancel.setTranslationX(0);
            return;
        }

        recordARL.setTranslationX(x);
        layoutSlideCancel.setTranslationX(x);
        layoutLock.setTranslationY(0);
        recordARL.setTranslationY(0);

        if (Math.abs(x) < imageViewMic.getWidth() / 2) {
            if (layoutLock.getVisibility() != View.VISIBLE) {
                layoutLock.setVisibility(View.VISIBLE);
            }
        } else {
            if (layoutLock.getVisibility() != View.GONE) {
                layoutLock.setVisibility(View.GONE);
            }
        }
    }

    private void locked() {
        stopTrackingAction = true;
        stopRecording(RecordingBehaviour.LOCKED);
        isLocked = true;
    }

    private void canceled() {
        stopTrackingAction = true;
        stopRecording(RecordingBehaviour.CANCELED);
    }

    private void stopRecording(RecordingBehaviour recordingBehaviour) {

        stopTrackingAction = true;
        firstX = 0;
        firstY = 0;
        lastX = 0;
        lastY = 0;

        userBehaviour = UserBehaviour.NONE;

        recordARL.animate().scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(100).setInterpolator(new LinearInterpolator()).start();
        layoutSlideCancel.setTranslationX(0);
        layoutSlideCancel.setVisibility(View.GONE);

        layoutLock.setVisibility(View.GONE);
        layoutLock.setTranslationY(0);
        imageViewLockArrow.clearAnimation();
        imageViewLock.clearAnimation();

        if (isLocked) {
            return;
        }

        if (recordingBehaviour == RecordingBehaviour.LOCKED) {
            stopARL.setVisibility(View.VISIBLE);
            pauseResumeARL.setVisibility(View.VISIBLE);
            recordARL.setEnabled(false);
            recordARL.setFocusable(false);

            if (recordingListener != null)
                recordingListener.onRecordingLocked();

        } else if (recordingBehaviour == RecordingBehaviour.CANCELED) {
            timeText.clearAnimation();
            timeText.setVisibility(View.INVISIBLE);
            imageViewMic.setVisibility(View.INVISIBLE);
            stopARL.setVisibility(View.GONE);
            stopARL.setVisibility(View.GONE);
            pauseResumeARL.setEnabled(true);
            recordARL.setFocusable(true);
            recordARL.setAlpha(0.7f);

            timerTask.cancel();

            delete();

            if (recordingListener != null)
                recordingListener.onRecordingCanceled();

            new Handler().postDelayed(() -> recordingLayout.setVisibility(View.GONE), 1500);

        } else if (recordingBehaviour == RecordingBehaviour.RELEASED || recordingBehaviour == RecordingBehaviour.LOCK_DONE) {
            timeText.clearAnimation();
            timeText.setVisibility(View.INVISIBLE);
            imageViewMic.setVisibility(View.INVISIBLE);
            stopARL.setVisibility(View.GONE);
            pauseResumeARL.setVisibility(View.GONE);
            recordARL.setEnabled(true);
            recordARL.setFocusable(true);
            recordARL.setAlpha(1f);

            timerTask.cancel();

            if (recordingListener != null)
                recordingListener.onRecordingCompleted();


            new Handler().postDelayed(() -> recordingLayout.setVisibility(View.GONE), 50);

        }
    }

    private void startRecord() {
        if (recordingListener != null)
            recordingListener.onRecordingStarted();

        stopTrackingAction = false;
        recordARL.animate().scaleXBy(1f).scaleYBy(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();

        recordingLayout.setVisibility(View.VISIBLE);
        timeText.setVisibility(View.VISIBLE);
        layoutLock.setVisibility(View.VISIBLE);
        layoutSlideCancel.setVisibility(View.VISIBLE);
        imageViewMic.setVisibility(View.VISIBLE);
        timeText.startAnimation(animBlink);
        imageViewLockArrow.clearAnimation();
        imageViewLock.clearAnimation();
        imageViewLockArrow.startAnimation(animJumpFast);
        imageViewLock.startAnimation(animJump);

        if (audioTimer == null) {
            audioTimer = new Timer();
            timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    // timeText.setText(timeFormatter.format(new Date(audioTotalTime * 1000)));
                    audioTotalTime++;
                });
            }
        };

        audioTotalTime = 0;
        audioTimer.schedule(timerTask, 0, 1000);
    }

    private void delete() {
        imageViewMic.setVisibility(View.VISIBLE);
        imageViewMic.setRotation(0);
        isDeleting = true;
        recordARL.setEnabled(false);

        handler.postDelayed(() -> {
            isDeleting = false;
            recordARL.setEnabled(true);
        }, 1250);

        imageViewMic.animate().translationY(-dp * 150).rotation(180).scaleXBy(0.6f)
                .scaleYBy(0.6f).setDuration(500).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        dustin.setTranslationX(-dp * 40);
                        dustin_cover.setTranslationX(-dp * 40);

                        dustin_cover.animate().translationX(0).rotation(-120).setDuration(350).setInterpolator(new DecelerateInterpolator()).start();

                        dustin.animate().translationX(0).setDuration(350).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                dustin.setVisibility(View.VISIBLE);
                                dustin_cover.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        imageViewMic.animate().translationY(0).scaleX(1).scaleY(1).setDuration(350).setInterpolator(new LinearInterpolator()).setListener(
                                new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        imageViewMic.setVisibility(View.INVISIBLE);
                                        imageViewMic.setRotation(0);

                                        dustin_cover.animate().rotation(0).setDuration(150).setStartDelay(50).start();
                                        dustin.animate().translationX(-dp * 40).setDuration(200).setStartDelay(250).setInterpolator(new DecelerateInterpolator()).start();
                                        dustin_cover.animate().translationX(-dp * 40).setDuration(200).setStartDelay(250).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        }).start();
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }
                        ).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }


    public MaterialRippleLayout getPauseResumeARL() {
        return pauseResumeARL;
    }

    public TextView getTimeText() {
        return timeText;
    }

    public EmoticonEditText getMessageET() {
        return messageET;
    }

    public MaterialRippleLayout getSendMRL() {
        return sendMRL;
    }

    public MaterialRippleLayout getEmojiToggle() {
        return emojiToggle;
    }


    protected void init(Context context) {

        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        //load rootView from xml
        View rootView = mLayoutInflater.inflate(R.layout.widget_chatview, this, true);

        //initialize UI
        mLayoutRoot = rootView.findViewById(R.id.rootRL);
        recordingLayout = rootView.findViewById(R.id.recordingLayout);
        keyboard_container = rootView.findViewById(R.id.keyboard_container);
        chatRV = rootView.findViewById(R.id.chatRV);
        sendLL = rootView.findViewById(R.id.sendLL);
        sendMRL = rootView.findViewById(R.id.sendMRL);
        recordARL = rootView.findViewById(R.id.recordARL);
        stopARL = rootView.findViewById(R.id.stopARL);
        pauseResumeARL = rootView.findViewById(R.id.pauseResumeMRL);
        moreHSV = rootView.findViewById(R.id.moreLL);
        messageET = rootView.findViewById(R.id.messageET);
        galleryMRL = rootView.findViewById(R.id.galleryMRL);
        videoMRL = rootView.findViewById(R.id.videoMRL);
        cameraMRL = rootView.findViewById(R.id.cameraMRL);
        audioMRL = rootView.findViewById(R.id.audioMRL);
        emojiToggle = rootView.findViewById(R.id.emojiToggle);
        expandIconView = rootView.findViewById(R.id.expandIconView);
        layoutLock = rootView.findViewById(R.id.layoutLock);
        imageViewLockArrow = rootView.findViewById(R.id.imageViewLockArrow);
        layoutDustin = rootView.findViewById(R.id.layoutDustin);

        timeText = rootView.findViewById(R.id.textViewTime);
        layoutSlideCancel = rootView.findViewById(R.id.layoutSlideCancel);
        imageViewMic = rootView.findViewById(R.id.imageViewMic);
        dustin = rootView.findViewById(R.id.dustin);
        dustin_cover = rootView.findViewById(R.id.dustin_cover);
        imageViewLock = rootView.findViewById(R.id.imageViewLock);

        handler = new Handler(Looper.getMainLooper());

        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources().getDisplayMetrics());

        animBlink = AnimationUtils.loadAnimation(getContext(),
                R.anim.blink);
        animJump = AnimationUtils.loadAnimation(getContext(),
                R.anim.jump);
        animJumpFast = AnimationUtils.loadAnimation(getContext(),
                R.anim.jump_fast);


        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(chatRV, messageList, context);
        WrapContentLinearLayoutManager layoutManager =
                new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        chatRV.setLayoutManager(layoutManager);
        chatRV.setItemAnimator(new ScaleInBottomAnimator(new OvershootInterpolator(1f)));
        chatRV.setAdapter(messageAdapter);

        messageET.setEmoticonProvider(SamsungEmoticonProvider.create());

        expandIconView.setState(ExpandIconView.LESS, false);

        expandIconView.setOnClickListener(view -> {
            if (more) {
                expandIconView.setState(ExpandIconView.LESS, true);
                moreHSV.setVisibility(View.GONE);
                more = false;
            } else {
                expandIconView.setState(ExpandIconView.MORE, true);
                moreHSV.setVisibility(View.VISIBLE);
                more = true;
            }
        });


        galleryMRL.setOnClickListener(view -> galleryButtonClicked());

        videoMRL.setOnClickListener(view -> videoButtonClicked());

        cameraMRL.setOnClickListener(view -> cameraButtonClicked());

        audioMRL.setOnClickListener(view -> audioButtonClicked());
        setupRecording();


    }

    protected void setAttributes(TypedArray attrs) {
        //set Attributes from xml
        showSenderLayout(attrs.getBoolean(R.styleable.ChatView_showSenderLayout, true));
        showLeftBubbleIcon(attrs.getBoolean(R.styleable.ChatView_showLeftBubbleIcon, showLeftBubbleIcon));
        showRightBubbleIcon(attrs.getBoolean(R.styleable.ChatView_showRightBubbleIcon, showRightBubbleIcon));
        setLeftBubbleLayoutColor(attrs.getColor(R.styleable.ChatView_leftBubbleLayoutColor, getResources().getColor(leftBubbleLayoutColor)));
        setRightBubbleLayoutColor(attrs.getColor(R.styleable.ChatView_rightBubbleLayoutColor, getResources().getColor(rightBubbleLayoutColor)));
        setLeftBubbleTextColor(attrs.getColor(R.styleable.ChatView_leftBubbleTextColor, getResources().getColor(leftBubbleTextColor)));
        setRightBubbleTextColor(attrs.getColor(R.styleable.ChatView_rightBubbleTextColor, getResources().getColor(rightBubbleTextColor)));
        setChatViewBackgroundColor(attrs.getColor(R.styleable.ChatView_chatViewBackgroundColor, mContext.getResources().getColor(chatViewBackgroundColor)));
        setTimeTextColor(attrs.getColor(R.styleable.ChatView_timeTextColor, mContext.getResources().getColor(timeTextColor)));
        setSenderNameTextColor(attrs.getColor(R.styleable.ChatView_senderNameTextColor, getResources().getColor(senderNameTextColor)));
        showSenderName(attrs.getBoolean(R.styleable.ChatView_showSenderName, showSenderName));
        setTextSize(attrs.getDimension(R.styleable.ChatView_textSize, 20));
        setChatViewBackgroundColor(attrs.getColor(R.styleable.ChatView_chatViewBackgroundColor, getResources().getColor(chatViewBackgroundColor)));
    }

    public interface OnClickGalleryButtonListener {
        void onGalleryButtonClick();
    }

    public interface OnClickVideoButtonListener {
        void onVideoButtonClick();
    }

    public interface OnClickCameraButtonListener {
        void onCameraButtonClicked();
    }

    public interface OnClickAudioButtonListener {
        void onAudioButtonClicked();
    }


    public void setOnClickGalleryButtonListener(OnClickGalleryButtonListener onClickGalleryButtonListener) {
        this.onClickGalleryButtonListener = onClickGalleryButtonListener;
    }

    public void setOnClickVideoButtonListener(OnClickVideoButtonListener onClickVideoButtonListener) {
        this.onClickVideoButtonListener = onClickVideoButtonListener;
    }

    public void setOnClickCameraButtonListener(OnClickCameraButtonListener onClickCameraButtonListener) {
        this.onClickCameraButtonListener = onClickCameraButtonListener;
    }

    public void setOnClickAudioButtonListener(OnClickAudioButtonListener onClickAudioButtonListener) {
        this.onClickAudioButtonListener = onClickAudioButtonListener;
    }


    public void galleryButtonClicked() {
        if (onClickGalleryButtonListener != null) {
            onClickGalleryButtonListener.onGalleryButtonClick();
        }
    }

    public void videoButtonClicked() {
        if (onClickVideoButtonListener != null) {
            onClickVideoButtonListener.onVideoButtonClick();
        }
    }

    public void cameraButtonClicked() {
        if (onClickCameraButtonListener != null) {
            onClickCameraButtonListener.onCameraButtonClicked();
        }
    }

    public void audioButtonClicked() {
        if (onClickAudioButtonListener != null) {
            onClickAudioButtonListener.onAudioButtonClicked();
        }
    }


    public List<Message> getMessageList() {
        return this.messageList;
    }

    //Use this method to add a message to chatview
    public void addMessage(Message message, boolean scrollToBottom) {
        if (!trackingLocalIds.contains(message.getLocal_id())) {
            messageList.add(0, message);
            messageAdapter.notifyItemInserted(0);
            trackingLocalIds.add(message.getLocal_id());

//        messageAdapter.notifyDataSetChanged();
            if (scrollToBottom)
                chatRV.smoothScrollToPosition(0);
            mLayoutRoot.invalidate();
        }
    }

    //Use this method to remove a message from chatview
    public void removeMessage(Message message) {
        messageList.remove(message);
        messageAdapter.notifyDataSetChanged();
    }

    //Use this method to clear all messages
    public void clearMessages() {
        messageList.clear();
        messageAdapter.notifyDataSetChanged();
    }

    public void updateMessageStatus(Message message) {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getLocal_id().equals(message.getLocal_id())) {
                messageList.get(i).setStatus(message.getStatus());
                messageAdapter.notifyDataSetChanged();
            }
        }
    }


    public void replaceMessage(Message message) {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getLocal_id().equals(message.getLocal_id())) {
                messageList.set(i, message);
                messageAdapter.notifyDataSetChanged();
            }
        }
    }


    //For hiding or showing sender layout which contains an edittext ,send button and many others features
    public void showSenderLayout(boolean b) {
        this.showSenderLL = b;
        if (b) {
            sendLL.setVisibility(VISIBLE);
        } else {
            sendLL.setVisibility(GONE);
        }
    }

    //For groups (showing or hiding sender name which appears on top of the message)
    public void showSenderName(boolean b) {
        messageAdapter.showSenderName(b);
    }

    //For showing or hiding sender icon in left
    public void showLeftBubbleIcon(boolean b) {
        messageAdapter.showLeftBubbleIcon(b);
    }

    //For showing or hiding receiver icon in right
    public void showRightBubbleIcon(boolean b) {
        messageAdapter.showRightBubbleIcon(b);
    }


    //For changing left bubble layout color
    public void setLeftBubbleLayoutColor(int color) {
        messageAdapter.setLeftBubbleLayoutColor(color);
    }

    //for changing right bubble layout color
    public void setRightBubbleLayoutColor(int color) {
        messageAdapter.setRightBubbleLayoutColor(color);
    }

    //For changing left bubble text color
    public void setLeftBubbleTextColor(int color) {
        messageAdapter.setLeftBubbleTextColor(color);
    }

    //For changing right bubble text color
    public void setRightBubbleTextColor(int color) {
        messageAdapter.setRightBubbleTextColor(color);
    }

    //For changing chatview background color
    public void setChatViewBackgroundColor(int color) {
        mLayoutRoot.setBackgroundColor(color);
    }

    //For changing time text color which is displayed (expands) when message is clicked
    public void setTimeTextColor(int color) {
        messageAdapter.setTimeTextColor(color);
    }

    //For changing typeface of text inside
    public void setTypeface(Typeface typeface) {
        messageAdapter.setTypeface(typeface);
    }

    public void setSenderNameTextColor(int color) {
        messageAdapter.setSenderNameTextColor(color);
    }

    public void setTextSize(float size) {
        messageAdapter.setTextSize(size);
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }

    }
}
