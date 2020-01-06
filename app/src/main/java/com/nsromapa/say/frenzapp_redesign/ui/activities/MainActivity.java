package com.nsromapa.say.frenzapp_redesign.ui.activities;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.Home;
import com.nsromapa.say.frenzapp_redesign.adapters.DrawerAdapter;
import com.nsromapa.say.frenzapp_redesign.helpers.DrawerItem;
import com.nsromapa.say.frenzapp_redesign.helpers.SimpleItem;
import com.nsromapa.say.frenzapp_redesign.helpers.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    public static MainActivity activity;
    public static Fragment mCurrentFragment;
    private static final int POS_HOME = 0;
    private static final int POS_CHATS = 1;
    private static final int POS_SEND_MESSAGE = 2;
    private static final int POS_CREATE_GROUP = 3;
    private static final int POS_CREATE_CHANNEL = 4;
    private static final int POS_CONVENTION = 5;
    private static final int POS_FRIENDS = 6;
    private static final int POS_INVITE = 7;
    private static final int POS_SETTINGS = 8;
    private static final int POS_ABOUT = 9;
    private static final int POS_LOGOUT = 11;

    public static Toolbar toolbar;
    private DrawerAdapter adapter;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));// set status background white
        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_CHATS),
                createItemFor(POS_SEND_MESSAGE),
                createItemFor(POS_CREATE_GROUP),
                createItemFor(POS_CREATE_CHANNEL),
                createItemFor(POS_CONVENTION),
                createItemFor(POS_FRIENDS),
                createItemFor(POS_INVITE),
                createItemFor(POS_SETTINGS),
                createItemFor(POS_ABOUT),
                new SpaceItem(48),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        adapter.setListener(this);
        mCurrentFragment = new Home();


        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);
//        setUserProfile();
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            finish();
        }else if (position == POS_HOME) {
            slidingRootNav.closeMenu();
            Fragment selectedScreen = new Home();
            showFragment(selectedScreen);
        }else{
            Fragment selectedScreen = Home.createFor(screenTitles[position]);
            showFragment(selectedScreen);
        }
    }


    public static void showFragment(Fragment fragment) {

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        mCurrentFragment = fragment;
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.minimal_black))
                .withTextTint(color(R.color.minimal_black))
                .withSelectedIconTint(color(R.color.colorAccentt))
                .withSelectedTextTint(color(R.color.colorAccentt));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }


    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isMainActivityActive", false).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isMainActivityActive", false).apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isMainActivityActive", true).apply();
    }
}

