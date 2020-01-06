package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nsromapa.say.frenzapp_redesign.App;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.StoriesData;
import com.nsromapa.say.frenzapp_redesign.helpers.StoriesProgressView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.grantland.widget.AutofitTextView;

import static com.nsromapa.say.frenzapp_redesign.ui.getTextBackground.setmImageHolderBg;


public class StoryViewActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private StoriesProgressView storiesProgressView;
    private ProgressBar mProgressBar;
    private LinearLayout mViewHolderLayout;
    private ImageView close_imageView;
    private int counter = 0;
    private ArrayList<StoriesData> mStoriesList;
    private String posterName, posterImage;
    private TextView postedTimeTV;
    private ArrayList<View> mediaPlayerArrayList = new ArrayList<>();

    long pressTime = 0L;
    long limit = 500L;


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_story_view);
        mProgressBar = findViewById(R.id.progressBar);
        mViewHolderLayout = findViewById(R.id.holder_linear_layout);
        close_imageView = findViewById(R.id.close_imageView);
        CircleImageView posterImageView = findViewById(R.id.poster_image);
        TextView posterNameTV = findViewById(R.id.poster_name);
        postedTimeTV = findViewById(R.id.posted_time);
        storiesProgressView = findViewById(R.id.stories);

        if (getIntent() != null) {
            Bundle b = getIntent().getExtras();
            mStoriesList = (ArrayList<StoriesData>) b.getSerializable("storiesList");
            assert mStoriesList != null;
            storiesProgressView.setStoriesCount(mStoriesList.size());

            posterName = getIntent().getStringExtra("posterName");
            posterImage = getIntent().getStringExtra("posterImage");
        } else {
            finish();
        }

        close_imageView.setOnClickListener(v -> finish());
        Glide.with(this)
                .load(posterImage)
                .apply(new RequestOptions().placeholder(R.drawable.contact_placeholder))
                .into(posterImageView);
        posterNameTV.setText(posterName);


//        prepareStoriesList();
        storiesProgressView.setStoriesListener(this);
        for (int i = 0; i < mStoriesList.size(); i++) {
            if (mStoriesList.get(i).mimeType.contains("video")) {
                mediaPlayerArrayList.add(getVideoView(i));
            } else if (mStoriesList.get(i).mimeType.contains("image")) {
                mediaPlayerArrayList.add(getImageView(i));
            } else if (mStoriesList.get(i).mimeType.contains("text")) {
                mediaPlayerArrayList.add(getTextView(i));
            }
        }

        setStoryView(counter);

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);
    }

    private void setStoryView(final int counter) {
        final View view = mediaPlayerArrayList.get(counter);
        mViewHolderLayout.addView(view);
        postedTimeTV.setText(mStoriesList.get(counter).postedTime);

        if (view instanceof VideoView) {
            final VideoView video = (VideoView) view;
            video.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.setOnInfoListener((mediaPlayer1, i, i1) -> {
                    switch (i) {
                        case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                            mProgressBar.setVisibility(View.GONE);
                            storiesProgressView.resume();
                            return true;
                        }
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING: {
                            mProgressBar.setVisibility(View.VISIBLE);
                            storiesProgressView.pause();
                            return true;
                        }
                    }
                    return false;
                });
                video.start();
                mProgressBar.setVisibility(View.GONE);
                storiesProgressView.setStoryDuration(mediaPlayer.getDuration());
                storiesProgressView.startStories(counter);
                setUserSeenThisStatus();
            });
        } else if (view instanceof ImageView) {
            final ImageView image = (ImageView) view;

            postedTimeTV.setText(mStoriesList.get(counter).postedTime);
            mProgressBar.setVisibility(View.GONE);
            Glide.with(this)
                    .load(mStoriesList.get(counter).mediaUrl).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Toasty.error(StoryViewActivity.this, "Failed to load media...", Toasty.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mProgressBar.setVisibility(View.GONE);
                    storiesProgressView.setStoryDuration(5000);
                    storiesProgressView.startStories(counter);
                    setUserSeenThisStatus();
                    return false;
                }
            }).into(image);
        } else if (view instanceof AutofitTextView) {
            final AutofitTextView textView = (AutofitTextView) view;

            postedTimeTV.setText(mStoriesList.get(counter).postedTime);

            textView.setText(mStoriesList.get(counter).description);
            setmImageHolderBg(mStoriesList.get(counter).background, textView);

            mProgressBar.setVisibility(View.GONE);
            storiesProgressView.setStoryDuration(5000);
            storiesProgressView.startStories(counter);
            setUserSeenThisStatus();
        }
    }

    private void setUserSeenThisStatus() {
        Toast.makeText(this, "Seen", Toast.LENGTH_SHORT).show();
    }


    private VideoView getVideoView(int position) {
        final VideoView video = new VideoView(this);
        HttpProxyCacheServer proxy = App.getProxy(this);
        String proxyUrl = proxy.getProxyUrl(mStoriesList.get(position).mediaUrl);
        video.setVideoPath(proxyUrl);
        video.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return video;
    }

    private ImageView getImageView(int position) {
        final ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return imageView;
    }


    private AutofitTextView getTextView(int position) {
        final AutofitTextView textView = new AutofitTextView(this);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxTextSize(60);
        textView.setMinTextSize(14);
        textView.setSizeToFit();
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setSingleLine(true);
        textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return textView;
    }

    @Override
    public void onNext() {
        storiesProgressView.destroy();
        mViewHolderLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        setStoryView(++counter);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        storiesProgressView.destroy();
        mViewHolderLayout.removeAllViews();
        mProgressBar.setVisibility(View.VISIBLE);
        setStoryView(--counter);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
