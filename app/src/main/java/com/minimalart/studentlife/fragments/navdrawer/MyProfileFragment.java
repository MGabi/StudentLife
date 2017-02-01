package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.models.User;

public class MyProfileFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean isTheTitleVisible = false;

    private TextView title;
    private TextView upTitle;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ImageButton backButton;
    private User user;
    private String currentUserUID;

    public MyProfileFragment() {

    }

    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initViews(view);
        appBarLayout.addOnOffsetChangedListener(this);
        startAlphaAnimation(title, 0, View.INVISIBLE);
        return view;
    }

    public void initViews(View view){
        toolbar = (Toolbar) view.findViewById(R.id.myprofile_toolbar);
        title = (TextView) view.findViewById(R.id.myprofile_textview_title);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.myprofile_appbar);
        backButton = (ImageButton) view.findViewById(R.id.my_profile_back);
        upTitle = (TextView) view.findViewById(R.id.myprofile_textview_title_dissapearing);
        user = ((MainActivity)getActivity()).getCurrentLoggedUser();
        currentUserUID = ((MainActivity)getActivity()).getCurrentUserUID();

        title.setText(user.getName());
        upTitle.setText(user.getName() + " " + user.getSecName());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!isTheTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isTheTitleVisible = true;
            }

        } else {

            if (isTheTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isTheTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
