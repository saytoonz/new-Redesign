package com.nsromapa.say.frenzapp_redesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.models.FollowersList;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.ViewHolder> {

    private Context context;
    private List<FollowersList> followersLists;
    private  Activity activity;

    public FollowersListAdapter(Context context, List<FollowersList> followersLists, Activity activity) {
        this.context = context;
        this.followersLists = followersLists;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FollowersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_followers_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersListAdapter.ViewHolder holder, int position) {
        FollowersList list = followersLists.get(position);

        Glide.with(context)
                .load(list.getImage())
                .into(holder.follower_image);


        holder.username.setText(list.getUsername());
        holder.fullname.setText(list.getFullname());



    }

    @Override
    public int getItemCount() {
        return followersLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView follower_image;
        private TextView username, fullname;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            username = mView.findViewById(R.id.username_id);
            fullname = mView.findViewById(R.id.fullname_id);
            follower_image = mView.findViewById(R.id.user_image);
        }
    }
}
