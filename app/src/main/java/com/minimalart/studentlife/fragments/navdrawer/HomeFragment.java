package com.minimalart.studentlife.fragments.navdrawer;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.HomeUserAnnouncesAdapter;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private User currentUser;
    private String currentUserUID;
    private TextView name;
    private RecyclerView homeUserAnnouncesRecyclerView;
    private RecyclerView foodRecyclerView;
    private HomeUserAnnouncesAdapter homeUserAnnouncesAdapter;

    private static final String REF_RENT = "rent-announces";
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * @return a reference to this fragment
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        getUserDataFromFirebase();

        return view;
    }

    public void getUserDataFromFirebase(){
        DatabaseReference dbRef;
        try {
            dbRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }catch(NullPointerException e){
            Toast.makeText(getContext(), R.string.error_unknown, Toast.LENGTH_LONG).show();
            return;
        }

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    User user = dataSnapshot.getValue(User.class);
                    setCurrentUser(user);
                    setUserAnnounces();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initializeViews(View view){
        name = (TextView) view.findViewById(R.id.home_text_name);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayoutHome);

        homeUserAnnouncesRecyclerView = (RecyclerView) view.findViewById(R.id.home_my_rents_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        homeUserAnnouncesRecyclerView.setLayoutManager(llm);
        homeUserAnnouncesRecyclerView.setHasFixedSize(true);

        homeUserAnnouncesAdapter = new HomeUserAnnouncesAdapter(new ArrayList<CardRentAnnounce>(), getContext());
        homeUserAnnouncesRecyclerView.setAdapter(homeUserAnnouncesAdapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        homeUserAnnouncesRecyclerView.addItemDecoration(new SpaceHorizontalItemDecoration(spacingInPixels));

        showRefreshLayout(swipe);
        name.setVisibility(View.GONE);

    }

    public void setUserAnnounces(){
        final ArrayList<CardRentAnnounce> cardRentAnnounces = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_RENT);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CardRentAnnounce card = ds.getValue(CardRentAnnounce.class);
                        card.setAnnounceID(ds.getKey());
                        if(card.getUserUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            cardRentAnnounces.add(card);
                    }
                    updateAdapter(cardRentAnnounces);
                    setViews();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateAdapter(ArrayList<CardRentAnnounce> cardRentAnnounces){
        Log.v("UPDATES", "in updateAdapter");
        this.homeUserAnnouncesAdapter.loadNewData(cardRentAnnounces);
        homeUserAnnouncesAdapter.notifyDataSetChanged();
    }
    public void setViews(){
        name.setText("Salut, " + getCurrentUser().getName() + ".");
        name.setVisibility(View.VISIBLE);
        stopRefreshLayout(swipe);
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public void setCurrentUserUID(String UID){
        this.currentUserUID = UID;
    }

    public String getCurrentUserUID(){
        return currentUserUID;
    }

    public void showRefreshLayout(SwipeRefreshLayout srf){
        srf.setRefreshing(true);
    }

    public void stopRefreshLayout(SwipeRefreshLayout srf){
        srf.setRefreshing(false);
    }
}

class SpaceHorizontalItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceHorizontalItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildLayoutPosition(view);

        outRect.top = space;
        outRect.bottom = space;
        outRect.right = space;

        if (position == 0){
            outRect.left = space;
        } else{
            outRect.left = 0;
        }
    }
}

