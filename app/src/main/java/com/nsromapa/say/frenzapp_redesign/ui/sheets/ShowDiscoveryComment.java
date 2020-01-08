package com.nsromapa.say.frenzapp_redesign.ui.sheets;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowDiscoveryComment extends BottomDrawerFragment {
    private DiscoveryCommentAdapter discoveryCommentAdapter;

    private String discoveryComment_jObj;
    private DiscoveryComment description;
    private Context context;

    public ShowDiscoveryComment(Context context, String discoveryComment_jObj, DiscoveryComment description) {
        this.discoveryComment_jObj = discoveryComment_jObj;
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

        RecyclerView bottom_sheetRecycler = view.findViewById(R.id.bottom_sheetrecycler);
        bottom_sheetRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        discoveryCommentAdapter = new DiscoveryCommentAdapter(getContext());
        bottom_sheetRecycler.setLayoutManager(linearLayoutManager);

        EndlessScrollListener endlessScrollListener = new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, int quantity) {
                if (!discoveryCommentAdapter.loading)
                    getCommentsData(String.valueOf(quantity));
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

    private void getCommentsData(String quantity) {
        discoveryCommentAdapter.showLoading();
        List<DiscoveryComment> discoveryComments = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(discoveryComment_jObj);
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

            new Handler().postDelayed(() -> {
                //hide loading
                discoveryCommentAdapter.hideLoading();
                //add products to recyclerView
                discoveryCommentAdapter.addProducts(discoveryComments);
            }, 1000);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void createComment(EditText create_comment) {

    }
//
//    @NotNull
//    @Override
//    public BottomDrawerDialog configureBottomDrawer() {
////        return BottomDrawerDialog.style.TextAppearance_Compat_Notification_Line2
//    }
}
