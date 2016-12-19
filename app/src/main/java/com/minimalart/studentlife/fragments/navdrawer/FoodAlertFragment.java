package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;

public class FoodAlertFragment extends Fragment {

    public FoodAlertFragment() {
        // Required empty public constructor
    }

    /**
     * @return a reference to this fragment
     */
    public static FoodAlertFragment newInstance() {
        FoodAlertFragment fragment = new FoodAlertFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_alert, container, false);
    }

}
