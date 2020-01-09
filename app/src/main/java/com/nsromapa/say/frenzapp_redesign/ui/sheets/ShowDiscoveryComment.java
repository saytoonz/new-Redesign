package com.nsromapa.say.frenzapp_redesign.ui.sheets;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.heyalex.bottomdrawer.BottomDrawerDialog;
import com.github.heyalex.bottomdrawer.BottomDrawerFragment;
import com.github.heyalex.handle.PullHandleView;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.DiscoveriesStatusAdapter;
import com.nsromapa.say.frenzapp_redesign.adapters.DiscoveryCommentAdapter;
import com.nsromapa.say.frenzapp_redesign.helpers.EndlessScrollListener;
import com.nsromapa.say.frenzapp_redesign.models.Discoveries;
import com.nsromapa.say.frenzapp_redesign.models.DiscoveryComment;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.DISCOVER_STORIES;

public class ShowDiscoveryComment extends BottomDrawerFragment {
    private DiscoveryCommentAdapter discoveryCommentAdapter;
    private int loadCount = 0;
    private String discovery_id;
    private DiscoveryComment description;
    private Context context;

    private LinearLayout error_view;
    private LinearLayout default_item;

    public ShowDiscoveryComment(Context context, String discovery_id, DiscoveryComment description) {
        this.discovery_id = discovery_id;
        this.description = description;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_discovery, container, false);
        ImageView cancelButton = view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(v -> dismissWithBehavior());

        CircleImageView user_image = view.findViewById(R.id.user_image);
        Glide.with(context)
                .load(Utils.getUserImage())
                .into(user_image);

        TextView user_name = view.findViewById(R.id.user_name);
        user_name.setText(Utils.getUserName());
        EditText create_comment = view.findViewById(R.id.create_comment);

        ImageView send_comment = view.findViewById(R.id.send_comment);
        send_comment.setOnClickListener(v -> createComment(create_comment));

        default_item = view.findViewById(R.id.default_item);
        error_view = view.findViewById(R.id.error_view);

        RecyclerView bottom_sheetRecycler = view.findViewById(R.id.bottom_sheetrecycler);
        bottom_sheetRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        discoveryCommentAdapter = new DiscoveryCommentAdapter(getContext());
        bottom_sheetRecycler.setLayoutManager(linearLayoutManager);

        EndlessScrollListener endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, int quantity) {
                if (!discoveryCommentAdapter.loading)
                    showDiscoveryComments(String.valueOf(quantity));
            }
        };
        bottom_sheetRecycler.addOnScrollListener(endlessScrollListener);
        bottom_sheetRecycler.setAdapter(discoveryCommentAdapter);


        List<DiscoveryComment> commentList = new ArrayList<>();
        commentList.add(description);
        discoveryCommentAdapter.addProducts(commentList);

        endlessScrollListener.onLoadMore(0, 0, 10);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void showDiscoveryComments(String quantity) {

        default_item.setVisibility(View.GONE);
        error_view.setVisibility(View.GONE);

        discoveryCommentAdapter.showLoading();
        List<DiscoveryComment> discoveryComments = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> {
                    Log.e("Volley Result", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("DiscoveriesComments");

                            default_item.setVisibility(View.GONE);
                            error_view.setVisibility(View.GONE);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject commentObj = jsonArray.getJSONObject(i);
                                JSONObject commenterObj = commentObj.getJSONObject("1");

                                discoveryComments.add(new DiscoveryComment(
                                        commenterObj.getString("id"),
                                        commenterObj.getString("username"),
                                        commenterObj.getString("image"),
                                        commenterObj.toString(),
                                        commentObj.getString("id"),
                                        commentObj.getString("0"),
                                        commentObj.getString("comment"),
                                        commentObj.getString("likes"),
                                        commentObj.getString("dislikes"),
                                        "comment"));
                            }

                            loadCount += 16;

                            new Handler().postDelayed(() -> {
                                //hide loading
                                discoveryCommentAdapter.hideLoading();
                                //add products to recyclerView
                                discoveryCommentAdapter.addProducts(discoveryComments);
                            }, 1000);


                    } catch (JSONException e) {
                        error_view.setVisibility(View.VISIBLE);
                        discoveryCommentAdapter.hideLoading();
                    }

                },
                error -> {
                    error_view.setVisibility(View.VISIBLE);
                    discoveryCommentAdapter.hideLoading();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("discovery_comments", "true");
                postMap.put("load", String.valueOf(loadCount));
                postMap.put("discovery_id", discovery_id);
                postMap.put("quantity", quantity);
                return postMap;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }

    private void createComment(EditText create_comment) {
        if (!TextUtils.isEmpty(create_comment.getText().toString())){
            final  String comment = create_comment.getText().toString();
            create_comment.setText("");

            List<DiscoveryComment> discoveryComments = new ArrayList<>();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                    response -> {
                        Log.e("Volley Result", "" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("MyNewComment");

                            if (jsonArray.length() > 0) {
                                default_item.setVisibility(View.GONE);
                                error_view.setVisibility(View.GONE);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentObj = jsonArray.getJSONObject(i);
                                    JSONObject commenterObj = commentObj.getJSONObject("1");

                                    discoveryComments.add(new DiscoveryComment(
                                            commenterObj.getString("id"),
                                            commenterObj.getString("username"),
                                            commenterObj.getString("image"),
                                            commenterObj.toString(),
                                            commentObj.getString("id"),
                                            commentObj.getString("0"),
                                            comment,
                                            "0",
                                            "0",
                                            "comment"));
                                }

                                new Handler().postDelayed(() -> {
                                    //hide loading
                                    discoveryCommentAdapter.hideLoading();
                                    //add products to recyclerView
                                    discoveryCommentAdapter.addProducts(discoveryComments);
                                }, 1000);
                            }else{
                                Toasty.error(context, getString(R.string.sorry_comment_not_sent)).show();
                            }

                        } catch (JSONException e) {
                            Toasty.error(context, getString(R.string.there_was_an_error)).show();
                        }

                    },
                    error -> {
                        Toasty.error(context, getString(R.string.there_was_an_error)).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postMap = new HashMap<>();
                    postMap.put("user_id", Utils.getUserUid());
                    postMap.put("comment_discovery_post", "true");
                    postMap.put("comment",comment);
                    postMap.put("discovery_id", discovery_id);
                    return postMap;
                }
            };

            Volley.newRequestQueue(context).add(stringRequest);
        }
    }
//
//    @NotNull
//    @Override
//    public BottomDrawerDialog configureBottomDrawer() {
////        return BottomDrawerDialog.style.TextAppearance_Compat_Notification_Line2
//    }
}
