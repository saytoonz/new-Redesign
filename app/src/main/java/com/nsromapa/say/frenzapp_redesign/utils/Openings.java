package com.nsromapa.say.frenzapp_redesign.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nsromapa.say.frenzapp_redesign.R;

import static com.nsromapa.say.frenzapp_redesign.utils.Utils.areWeFriends;
import static com.nsromapa.say.frenzapp_redesign.utils.Utils.getUserInfoFromUserJSON;
import static com.nsromapa.say.frenzapp_redesign.utils.Utils.isFollowingMe;
import static com.nsromapa.say.frenzapp_redesign.utils.Utils.isMeFollowing;

public class Openings {
    private static String TAG = "Openings";


    public static void profileWithUserJson(Context context, String userJson){
        Toast.makeText(context, getUserInfoFromUserJSON(userJson,"id"), Toast.LENGTH_SHORT).show();
    }


    public static void profileWithUserId(Context context, String userId){
        Toast.makeText(context, userId, Toast.LENGTH_SHORT).show();
    }

    public static void showImageAlertWithJson(Context context, String username, String imageUrl, String userJson){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog_imageview);

        TextView name = dialog.findViewById(R.id.name);
        ImageView image = dialog.findViewById(R.id.goProDialogImage);
        ImageView cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
        ImageView open_profile = dialog.findViewById(R.id.open_profile);
        TextView user_action = dialog.findViewById(R.id.user_action);

        if (areWeFriends(getUserInfoFromUserJSON(userJson, "id")))
            user_action.setText(context.getResources().getString(R.string.unfriend));
        else  if (isFollowingMe(getUserInfoFromUserJSON(userJson, "id")))
            user_action.setText(context.getResources().getString(R.string.follow_back));
        else  if (isMeFollowing(getUserInfoFromUserJSON(userJson, "id")))
            user_action.setText(context.getResources().getString(R.string.unfollow));
        else
            user_action.setText(context.getResources().getString(R.string.follow));


        name.setText(username);
        cancel_dialog.setOnClickListener(v -> dialog.dismiss());
        open_profile.setOnClickListener(v -> profileWithUserJson(context,userJson));
        user_action.setOnClickListener(v -> Toast.makeText(context, "User Action Button Clicked", Toast.LENGTH_SHORT).show());
        Glide.with(context)
                .load(imageUrl)
                .into(image);
        dialog.show();
    }


    
    public static void showImageAlertWithUserId(Context context, String  username, String imageUrl, String userId){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog_imageview);

        TextView name = dialog.findViewById(R.id.name);
        ImageView image = dialog.findViewById(R.id.goProDialogImage);
        ImageView cancel_dialog = dialog.findViewById(R.id.cancel_dialog);
        ImageView open_profile = dialog.findViewById(R.id.open_profile);
        TextView user_action = dialog.findViewById(R.id.user_action);

        if (areWeFriends(userId))
            user_action.setText(context.getResources().getString(R.string.unfriend));
        else  if (isFollowingMe(userId))
            user_action.setText(context.getResources().getString(R.string.follow_back));
        else  if (isMeFollowing(userId))
            user_action.setText(context.getResources().getString(R.string.unfollow));
        else
            user_action.setText(context.getResources().getString(R.string.follow));


        name.setText(username);
        cancel_dialog.setOnClickListener(v -> dialog.dismiss());
        open_profile.setOnClickListener(v -> profileWithUserId(context,userId));
        user_action.setOnClickListener(v -> Toast.makeText(context, "User Action Button Clicked", Toast.LENGTH_SHORT).show());
        Glide.with(context)
                .load(imageUrl)
                .into(image);
        dialog.show();
    }
}
