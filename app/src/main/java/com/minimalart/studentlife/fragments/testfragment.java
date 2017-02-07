package com.minimalart.studentlife.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;

public class TestFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private int poz;


    public TestFragment() {
        // Required empty public constructor
    }

    public static TestFragment newInstance() {
        TestFragment fragment = new TestFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_testfragment, container, false);
        v.findViewById(R.id.card_frag_contact_image).setTransitionName("bla" + poz);
        return v;
    }

    public void setPoz(int p){
        poz = p;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
