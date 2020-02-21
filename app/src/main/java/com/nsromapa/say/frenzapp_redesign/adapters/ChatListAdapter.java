package com.nsromapa.say.frenzapp_redesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nex3z.notificationbadge.NotificationBadge;
import com.nsromapa.emoticompack.samsung.SamsungEmoticonProvider;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.services.BackServices;
import com.nsromapa.say.frenzapp_redesign.services.ChatViewActivityServices;
import com.nsromapa.say.frenzapp_redesign.ui.activities.ChatViewActivity;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.ui.view.AnimCheckBox;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.chatFragment_isSelectionMode;
import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.checkSelectIcon;
import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.setSelectionCount;
import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.showMenuSelectionView;
import static com.nsromapa.say.frenzapp_redesign.utils.Openings.showImageAlertWithJson;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<ChatList> chatLists;
    private List<String> selectedList = new ArrayList<>();

    public ChatListAdapter(Context context, Activity activity, List<ChatList> chatListPost) {
        this.context = context;
        this.chatLists = chatListPost;
        this.activity = activity;
        chatFragment_isSelectionMode = false;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        ChatList list = chatLists.get(position);
        list.setItemPosition(position);

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
        if ((list.getMessage_status().equals("1") || list.getMessage_status().equals("2"))) {
            if (list.getSender().equals(Utils.getUserUid(context)) && Integer.parseInt(list.getNotification_count_sender()) > 0) {
                holder.unreadCount.setVisibility(View.VISIBLE);
                holder.unreadCount.setText(list.getNotification_count_sender());

            } else if (!list.getSender().equals(Utils.getUserUid(context)) && Integer.parseInt(list.getNotification_count_receiver()) > 0) {
                holder.unreadCount.setVisibility(View.VISIBLE);
                holder.unreadCount.setText(list.getNotification_count_receiver());
            } else {
                holder.unreadCount.setVisibility(View.GONE);
            }

        } else holder.unreadCount.setVisibility(View.GONE);

        //Is Notification muted setUp
        if (Utils.isFriendChatNotificationMuted(context, Utils.getUserInfoFromUserJSON(list.getFriendJson(),"id"))) {
            holder.conversation_mute_icon.setVisibility(View.VISIBLE);
        } else {
            holder.conversation_mute_icon.setVisibility(View.GONE);
        }


        //Message Status setup
        // with images to show for each status
        if (list.getSender().equals(Utils.getUserUid(context))) {
            holder.delivery_status_last_msg.setVisibility(View.VISIBLE);
            switch (list.getMessage_status()) {
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
        if (chatFragment_isSelectionMode) {
            holder.contact_checkbox.setVisibility(View.VISIBLE);
            if (selectedList.contains(String.valueOf(position))) {
                setSelectionCount(String.valueOf(selectedList.size()));
                holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.button_focused));
                holder.contact_checkbox.setChecked(true, true);
                checkSelectIcon();
            } else {
                setSelectionCount(String.valueOf(selectedList.size()));
                holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.white));
                holder.contact_checkbox.setChecked(false, true);
                checkSelectIcon();
            }
        } else {
            holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.contact_checkbox.setChecked(false, true);
            holder.contact_checkbox.setVisibility(View.GONE);
        }


        //Set up selection by clicking on the check icon
        holder.contact_checkbox.setOnClickListener(v -> {
            if (chatFragment_isSelectionMode) {
                if (holder.contact_checkbox.isChecked()) {
                    holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.contact_checkbox.setChecked(false, true);
                    selectedList.remove(String.valueOf(position));
                    setSelectionCount(String.valueOf(selectedList.size()));
                    checkSelectIcon();
                } else {
                    holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.button_focused));
                    if (!selectedList.contains(String.valueOf(position))) {
                        selectedList.add(String.valueOf(position));
                    }
                    holder.contact_checkbox.setChecked(true, true);
                    setSelectionCount(String.valueOf(selectedList.size()));
                    checkSelectIcon();
                }

            }
        });

        //Set up selection by clicking on the whole View
        holder.mView.setOnClickListener(v -> {
            if (chatFragment_isSelectionMode) {
                if (holder.contact_checkbox.isChecked()) {
                    holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.white));
                    holder.contact_checkbox.setChecked(false, true);
                    selectedList.remove(String.valueOf(position));
                    setSelectionCount(String.valueOf(selectedList.size()));
                    checkSelectIcon();
                } else {
                    holder.chatlist_item_laytout.setBackgroundColor(context.getResources().getColor(R.color.button_focused));
                    if (!selectedList.contains(String.valueOf(position))) {
                        selectedList.add(String.valueOf(position));
                    }
                    holder.contact_checkbox.setChecked(true, true);
                    setSelectionCount(String.valueOf(selectedList.size()));
                    checkSelectIcon();
                }

            } else {
                Intent intent = new Intent(context, ChatViewActivity.class);
                intent.putExtra("chatType", list.getChat_type());
                intent.putExtra("thisUserId", Utils.getUserInfoFromUserJSON(list.getFriendJson(), "id"));
                intent.putExtra("thisUserJson", list.getFriendJson());
                intent.putExtra("backToMain", false);
                context.startActivity(intent);

            }
        });


        //Set up enable selection
        holder.mView.setOnLongClickListener(v -> {
            if (!chatFragment_isSelectionMode) {
                enableSelection();
                selectedList.clear();
                selectedList.add(String.valueOf(position));
                setSelectionCount(String.valueOf(selectedList.size()));
            }
            return true;
        });


        if (list.getOnline_status().equals(activity.getResources().getString(R.string.online)))
            holder.online_status_image.setVisibility(View.VISIBLE);
        else if (list.getOnline_status().equals("typing_" + Utils.getUserUid(context))) {
            holder.online_status_image.setVisibility(View.VISIBLE);
            holder.message.setText(activity.getResources().getString(R.string.typing));
        } else if (list.getOnline_status().contains("typing_")) {
            holder.online_status_image.setVisibility(View.VISIBLE);
        } else {
            holder.message.setText(list.getMessage());
            holder.online_status_image.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private AnimCheckBox contact_checkbox;
        private CircleImageView user_image, online_status_image;
        private TextView name, timestamp;
        private EmoticonTextView message;
        private NotificationBadge unreadCount;
        private LinearLayout chatlist_item_laytout;
        private ImageView conversation_mute_icon, delivery_status_last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            name = mView.findViewById(R.id.name);
            chatlist_item_laytout = mView.findViewById(R.id.chatlist_item_laytout);
            message = mView.findViewById(R.id.message);
            timestamp = mView.findViewById(R.id.messageTime);
            user_image = mView.findViewById(R.id.pic);
            unreadCount = mView.findViewById(R.id.unreadCount);
            conversation_mute_icon = mView.findViewById(R.id.conversation_mute_icon);
            delivery_status_last_msg = mView.findViewById(R.id.delivery_status_last_msg);
            contact_checkbox = mView.findViewById(R.id.contact_checkbox);
            online_status_image = mView.findViewById(R.id.online_status_image);
            message.setEmoticonProvider(SamsungEmoticonProvider.create());
        }
    }


    private void enableSelection() {
        chatFragment_isSelectionMode = true;
        notifyDataSetChanged();
        showMenuSelectionView();
    }

    public void disableSelection() {
        chatFragment_isSelectionMode = false;
        selectedList.clear();
        notifyDataSetChanged();
        checkSelectIcon();
    }

    public List<String> getSelectedList() {
        return selectedList;
    }

    public void MuteUnMute(int position) {
        ChatList user = chatLists.get(position);
        String uid = Utils.getUserInfoFromUserJSON(user.getFriendJson(), "id");
        Utils.setFriendChatNotificationMuted(context, uid);
        notifyDataSetChanged();
    }


    public void setAllMessageRed(int position) {
        ChatList user = chatLists.get(position);
        String uid = Utils.getUserInfoFromUserJSON(user.getFriendJson(), "id");
        Intent intent = new Intent(context, BackServices.class);
        intent.putExtra("doWhat", "mark_all_as_read");
        intent.putExtra("friendId", uid);
        context.startService(intent);
    }


    public void SelectAll(){
        if (selectedList.size()>0){
            selectedList.clear();
            setSelectionCount("0");
            notifyDataSetChanged();
            checkSelectIcon();
        }else {
            for (int i = 0; i < chatLists.size(); i++){
                selectedList.add(String.valueOf(chatLists.get(i).getItemPosition()));
                setSelectionCount(String.valueOf(selectedList.size()));
                notifyDataSetChanged();
                checkSelectIcon();
            }
        }
    }



    public void deleteChat(int position, String deleteType) {
        ChatList user = chatLists.get(position);
        String uid = Utils.getUserInfoFromUserJSON(user.getFriendJson(), "id");
        String friendName = Utils.getUserInfoFromUserJSON(user.getFriendJson(), "username");
        String friendImage = Utils.getUserInfoFromUserJSON(user.getFriendJson(), "image");
        Intent intent = new Intent(context, BackServices.class);
        intent.putExtra("doWhat", "delete_chat");
        intent.putExtra("deleteType", deleteType);
        intent.putExtra("friendId", uid);
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendImage", friendImage);
        context.startService(intent);
    }


}
