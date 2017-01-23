package com.minimalart.studentlife.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;

public class OpenRentAnnounceFragment extends Fragment {

    public OpenRentAnnounceFragment() {
        // Required empty public constructor
    }

    public static OpenRentAnnounceFragment newInstance() {
        OpenRentAnnounceFragment fragment = new OpenRentAnnounceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_rent_announce, container, false);

        return view;
    }
}
