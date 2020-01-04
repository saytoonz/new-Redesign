package com.nsromapa.say.frenzapp_redesign.ui.fragment.home;

import android.content.Context;
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
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
            public void onLoadMore(int page, int totalItemsCount) {
                if (!discoveriesStatusAdapter.loading)
                    getDiscoveriesListData();
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
        endlessScrollListener.onLoadMore(0, 0);
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
                        story.getString("0")
                ));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        mStoriesList.add(new StoriesData("https://4.bp.blogspot.com/-IvTudMG6xNs/XPlXQ7Vxl_I/AAAAAAAAVcc/rZQR7Jcvbzoor3vO_lCtMHPZG7sO3VJOgCK4BGAYYCw/s1600/1D6A0131.JPG-01.jpeg.jpg",
//                "image/png","10 hrs"));
//        mStoriesList.add(new StoriesData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
//                "video/mp4", "now"));
//        mStoriesList.add(new StoriesData("http://4.bp.blogspot.com/-CmDFsVPzKSk/XadSsWodkxI/AAAAAAAAWjw/BBL2XfSgz0MnNPwp2Utsj1Sd5EM7RlGGgCK4BGAYYCw/s1600/download.png",
//                "image/png","1 min"));
//        mStoriesList.add(new StoriesData("https://1.bp.blogspot.com/-7uvberoubSg/XKh7rdskK0I/AAAAAAAAUaM/1B4CAK5oieUYApo1s9ZifReQRihVTXPvgCLcBGAs/s1600/Screenshot%2B2019-04-06%2Bat%2B3.08.31%2BPM.png",
//                "image/png", "6 hrs"));
//        mStoriesList.add(new StoriesData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
//                "video/mp4", "11 hrs"));
//        mStoriesList.add(new StoriesData("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
//                "video/mp4", "23 mins"));

        return mStoriesList;
    }



    private void getDiscoveriesListData() {
        //show loading in recyclerview
        discoveriesStatusAdapter.showLoading();

        List<Discoveries> discoveries = new ArrayList<>();
        discoveries.add(new Discoveries("Alkane",
                "https://4.bp.blogspot.com/-IvTudMG6xNs/XPlXQ7Vxl_I/AAAAAAAAVcc/rZQR" +
                        "7Jcvbzoor3vO_lCtMHPZG7sO3VJOgCK4BGAYYCw/s1600/1D6A0131.JPG-01.jpeg.jpg"));
        discoveries.add(new Discoveries("Ethane",
                "http://4.bp.blogspot.com/-CmDFsVPzKSk/XadSsWodkxI/AAA" +
                        "AAAAAWjw/BBL2XfSgz0MnNPwp2Utsj1Sd5EM7RlGGgCK4BGAYYCw/s1600/download.png"));
        discoveries.add(new Discoveries("Alkyne",
                "https://1.bp.blogspot.com/-7uvberoubSg/XKh7rdskK0I/AAAAAAAAU" +
                        "aM/1B4CAK5oieUYApo1s9ZifReQRihVTXPvgCLcBGAs/s1600/Screenshot%2B20" +
                        "19-04-06%2Bat%2B3.08.31%2BPM.pn"));
        discoveries.add(new Discoveries("Benzene",
                "https://pbs.twimg.com/profile_images/990460935046230016/k__sGK8z_400x400.jpg"));
        discoveries.add(new Discoveries("Amide",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLAyxdUJ8KLw1V8EHfAcSi6X94x13WHxQrgSIlve16-SFeVZYIGg&s"));
        discoveries.add(new Discoveries("Amino Acid",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRLAyxdUJ8KLw1V8EHfAcSi6X94x13WHxQrgSIlve16-SFeVZYIGg&s"));
        discoveries.add(new Discoveries("Phenol",
                "https://pbs.twimg.com/profile_images/998555245826355200/PkFwgyGU.jpg"));
        discoveries.add(new Discoveries("Carbonxylic",
                "https://avatars3.githubusercontent.com/u/35128520?s=460&v=4"));
        discoveries.add(new Discoveries("Nitril",
                "https://cdn.pixabay.com/photo/2017/04/05/11/56/image-in-the-image-2204798_960_720.jpg"));
        discoveries.add(new Discoveries("Ether",
                "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg"));
        discoveries.add(new Discoveries("Ester",
                "https://images.pexels.com/photos/459225/pexels-photo-459225.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"));
        discoveries.add(new Discoveries("Alcohol",
                "https://images.unsplash.com/photo-1535498730771-e735b998cd64?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"));

        new Handler().postDelayed(() -> {
            //hide loading
            discoveriesStatusAdapter.hideLoading();
            //add products to recyclerView
            discoveriesStatusAdapter.addProducts(discoveries);
//                loadCount +=3;
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        statusAdapter.onActivityResult(requestCode, resultCode, data);
    }
}
