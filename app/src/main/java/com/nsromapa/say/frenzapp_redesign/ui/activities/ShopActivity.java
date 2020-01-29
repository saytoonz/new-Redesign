package com.nsromapa.say.frenzapp_redesign.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.setUserOnlineStatus;


public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserOnlineStatus(this,getResources().getString(R.string.online), Utils.getUserUid());
    }

    @Override
    protected void onPause() {
        setUserOnlineStatus(this,getResources().getString(R.string.offline), Utils.getUserUid());
        super.onPause();
    }
}
