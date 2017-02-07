package com.minimalart.studentlife.fragments.navdrawer;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.fragments.OpenRentAnnounceFragment;
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

public class SearchRentFragment extends Fragment {

    private RecyclerView rentRecyclerView;
    private RentAnnounceAdapter rentAnnounceAdapter;
    private SwipeRefreshLayout swipe;
    private FloatingSearchView floatingSearchView;
    private ArrayList<CardRentAnnounce> fullList;

    private static final String REF_RENT = "rent-announces";
    private static final int SLIDE_DURATION = 200;
    @ColorInt int colorPrimary;
    @ColorInt int colorPrimaryDark;
    @ColorInt int colorAccent;

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
        swipe.setRefreshing(true);
        getRentAnnounces();
        rentRecyclerView.addItemDecoration(new DividerItemDecoration(rentRecyclerView.getContext(), llm.getOrientation()));

        rentAnnounceAdapter.setOnCardAnnounceClickedListener(new OnCardAnnounceClickedListener() {
            @Override
            public void onCardClicked(CardRentAnnounce card, ImageView imageView, int poz) {
                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();

                OpenRentAnnounceFragment newFragment = OpenRentAnnounceFragment.newInstanceWithImage(card, bitmap, poz);

                newFragment.setEnterTransition(new Slide().setDuration(SLIDE_DURATION));
                newFragment.setExitTransition(new Slide().setDuration(SLIDE_DURATION));

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main_without_toolbar, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        rentAnnounceAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("ONLONGTEST", "ONLONG CLICKED FRAG RENT");
                return true;
            }
        });

        colorPrimary = Utils.getInstance().getColorPrimary(getContext());
        colorPrimaryDark = Utils.getInstance().getColorPrimaryDark(getContext());
        colorAccent = Utils.getInstance().getColorAccent(getContext());
        swipe.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark);

    }

    /**
     * Downloading rent announces from firebase database
     * @return a list with current announces in database
     */
    @SuppressWarnings("unchecked")
    public void getRentAnnounces() {
        final ArrayList<CardRentAnnounce> cardRentAnnounces = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_RENT);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CardRentAnnounce card = ds.getValue(CardRentAnnounce.class);
                        card.setAnnounceID(ds.getKey());
                        cardRentAnnounces.add(card);
                    }
                }
                rentAnnounceAdapter.loadNewData(cardRentAnnounces);
                rentAnnounceAdapter.notifyDataSetChanged();
                fullList = cardRentAnnounces;
                swipe.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Setting click listeners for every button
     * and defining personal action for any of them
     */
    public void setOnClickListeners(){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRentAnnounces();
            }
        });

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                ArrayList<CardRentAnnounce> newList = new ArrayList<>();
                for(CardRentAnnounce card : fullList){
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
}
