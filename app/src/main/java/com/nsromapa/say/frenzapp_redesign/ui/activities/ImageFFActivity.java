package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;
import com.squareup.picasso.Picasso;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.setUserOnlineStatus;

public class ImageFFActivity extends AppCompatActivity {

    PhotoView photoView;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_ff);

        photoView = findViewById(R.id.photoView);
        imageView = findViewById(R.id.imageView);
        if (getIntent()!= null && getIntent().hasExtra("photoURI")){
            if (getIntent().hasExtra("its_gif")){
                photoView.setVisibility(View.GONE);
                Glide.with(this)
                        .asGif()
                        .load(getIntent().getStringExtra("photoURI"))
                        .into(imageView);
            }else{
                imageView.setVisibility(View.GONE);
                Picasso.get().load(getIntent().getStringExtra("photoURI")).into(photoView);
            }
        }else{
            finish();
        }

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.image_transition));
        photoView.setTransitionName("photoTransition");
        imageView.setTransitionName("photoTransition");
    }

    @Override
    public void onBackPressed() {
        //To support reverse transitions when user clicks the device back button
        supportFinishAfterTransition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserOnlineStatus(this,getResources().getString(R.string.online), Utils.getUserUid());
    }

    @Override
    protected void onPause() {
        setUserOnlineStatus(this,getResources().getString(R.string.offline), Utils.getUserUid());
        super.onPause();
    }
}
