package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.others.SpaceGridItemDecoration;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

public class FoodZoneFragment extends Fragment {

    private RecyclerView foodRecyclerView;
    private FoodZoneAdapter foodZoneAdapter;
    private SwipeRefreshLayout swipe;
    private FloatingSearchView searchView;

    @ColorInt int colorPrimary;
    @ColorInt int colorPrimaryDark;
    @ColorInt int colorAccent;

    public FoodZoneFragment() {
        // Required empty public constructor
    }

    /**
     * @return a reference to this fragment
     */
    public static FoodZoneFragment newInstance() {
        FoodZoneFragment fragment = new FoodZoneFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_zone, container, false);

        initializeViews(view);
        setOnClickListeners();

        return view;
    }

    /**
     * Initializing the necessary views
     * @param view : context view, responsible for findViewById method
     */
    public void initializeViews(View view){
        foodRecyclerView = (RecyclerView)view.findViewById(R.id.frag_food_zone_recyclerview);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.frag_search_food_swipe);
        searchView = (FloatingSearchView) view.findViewById(R.id.search_food_searchview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(2);
        foodRecyclerView.setLayoutManager(gridLayoutManager);
        foodRecyclerView.setHasFixedSize(true);

        foodZoneAdapter = new FoodZoneAdapter(new ArrayList<CardFoodZone>(), getContext());
        foodRecyclerView.setAdapter(foodZoneAdapter);
        foodZoneAdapter.loadNewData(Utils.getInstance().getFood());

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        foodRecyclerView.addItemDecoration(new SpaceGridItemDecoration(spacingInPixels));

        colorPrimary = Utils.getInstance().getColorPrimary(getContext());
        colorPrimaryDark = Utils.getInstance().getColorPrimaryDark(getContext());
        colorAccent = Utils.getInstance().getColorAccent(getContext());
        swipe.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark);
    }

    /**
     * Setting click listeners for every view
     * and defining personal action for any of them
     */
    public void setOnClickListeners(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                foodZoneAdapter.loadNewData(Utils.getInstance().getFood());
                swipe.setRefreshing(false);
            }
        });

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                ArrayList<CardFoodZone> newList = new ArrayList<>();
                Log.v("CARDLIST", String.valueOf(foodZoneAdapter.getList().size()));
                for(CardFoodZone card : foodZoneAdapter.getList()){
                    if(card.getFoodDesc().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getFoodLoc().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getFoodPrice().toLowerCase().contains(newQuery.toLowerCase()) ||
                            card.getFoodTitle().toLowerCase().contains(newQuery.toLowerCase()))
                        newList.add(card);
                }

                foodZoneAdapter.loadNewData(newList);
                foodZoneAdapter.notifyDataSetChanged();
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
        Utils.getInstance().registerCallbackAdapterFood(foodZoneAdapter);
    }

    /**
     * Unregistering the callback
     * save memory
     */
    @Override
    public void onStop() {
        super.onStop();
        Utils.getInstance().unregisterCallbackAdapterFood();
    }

}