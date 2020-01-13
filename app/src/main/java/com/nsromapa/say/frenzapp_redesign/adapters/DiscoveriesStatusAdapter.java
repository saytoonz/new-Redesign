package com.nsromapa.say.frenzapp_redesign.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.ui.activities.DiscoverActivity;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

import static com.nsromapa.say.frenzapp_redesign.utils.getTextBackground.setImageHolderBg;

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
        if (holder instanceof DiscoveriesHolder) {
            Discoveries list = discoveriesList.get(position);
            DiscoveriesHolder discoveriesHolder = (DiscoveriesHolder) holder;

            if (list.getMimeType().contains("image")) {
                discoveriesHolder.discovery_play.setVisibility(View.GONE);
                discoveriesHolder.discovery_text.setVisibility(View.GONE);
                discoveriesHolder.discovery_photo.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(list.getMediaUrl())
                        .into(discoveriesHolder.discovery_photo);

            } else if (list.getMimeType().contains("text")) {
                discoveriesHolder.discovery_play.setVisibility(View.GONE);
                discoveriesHolder.discovery_photo.setVisibility(View.GONE);
                discoveriesHolder.discovery_text.setVisibility(View.VISIBLE);

                discoveriesHolder.discovery_text.setText(list.getDescription());
                setImageHolderBg(list.getBackground(), discoveriesHolder.discovery_text);

            } else if (list.getMimeType().contains("video")) {
                discoveriesHolder.discovery_text.setVisibility(View.GONE);
                discoveriesHolder.discovery_play.setVisibility(View.VISIBLE);
                discoveriesHolder.discovery_photo.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(list.getMediaUrl())
                        .into(discoveriesHolder.discovery_photo);

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        int rnd = (int) (Math.random() * 4);
                        if (rnd == 0)
                            discoveriesHolder.discovery_play.setColorFilter(Color.BLUE);
                        if (rnd == 1)
                            discoveriesHolder.discovery_play.setColorFilter(Color.RED);
                        if (rnd == 2)
                            discoveriesHolder.discovery_play.setColorFilter(Color.GRAY);
                        if (rnd == 3)
                            discoveriesHolder.discovery_play.setColorFilter(Color.BLACK);

                        discoveriesHolder.discovery_play.invalidate();
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(runnable, 2000);
            }


            discoveriesHolder.card_view.setOnClickListener(v -> {
                Intent intent = new Intent(context, DiscoverActivity.class);
                intent.putExtra("discovery", list);
                context.startActivity(intent);
            });


        } else if (holder instanceof LoadingHolder) {
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
        ImageView discovery_photo, discovery_play;
        AutofitTextView discovery_text;
        CardView card_view;

        DiscoveriesHolder(View itemView) {
            super(itemView);
            card_view = itemView.findViewById(R.id.card_view);
            discovery_photo = itemView.findViewById(R.id.discovery_photo);
            discovery_text = itemView.findViewById(R.id.discovery_text);
            discovery_play = itemView.findViewById(R.id.discover_play);
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
