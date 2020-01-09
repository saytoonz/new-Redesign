package com.nsromapa.say.frenzapp_redesign.ui.sheets;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.heyalex.bottomdrawer.BottomDrawerFragment;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.StoryViewersAdapter;
import com.nsromapa.say.frenzapp_redesign.models.Viewers;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.nsromapa.say.frenzapp_redesign.utils.Constants.DISCOVER_STORIES;

public class ShowStoryViewers extends BottomDrawerFragment {
    private StoryViewersAdapter storyViewersAdapter;
    private String story_id;
    private Context context;
    private String from;

    private LinearLayout error_view;
    private LinearLayout default_item;

    public ShowStoryViewers(Context context, String story_id, String from) {
        this.story_id = story_id;
        this.context = context;
        this.from = from;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_status_views, container, false);
        ImageView cancelButton = view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(v -> dismissWithBehavior());

        CircleImageView delete_status_iv = view.findViewById(R.id.delete_status_iv);
        TextView delete_status_tv = view.findViewById(R.id.delete_status_tv);

        delete_status_iv.setOnClickListener(v -> askToDeleteStory());
        delete_status_tv.setOnClickListener(v -> askToDeleteStory());


        default_item = view.findViewById(R.id.default_item);
        error_view = view.findViewById(R.id.error_view);

        RecyclerView bottom_sheetRecycler = view.findViewById(R.id.bottom_sheetrecycler);
        bottom_sheetRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        storyViewersAdapter = new StoryViewersAdapter(getContext());
        bottom_sheetRecycler.setLayoutManager(linearLayoutManager);
        bottom_sheetRecycler.setAdapter(storyViewersAdapter);

        showStoryViewers();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void showStoryViewers() {

        default_item.setVisibility(View.GONE);
        error_view.setVisibility(View.GONE);

        storyViewersAdapter.showLoading();
        List<Viewers> viewersArrayList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> {
                    Log.e("Volley Result", "" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Viewers");

                        default_item.setVisibility(View.GONE);
                        error_view.setVisibility(View.GONE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject viewerObj = jsonArray.getJSONObject(i);
                            viewersArrayList.add(new Viewers(
                                    viewerObj.getString("id"),
                                    viewerObj.getString("full_name"),
                                    viewerObj.getString("username"),
                                    viewerObj.getString("image")));
                        }

                        new Handler().postDelayed(() -> {
                            //hide loading
                            storyViewersAdapter.hideLoading();
                            //add products to recyclerView
                            storyViewersAdapter.addProducts(viewersArrayList);
                        }, 1000);


                    } catch (JSONException e) {
                        error_view.setVisibility(View.VISIBLE);
                        storyViewersAdapter.hideLoading();
                    }

                },
                error -> {
                    error_view.setVisibility(View.VISIBLE);
                    storyViewersAdapter.hideLoading();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("story_viewers", "true");
                postMap.put("story_id", story_id);
                return postMap;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }



    private void askToDeleteStory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage("Do you want to Delete this Story?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteStory();
        }).setNegativeButton("No", (dialog, which) -> {
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStory() {
        Toasty.normal(context, "Deleting Story...").show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DISCOVER_STORIES,
                response -> Toasty.success(context, "Story deleted").show(),
                error -> Toasty.error(context, "Couldn't delete Story Please Try again").show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("user_id", Utils.getUserUid());
                postMap.put("story_delete", "true");
                postMap.put("story_id", story_id);
                postMap.put("delete_from_status_or_discovery", from);
                return postMap;
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
        Objects.requireNonNull(getActivity()).finish();
    }

}
