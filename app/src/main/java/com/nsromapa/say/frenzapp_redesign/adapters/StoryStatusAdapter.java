package com.nsromapa.say.frenzapp_redesign.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.models.StoryStatus;
import com.nsromapa.say.frenzapp_redesign.stories_lib.CircularStatusView;
import com.nsromapa.say.frenzapp_redesign.ui.activities.StoryViewActivity;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class StoryStatusAdapter extends RecyclerView.Adapter<StoryStatusAdapter.ViewHolder> {
    private List<StoryStatus> statusList;
    private Context context;
    private Activity activity;


    public StoryStatusAdapter(List<StoryStatus> statusList, Context context, Activity activity) {
        this.statusList = statusList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public StoryStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status_stories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryStatusAdapter.ViewHolder holder, int position) {
        StoryStatus list = statusList.get(position);

        Glide.with(context)
                .load(list.getLastStory())
                .into(holder.first_stat);

        Glide.with(context)
                .load(list.getPosterImage())
                .into(holder.poster_image);

        holder.poster_info.setText(list.getPosterName());

        if (list.getPosterId().equals(Utils.getUserUid())) {
            holder.add_new_story.show();
            holder.add_new_story.setOnClickListener(v -> Toasty.success(context, "Create new Story").show());
        } else {
            holder.circularStatusView.setVisibility(View.VISIBLE);
            holder.add_new_story.hide();
        }

        holder.circularStatusView.setPortionsCount(list.getStories().size());
        holder.circularStatusView.setPortionsColor(context.getResources().getColor(R.color.colorAccent));
        holder.circularStatusView.setOnClickListener(v -> {
            if (list.getStories().size() > 0) {
                Intent intent = new Intent(context, StoryViewActivity.class);
                intent.putExtra("storiesList", list.getStories());
                intent.putExtra("posterName", list.getPosterName());
                intent.putExtra("posterImage", list.getPosterImage());
                activity.startActivityForResult(intent, 100);
            }
        });
        holder.view_stat_view.setOnClickListener(v -> {
            if (list.getStories().size() > 0) {
                Intent intent = new Intent(context, StoryViewActivity.class);
                intent.putExtra("storiesList", list.getStories());
                intent.putExtra("posterName", list.getPosterName());
                intent.putExtra("posterImage", list.getPosterImage());
                activity.startActivityForResult(intent, 100);
            }
        });

    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircularStatusView circularStatusView;
        CircleImageView poster_image;
        FloatingActionButton add_new_story;
        ImageView first_stat;
        TextView poster_info;
        View view_stat_view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view_stat_view = itemView.findViewById(R.id.view_stat_view);
            circularStatusView = itemView.findViewById(R.id.circular_status_view);
            poster_image = itemView.findViewById(R.id.poster_image);
            add_new_story = itemView.findViewById(R.id.add_new_story);
            first_stat = itemView.findViewById(R.id.first_stat);
            poster_info = itemView.findViewById(R.id.poster_info);
        }
    }

//    private void setUserSeenThisStatus(boolean areAllSeen, int i) {
//        if (areAllSeen) {
//            holder.circularStatusView.setPortionsColor(seenColor);
//        } else {
//            int color = status.isSeen() ? seenColor : notSeenColor;
//            holder.circularStatusView.setPortionColorForIndex(i, color);
//
//        }
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if(resultCode == Activity.RESULT_OK){
//                String position=data.getStringExtra("position");
                String result=data.getStringExtra("viewed");
                assert result != null;
                Log.e("MyAdapter", "position " + "position"+ " - data " + result);
            }
        }
    }
}
