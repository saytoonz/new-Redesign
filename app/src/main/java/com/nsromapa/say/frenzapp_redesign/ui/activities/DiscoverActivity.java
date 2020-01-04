package com.nsromapa.say.frenzapp_redesign.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.models.StoriesData;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitTextView;

public class DiscoverActivity extends AppCompatActivity {

    private Discoveries mDiscoveryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        if (getIntent() != null) {
            Bundle b = getIntent().getExtras();
            mDiscoveryList = (Discoveries) b.getSerializable("discovery");
        } else {
            finish();
        }

        ProgressBar mProgressBar = findViewById(R.id.progressBar);
        LinearLayout mViewHolderLayout = findViewById(R.id.holder_linear_layout);

        //Toolbar vies init
        CircleImageView posterImageView = findViewById(R.id.poster_image);
        TextView posterNameTV = findViewById(R.id.poster_name);
        TextView postedTimeTV = findViewById(R.id.posted_time);
        ImageView close_imageView = findViewById(R.id.close_imageView);

        close_imageView.setOnClickListener(v -> finish());
        setDiscoveryInfos(posterImageView, posterNameTV, postedTimeTV);


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


        if (mDiscoveryList.getMimeType().contains("image")){
            discover_videoView.setVisibility(View.GONE);
            discover_text.setVisibility(View.GONE);
            discover_image.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(mDiscoveryList.getMediaUrl())
                    .into(discover_image);

        }else if (mDiscoveryList.getMimeType().contains("video")){
            discover_text.setVisibility(View.GONE);
            discover_image.setVisibility(View.GONE);
            discover_videoView.setVisibility(View.VISIBLE);



        }else if (mDiscoveryList.getMimeType().contains("text")){
            discover_image.setVisibility(View.GONE);
            discover_videoView.setVisibility(View.GONE);
            discover_text.setVisibility(View.VISIBLE);


        }

    }

    private void setDiscoveryInfos(CircleImageView posterImageView,
                                   TextView posterNameTV, TextView postedTimeTV) {
        try {
            JSONObject posterJson = new JSONObject(mDiscoveryList.getPosterJson());

            Glide.with(this)
                    .load(posterJson.getString("image"))
                    .into(posterImageView);

            posterNameTV.setText(posterJson.getString("username"));

            postedTimeTV.setText(mDiscoveryList.getTimeAgo());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
