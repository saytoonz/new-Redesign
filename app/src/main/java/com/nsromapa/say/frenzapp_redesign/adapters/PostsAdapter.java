package com.nsromapa.say.frenzapp_redesign.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.MultipleImage;
import com.nsromapa.say.frenzapp_redesign.models.MultipleVideos;
import com.nsromapa.say.frenzapp_redesign.models.Post;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkPosition;
import com.watermark.androidwm_light.bean.WatermarkText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import me.grantland.widget.AutofitTextView;

import static com.nsromapa.say.frenzapp_redesign.utils.getTextBackground.setImageHolderBg;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<Post> postList;
    private Context context;
    private Activity activity;
    private boolean isOwner;
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public PostsAdapter(List<Post> postList, Context context,
                        Activity activity) {
        this.postList = postList;
        this.activity = activity;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            setupViews(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private CircleImageView user_image;
        private TextView user_name, timestamp, post_desc;
        private MaterialFavoriteButton sav_button, like_btn, share_btn, comment_btn, stat_btn;
        private FrameLayout mImageholder, mVideoholder;
        private FrameLayout pager_layout, video_pager_layout;
        private RelativeLayout indicator_holder, video_indicator_holder;
        private AutofitTextView post_text;
        private ImageView delete;
        private ViewPager pager, video_pager;
        private View vBgLike;
        private ImageView ivLike;
        private DotsIndicator indicator2, indicator3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            user_image = mView.findViewById(R.id.post_user_image);
            like_btn = mView.findViewById(R.id.like_button);
            vBgLike = mView.findViewById(R.id.vBgLike);
            ivLike = mView.findViewById(R.id.ivLike);
            stat_btn = mView.findViewById(R.id.stat_button);
            user_name = mView.findViewById(R.id.post_username);
            timestamp = mView.findViewById(R.id.post_timestamp);
            post_desc = mView.findViewById(R.id.post_desc);
            post_text = mView.findViewById(R.id.post_text);
            pager = mView.findViewById(R.id.pager);
            pager_layout = mView.findViewById(R.id.pager_layout);
            comment_btn = mView.findViewById(R.id.comment_button);
            share_btn = mView.findViewById(R.id.share_button);
            delete = mView.findViewById(R.id.delete_button);
            sav_button = mView.findViewById(R.id.save_button);
            mImageholder = mView.findViewById(R.id.image_holder);
            indicator2 = mView.findViewById(R.id.indicator);
            indicator_holder = mView.findViewById(R.id.indicator_holder);
            indicator3 = mView.findViewById(R.id.video_indicator);

            video_indicator_holder = mView.findViewById(R.id.video_indicator_holder);
            video_pager_layout = mView.findViewById(R.id.video_pager_layout);
            video_pager = mView.findViewById(R.id.video_pager);
            mVideoholder = mView.findViewById(R.id.video_holder);
        }
    }




    private void setupViews(final ViewHolder holder) {

        int pos = holder.getAdapterPosition();

        getLikeandFav(holder);

        if (!TextUtils.isEmpty(postList.get(pos).getUsername())) {
            holder.user_name.setText(postList.get(pos).getUsername());
        }

        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.placeholder))
                .load(postList.get(pos).getUserimage())
                .into(holder.user_image);
        holder.timestamp.setText(postList.get(pos).getTimestamp());

        if (isOnline()) {
            enableDoubleTap(holder);
        }


        holder.stat_btn.setOnFavoriteChangeListener((buttonView, favorite) -> {

//            mmBottomSheetDialog.show();
//
//            final ProgressBar pbr = statsheetView.findViewById(R.id.pbar);
//            final LinearLayout main=statsheetView.findViewById(R.id.main);
//            final TextView smile=statsheetView.findViewById(R.id.smiles);
//            final TextView save=statsheetView.findViewById(R.id.saves);

//            pbar.setVisibility(View.VISIBLE);
//            main.setVisibility(View.GONE);
//            FirebaseFirestore.getInstance().collection("Posts")
//                    .document(postList.get(holder.getAdapterPosition()).postId)
//                    .collection("Liked_Users")
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//
//                        smile.setText(String.format(context.getString(R.string.s_people_have_smiled_for_this_post),queryDocumentSnapshots.size()));
//
//                        FirebaseFirestore.getInstance().collection("Posts")
//                                .document(postList.get(holder.getAdapterPosition()).postId)
//                                .collection("Saved_Users")
//                                .get()
//                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
//
//                                    save.setText(String.format(context.getString(R.string.s_people_have_saved_this_post), queryDocumentSnapshots1.size()));
//                                    pbar.setVisibility(View.GONE);
//                                    main.setVisibility(View.VISIBLE);
//
//                                })
//                                .addOnFailureListener(e -> e.printStackTrace());
//
//                    })
//                    .addOnFailureListener(e -> e.printStackTrace());
//
        });

        switch (postList.get(pos).getPost_type()) {
            case "text_only": {
                holder.mVideoholder.setVisibility(View.GONE);
                holder.mImageholder.setVisibility(View.VISIBLE);
                holder.pager_layout.setVisibility(View.GONE);
                holder.post_desc.setVisibility(View.GONE);
                setImageHolderBg(postList.get(pos).getColor(), holder.mImageholder);
                holder.post_text.setVisibility(View.VISIBLE);
                holder.post_text.setText(postList.get(pos).getDescription());
                holder.post_text.setMovementMethod(new ScrollingMovementMethod());

                holder.share_btn.setOnFavoriteAnimationEndListener((buttonView, favorite) -> {

                    Intent intent = new Intent(Intent.ACTION_SEND)
                            .setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, getBitmapUri(getBitmap(holder.mImageholder), postList.get(holder.getAdapterPosition()).getName()));
                    try {
                        context.startActivity(Intent.createChooser(intent, "Share using..."));
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }

                });

                break;
            }
            case "images_only": {
                ArrayList<MultipleImage> multipleImages = new ArrayList<>();
                PostPhotosAdapter photosAdapter = new PostPhotosAdapter(context, activity,
                        multipleImages, false, postList.get(holder.getAdapterPosition()).getPostId(),
                        holder.like_btn, postList.get(holder.getAdapterPosition()).getUserId());

                String[] items = postList.get(pos).getImages_url().split(",");
                List<String> imagesList = new ArrayList<>(Arrays.asList(items));

                for (int i = 0; i < imagesList.size(); i++) {
                    if (!TextUtils.isEmpty(imagesList.get(i))) {
                        MultipleImage image = new MultipleImage(imagesList.get(i));
                        multipleImages.add(image);
                        photosAdapter.notifyDataSetChanged();
                        Log.i("url0", imagesList.get(i));
                    }
                }


                holder.pager.setAdapter(photosAdapter);
                photosAdapter.notifyDataSetChanged();
                if (imagesList.size() > 1) {
                    holder.indicator_holder.setVisibility(View.VISIBLE);
                    holder.indicator2.setDotsClickable(true);
                    holder.indicator2.setViewPager(holder.pager);
                } else {
                    holder.indicator_holder.setVisibility(View.GONE);
                    holder.indicator2.setDotsClickable(false);
                }

                holder.mVideoholder.setVisibility(View.GONE);
                holder.mImageholder.setVisibility(View.VISIBLE);
                holder.pager_layout.setVisibility(View.VISIBLE);
                holder.indicator_holder.setVisibility(View.VISIBLE);
                holder.post_text.setVisibility(View.GONE);
                holder.post_desc.setVisibility(View.VISIBLE);
                String desc = "<b>" + postList.get(pos).getUsername() + "</b> : " + postList.get(pos).getDescription();
                holder.post_desc.setText(Html.fromHtml(desc));

                holder.share_btn.setOnFavoriteAnimationEndListener((buttonView, favorite) ->
                        new DownloadTask(context, holder).execute(stringToURL(multipleImages.get(holder.pager.getCurrentItem()).getUrl())));

                break;
            }
            case "videos_only": {
                ArrayList<MultipleVideos> multipleVideos = new ArrayList<>();
                PostVideosAdapter videosAdapter = new PostVideosAdapter(context, activity,
                        multipleVideos);

                String[] items = postList.get(pos).getImages_url().split(",");
                List<String> videosList = new ArrayList<>(Arrays.asList(items));

                for (int i = 0; i < videosList.size(); i++) {
                    if (!TextUtils.isEmpty(videosList.get(i))) {
                        MultipleVideos video = new MultipleVideos(videosList.get(i), 0);
                        multipleVideos.add(video);
                        videosAdapter.notifyDataSetChanged();
                        Log.i("urls", videosList.get(i));
                    }
                }


                holder.video_pager.setAdapter(videosAdapter);
                videosAdapter.notifyDataSetChanged();
                if (videosList.size() > 1) {
                    holder.video_indicator_holder.setVisibility(View.VISIBLE);
                    holder.indicator3.setDotsClickable(true);
                    holder.indicator3.setViewPager(holder.pager);
                } else {
                    holder.video_indicator_holder.setVisibility(View.GONE);
                    holder.indicator3.setDotsClickable(false);
                }


                holder.mImageholder.setVisibility(View.GONE);
                holder.mVideoholder.setVisibility(View.VISIBLE);
                holder.video_pager_layout.setVisibility(View.VISIBLE);
                holder.video_indicator_holder.setVisibility(View.VISIBLE);
                holder.post_desc.setVisibility(View.VISIBLE);
                String desc = "<b>" + postList.get(pos).getUsername() + "</b> : " + postList.get(pos).getDescription();
                holder.post_desc.setText(Html.fromHtml(desc));

                holder.share_btn.setVisibility(View.INVISIBLE);
                holder.share_btn.setEnabled(false);

//                    holder.share_btn.setOnFavoriteAnimationEndListener((buttonView, favorite) ->
//                    new DownloadTask(context, holder).execute(stringToURL(multipleImages.get(holder.pager.getCurrentItem()).getUrl())));

                break;
            }
        }
    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void getLikeandFav(final ViewHolder holder) {

        //forLiked
//        mFirestore.collection("Posts")
//                .document(postList.get(holder.getAdapterPosition()).postId)
//                .collection("Liked_Users")
//                .document(mCurrentUser.getUid())
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                        if (documentSnapshot.exists()) {
//                            boolean liked = documentSnapshot.getBoolean("liked");
//
//                            if (liked) {
//                                holder.like_btn.setFavorite(true,false);
//                            } else {
//                                holder.like_btn.setFavorite(false,false);
//                            }
//                        } else {
//                            Log.e("Like", "No document found");
//
//                        }
//
//                        if(isOnline()) {
//                            holder.like_btn.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
//                                @Override
//                                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
//                                    if (favorite) {
//                                        Map<String, Object> likeMap = new HashMap<>();
//                                        likeMap.put("liked", true);
//
//                                        try {
//
//                                            mFirestore.collection("Posts")
//                                                    .document(postList.get(holder.getAdapterPosition()).postId)
//                                                    .collection("Liked_Users")
//                                                    .document(mCurrentUser.getUid())
//                                                    .set(likeMap)
//                                                    .addOnSuccessListener(aVoid -> {
//
//                                                        UserHelper userHelper=new UserHelper(context);
//                                                        Cursor rs = userHelper.getData(1);
//                                                        rs.moveToFirst();
//
//                                                        String image = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_IMAGE));
//                                                        String username = rs.getString(rs.getColumnIndex(UserHelper.CONTACTS_COLUMN_USERNAME));
//
//                                                        if (!rs.isClosed()) {
//                                                            rs.close();
//                                                        }
//
//                                                        addToNotification(postList.get(holder.getAdapterPosition()).getUserId(),
//                                                                mCurrentUser.getUid(),
//                                                                image,
//                                                                username,
//                                                                "Liked your post",
//                                                                postList.get(holder.getAdapterPosition()).postId,
//                                                                "like");
//
//
//                                                    })
//                                                    .addOnFailureListener(e -> Log.e("Error like", e.getMessage()));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else {
//                                        Map<String, Object> likeMap = new HashMap<>();
//                                        likeMap.put("liked", false);
//
//                                        try {
//
//                                            mFirestore.collection("Posts")
//                                                    .document(postList.get(holder.getAdapterPosition()).postId)
//                                                    .collection("Liked_Users")
//                                                    .document(mCurrentUser.getUid())
//                                                    //.set(likeMap)
//                                                    .delete()
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            //holder.like_count.setText(String.valueOf(Integer.parseInt(holder.like_count.getText().toString())-1));
//                                                            //Toast.makeText(context, "Unliked post '" + postList.get(holder.getAdapterPosition()).postId, Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            Log.e("Error unlike", e.getMessage());
//                                                        }
//                                                    });
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            });
//                        }
//
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Error Like", e.getMessage());
//                    }
//                });

        //forFavourite
//        mFirestore.collection("Posts")
//                .document(postList.get(holder.getAdapterPosition()).postId)
//                .collection("Saved_Users")
//                .document(mCurrentUser.getUid())
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                        if (documentSnapshot.exists()) {
//                            boolean fav = documentSnapshot.getBoolean("Saved");
//
//                            if (fav) {
//                                holder.sav_button.setFavorite(true,false);
//                            } else {
//                                holder.sav_button.setFavorite(false,false);
//                            }
//                        } else {
//                            Log.e("Fav", "No document found");
//
//                        }
//
//                        if(isOnline()) {
//                            holder.sav_button.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
//                                @Override
//                                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
//                                    if (favorite) {
//
//                                        Map<String, Object> favMap = new HashMap<>();
//                                        favMap.put("Saved", true);
//
//                                        try {
//
//                                            mFirestore.collection("Posts")
//                                                    .document(postList.get(holder.getAdapterPosition()).postId)
//                                                    .collection("Saved_Users")
//                                                    .document(mCurrentUser.getUid())
//                                                    .set(favMap)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//
//                                                            Map<String, Object> postMap = new HashMap<>();
//
//                                                            postMap.put("userId", postList.get(holder.getAdapterPosition()).getUserId());
//                                                            postMap.put("name", postList.get(holder.getAdapterPosition()).getName());
//                                                            postMap.put("username", postList.get(holder.getAdapterPosition()).getUsername());
//                                                            postMap.put("timestamp", postList.get(holder.getAdapterPosition()).getTimestamp());
//                                                            postMap.put("image_count", postList.get(holder.getAdapterPosition()).getImage_count());
//                                                            postMap.put("description", postList.get(holder.getAdapterPosition()).getDescription());
//                                                            postMap.put("color", postList.get(holder.getAdapterPosition()).getColor());
//
//                                                            try {
//                                                                postMap.put("image_url_0", postList.get(holder.getAdapterPosition()).getImage_url_0());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            try {
//                                                                postMap.put("image_url_1", postList.get(holder.getAdapterPosition()).getImage_url_1());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            try {
//                                                                postMap.put("image_url_2", postList.get(holder.getAdapterPosition()).getImage_url_2());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            try {
//                                                                postMap.put("image_url_3", postList.get(holder.getAdapterPosition()).getImage_url_3());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            try {
//                                                                postMap.put("image_url_4", postList.get(holder.getAdapterPosition()).getImage_url_4());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            try {
//                                                                postMap.put("image_url_5", postList.get(holder.getAdapterPosition()).getImage_url_5());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            try {
//                                                                postMap.put("image_url_6", postList.get(holder.getAdapterPosition()).getImage_url_6());
//                                                            } catch (Exception e) {
//                                                                e.printStackTrace();
//                                                            }
//
//                                                            mFirestore.collection("Users")
//                                                                    .document(mCurrentUser.getUid())
//                                                                    .collection("Saved_Posts")
//                                                                    .document(postList.get(holder.getAdapterPosition()).postId)
//                                                                    .set(postMap)
//                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                        @Override
//                                                                        public void onSuccess(Void aVoid) {
//                                                                            // Toast.makeText(context, "Added to Saved_Posts, post '" + postList.get(holder.getAdapterPosition()).postId, Toast.LENGTH_SHORT).show();
//                                                                        }
//                                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                                @Override
//                                                                public void onFailure(@NonNull Exception e) {
//                                                                    Log.e("Error add fav", e.getMessage());
//                                                                }
//                                                            });
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            Log.e("Error fav", e.getMessage());
//                                                        }
//                                                    });
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    } else {
//
//                                        Map<String, Object> favMap = new HashMap<>();
//                                        favMap.put("Saved", false);
//
//                                        try {
//
//                                            mFirestore.collection("Posts")
//                                                    .document(postList.get(holder.getAdapterPosition()).postId)
//                                                    .collection("Saved_Users")
//                                                    .document(mCurrentUser.getUid())
//                                                    //.set(favMap)
//                                                    .delete()
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//
//                                                            mFirestore.collection("Users")
//                                                                    .document(mCurrentUser.getUid())
//                                                                    .collection("Saved_Posts")
//                                                                    .document(postList.get(holder.getAdapterPosition()).postId)
//                                                                    .delete()
//                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                        @Override
//                                                                        public void onSuccess(Void aVoid) {
//                                                                            // Toast.makeText(context, "Removed from Saved_Posts, post '" + postList.get(holder.getAdapterPosition()).postId, Toast.LENGTH_SHORT).show();
//                                                                        }
//                                                                    })
//                                                                    .addOnFailureListener(new OnFailureListener() {
//                                                                        @Override
//                                                                        public void onFailure(@NonNull Exception e) {
//                                                                            Log.e("Error remove fav", e.getMessage());
//                                                                        }
//                                                                    });
//
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            Log.e("Error fav", e.getMessage());
//                                                        }
//                                                    });
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            });
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Error Fav", e.getMessage());
//                    }
//                });

    }


    private void enableDoubleTap(final ViewHolder holder) {

        //Double Tap for Photo is set on PostPhotosAdapter

        final GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                animatePhotoLike(holder);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
            }
        }
        );

        holder.post_text.setOnTouchListener((v, event) -> detector.onTouchEvent(event));

    }


    private void animatePhotoLike(final ViewHolder holder) {
        holder.vBgLike.setVisibility(View.VISIBLE);
        holder.ivLike.setVisibility(View.VISIBLE);

        holder.vBgLike.setScaleY(0.1f);
        holder.vBgLike.setScaleX(0.1f);
        holder.vBgLike.setAlpha(1f);
        holder.ivLike.setScaleY(0.1f);
        holder.ivLike.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetLikeAnimationState(holder);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                holder.like_btn.setFavorite(true, true);
            }
        });
        animatorSet.start();

    }

    private void resetLikeAnimationState(ViewHolder holder) {
        holder.vBgLike.setVisibility(View.INVISIBLE);
        holder.ivLike.setVisibility(View.INVISIBLE);
    }


    private URL stringToURL(String urlString) {
        try {
            URL url = new URL(urlString);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getBitmapUri(Bitmap bitmap, String name) {

        try {

            OutputStream outputStream = null;
            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/FrenzApp/");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, "FrenzApp_POST_" + System.currentTimeMillis() + ".png");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }

            outputStream = new FileOutputStream(file);
            BufferedOutputStream outputStream1 = new BufferedOutputStream(outputStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream1);
            outputStream1.flush();
            outputStream1.close();
            return Uri.parse(file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Toasty.error(context, "Sharing failed", Toasty.LENGTH_SHORT, true).show();
            return null;
        }
    }


    private Bitmap getBitmap(FrameLayout view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.parseColor("#212121"));
        }
        view.draw(canvas);

        WatermarkText watermarkText = new WatermarkText("FrenzApp")
                .setPosition(new WatermarkPosition(0.5, 0.5, 30))
                .setTextColor(Color.WHITE)
                .setTextFont(R.font.bold)
                .setTextAlpha(100)
                .setTextSize(20);

        Bitmap watermarked_bitmap = WatermarkBuilder.create(context, bitmap)
                .loadWatermarkText(watermarkText)
                .setTileMode(true)
                .getWatermark()
                .getOutputImage();

        return watermarked_bitmap;
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<URL, Integer, List<Bitmap>> {

        ProgressDialog mProgressDialog;
        ViewHolder holder;
        Context context;

        public DownloadTask(Context context, ViewHolder holder) {
            this.context = context;
            this.holder = holder;
        }

        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setMessage("We are processing the image for sharing...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mProgressDialog.setProgress(0);
        }

        protected List<Bitmap> doInBackground(URL... urls) {
            int count = urls.length;
            HttpURLConnection connection = null;
            List<Bitmap> bitmaps = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                URL currentURL = urls[i];
                try {
                    connection = (HttpURLConnection) currentURL.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
                    bitmaps.add(bmp);
                    publishProgress((int) (((i + 1) / (float) count) * 100));
                    if (isCancelled()) {
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connection.disconnect();
                }
            }
            return bitmaps;
        }

        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress[0]);
        }

        // On AsyncTask cancelled
        protected void onCancelled() {
        }

        protected void onPostExecute(List<Bitmap> result) {
            mProgressDialog.dismiss();
            for (int i = 0; i < result.size(); i++) {
                Bitmap bitmap = result.get(i);

                WatermarkText watermarkText = new WatermarkText("FrenzApp")
                        .setPosition(new WatermarkPosition(0.5, 0.5, 30))
                        .setTextColor(Color.WHITE)
                        .setTextFont(R.font.bold)
                        .setTextAlpha(150)
                        .setTextSize(20);

                Bitmap watermarked_bitmap = WatermarkBuilder.create(context, bitmap)
                        .loadWatermarkText(watermarkText)
                        .setTileMode(true)
                        .getWatermark()
                        .getOutputImage();

                Uri imageInternalUri = getBitmapUri(watermarked_bitmap, postList.get(holder.getAdapterPosition()).getName());

                try {
                    Intent intent = new Intent(Intent.ACTION_SEND)
                            .setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, imageInternalUri);
                    context.startActivity(Intent.createChooser(intent, "Share using..."));
                } catch (Exception e) {
                    Toasty.error(context, "Sharing failed", Toasty.LENGTH_SHORT, true).show();
                    e.printStackTrace();
                }

            }
        }
    }


}
