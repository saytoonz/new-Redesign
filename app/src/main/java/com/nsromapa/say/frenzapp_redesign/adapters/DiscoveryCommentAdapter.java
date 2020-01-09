package com.nsromapa.say.frenzapp_redesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.models.DiscoveryComment;
import com.nsromapa.say.frenzapp_redesign.ui.activities.DiscoverActivity;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.DISCOVER_STORIES;

public class DiscoveryCommentAdapter extends RecyclerView.Adapter {
    private List<DiscoveryComment> commentList;
    private Context context;
    public static final int LOADING_ITEM = 0;
    public static final int COMMENTS_ITEM = 1;
    private int LoadingItemPos;
    public boolean loading = false;


    public DiscoveryCommentAdapter(Context context) {
        this.commentList = new ArrayList<>();
        this.context = context;
    }

    //method to add products as soon as they fetched
    public void addProducts(List<DiscoveryComment> commentList) {
        int lastPos = commentList.size();
        this.commentList.addAll(commentList);
        notifyItemRangeInserted(lastPos, commentList.size());
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Check which view has to be populated
        if (viewType == LOADING_ITEM) {
            View row = inflater.inflate(R.layout.item_custom_row_loading, parent, false);
            return new DiscoveryCommentAdapter.LoadingHolder(row);

        } else if (viewType == COMMENTS_ITEM) {
            View row = inflater.inflate(R.layout.item_discovery_comment, parent, false);
            return new DiscoveryCommentAdapter.CommentsHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DiscoveryCommentAdapter.CommentsHolder) {
            DiscoveryComment list = commentList.get(position);

            if (!TextUtils.isEmpty(list.getComment())){
                DiscoveryCommentAdapter.CommentsHolder p0 = (DiscoveryCommentAdapter.CommentsHolder) holder;

                Glide.with(context)
                        .load(list.getCommenter_image())
                        .into(p0.commenter_image);
                p0.commenter_name.setText(list.getCommenter_name());

                p0.comment_time.setText(list.getComment_time());
                p0.comment_tv.setText(list.getComment());
                p0.likers.setText(list.getComment_likes());
                p0.dislikers.setText(list.getComment_dislikes());

                p0.like_comment.setOnClickListener(v -> likeComment(list.getComment_id()));

                p0.dislike_comment.setOnClickListener(v -> dislikeComment(list.getComment_id()));

                if (list.getComment_or_description().equals("comment")
                        && list.getCommenter_id().equals(Utils.getUserUid())){
                    p0.delete_comment.setVisibility(View.VISIBLE);
                    p0.delete_comment.setOnClickListener (v -> askToDeleteComment(list.getComment_id(), position));
                }
                else
                    p0.delete_comment.setVisibility(View.GONE);


                if (list.getComment_or_description().equals("description")){
                    p0.actionsContainerLL.setVisibility(View.GONE);
                }


            }

        } else if (holder instanceof DiscoveryCommentAdapter.LoadingHolder) {
            DiscoveryCommentAdapter.LoadingHolder loadingHolder = (DiscoveryCommentAdapter.LoadingHolder) holder;
            //get Loading views from  holder here
        }
    }


    @Override
    public int getItemViewType(int position) {
        DiscoveryComment discoveries = commentList.get(position);
        if (discoveries.isLoading()) {
            return LOADING_ITEM;
        } else {
            return COMMENTS_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    //holds view of loading item
    private class LoadingHolder extends RecyclerView.ViewHolder {
        ProgressBar progress_loader;

        LoadingHolder(View itemView) {
            super(itemView);
            progress_loader = itemView.findViewById(R.id.progress_loader);
        }
    }

    private class CommentsHolder extends RecyclerView.ViewHolder {
        TextView commenter_name, comment_time, comment_tv, likers, dislikers;
        CircleImageView commenter_image;
        LinearLayout actionsContainerLL;
        ImageView like_comment, dislike_comment, delete_comment;

        CommentsHolder(View itemView) {
            super(itemView);
            commenter_name = itemView.findViewById(R.id.commenter_name);
            commenter_image = itemView.findViewById(R.id.commenter_image);
            comment_time = itemView.findViewById(R.id.comment_time);
            comment_tv = itemView.findViewById(R.id.comment_tv);
            actionsContainerLL = itemView.findViewById(R.id.actionsContainerLL);
            likers = itemView.findViewById(R.id.likers);
            dislikers = itemView.findViewById(R.id.dislikers);
            like_comment = itemView.findViewById(R.id.like_comment);
            dislike_comment = itemView.findViewById(R.id.dislike_comment);
            delete_comment = itemView.findViewById(R.id.delete_comment);
        }
    }






    private void dislikeComment(String commentId) {

    }

    private void likeComment(String commentId) {

    }





    private void askToDeleteComment(String comment_id, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want to delete this Comment?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteComment(comment_id);
            commentList.remove(position);
            notifyDataSetChanged();

        }).setNegativeButton("No", (dialog, which) -> {
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteComment(String comment_id) {
        Toasty.normal(context, "Deleting Comment").show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> Toasty.success(context, "Story deleted").show(),
                error -> Toasty.error(context, "Couldn't delete Story Please Try again").show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("comment_discovery_delete", "true");
                postMap.put("comment_id", comment_id);
                return postMap;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }



    //method to show loading
    public void showLoading() {
        DiscoveryComment comments = new DiscoveryComment();
        comments.setLoading(true);
        commentList.add(comments);
        LoadingItemPos = commentList.size();
        notifyItemInserted(commentList.size());
        loading = true;
    }

    //method to hide loading
    public void hideLoading() {
        if (LoadingItemPos <= commentList.size()) {
            commentList.remove(LoadingItemPos - 1);
            notifyItemRemoved(LoadingItemPos);
            loading = false;
        }
    }
}
