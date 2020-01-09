package com.nsromapa.say.frenzapp_redesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.Followers;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.ViewHolder> {

    private Context context;
    private List<Followers> followers;
    private  Activity activity;

    public FollowersListAdapter(Context context, List<Followers> followers, Activity activity) {
        this.context = context;
        this.followers = followers;
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
        Followers list = followers.get(position);

        Glide.with(context)
                .load(list.getImage())
                .into(holder.follower_image);


        holder.username.setText(list.getUsername());
        holder.fullname.setText(list.getFullname());
        holder.buttonRemove.setOnClickListener(v -> Toast.makeText(context, list.getUser_id(), Toast.LENGTH_SHORT).show());
        holder.follower_image.setOnClickListener(v -> Toast.makeText(context, list.getImage(), Toast.LENGTH_SHORT).show());
        holder.linearLayout.setOnClickListener(v -> Toast.makeText(context, list.getUsername(), Toast.LENGTH_SHORT).show());


    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView follower_image;
        private TextView username, fullname;
        private Button buttonRemove;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            username = mView.findViewById(R.id.username_id);
            fullname = mView.findViewById(R.id.fullname_id);
            follower_image = mView.findViewById(R.id.user_image);
            buttonRemove = mView.findViewById(R.id.remove_followers_btn);
            linearLayout = mView.findViewById(R.id.linearLayout);


        }
    }
}
