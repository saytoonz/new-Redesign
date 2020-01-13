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
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.FollowersListAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import static com.nsromapa.say.frenzapp_redesign.utils.Constants.ALL_FOLLOWERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Followers extends Fragment {
    private View mView;
    private FollowersListAdapter myAdapter;
    private List<com.nsromapa.say.frenzapp_redesign.models.Followers> followers;
    private Button removeBtn;



    public Followers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        followers = new ArrayList<>();
        RecyclerView followersRecyclerView = view.findViewById(R.id.followers_ryc_id);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        myAdapter = new FollowersListAdapter(getContext(), followers,getActivity());
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ALL_FOLLOWERS, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("Followers");

                if (jsonArray.length()>0){

                    for (int i = 0; i < jsonArray.length() ; i++) {
                        JSONObject coming_followers = jsonArray.getJSONObject(i);


                    }

                }else {
                    mView.findViewById(R.id.default_item).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.error_view).setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                mView.findViewById(R.id.error_view).setVisibility(View.VISIBLE);
            }
        }, error -> {

        }){

        };

        followers.add(new com.nsromapa.say.frenzapp_redesign.models.Followers(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@GOAD",
                "Godson Oheneba  ",
                "1577761695",
                "1"));


        followers.add(new com.nsromapa.say.frenzapp_redesign.models.Followers(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@SAY",
                "Godson   ",
                "1577761695",
                "1"));


        followers.add(new com.nsromapa.say.frenzapp_redesign.models.Followers(
                "https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg",
                "@GOAD",
                "Godson Oheneba  ",
                "1577761695",
                "1"));





        myAdapter.notifyDataSetChanged();
    }

}
