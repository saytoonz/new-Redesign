package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nsromapa.say.frenzapp_redesign.App;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.models.DiscoveryComment;
import com.nsromapa.say.frenzapp_redesign.ui.sheets.ShowDiscoveryComment;
import com.nsromapa.say.frenzapp_redesign.ui.sheets.ShowStoryViewers;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;
import com.otaliastudios.zoom.ZoomImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.grantland.widget.AutofitTextView;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.setUserOnlineStatus;
import static com.nsromapa.say.frenzapp_redesign.utils.Constants.DISCOVER_STORIES;
import static com.nsromapa.say.frenzapp_redesign.utils.Openings.profileWithUserJson;
import static com.nsromapa.say.frenzapp_redesign.utils.getTextBackground.setImageHolderBg;

public class DiscoverActivity extends AppCompatActivity {

    private Discoveries mDiscoveryList;
    private ProgressBar mProgressBar;
    private LinearLayout mViewHolderLayout;
    private DiscoveryComment description;
    private LinearLayout viewsLinearLayout;
    private ImageView delete_discovery;

    private boolean is_liked_by_me = false;

    @SuppressLint("ClickableViewAccessibility")
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
        delete_discovery = findViewById(R.id.delete_discovery);



        //Action Views
        LinearLayout likeLinearLayout = findViewById(R.id.like_linearLayout);
        ImageView liked_iv = likeLinearLayout.findViewById(R.id.like_dislike_iv);
        LinearLayout commentLinearLayout = findViewById(R.id.comment_linearLayout);
        viewsLinearLayout = findViewById(R.id.views_linearLayout);
        TextView likeTextView = findViewById(R.id.like_textView);
        TextView commentTextView = findViewById(R.id.comment_textView);
        TextView viewsTextView = findViewById(R.id.views_textView);

        if (mDiscoveryList.getI_liked().equals("yes")){
            is_liked_by_me = true;
           liked_iv.setColorFilter(Color.RED);
            likeTextView.setTextColor(Color.RED);
        }else{
            is_liked_by_me = false;
            liked_iv.setColorFilter(Color.WHITE);
            likeTextView.setTextColor(Color.WHITE);
        }

        close_imageView.setOnClickListener(v -> finish());
        setDiscoveryInfo(posterImageView, posterNameTV, postedTimeTV);

        Resources res = getResources();
        commentTextView.setText(String.format(res.getString(R.string.comments), mDiscoveryList.getComments()));
        likeTextView.setText(String.format(res.getString(R.string.likes), mDiscoveryList.getLikes()));
        viewsTextView.setText(String.format(res.getString(R.string.views), mDiscoveryList.getWatches()));


        likeLinearLayout.setOnClickListener(v -> {
            if (is_liked_by_me){
                liked_iv.setColorFilter(Color.WHITE);
                likeTextView.setTextColor(Color.WHITE);
                is_liked_by_me = false;
                int likes = Integer.parseInt(mDiscoveryList.getLikes());
                mDiscoveryList.setLikes(String.valueOf(likes-1));
                likeTextView.setText(String.format(res.getString(R.string.likes), String.valueOf(likes-1)));
                disLikeDiscovery();
            }else{
                liked_iv.setColorFilter(Color.RED);
                likeTextView.setTextColor(Color.RED);
                is_liked_by_me = true;
                int likes = Integer.parseInt(mDiscoveryList.getLikes());
                mDiscoveryList.setLikes(String.valueOf(likes+1));
                likeTextView.setText(String.format(res.getString(R.string.likes), String.valueOf(likes+1)));
                likeDiscovery();
            }
        });
        VideoView discover_videoView = findViewById(R.id.discover_videoView);
        AutofitTextView discover_text = findViewById(R.id.discover_text);
        ZoomImageView discover_image = findViewById(R.id.discover_image);
        discover_text.setMovementMethod(new ScrollingMovementMethod());


        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (discover_image.getZoom() > 1 || discover_image.getZoom() < 1) {
                    discover_image.zoomTo(1f, true);
                } else {
                    discover_image.zoomTo(2.7f, true);
                }
                return true;
            }
        });
        discover_image.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        commentLinearLayout.setOnClickListener(v -> this.getSupportFragmentManager().beginTransaction()
                .add(new ShowDiscoveryComment(getApplicationContext(), mDiscoveryList.getId(), description), "DicoverActivity")
                .commit());

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
        mProgressBar.setVisibility(View.GONE);
        discover_text.setText(mDiscoveryList.getDescription());
        setImageHolderBg(mDiscoveryList.getBackground(), mViewHolderLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
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
            video.start();
            mProgressBar.setVisibility(View.GONE);
        });

        video.setOnCompletionListener(mp12 -> video.start());


        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (video.isPlaying()) {
                    video.pause();
                } else {
                    video.start();
                }
                return true;
            }
        });

        video.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void setImageDiscovery(ZoomImageView discover_image) {
        mProgressBar.setVisibility(View.GONE);
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

            description = new DiscoveryComment(
                    posterJson.getString("id"),
                    posterJson.getString("username"),
                    posterJson.getString("image"),
                    posterJson.toString(),
                    "",
                    mDiscoveryList.getTimeAgo(),
                    mDiscoveryList.getDescription(),
                    "",
                    "",
                    "description"
            );


            posterNameTV.setOnClickListener(v -> profileWithUserJson(this, mDiscoveryList.getPosterJson()));
            posterImageView.setOnClickListener(v -> profileWithUserJson(this, mDiscoveryList.getPosterJson()));
            if (posterJson.getString("id").equals(Utils.getUserUid(getApplicationContext()))) {
                delete_discovery.setVisibility(View.VISIBLE);
                delete_discovery.setOnClickListener(v -> askToDeleteStory());
                viewsLinearLayout.setOnClickListener(v ->
                        this.getSupportFragmentManager().beginTransaction()
                                .add(new ShowStoryViewers(getApplicationContext(), mDiscoveryList.getId(), "discoveries"), "StoryViewActivity")
                                .commit());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void askToDeleteStory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Delete this Story?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteStory();
        }).setNegativeButton("No", (dialog, which) -> {
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStory() {
        Toasty.normal(getApplicationContext(), "Deleting Story").show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> Toasty.success(getApplicationContext(), "Story deleted").show(),
                error -> Toasty.error(getApplicationContext(), "Couldn't delete Story Please Try again").show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid(getApplicationContext()));
                postMap.put("story_delete", "true");
                postMap.put("story_id", mDiscoveryList.getId());
                postMap.put("delete_from_status_or_discovery", "discoveries");
                return postMap;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
        finish();
    }



    private void disLikeDiscovery() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response ->{},
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid(getApplicationContext()));
                postMap.put("unlike_discovery", "true");
                postMap.put("discovery_id", mDiscoveryList.getId());
                return postMap;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    private void likeDiscovery() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response ->{},
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid(getApplicationContext()));
                postMap.put("like_discovery", "true");
                postMap.put("discovery_id", mDiscoveryList.getId());
                return postMap;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }




    private void addToWatchesList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response ->{},
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid(getApplicationContext()));
                postMap.put("watch_story", "true");
                postMap.put("discovery_id", mDiscoveryList.getId());
                return postMap;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUserOnlineStatus(this,getResources().getString(R.string.online), Utils.getUserUid(getApplicationContext()));

    }

    @Override
    protected void onPause() {
        setUserOnlineStatus(this,getResources().getString(R.string.offline), Utils.getUserUid(getApplicationContext()));
        super.onPause();
    }
}
