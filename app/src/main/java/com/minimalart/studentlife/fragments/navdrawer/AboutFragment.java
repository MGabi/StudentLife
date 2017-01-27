package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;

/**
 * this is an informational fragment
 * only shows licenses & other related things such as used libraries etc.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {

    }

    /**
     * @return a reference to this fragment
     */
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
