package com.minimalart.studentlife.fragments.navdrawer;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.services.DataService;

import java.util.ArrayList;

public class FoodZoneFragment extends Fragment {

    private RecyclerView foodRecyclerView;
    private FoodZoneAdapter foodZoneAdapter;

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

        foodRecyclerView = (RecyclerView)view.findViewById(R.id.frag_food_zone_recyclerview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(2);
        foodRecyclerView.setLayoutManager(gridLayoutManager);
        foodRecyclerView.setHasFixedSize(true);

        foodZoneAdapter = new FoodZoneAdapter(new ArrayList<CardFoodZone>(), getContext());
        foodRecyclerView.setAdapter(foodZoneAdapter);
        foodZoneAdapter.loadNewData(DataService.getInstance().getFood());

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        foodRecyclerView.addItemDecoration(new SpaceGridItemDecoration(spacingInPixels));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DataService.getInstance().registerCallbackAdapterFood(foodZoneAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        DataService.getInstance().unregisterCallbackAdapterFood();
    }

}

class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceGridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildLayoutPosition(view);

        outRect.right = space;
        outRect.bottom = space/2;

        // Add top margin only for the first item to avoid double space between items
        if (position == 0 || position == 1){
            outRect.top = space;
        } else{
            outRect.top = 0;
        }

        if(position % 2 != 0){
            outRect.left = 0;
        } else{
            outRect.left = space;
        }
    }
}
