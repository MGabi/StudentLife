package com.minimalart.studentlife.fragments.navdrawer;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.services.DataService;

import java.util.ArrayList;

public class SearchRentFragment extends Fragment {

    private ImageButton searchBtn;
    private EditText searchTextField;
    private RecyclerView rentRecyclerView;
    private RentAnnounceAdapter rentAnnounceAdapter;
    private SwipeRefreshLayout swipe;

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

        searchBtn = (ImageButton)view.findViewById(R.id.img_search_rent_search);
        searchTextField = (EditText)view.findViewById(R.id.search_rent_textfield);
        rentRecyclerView = (RecyclerView)view.findViewById(R.id.frag_search_rent_recyclerview);
        swipe = (SwipeRefreshLayout)view.findViewById(R.id.frag_search_swipe_layout);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rentRecyclerView.setLayoutManager(llm);

        rentAnnounceAdapter = new RentAnnounceAdapter(new ArrayList<CardRentAnnounce>(), getContext());
        rentRecyclerView.setAdapter(rentAnnounceAdapter);
        rentAnnounceAdapter.loadNewData(DataService.getInstance().getRentAnnounces());
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        rentRecyclerView.addItemDecoration(new SpaceVerticalItemDecoration(spacingInPixels));

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rentAnnounceAdapter.loadNewData(DataService.getInstance().getRentAnnounces());
                swipe.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DataService.getInstance().registerCallbackAdapterRent(rentAnnounceAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        DataService.getInstance().unregisterCallbackAdapterRent();
    }
}
