package com.nsromapa.say.frenzapp_redesign.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.adapters.DrawerAdapter;
import com.nsromapa.say.frenzapp_redesign.asyncs.CheckAndStartBootService;
import com.nsromapa.say.frenzapp_redesign.asyncs.UpdateOnlineStatus;
import com.nsromapa.say.frenzapp_redesign.helpers.DrawerItem;
import com.nsromapa.say.frenzapp_redesign.helpers.SimpleItem;
import com.nsromapa.say.frenzapp_redesign.helpers.SpaceItem;
import com.nsromapa.say.frenzapp_redesign.services.BootCompleteService;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.Followers;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.Home;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Feeds;
import com.nsromapa.say.frenzapp_redesign.utils.Utils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.fragment.Home.updateFragment;
import static com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats.disableSelection;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    public static MainActivity activity;
    public static Fragment mCurrentFragment;
    public static String mCurrentFragmentInHOME;
    public static boolean chatFragment_isSelectionMode = false;
    public static boolean mState = true;

    private static final int POS_HOME = 0;
    private static final int POS_FOLLOWERS = 1;
    private static final int POS_FOLLOWING = 2;
    private static final int POS_FRIENDS = 3;
    private static final int POS_INVITE = 4;
    private static final int POS_SETTINGS = 5;
    private static final int POS_ABOUT = 6;
    private static final int POS_LOGOUT = 7;

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
                createItemFor(POS_FOLLOWERS),
                createItemFor(POS_FOLLOWING),
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

        mCurrentFragmentInHOME = "Feeds";
        adapter.setSelected(POS_HOME);
//        setUserProfile();
    }


    public static void setUserOnlineStatus(Context context, String setMyOnlineStatus, String whoseStatus) {
        new UpdateOnlineStatus(context, setMyOnlineStatus, whoseStatus).execute();
    }


    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            mState = false;
            finish();
        } else if (position == POS_HOME) {
            mState = true;
            slidingRootNav.closeMenu();
            mCurrentFragmentInHOME = "Feeds";
            Fragment selectedScreen = new Home();
            showFragment(selectedScreen);
        } else if (position == POS_FOLLOWERS) {
            mState = false;
            slidingRootNav.closeMenu();
            Fragment selectedScreen = new Followers();
            showFragment(selectedScreen);
        } else {
            mState = false;
            Fragment selectedScreen = Home.createFor(screenTitles[position]);
            showFragment(selectedScreen);
        }
    }


    public void showFragment(Fragment fragment) {
        if (requireContext() != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            mCurrentFragment = fragment;
        } else {
            this.recreate();
        }
    }

    public final Context requireContext() {
        Context context = getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to a context.");
        }
        return context;
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
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isMainActivity", true).apply();
        Intent intent = new Intent(this, BootCompleteService.class);
        if (startService(intent) == null) {
            startService(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserOnlineStatus(this, getResources().getString(R.string.online), Utils.getUserUid());
    }

    @Override
    public void onPause() {
        super.onPause();
        setUserOnlineStatus(this, getResources().getString(R.string.offline), Utils.getUserUid());
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("isMainActivity", false).apply();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (slidingRootNav.isMenuOpened()) {
            slidingRootNav.closeMenu(true);

        } else if (!mState) {
            toolbar.setTitle("Home");
            try {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
            }
            this.invalidateOptionsMenu();
            mState = true;
            showFragment(new Home());
            if (slidingRootNav.isMenuOpened()) {
                slidingRootNav.closeMenu(true);
            }
            adapter.setSelected(POS_HOME);

        } else {
            if (chatFragment_isSelectionMode && mCurrentFragmentInHOME.equals(getResources().getString(R.string.chats)))
                disableSelection();
            else {
                updateFragment(new Feeds(), getResources().getString(R.string.feeds));
                mCurrentFragmentInHOME = getResources().getString(R.string.feeds);
            }

//                super.onBackPressed();
        }
    }
}

