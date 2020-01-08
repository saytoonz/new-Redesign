package com.nsromapa.say.frenzapp_redesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.ui.activities.DiscoverActivity;

import java.util.ArrayList;
import java.util.List;

public class DiscoveriesStatusAdapter extends RecyclerView.Adapter {
    private View view;
    private List<Discoveries> discoveriesList;
    private Context context;
    public static final int LOADING_ITEM = 0;
    public static final int DISCOVERIES_ITEM = 1;
    private int LoadingItemPos;
    public boolean loading = false;

    public DiscoveriesStatusAdapter(Context context) {
        this.discoveriesList = new ArrayList<>();
        this.context = context;
    }

    //method to add products as soon as they fetched
    public void addProducts(List<Discoveries> itemList) {
        int lastPos = itemList.size();
        this.discoveriesList.addAll(itemList);
        notifyItemRangeInserted(lastPos, itemList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //Check which view has to be populated
        if (viewType == LOADING_ITEM) {
            View row = inflater.inflate(R.layout.item_custom_row_loading, parent, false);
            return new LoadingHolder(row);

        } else if (viewType == DISCOVERIES_ITEM) {
            View row = inflater.inflate(R.layout.item_status_discoveries, parent, false);
            return new DiscoveriesHolder(row);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DiscoveriesHolder){
            Discoveries list = discoveriesList.get(position);
            DiscoveriesHolder discoveriesHolder = (DiscoveriesHolder) holder;

            Glide.with(context)
                    .load(list.getMediaUrl())
                    .into(discoveriesHolder.discovery_photo);

            discoveriesHolder.discovery_photo.setOnClickListener(v -> {
                Intent intent = new Intent(context, DiscoverActivity.class);
                intent.putExtra("discovery", list);
                context.startActivity(intent);
            });



        }else if (holder instanceof LoadingHolder){
            LoadingHolder loadingHolder = (LoadingHolder) holder;
            //get Loading views from  holder here
        }
    }

    @Override
    public int getItemViewType(int position) {
        Discoveries discoveries = discoveriesList.get(position);
        if (discoveries.isLoading()) {
            return LOADING_ITEM;
        } else {
            return DISCOVERIES_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return discoveriesList.size();
    }


    //holds view of loading item
    private class LoadingHolder extends RecyclerView.ViewHolder {
        ProgressBar progress_loader;
        LoadingHolder(View itemView) {
            super(itemView);
            progress_loader = itemView.findViewById(R.id.progress_loader);
        }
    }

    private class DiscoveriesHolder extends RecyclerView.ViewHolder {
        ImageView discovery_photo;
        DiscoveriesHolder(View itemView) {
            super(itemView);
            discovery_photo = itemView.findViewById(R.id.discovery_photo);
        }
    }



    //method to show loading
    public void showLoading() {
        Discoveries discoveries = new Discoveries();
        discoveries.setLoading(true);
        discoveriesList.add(discoveries);
        LoadingItemPos = discoveriesList.size();
        notifyItemInserted(discoveriesList.size());
        loading = true;
    }

    //method to hide loading
    public void hideLoading() {
        if (LoadingItemPos <= discoveriesList.size()) {
            discoveriesList.remove(LoadingItemPos - 1);
            notifyItemRemoved(LoadingItemPos);
            loading = false;
        }
    }
}
