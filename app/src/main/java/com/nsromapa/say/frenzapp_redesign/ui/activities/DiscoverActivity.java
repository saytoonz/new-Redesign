package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nsromapa.say.frenzapp_redesign.App;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.grantland.widget.AutofitTextView;

import static com.nsromapa.say.frenzapp_redesign.ui.getTextBackground.setmImageHolderBg;
import static com.nsromapa.say.frenzapp_redesign.utils.OpenIntents.profileWithUserJson;

public class DiscoverActivity extends AppCompatActivity {

    private Discoveries mDiscoveryList;
    private ProgressBar mProgressBar;
    private LinearLayout mViewHolderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        if (getIntent() != null) {
            Bundle b = getIntent().getExtras();
            assert b != null;
            mDiscoveryList = (Discoveries) b.getSerializable("discovery");
        } else {
            finish();
        }

        mProgressBar = findViewById(R.id.progressBar);
        mViewHolderLayout = findViewById(R.id.holder_linear_layout);

        //Toolbar vies init
        CircleImageView posterImageView = findViewById(R.id.poster_image);
        TextView posterNameTV = findViewById(R.id.poster_name);
        TextView postedTimeTV = findViewById(R.id.posted_time);
        ImageView close_imageView = findViewById(R.id.close_imageView);

        close_imageView.setOnClickListener(v -> finish());
        setDiscoveryInfo(posterImageView, posterNameTV, postedTimeTV);


        //Action Views
        LinearLayout likeLinearLayout = findViewById(R.id.like_linearLayout);
        LinearLayout commentLinearLayout = findViewById(R.id.comment_linearLayout);
        LinearLayout viewsLinearLayout = findViewById(R.id.views_linearLayout);
        TextView likeTextView = findViewById(R.id.like_textView);
        TextView commentTextView = findViewById(R.id.comment_textView);
        TextView viewsTextView = findViewById(R.id.views_textView);

        VideoView discover_videoView = findViewById(R.id.discover_videoView);
        AutofitTextView discover_text = findViewById(R.id.discover_text);
        ImageView discover_image = findViewById(R.id.discover_image);


        if (mDiscoveryList.getMimeType().contains("image")) {
            discover_videoView.setVisibility(View.GONE);
            discover_text.setVisibility(View.GONE);
            discover_image.setVisibility(View.VISIBLE);
            setImageDiscovery(discover_image);

        } else if (mDiscoveryList.getMimeType().contains("video")) {
            discover_text.setVisibility(View.GONE);
            discover_image.setVisibility(View.GONE);
            discover_videoView.setVisibility(View.VISIBLE);
            setVideoDiscovery(discover_videoView);

        } else if (mDiscoveryList.getMimeType().contains("text")) {
            discover_image.setVisibility(View.GONE);
            discover_videoView.setVisibility(View.GONE);
            discover_text.setVisibility(View.VISIBLE);
            setTextDiscovery(discover_text);
        }

    }

    private void setTextDiscovery(AutofitTextView discover_text) {
        discover_text.setText(mDiscoveryList.getDescription());
        setmImageHolderBg(mDiscoveryList.getBackground(), mViewHolderLayout);
    }

    private void setVideoDiscovery(VideoView video) {
        HttpProxyCacheServer proxy = App.getProxy(this);
        String proxyUrl = proxy.getProxyUrl(mDiscoveryList.getMediaUrl());
        video.setVideoPath(proxyUrl);
        video.setOnPreparedListener(mp -> {
            mp.setOnInfoListener((mp1, what, extra) -> {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                        mProgressBar.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING: {
                        mProgressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                return false;
            });
        });
    }

    private void setImageDiscovery(ImageView discover_image) {
        Glide.with(this)
                .load(mDiscoveryList.getMediaUrl())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toasty.error(DiscoverActivity.this, "Failed to load media...", Toasty.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mProgressBar.setVisibility(View.GONE);
                        addToWatchesList();
                        return false;
                    }
                }).into(discover_image);

    }

    private void setDiscoveryInfo(CircleImageView posterImageView,
                                   TextView posterNameTV, TextView postedTimeTV) {
        try {
            JSONObject posterJson = new JSONObject(mDiscoveryList.getPosterJson());

            Glide.with(this)
                    .load(posterJson.getString(("image")))
                    .into(posterImageView);

            posterNameTV.setText(posterJson.getString("username"));
            postedTimeTV.setText(mDiscoveryList.getTimeAgo());

            posterNameTV.setOnClickListener(v -> profileWithUserJson(this, mDiscoveryList.getPosterJson()));
            posterImageView.setOnClickListener(v -> profileWithUserJson(this, mDiscoveryList.getPosterJson()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addToWatchesList() {

    }
}
