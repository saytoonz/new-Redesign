package com.nsromapa.say.frenzapp_redesign.ui.fragment;


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
import com.nsromapa.say.frenzapp_redesign.adapters.FollowersListAdapter;
import com.nsromapa.say.frenzapp_redesign.models.ChatList;
import com.nsromapa.say.frenzapp_redesign.models.FollowersList;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Followers extends Fragment {

    private FollowersListAdapter myAdapter;
    private List<FollowersList> followersLists;



    public Followers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        followersLists = new ArrayList<>();
        RecyclerView followersRecyclerView = view.findViewById(R.id.followers_ryc_id);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new FollowersListAdapter(getContext(),followersLists,getActivity());
        followersRecyclerView.setLayoutManager(mLayoutManager);
        followersRecyclerView.setHasFixedSize(true);
        followersRecyclerView.setAdapter(myAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFollowers();
    }

    private void getFollowers() {
        followersLists.add(new FollowersList(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@GOAD",
                "Godson Oheneba Dacosta ",
                "1577761695"
        ));


        followersLists.add(new FollowersList(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@SAY",
                "SamuelAnin Yeboah ",
                "1577761695"
        ));


        followersLists.add(new FollowersList(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@POAD",
                "Godson Oheneba Dacosta ",
                "1577761695"
        ));


        followersLists.add(new FollowersList(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@ARCPOPE",
                "Nyamekye Boi ",
                "1577761695"
        ));



        myAdapter.notifyDataSetChanged();
    }

}
