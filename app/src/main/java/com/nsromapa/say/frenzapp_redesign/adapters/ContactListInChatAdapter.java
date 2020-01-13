package com.nsromapa.say.frenzapp_redesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nex3z.notificationbadge.NotificationBadge;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.ui.view.AnimCheckBox;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsromapa.say.frenzapp_redesign.utils.Openings.showImageAlertWithJson;


public class ContactListInChatAdapter extends RecyclerView.Adapter<ContactListInChatAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<ChatList> chatLists;
    private boolean isSelection = false;
    private List<String> selectedList = new ArrayList<>();

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

        //UserImage SetUP
        // with its onclick function
        Glide.with(context)
                .load(list.getUserImage())
                .apply(new RequestOptions().placeholder(R.drawable.contact_placeholder))
                .into(holder.user_image);
        holder.user_image.setOnClickListener(v -> showImageAlertWithJson(context,
                list.getUsername(), list.getUserImage(), list.getFriendJson()));


        //Set User/Group/channel name,
        // message and message time
        holder.name.setText(list.getUsername());
        holder.message.setText(list.getMessage());
        holder.timestamp.setText(Utils.getTimeAgo(Long.parseLong(list.getTimestamp())));

        //Notification badge SetUp with number of unread messages
        if ((list.getStatus().equals("1") || list.getStatus().equals("2"))
                && !list.getSender().equals(Utils.getUserUid())
                && Integer.parseInt(list.getNotification_count()) > 0) {
            holder.unreadCount.setVisibility(View.VISIBLE);
            holder.unreadCount.setText(list.getNotification_count());
        } else holder.unreadCount.setVisibility(View.GONE);

        //Is Notification muted setUp
        if (Utils.isFriendChatNotificationMuted(context,
                Utils.getUserInfoFromUserJSON(list.getFriendJson(),
                        "id"))) {
            holder.conversation_mute_icon.setVisibility(View.VISIBLE);
        } else {
            holder.conversation_mute_icon.setVisibility(View.GONE);
        }


        //Message Status setup
        // with images to show for each status
        if (list.getSender().equals(Utils.getUserUid())) {
            holder.delivery_status_last_msg.setVisibility(View.VISIBLE);
            switch (list.getStatus()) {
                case "1":
                    holder.delivery_status_last_msg.setImageResource(R.drawable.ic_tick_sent_grey_24dp);
                    break;
                case "2":
                    holder.delivery_status_last_msg.setImageResource(R.drawable.ic_delivered_tick);
                    break;
                case "3":
                    holder.delivery_status_last_msg.setImageResource(R.drawable.ic_read_status);
                    break;
                default:
                    holder.delivery_status_last_msg.setImageResource(R.drawable.ic_message_pending_gray_24dp);
                    break;
            }
        } else {
            holder.delivery_status_last_msg.setVisibility(View.GONE);
        }


        //Set up selection Icon Visibility
        if (isSelection){
            holder.contact_checkbox.setVisibility(View.VISIBLE);
            if (selectedList.contains(String.valueOf(position)))
                holder.contact_checkbox.setChecked(true, true);
            else
                holder.contact_checkbox.setChecked(false, true);
        }



        //Set up selection by clicking on the check icon
        holder.contact_checkbox.setOnClickListener(v -> {
            if (isSelection) {
                if (holder.contact_checkbox.isChecked()) {
                    holder.contact_checkbox.setChecked(false, true);
                    selectedList.remove(String.valueOf(position));
                } else {
                    if (!selectedList.contains(String.valueOf(position))) {
                        selectedList.add(String.valueOf(position));
                    }
                    holder.contact_checkbox.setChecked(true, true);
                }

            }
        });

        //Set up selection by clicking on the whole View
        holder.mView.setOnClickListener(v -> {
            if (isSelection) {
                if (holder.contact_checkbox.isChecked()) {
                    holder.contact_checkbox.setChecked(false, true);
                    selectedList.remove(String.valueOf(position));
                } else {
                    if (!selectedList.contains(String.valueOf(position))) {
                        selectedList.add(String.valueOf(position));
                    }
                    holder.contact_checkbox.setChecked(true, true);
                }

            }
        });


        //Set up enable selection
        holder.mView.setOnLongClickListener(v -> {
            if (!isSelection){
                enableSelection();
                selectedList.clear();
                selectedList.add(String.valueOf(position));
            }
            return true;
        });

    }

    private void enableSelection() {
        isSelection = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private AnimCheckBox contact_checkbox;
        private CircleImageView user_image;
        private TextView name, message, timestamp;
        private NotificationBadge unreadCount;
        private ImageView conversation_mute_icon, delivery_status_last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            name = mView.findViewById(R.id.name);
            message = mView.findViewById(R.id.message);
            timestamp = mView.findViewById(R.id.messageTime);
            user_image = mView.findViewById(R.id.pic);
            unreadCount = mView.findViewById(R.id.unreadCount);
            conversation_mute_icon = mView.findViewById(R.id.conversation_mute_icon);
            delivery_status_last_msg = mView.findViewById(R.id.delivery_status_last_msg);
            contact_checkbox = mView.findViewById(R.id.contact_checkbox);
        }
    }

    public boolean isSelection() {
        return isSelection;
    }

    public void setSelection(boolean selection) {
        isSelection = selection;
    }
}
