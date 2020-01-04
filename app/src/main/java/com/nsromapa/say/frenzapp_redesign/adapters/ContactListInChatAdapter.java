package com.nsromapa.say.frenzapp_redesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.grantland.widget.AutofitTextView;


public class ContactListInChatAdapter extends RecyclerView.Adapter<ContactListInChatAdapter.ViewHolder> {

    private Context context;
    private List<ChatList> chatLists;
    private  Activity activity;

    public ContactListInChatAdapter(Context context, List<ChatList> chatListPost, Activity activity) {
        this.context = context;
        this.chatLists = chatListPost;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ContactListInChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListInChatAdapter.ViewHolder holder, int position) {
        ChatList list = chatLists.get(position);

        Glide.with(context)
                .load(list.getUserimage())
                .into(holder.user_image);


        holder.name.setText(list.getUsername());
        holder.message.setText(list.getMessage());
        holder.timestamp.setText(list.getTimestamp());


    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView user_image;
        private TextView name, message, timestamp;
        private ImageView read;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            name = mView.findViewById(R.id.Chat_List_name_Id);
            message = mView.findViewById(R.id.Chat_List_message_Id);
            timestamp = mView.findViewById(R.id.Chat_List_time_Id);
            user_image = mView.findViewById(R.id.user_image);
        }
    }
}
