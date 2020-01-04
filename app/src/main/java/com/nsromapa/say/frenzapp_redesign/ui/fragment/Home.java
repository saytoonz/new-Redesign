package com.nsromapa.say.frenzapp_redesign.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nsromapa.say.bottomnav.view.CurvedBottomNavigationView;
import com.nsromapa.say.frenzapp_redesign.R;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Chats;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Feeds;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.home.Stories;
import com.nsromapa.say.frenzapp_redesign.ui.fragment.home.VideoFeeds;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.nsromapa.say.frenzapp_redesign.ui.activities.MainActivity.toolbar;


public class Home extends Fragment {

    private CurvedBottomNavigationView bottom_navigation_view;
    private FloatingActionButton fab_news_feed;
    private static final String EXTRA_TEXT = "text";

    public static Home createFor(String text) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        bottom_navigation_view = view.findViewById(R.id.bottom_navigation_view);
        fab_news_feed = view.findViewById(R.id.fab_news_feed);
        setDash(bottom_navigation_view);
        showFragment(new Feeds(),"Feeds");
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        fab_news_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPost();
            }
        });
    }

    private void createNewPost() {
        Toast.makeText(getContext(), "Create NewPost", Toast.LENGTH_SHORT).show();
    }


    // Set up the bottom navigation bar.....
    private void setDash(CurvedBottomNavigationView view) {
        view.inflateMenu(R.menu.bnvm_dash);
        view.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        view.enableAnimation(false);
        view.setTextSize(10.0f);
        view.setIconsMarginTop(12);
        view.setIconSize(30.0f, 30.0f);

        view.getBottomNavigationItemView(2).setClickable(false);

        for (int i = 0; i < view.getItemCount(); i++) {
            view.getLargeLabelAt(i).setPadding(0, 0, 0, 0);
            view.getBottomNavigationItemView(i).setBackground(null);
        }

        //Setup the menu icon click listener
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nv_explorer:
                        showFragment(new VideoFeeds(), "Videos");
                        break;
                    case R.id.nv_profile:
                        showFragment(new Stories(), "Stories");
                        break;
                    case R.id.nv_production:
                        showFragment(new Chats(), "Chats");
                        break;
                    case R.id.nv_stacks:
                        showFragment(new Feeds(), "Feeds");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void showFragment(Fragment fragment, String title){
        toolbar.setTitle(title);
        Objects.requireNonNull(getActivity())
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }
}
