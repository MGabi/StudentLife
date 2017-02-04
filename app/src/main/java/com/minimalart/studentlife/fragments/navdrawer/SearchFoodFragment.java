package com.minimalart.studentlife.fragments.navdrawer;


import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.others.SpaceGridItemDecoration;
import com.minimalart.studentlife.others.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchFoodFragment extends Fragment {

    private static final String REF_FOOD = "food-announces";

    private RecyclerView foodRecyclerView;
    private FoodZoneAdapter foodZoneAdapter;
    private SwipeRefreshLayout swipe;
    private FloatingSearchView searchView;
    private ArrayList<CardFoodZone> fullList;
    @ColorInt int colorPrimary;
    @ColorInt int colorPrimaryDark;
    @ColorInt int colorAccent;

    public SearchFoodFragment() {
        // Required empty public constructor
    }

    /**
     * @return a reference to this fragment
     */
    public static SearchFoodFragment newInstance() {
        SearchFoodFragment fragment = new SearchFoodFragment();
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
        swipe.setRefreshing(true);
        getFood();

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        foodRecyclerView.addItemDecoration(new SpaceGridItemDecoration(spacingInPixels));

        colorPrimary = Utils.getInstance().getColorPrimary(getContext());
        colorPrimaryDark = Utils.getInstance().getColorPrimaryDark(getContext());
        colorAccent = Utils.getInstance().getColorAccent(getContext());
        swipe.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark);
    }

    /**
     * Downloading food announces from firebase database
     * @return a list with current foods in database
     */
    @SuppressWarnings("unchecked")
    public void getFood(){
        final ArrayList<CardFoodZone> cardFoodZones = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_FOOD);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        CardFoodZone card = ds.getValue(CardFoodZone.class);
                        card.setFoodID(ds.getKey());
                        cardFoodZones.add(card);
                    }
                    foodZoneAdapter.loadNewData(cardFoodZones);
                    foodZoneAdapter.notifyDataSetChanged();
                    fullList = cardFoodZones;
                    swipe.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Setting click listeners for every view
     * and defining personal action for any of them
     */
    public void setOnClickListeners(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFood();
            }
        });

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                ArrayList<CardFoodZone> newList = new ArrayList<>();
                for (CardFoodZone card : fullList) {
                    if (card.getFoodDesc().toLowerCase().contains(newQuery.toLowerCase()) ||
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
}