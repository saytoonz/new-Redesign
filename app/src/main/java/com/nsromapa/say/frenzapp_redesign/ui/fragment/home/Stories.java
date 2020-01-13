package com.nsromapa.say.frenzapp_redesign.ui.fragment.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.DiscoveriesStatusAdapter;
import com.nsromapa.say.frenzapp_redesign.adapters.StoryStatusAdapter;
import com.nsromapa.say.frenzapp_redesign.helpers.EndlessScrollListener;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.models.StoriesData;
import com.nsromapa.say.frenzapp_redesign.models.StoryStatus;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.DISCOVER_STORIES;


public class Stories extends Fragment {

    private List<StoryStatus> statusList;
    private StoryStatusAdapter statusAdapter;
    private DiscoveriesStatusAdapter discoveriesStatusAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stories, container, false);
        statusList = new ArrayList<>();
        RecyclerView storyRecycler = view.findViewById(R.id.story_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        statusAdapter = new StoryStatusAdapter(statusList, container.getContext(), getActivity());
        storyRecycler.setLayoutManager(linearLayoutManager);
        storyRecycler.setHasFixedSize(true);
        storyRecycler.setAdapter(statusAdapter);

        RecyclerView discovery_recycler = view.findViewById(R.id.discovery_recycler);
        discovery_recycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        discoveriesStatusAdapter = new DiscoveriesStatusAdapter(getContext());
        discovery_recycler.setLayoutManager(gridLayoutManager);
        EndlessScrollListener endlessScrollListener = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, int quantity) {
                if (!discoveriesStatusAdapter.loading)
                    getDiscoveriesListData(String.valueOf(quantity));
            }
        };
        //to give loading item full single row
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (discoveriesStatusAdapter.getItemViewType(position)) {
                    case DiscoveriesStatusAdapter.DISCOVERIES_ITEM:
                        return 1;
                    case DiscoveriesStatusAdapter.LOADING_ITEM:
                        return 2; //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });

        discovery_recycler.addOnScrollListener(endlessScrollListener);
        discovery_recycler.setAdapter(discoveriesStatusAdapter);
        endlessScrollListener.onLoadMore(0, 0, 21);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMyStories_thenFollowingsStories();
    }

    private void getMyStories_thenFollowingsStories() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> {
                    Log.e("Volley Result", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Stories");

                        if (jsonArray.length() > 0) {
                            JSONObject theStories = jsonArray.getJSONObject(0);
                            statusList.add(new StoryStatus(
                                    Utils.getUserUid(),
                                    "Your Stories",
                                    Utils.getUserImage(),
                                    theStories.getString("media_url"),
                                    prepareStoriesList(jsonArray.toString())));

                            statusAdapter.notifyDataSetChanged();

                        } else {
                            statusList.add(new StoryStatus(
                                    Utils.getUserUid(),
                                    "Your Stories",
                                    Utils.getUserImage(),
                                    Utils.getUserImage(),
                                    prepareStoriesList("")));

                            statusAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        statusList.add(new StoryStatus(
                                Utils.getUserUid(),
                                "Your Stories",
                                Utils.getUserImage(),
                                Utils.getUserImage(),
                                prepareStoriesList("")));
                    }


                    getFollowingsStories();


                },
                error -> {

                    statusList.add(new StoryStatus(
                            Utils.getUserUid(),
                            "Your Stories",
                            Utils.getUserImage(),
                            Utils.getUserImage(),
                            prepareStoriesList("")));
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("my_own", "true");
                return postMap;
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            Volley.newRequestQueue(Objects.requireNonNull(getContext())).add(stringRequest);
        else
            Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);

    }

    private void getFollowingsStories() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> {
                    Log.e("Volley Result", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Stories");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONArray singlePosterPosts = jsonArray.getJSONArray(i);
                            JSONObject theStories = singlePosterPosts.getJSONObject(0);

//                            for (int j = 0; j < singlePosterPosts.length(); j++) {
                            JSONObject poster_info = theStories.getJSONObject("1");
                            if (!poster_info.getString("id").equals(Utils.getUserUid()))
                                statusList.add(new StoryStatus(
                                        poster_info.getString("id"),
                                        poster_info.getString("username"),
                                        poster_info.getString("image"),
                                        singlePosterPosts.getJSONObject(0).getString("media_url"),
                                        prepareStoriesList(singlePosterPosts.toString())));

                            statusAdapter.notifyDataSetChanged();
//                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("following", Utils.getMyFollowings());
//                postMap.put("load", String.valueOf(loadCount));
                return postMap;
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            Volley.newRequestQueue(Objects.requireNonNull(getContext())).add(stringRequest);
        else
            Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);

    }


    private ArrayList<StoriesData> prepareStoriesList(String stories) {
        ArrayList<StoriesData> mStoriesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(stories);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Log.e("getStories", "onBindViewHolder: " + jsonObject);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject story = jsonArray.getJSONObject(i);
                mStoriesList.add(new StoriesData(
                        story.getString("id"),
                        story.getString("media_url"),
                        story.getString("type"),
                        story.getString("description"),
                        story.getString("background"),
                        story.getString("0")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mStoriesList;
    }


    private void getDiscoveriesListData(String quantity) {
        //show loading in recyclerview
        discoveriesStatusAdapter.showLoading();
        List<Discoveries> discoveries = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> {
                    Log.e("Volley Result", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Discoveries");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject discoveryPosts = jsonArray.getJSONObject(i);
                            JSONObject poster_info = discoveryPosts.getJSONObject("1");

                            discoveries.add(new Discoveries(
                                    discoveryPosts.getString("id"),
                                    discoveryPosts.getString("media_url"),
                                    discoveryPosts.getString("type"),
                                    discoveryPosts.getString("description"),
                                    discoveryPosts.getString("background"),
                                    discoveryPosts.getString("0"),
                                    discoveryPosts.getString("likes"),
                                    discoveryPosts.getString("watches"),
                                    discoveryPosts.getString("comments"),
                                    discoveryPosts.getString("2"),
                                    poster_info.toString()
                            ));
                        }


                        new Handler().postDelayed(() -> {
                            //hide loading
                            discoveriesStatusAdapter.hideLoading();
                            //add products to recyclerView
                            discoveriesStatusAdapter.addProducts(discoveries);
                        }, 1000);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("discoveries", "true");
                postMap.put("limit", quantity);
                return postMap;
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            Volley.newRequestQueue(Objects.requireNonNull(getContext())).add(stringRequest);
        else
            Volley.newRequestQueue(Objects.requireNonNull(getActivity())).add(stringRequest);

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        statusAdapter.onActivityResult(requestCode, resultCode, data);
    }
}
