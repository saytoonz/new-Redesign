package com.nsromapa.say.frenzapp_redesign.ui.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.ContactListInChatAdapter;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;

import java.util.ArrayList;
import java.util.List;

public class Chats extends Fragment {
    private ContactListInChatAdapter myAdapter;
    private List<ChatList> chatList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatList = new ArrayList<>();
        RecyclerView chatRecyclerView = view.findViewById(R.id.chats_list_recyclerview_id);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new ContactListInChatAdapter(getContext(),chatList,getActivity());
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
        chatList.add(new ChatList(
                "This is the message 1",
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "Another ",
                "1577761695"
        ));

        chatList.add(new ChatList(
                "This is the message 2",
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "username",
                "1577761695"
        ));

        chatList.add(new ChatList(
                "This is the message 3",
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "My name",
                "1577761695"
        ));

        chatList.add(new ChatList(
                "This is the message 4",
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "Heysh",
                "1577761695"
        ));

        myAdapter.notifyDataSetChanged();
    }


}
