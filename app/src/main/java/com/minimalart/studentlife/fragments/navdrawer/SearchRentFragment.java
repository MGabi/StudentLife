package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.services.DataService;

import java.util.ArrayList;

public class SearchRentFragment extends Fragment {

    private ImageButton homeBtn;
    private ImageButton searchBtn;
    private EditText searchTextField;
    private RecyclerView rentRecyclerView;

    public SearchRentFragment() {
    }

    /**
     * @return a reference to this fragment
     */
    public static SearchRentFragment newInstance() {
        SearchRentFragment fragment = new SearchRentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_rent, container, false);

        homeBtn = (ImageButton)view.findViewById(R.id.img_search_rent_home);
        searchBtn = (ImageButton)view.findViewById(R.id.img_search_rent_search);
        searchTextField = (EditText)view.findViewById(R.id.search_rent_textfield);
        rentRecyclerView = (RecyclerView)view.findViewById(R.id.frag_search_rent_recyclerview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rentRecyclerView.setLayoutManager(llm);

        RentAnnounceAdapter rentAnnounceAdapter = new RentAnnounceAdapter(new ArrayList<CardRentAnnounce>());
        rentRecyclerView.setAdapter(rentAnnounceAdapter);
        rentAnnounceAdapter.loadNewData(DataService.getInstance().getRentAnnounces());
        return view;
    }

}
