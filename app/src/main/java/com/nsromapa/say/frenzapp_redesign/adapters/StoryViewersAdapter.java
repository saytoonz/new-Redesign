package com.nsromapa.say.frenzapp_redesign.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Viewers;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryViewersAdapter extends RecyclerView.Adapter {
    private List<Viewers> viewersList;
    private Context context;
    public static final int LOADING_ITEM = 0;
    public static final int COMMENTS_ITEM = 1;
    private int LoadingItemPos;
    public boolean loading = false;


    public StoryViewersAdapter(Context context) {
        this.viewersList = new ArrayList<>();
        this.context = context;
    }

    //method to add products as soon as they fetched
    public void addProducts(List<Viewers> commentList) {
        int lastPos = commentList.size();
        this.viewersList.addAll(commentList);
        notifyItemRangeInserted(lastPos, commentList.size());
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Check which view has to be populated
        if (viewType == LOADING_ITEM) {
            View row = inflater.inflate(R.layout.item_custom_row_loading, parent, false);
            return new StoryViewersAdapter.LoadingHolder(row);

        } else if (viewType == COMMENTS_ITEM) {
            View row = inflater.inflate(R.layout.item_followers_list, parent, false);
            return new ViewersHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewersHolder) {
            Viewers list = viewersList.get(position);
            ViewersHolder viewersHolder = (ViewersHolder) holder;

            Glide.with(context)
                    .load(list.getViewer_image())
                    .into(viewersHolder.user_image);

            viewersHolder.remove_followers_btn.setVisibility(View.GONE);
            viewersHolder.full_name_id.setText(list.getViewer_name());
            String username = "@"+list.getViewer_username();
            viewersHolder.username_id.setText(username);

        } else if (holder instanceof StoryViewersAdapter.LoadingHolder) {
            LoadingHolder loadingHolder = (LoadingHolder) holder;
            //get Loading views from  holder here
        }
    }


    @Override
    public int getItemViewType(int position) {
        Viewers viewers = viewersList.get(position);
        if (viewers.isLoading()) {
            return LOADING_ITEM;
        } else {
            return COMMENTS_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return viewersList.size();
    }


    //holds view of loading item
    private class LoadingHolder extends RecyclerView.ViewHolder {
        ProgressBar progress_loader;

        LoadingHolder(View itemView) {
            super(itemView);
            progress_loader = itemView.findViewById(R.id.progress_loader);
        }
    }

    private class ViewersHolder extends RecyclerView.ViewHolder {
        TextView  username_id, full_name_id;
        CircleImageView user_image;
        Button remove_followers_btn;

        ViewersHolder(View itemView) {
            super(itemView);
            user_image = itemView.findViewById(R.id.user_image);
            username_id = itemView.findViewById(R.id.username_id);
            full_name_id = itemView.findViewById(R.id.fullname_id);
            remove_followers_btn = itemView.findViewById(R.id.followers_back_btn);
        }
    }




    //method to show loading
    public void showLoading() {
        Viewers viewers = new Viewers();
        viewers.setLoading(true);
        viewersList.add(viewers);
        LoadingItemPos = viewersList.size();
        notifyItemInserted(viewersList.size());
        loading = true;
    }

    //method to hide loading
    public void hideLoading() {
        if (LoadingItemPos <= viewersList.size()) {
            viewersList.remove(LoadingItemPos - 1);
            notifyItemRemoved(LoadingItemPos);
            loading = false;
        }
    }
}
