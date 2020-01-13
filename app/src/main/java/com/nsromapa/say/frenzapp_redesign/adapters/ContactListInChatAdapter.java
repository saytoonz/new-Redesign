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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.Util;
import com.nex3z.notificationbadge.NotificationBadge;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsromapa.say.frenzapp_redesign.utils.Openings.showImageAlertWithJson;
import static com.nsromapa.say.frenzapp_redesign.utils.Openings.showImageAlertWithUserId;


public class ContactListInChatAdapter extends RecyclerView.Adapter<ContactListInChatAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<ChatList> chatLists;

    public ContactListInChatAdapter(Context context, Activity activity, List<ChatList> chatListPost) {
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
                .load(list.getUserImage())
                .apply(new RequestOptions().placeholder(R.drawable.contact_placeholder))
                .into(holder.user_image);
        holder.user_image.setOnClickListener(v -> showImageAlertWithJson(context, list.getUsername(), list.getUserImage(), list.getFriendJson()));

        holder.username.setText(list.getUsername());
        holder.message.setText(list.getMessage());
        holder.timestamp.setText(Utils.getTimeAgo(Long.parseLong(list.getTimestamp())));

        if (list.getStatus().equals("1") || list.getStatus().equals("2")) {
            holder.unreadCount.setVisibility(View.VISIBLE);
            holder.unreadCount.setText(list.getNotification_count());
        } else holder.unreadCount.setVisibility(View.GONE);

        if (Utils.isFriendChatNotificationMuted(context, Utils.getUserInfoFromUserJSON(list.getFriendJson(), "id"))){
            holder.conversation_mute_icon.setVisibility(View.VISIBLE);
        }else{
           holder.conversation_mute_icon.setVisibility(View.GONE);
        }

        if (list.getSender().equals(Utils.getUserUid())){
            holder.delivery_status_last_msg.setVisibility(View.GONE);
        }else{
            holder.delivery_status_last_msg.setVisibility(View.VISIBLE);
            if (list.getStatus().equals("1")){
//                holder.delivery_status_last_msg.setImageResource();
            }else if(list.getStatus().equals("2")){

            }else{

            }
        }

    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private CircleImageView user_image;
        private TextView username, message, timestamp;
        private NotificationBadge unreadCount;
        private ImageView conversation_mute_icon, delivery_status_last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            username = mView.findViewById(R.id.username);
            message = mView.findViewById(R.id.message);
            timestamp = mView.findViewById(R.id.messageTime);
            user_image = mView.findViewById(R.id.pic);
            unreadCount = mView.findViewById(R.id.unreadCount);
            conversation_mute_icon = mView.findViewById(R.id.conversation_mute_icon);
            delivery_status_last_msg = mView.findViewById(R.id.delivery_status_last_msg);
        }
    }
}
