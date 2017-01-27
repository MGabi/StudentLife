package com.minimalart.studentlife.fragments.navdrawer;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.services.DataService;

import java.util.ArrayList;

public class SearchRentFragment extends Fragment {

    private RecyclerView rentRecyclerView;
    private RentAnnounceAdapter rentAnnounceAdapter;
    private SwipeRefreshLayout swipe;
    private FloatingSearchView floatingSearchView;

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

        initializeViews(view);
        setOnClickListeners();

        return view;
    }

    /**
     * Initializing basic views
     * @param view : utilized for findViewById method
     */
    public void initializeViews(View view){
        rentRecyclerView = (RecyclerView)view.findViewById(R.id.frag_search_rent_recyclerview);
        swipe = (SwipeRefreshLayout)view.findViewById(R.id.frag_search_swipe_layout);
        floatingSearchView = (FloatingSearchView)view.findViewById(R.id.search_rent_searchview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rentRecyclerView.setLayoutManager(llm);

        rentAnnounceAdapter = new RentAnnounceAdapter(new ArrayList<CardRentAnnounce>(), getContext());
        rentRecyclerView.setAdapter(rentAnnounceAdapter);
        rentAnnounceAdapter.loadNewData(DataService.getInstance().getRentAnnounces());
        rentRecyclerView.addItemDecoration(new DividerItemDecoration(rentRecyclerView.getContext(), llm.getOrientation()));
        swipe.setColorSchemeResources(R.color.colorPrimary ,R.color.colorAccent, R.color.colorPrimaryDark);

    }

    /**
     * Setting click listeners for every button
     * and defining personal action for any of them
     */
    public void setOnClickListeners(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rentAnnounceAdapter.loadNewData(DataService.getInstance().getRentAnnounces());
                swipe.setRefreshing(false);
            }
        });

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                ArrayList<CardRentAnnounce> newList = new ArrayList<>();
                Log.v("CARDLIST", String.valueOf(rentAnnounceAdapter.getList().size()));
                for(CardRentAnnounce card : rentAnnounceAdapter.getList()){
                    if(card.getTitle().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getPrice().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getRooms().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getLocation().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getDescription().toLowerCase().contains(newQuery.toLowerCase()))
                        newList.add(card);
                }

                rentAnnounceAdapter.loadNewData(newList);
                rentAnnounceAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Registering the callbackAdapter
     * Is needed for waiting for results when downloading data from database
     */
    @Override
    public void onStart() {
        super.onStart();
        DataService.getInstance().registerCallbackAdapterRent(rentAnnounceAdapter);
    }

    /**
     * Unregistering the callback
     * save memory
     */
    @Override
    public void onStop() {
        super.onStop();
        DataService.getInstance().unregisterCallbackAdapterRent();
    }
}
