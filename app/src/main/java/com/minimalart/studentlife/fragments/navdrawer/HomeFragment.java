package com.minimalart.studentlife.fragments.navdrawer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.adapters.HomeUserAnnouncesAdapter;
import com.minimalart.studentlife.adapters.HomeUserFoodAdapter;
import com.minimalart.studentlife.fragments.OpenFoodAnnounceFragment;
import com.minimalart.studentlife.fragments.OpenRentAnnounceFragment;
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.interfaces.OnCardFoodClickedListener;
import com.minimalart.studentlife.interfaces.OnImageReadyListener;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;
import com.minimalart.studentlife.others.SpaceHorizontalItemDecoration;
import com.minimalart.studentlife.others.Utils;
import com.minimalart.studentlife.transitions.DetailsTransition;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private User currentUser;
    private String currentUserUID;
    private TextView name;

    private RecyclerView homeUserAnnouncesRecyclerView;
    private RecyclerView foodRecyclerView;
    private HomeUserAnnouncesAdapter homeUserAnnouncesAdapter;
    private HomeUserFoodAdapter homeUserFoodAdapter;

    private CardView cardRent;
    private CardView cardFood;

    @ColorInt int colorPrimary;
    @ColorInt int colorPrimaryDark;
    @ColorInt int colorAccent;

    private static final String REF_RENT = "rent-announces";
    private static final String REF_FOOD = "food-announces";
    private static final int SLIDE_DURATION = 200;

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
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
            showRefreshLayout(swipe);
            getUserDataFromFirebase();
        }
        else{
            Snackbar.make(view, getResources().getString(R.string.error_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v("NETWORKTEST", "MAIN");
                        }}).show();
            stopRefreshLayout(swipe);
        }
        return view;
    }

    /**
     * Downloads current user data from firebase
     */
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
                    prepareFavLists();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void prepareFavLists(){
        DatabaseReference favRent = FirebaseDatabase.getInstance().getReference()
                .child("users-details")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("rent-favorites");
        favRent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    ArrayList<String> favoriteRents = new ArrayList<>();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        favoriteRents.add(data.getKey());
                    }
                    setUserAnnounces(favoriteRents);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference favFood = FirebaseDatabase.getInstance().getReference()
                .child("users-details")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("food-favorites");
        favFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    ArrayList<String> favoriteFoods = new ArrayList<>();
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        favoriteFoods.add(data.getKey());
                    }
                    setUserFood(favoriteFoods);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*setUserAnnounces();
        setUserFood();*/
    }

    /**
     * Initialize views
     * @param view : used for findViewById method
     */
    public void initializeViews(View view){
        name = (TextView) view.findViewById(R.id.home_text_name);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayoutHome);
        cardFood = (CardView) view.findViewById(R.id.card_show_food);
        cardRent = (CardView) view.findViewById(R.id.card_show_rents);

        colorPrimary = Utils.getInstance().getColorPrimary(getContext());
        colorPrimaryDark = Utils.getInstance().getColorPrimaryDark(getContext());
        colorAccent = Utils.getInstance().getColorAccent(getContext());

        swipe.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareFavLists();
            }
        });

        homeUserAnnouncesRecyclerView = (RecyclerView) view.findViewById(R.id.home_my_rents_recyclerview);
        foodRecyclerView = (RecyclerView) view.findViewById(R.id.home_my_food_recyclerview);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        llm2.setOrientation(LinearLayoutManager.HORIZONTAL);
        homeUserAnnouncesRecyclerView.setLayoutManager(llm);
        foodRecyclerView.setLayoutManager(llm2);
        homeUserAnnouncesRecyclerView.setHasFixedSize(true);
        foodRecyclerView.setHasFixedSize(true);

        homeUserAnnouncesAdapter = new HomeUserAnnouncesAdapter(new ArrayList<CardRentAnnounce>(), getContext());
        homeUserAnnouncesRecyclerView.setAdapter(homeUserAnnouncesAdapter);
        homeUserFoodAdapter = new HomeUserFoodAdapter(new ArrayList<CardFoodZone>(), getContext());
        foodRecyclerView.setAdapter(homeUserFoodAdapter);

        homeUserAnnouncesAdapter.setOnImageReadyListener(new OnImageReadyListener() {
            @Override
            public void onImageReady() {
                /**
                 * Not done dealing with fragment shared element transition
                 * this is a callback for when image is completely loaded from
                 * storage with Glide/Picasso
                 * */
                /*getActivity().supportStartPostponedEnterTransition();*/
            }
        });

        homeUserAnnouncesAdapter.setOnCardAnnounceClickedListener(new OnCardAnnounceClickedListener() {
            @Override
            public void onCardClicked(CardRentAnnounce card, ImageView imageView, int poz) {
                Log.v("TRANSITIONTEST", "CLICKED FROM HOME FRAGMENT --- RENT");
                Log.v("TRANSITIONTEST", "in onClick: " + imageView.getTransitionName());

                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();
                /**
                 * TODO: HAVE TO FIGURE OUT A WAY TO MAKE SHARED ELEMENT TRANSITION
                 * TODO: TO WORK IN FRAGMENTS WITH ASYNC DOWNLOADED DATA !!!!!!!!
                 */
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

        homeUserAnnouncesAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("ONLONGTEST", "ONLONG CLICKED HOME RENT");
                return true;
            }
        });

        homeUserFoodAdapter.setOnCardFoodClickedListener(new OnCardFoodClickedListener() {
            @Override
            public void onCardClicked(CardFoodZone card, ImageView image, int poz) {
                Log.v("TRANSITIONTEST", "CLICKED FROM HOME FRAGMENT --- FOOD");
                Log.v("TRANSITIONTEST", "in onClick: " + image.getTransitionName());

                image.setDrawingCacheEnabled(true);
                Bitmap bitmap = image.getDrawingCache();

                OpenFoodAnnounceFragment newFragment = OpenFoodAnnounceFragment.newInstanceWithImage(card, bitmap, poz);

                newFragment.setEnterTransition(new Slide().setDuration(SLIDE_DURATION));
                newFragment.setExitTransition(new Slide().setDuration(SLIDE_DURATION));

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main_without_toolbar, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        homeUserFoodAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.v("ONLONGTEST", "ONLONG CLICKED HOME FOOD");
                return true;
            }
        });

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_home_spacing);
        homeUserAnnouncesRecyclerView.addItemDecoration(new SpaceHorizontalItemDecoration(spacingInPixels));
        foodRecyclerView.addItemDecoration(new SpaceHorizontalItemDecoration(spacingInPixels));

        showRefreshLayout(swipe);
        name.setVisibility(View.GONE);
        cardFood.setVisibility(View.GONE);
        cardRent.setVisibility(View.GONE);
    }

    /**
     * Setting up the current user rent announces in home fragment
     */
    public void setUserAnnounces(final ArrayList<String> favRents){
        final ArrayList<CardRentAnnounce> cardRentAnnounces = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_RENT);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if(favRents.contains(ds.getKey())) {
                            CardRentAnnounce card = ds.getValue(CardRentAnnounce.class);
                            card.setAnnounceID(ds.getKey());
                            //if(card.getUserUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            cardRentAnnounces.add(card);
                        }
                    }
                    updateAdapterRent(cardRentAnnounces);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Setting up the current user food announces in home fragment
     */
    public void setUserFood(final ArrayList<String> favFoods){
        final ArrayList<CardFoodZone> cardFoodZones = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_FOOD);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        if(favFoods.contains(ds.getKey())) {
                            CardFoodZone card = ds.getValue(CardFoodZone.class);
                            card.setFoodID(ds.getKey());
                            cardFoodZones.add(card);
                        }
                    }
                    updateAdapterFood(cardFoodZones);
                }
                setViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Updating the adapter for when download process is done, because download is done in another thread
     * @param cardRentAnnounces : downloaded list
     */
    public void updateAdapterRent(ArrayList<CardRentAnnounce> cardRentAnnounces){
        this.homeUserAnnouncesAdapter.loadNewData(cardRentAnnounces);
        homeUserAnnouncesAdapter.notifyDataSetChanged();
    }

    /**
     * Updating the adapter for when download process is done, because download is done in another thread
     * @param cardFoodZones : downloaded list
     */
    public void updateAdapterFood(ArrayList<CardFoodZone> cardFoodZones){
        this.homeUserFoodAdapter.loadNewData(cardFoodZones);
        homeUserFoodAdapter.notifyDataSetChanged();
    }

    /**
     * Setting up the views;
     */
    public void setViews(){
        name.setText("Salut, " + getCurrentUser().getName() + ".");
        name.setVisibility(View.VISIBLE);
        cardRent.setVisibility(View.VISIBLE);
        cardFood.setVisibility(View.VISIBLE);
        stopRefreshLayout(swipe);
    }

    /**
     * Setting current user
     * @param user : user to be setted
     */
    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    /**
     * Getting the current user
     * @return currentUser
     */
    public User getCurrentUser(){
        return currentUser;
    }

    /**
     * Setting current user UID for further usability
     * @param UID : UID for current user
     */
    public void setCurrentUserUID(String UID){
        this.currentUserUID = UID;
    }

    /**
     * Getting the current user UID
     * @return currentUserUID
     */
    public String getCurrentUserUID(){
        return currentUserUID;
    }

    /**
     * Putting refreshLayout in refresh state
     * @param srf
     */
    public void showRefreshLayout(SwipeRefreshLayout srf){
        srf.setRefreshing(true);
    }

    /**
     * Stopping refreshLayout from refreshing
     * @param srf
     */
    public void stopRefreshLayout(SwipeRefreshLayout srf){
        srf.setRefreshing(false);
    }
}

