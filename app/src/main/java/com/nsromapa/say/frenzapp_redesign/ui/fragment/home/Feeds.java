package com.nsromapa.say.frenzapp_redesign.ui.fragment.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.PostsAdapter;
import com.nsromapa.say.frenzapp_redesign.models.Post;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.mCurrentFragmentInHOME;
import static com.nsromapa.say.frenzapp_redesign.utils.Constants.NEWS_FEEDS;


public class Feeds extends Fragment {
    private List<Post> mPostsList;
    private View mView;
    private SwipeRefreshLayout refreshLayout;
    private PostsAdapter mAdapter_v19;
    private RequestQueue requestQueue;

    public Feeds() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_feeds, container, false);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter_v19.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        RecyclerView mPostsRecyclerView = view.findViewById(R.id.posts_recyclerview);

        mPostsList = new ArrayList<>();

        mAdapter_v19 = new PostsAdapter(mPostsList, view.getContext(), getActivity());
        mPostsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPostsRecyclerView.setHasFixedSize(true);
        mPostsRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        mPostsRecyclerView.setAdapter(mAdapter_v19);

        refreshLayout.setOnRefreshListener(() -> {
            mPostsList.clear();
            mAdapter_v19.notifyDataSetChanged();
            getAllPosts();
        });

        getAllPosts();
    }

    private void getAllPosts() {
        mView.findViewById(R.id.default_item).setVisibility(View.GONE);
        mView.findViewById(R.id.error_view).setVisibility(View.GONE);
        refreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, NEWS_FEEDS,
                response -> {
                    refreshLayout.setRefreshing(false);
                    Log.e("Volley Result", "" + response); //the response contains the result from the server, a json string or any other object returned by your server

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Posts");

                        if (jsonArray.length() > 0) {
                            mView.findViewById(R.id.default_item).setVisibility(View.GONE);
                            mView.findViewById(R.id.error_view).setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject coming_post = jsonArray.getJSONObject(i);
                                JSONObject user_info = coming_post.getJSONObject("1");
                                mPostsList.add(new Post(
                                        coming_post.getString("id"),
                                        coming_post.getString("user_id"),
                                        coming_post.getString("0"),
                                        "50",
                                        "",
                                        coming_post.getString("description"),
                                        coming_post.getString("color"),
                                        user_info.getString("username"),
                                        user_info.getString("image"),
                                        coming_post.getString("post_type"),
                                        coming_post.getString("image_urls"),
                                        coming_post.toString()
                                ));
                                mAdapter_v19.notifyDataSetChanged();
                            }
                            refreshLayout.setRefreshing(false);
                        } else {
                            if (mCurrentFragmentInHOME.equals(getResources().getString(R.string.feeds)))
                                mView.findViewById(R.id.default_item).setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (mCurrentFragmentInHOME.equals(getResources().getString(R.string.feeds)))
                            mView.findViewById(R.id.error_view).setVisibility(View.VISIBLE);
                    }


                },
                error -> {
                    if (requireContext() != null)
                        if (mCurrentFragmentInHOME.equals(getResources().getString(R.string.feeds))) {
                            refreshLayout.setRefreshing(false);
                            mView.findViewById(R.id.default_item).setVisibility(View.GONE);
                            mView.findViewById(R.id.error_view).setVisibility(View.VISIBLE);
                            TextView error_textV = mView.findViewById(R.id.error_view).findViewById(R.id.error_text);
                            error_textV.setText(Objects.requireNonNull(getActivity()).getString(R.string.server_connection_error));
                        }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", "1");
//                postMap.put("load", String.valueOf(loadCount));
                return postMap;
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        } else {
            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        }

        requestQueue.add(stringRequest);
    }


//    @Override
//    @NotNull
//    public final Context requireContext() {
//        Context context = getContext();
//        if (context == null) {
//            throw new IllegalStateException("Fragment " + this + " not attached to a context.");
//        }
//        return context;
//    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestQueue != null)
            requestQueue.cancelAll(this);
    }
}
