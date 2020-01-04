package com.nsromapa.say.frenzapp_redesign.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.danikula.videocache.HttpProxyCacheServer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.nsromapa.say.frenzapp_redesign.App;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.MultipleVideos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PostVideosAdapter extends PagerAdapter implements OnPreparedListener {


    private ArrayList<MultipleVideos> VIDEOS;
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private VideoView videoView;
    private View bigView;
    private ImageView soundOnView, soundOffView;
    private ImageView playVideo, pauseVideo;
    private boolean isVolumed = false;


    public PostVideosAdapter(Context context, Activity activity, ArrayList<MultipleVideos> VIDEOS) {
        this.context = context;
        this.VIDEOS = VIDEOS;
        this.activity = activity;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return VIDEOS.size();
    }


    private void animatePhotoLike(final View vBgLike, final ImageView ivLike) {
        vBgLike.setVisibility(View.VISIBLE);
        ivLike.setVisibility(View.VISIBLE);

        vBgLike.setScaleY(0.1f);
        vBgLike.setScaleX(0.1f);
        vBgLike.setAlpha(1f);
        ivLike.setScaleY(0.1f);
        ivLike.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(300);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(300);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(300);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetLikeAnimationState(vBgLike, ivLike);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        animatorSet.start();

    }

    private void resetLikeAnimationState(View vBgLike, ImageView ivLike) {
        vBgLike.setVisibility(View.INVISIBLE);
        ivLike.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        final View videoLayout = inflater.inflate(R.layout.item_viewpager_video, view, false);

        assert videoLayout != null;
        bigView = videoLayout.findViewById(R.id.vBigView);
        soundOnView = videoLayout.findViewById(R.id.soundOnView);
        soundOffView = videoLayout.findViewById(R.id.soundOffView);
        playVideo = videoLayout.findViewById(R.id.playVideo);
        pauseVideo = videoLayout.findViewById(R.id.pauseVideo);

// Make sure to use the correct VideoView import
        videoView = videoLayout.findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);

        HttpProxyCacheServer proxy = App.getProxy(context);
        String proxyUrl = proxy.getProxyUrl(VIDEOS.get(position).getUrlVideo());

        videoView.setVideoURI(Uri.parse(proxyUrl));


        view.addView(videoLayout, 0);

        return videoLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onPrepared() {

        if (isVolumed){
            videoView.setVolume(1.0f);
        }else {
            videoView.setVolume(0f);
        }

        videoView.setOnCompletionListener(() -> {
            videoView.restart();
                videoView.start();

        });

        final GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                if (videoView.isPlaying()) {
                    videoView.pause();
                    animatePhotoLike(bigView, pauseVideo);
                } else {
                    videoView.start();
                    animatePhotoLike(bigView, playVideo);
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (videoView.getVolume() >= 1) {
                    videoView.setVolume(0f);
                    isVolumed = false;
                    animatePhotoLike(bigView, soundOffView);
                } else {
                    videoView.setVolume(1.0f);
                    isVolumed = true;
                    animatePhotoLike(bigView, soundOnView);
                }


                return true;
            }
        }
        );

        videoView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));

    }
}
