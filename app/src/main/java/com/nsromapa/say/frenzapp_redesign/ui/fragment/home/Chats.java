package com.nsromapa.say.frenzapp_redesign.ui.fragment.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.ChatListAdapter;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.services.ChatListService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class Chats extends Fragment {
    private static ChatListAdapter myAdapter;
    private static List<ChatList> chatList;
    private Context context;
    private static LinearLayout default_item, selectionMenuHolder;
    private static FancyButton selectionCounter;
    private static FloatingActionButton closeSelection, muteSelected, deleteSelected, setReadSelected, selectAll;

    public Chats() {
    }

    public Chats(Context context) {
        this.context = context;
    }


    public static void updateChatList(List<ChatList> chatLists) {
        if (chatLists.isEmpty()) {
            if (chatList.isEmpty()) {
                default_item.setVisibility(View.VISIBLE);
            } else {
                default_item.setVisibility(View.GONE);
            }
        } else {
            if (!chatList.isEmpty())
                chatList.clear();
            default_item.setVisibility(View.GONE);
            chatList.addAll(chatLists);
            myAdapter.notifyDataSetChanged();
        }
    }

    public static void updateChatList(ChatList chatLists) {
        chatList.add(chatLists);
        myAdapter.notifyDataSetChanged();
    }

    public static void disableSelection() {
        myAdapter.disableSelection();
        setSelectionCount("0");
        selectionMenuHolder.setVisibility(View.GONE);
    }

    public static void setSelectionCount(String count) {
        selectionCounter.setText(count);
    }

    public static void showMenuSelectionView() {
        selectionMenuHolder.setVisibility(View.VISIBLE);
    }

    public static void checkSelectIcon() {
        if (myAdapter.getSelectedList().size() > 0) {
            selectAll.setImageResource(R.drawable.icons8_uncheck_all_100);
        } else {
            selectAll.setImageResource(R.drawable.icons8_check_all_100);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        default_item = view.findViewById(R.id.default_item);
        selectionMenuHolder = view.findViewById(R.id.selectionMenuHolder);
        selectionCounter = view.findViewById(R.id.selectionCounter);
        closeSelection = view.findViewById(R.id.closeSelection);
        muteSelected = view.findViewById(R.id.muteSelected);
        deleteSelected = view.findViewById(R.id.deleteSelected);
        setReadSelected = view.findViewById(R.id.setReadSelected);
        selectAll = view.findViewById(R.id.selectAll);

        chatList = new ArrayList<>();
        RecyclerView chatRecyclerView = view.findViewById(R.id.chats_list_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new ChatListAdapter(getContext(), getActivity(), chatList);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setAdapter(myAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContacts();

        closeSelection.setOnClickListener(v -> disableSelection());
        selectAll.setOnClickListener(v -> myAdapter.SelectAll());

        muteSelected.setOnClickListener(v -> {
            List<String> selectedList = myAdapter.getSelectedList();
            for (int i = 0; i < selectedList.size(); i++) {
                myAdapter.MuteUnMute(Integer.parseInt(selectedList.get(i)));
            }
            disableSelection();
        });

        setReadSelected.setOnClickListener(v -> {
            List<String> selectedList = myAdapter.getSelectedList();
            for (int i = 0; i < selectedList.size(); i++) {
                myAdapter.setAllMessageRed(Integer.parseInt(selectedList.get(i)));
            }
            disableSelection();
        });


        deleteSelected.setOnClickListener(v -> {
            String[] stringList = {"Delete From ChatList only", "Clear Messages", "Both"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Choose an option");
            builder.setItems(stringList, (dialog, which) -> {
              for (int i = 0; i<myAdapter.getSelectedList().size(); i++){
                  if (which == 0){
                      myAdapter.deleteChat(Integer.parseInt(myAdapter.getSelectedList().get(i)), "chatList_only");
                  }else if (which == 1){
                      myAdapter.deleteChat(Integer.parseInt(myAdapter.getSelectedList().get(i)), "messages_only");
                  }else if (which == 2){
                      myAdapter.deleteChat(Integer.parseInt(myAdapter.getSelectedList().get(i)), "both");
                  }
              }
                disableSelection();
            });
            builder.show();
        });
    }

    private void getContacts() {
        String response = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("chatList", "");
        if (!TextUtils.isEmpty(response)) {
            default_item.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("chatLists");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject chatListObj = jsonArray.getJSONObject(i);
                    JSONObject poster_info = chatListObj.getJSONObject("1");

                    ChatList chat = new ChatList(
                            chatListObj.getString("sender_id"),
                            chatListObj.getString("receiver_id"),
                            chatListObj.getString("last_message"),
                            poster_info.getString("image"),
                            poster_info.getString("username"),
                            chatListObj.getString("0"),
                            chatListObj.getString("message_status"),
                            chatListObj.getString("message_type"),
                            chatListObj.getString("notification_count_sender"),
                            chatListObj.getString("notification_count_receiver"),
                            getResources().getString(R.string.offline),
                            chatListObj.getString("chat_type"),
                            poster_info.toString()
                    );

                    updateChatList(chat);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            default_item.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        context.startService(new Intent(context, ChatListService.class));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.stopService(new Intent(context, ChatListService.class));
    }


}
