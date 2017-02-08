package com.minimalart.studentlife.fragments.navdrawer;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.minimalart.studentlife.adapters.HomeUserAnnouncesAdapter;
import com.minimalart.studentlife.adapters.HomeUserFoodAdapter;
import com.minimalart.studentlife.fragments.OpenFoodAnnounceFragment;
import com.minimalart.studentlife.fragments.OpenRentAnnounceFragment;
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.interfaces.OnCardFoodClickedListener;
import com.minimalart.studentlife.interfaces.OnFavRemoved;
import com.minimalart.studentlife.interfaces.OnImageReadyListener;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;
import com.minimalart.studentlife.others.SpaceHorizontalItemDecoration;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private User currentUser;
    private String currentUserUID;
    private TextView name;
    private TextView no_rents;
    private TextView no_foods;
    private ImageView networkError;

    private RecyclerView homeUserAnnouncesRecyclerView;
    private RecyclerView foodRecyclerView;
    private HomeUserAnnouncesAdapter homeUserAnnouncesAdapter;
    private HomeUserFoodAdapter homeUserFoodAdapter;

    private CardView cardRent;
    private CardView cardFood;

    private View rootView;

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
        rootView = view;
        networkError = (ImageView) view.findViewById(R.id.image_network_error);
        initializeViews(view);
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
            networkError.setVisibility(View.GONE);
            showRefreshLayout(swipe);
            getUserDataFromFirebase();
        }
        else{
            networkError.setVisibility(View.VISIBLE);
            Snackbar.make(rootView, getResources().getString(R.string.error_network_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, null).show();
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
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
            DatabaseReference favRent = FirebaseDatabase.getInstance().getReference()
                    .child("users-details")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("rent-favorites");
            favRent.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        ArrayList<String> favoriteRents = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
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
                    if (dataSnapshot != null) {
                        ArrayList<String> favoriteFoods = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            favoriteFoods.add(data.getKey());
                        }
                        setUserFood(favoriteFoods);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Snackbar.make(rootView, getResources().getString(R.string.error_no_network_connection), Snackbar.LENGTH_LONG).show();
            stopRefreshLayout(swipe);
        }
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
        no_rents = (TextView) view.findViewById(R.id.text_no_rents);
        no_foods = (TextView) view.findViewById(R.id.text_no_foods);

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
        hideRentRecycler();
        showText(no_rents);
        hideFoodRecycler();
        showText(no_foods);

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

        homeUserAnnouncesAdapter.setOnFavRemovedListener(new OnFavRemoved() {
            @Override
            public void onFavRemoved(final String ID, final int poz) {
                Log.v("ONLONGTEST", "ONLONG CLICKED HOME RENT");
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.desc_dialog_delete_rent))
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utils.getInstance().isConnectedToNetwork(getContext())){
                                    DatabaseReference ref = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("users-details")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("rent-favorites")
                                            .child(ID);
                                    ref.removeValue();
                                    homeUserAnnouncesAdapter.remove(poz);
                                    if(homeUserAnnouncesAdapter.getItemCount() == 0){
                                        hideRentRecycler();
                                        showText(no_rents);
                                    }
                                }else{
                                    Log.v("ONLONGTEST", "Not connected to network");
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v("ONLONGTEST", "OH NOOOOOOO");
                            }
                        })
                        .show();
            }
        });

        homeUserFoodAdapter.setOnCardFoodClickedListener(new OnCardFoodClickedListener() {
            @Override
            public void onCardClicked(CardFoodZone card, ImageView image, int poz) {
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

        homeUserFoodAdapter.setOnFavRemovedListener(new OnFavRemoved() {
            @Override
            public void onFavRemoved(final String ID, final int poz) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.desc_dialog_delete_food))
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utils.getInstance().isConnectedToNetwork(getContext())){
                                    DatabaseReference ref = FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("users-details")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("food-favorites")
                                            .child(ID);
                                    ref.removeValue();
                                    homeUserFoodAdapter.remove(poz);
                                    if(homeUserFoodAdapter.getItemCount() == 0) {
                                        hideFoodRecycler();
                                        showText(no_foods);
                                    }
                                }else{
                                    Log.v("ONLONGTEST", "Not connected to network");
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v("ONLONGTEST", "OH NOOOOOOO");
                            }
                        })
                        .show();
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
                    if(cardRentAnnounces.size() == 0){
                        hideRentRecycler();
                        showText(no_rents);
                    }else{
                        showRentRecycler();
                        hideText(no_rents);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void hideText(TextView text){
        text.setVisibility(View.GONE);
    }

    public void showText(TextView text){
        text.setVisibility(View.VISIBLE);
    }

    public void hideRentRecycler(){
        homeUserAnnouncesRecyclerView.setVisibility(View.GONE);
    }

    public void showRentRecycler(){
        homeUserAnnouncesRecyclerView.setVisibility(View.VISIBLE);
    }

    public void hideFoodRecycler(){
        foodRecyclerView.setVisibility(View.GONE);
    }

    public void showFoodRecycler(){
        foodRecyclerView.setVisibility(View.VISIBLE);
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
                    if(cardFoodZones.size() == 0){
                        hideFoodRecycler();
                        showText(no_foods);
                    }else{
                        showFoodRecycler();
                        hideText(no_foods);
                    }
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

