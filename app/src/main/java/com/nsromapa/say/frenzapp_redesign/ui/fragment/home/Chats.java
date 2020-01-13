package com.nsromapa.say.frenzapp_redesign.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.ContactListInChatAdapter;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.services.ChatListService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chats extends Fragment {
    private static ContactListInChatAdapter myAdapter;
    private static List<ChatList> chatList;
    private Context context;
    private static LinearLayout default_item;

    public Chats(Context context) {
    this.context = context;
    }



    public static void updateChatList(List<ChatList> chatLists){
        if (chatLists.isEmpty()){
            if (chatList.isEmpty()){
                default_item.setVisibility(View.VISIBLE);
            }else{
                default_item.setVisibility(View.GONE);
            }
        }else{
           if (!chatList.isEmpty())
               chatList.clear();
            default_item.setVisibility(View.GONE);
            chatList.addAll(chatLists);
            myAdapter.notifyDataSetChanged();
        }
    }

    public static void updateChatList(ChatList chatLists){
        chatList.add(chatLists);
        myAdapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        default_item = view.findViewById(R.id.default_item);
        chatList = new ArrayList<>();
        RecyclerView chatRecyclerView = view.findViewById(R.id.chats_list_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new ContactListInChatAdapter(getContext(), getActivity(),chatList);
        chatRecyclerView.setLayoutManager(mLayoutManager);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setAdapter(myAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContacts();
    }

    private void getContacts() {
       String response =  PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("chatList","");
       if (!TextUtils.isEmpty(response)){
           default_item.setVisibility(View.GONE);
           try {

               Log.e("PreferenceManager", response );
               JSONObject jsonObject = new JSONObject(response);
               JSONArray jsonArray = jsonObject.getJSONArray("chatLists");

               for (int i = 0; i < jsonArray.length(); i++) {
                   JSONObject chatListObj = jsonArray.getJSONObject(i);
                   JSONObject poster_info = chatListObj.getJSONObject("1");

                   Log.e("PreferenceManager", chatListObj.toString() );

                   ChatList chat =  new ChatList(
                           chatListObj.getString("sender_id"),
                           chatListObj.getString("receiver_id"),
                           chatListObj.getString("last_message"),
                           poster_info.getString("image"),
                           poster_info.getString("username"),
                           chatListObj.getString("0"),
                           chatListObj.getString("status"),
                           chatListObj.getString("message_type"),
                           chatListObj.getString("notification_count"),
                           poster_info.toString()
                   );

                       updateChatList(chat);
               }





           } catch (JSONException e) {
               e.printStackTrace();
           }
       }else{
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
