package com.minimalart.studentlife.fragments.navdrawer;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.fragments.OpenFoodAnnounceFragment;
import com.minimalart.studentlife.interfaces.OnAnnounceReported;
import com.minimalart.studentlife.interfaces.OnCardFoodClickedListener;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.others.SpaceGridItemDecoration;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

public class SearchFoodFragment extends Fragment {

    private static final String REF_FOOD = "food-announces";
    private static final String DEV_TITLE = "STUDENTLIFE report TIP : ";
    private static int SLIDE_DURATION = 200;

    private RecyclerView foodRecyclerView;
    private FoodZoneAdapter foodZoneAdapter;
    private SwipeRefreshLayout swipe;
    private FloatingSearchView searchView;
    private ArrayList<CardFoodZone> fullList;
    private TextView noFoodAdded;
    private TextView noQueryResult;
    private View rootView;

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
        rootView = view;

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
        noFoodAdded = (TextView) view.findViewById(R.id.frag_food_no_foods);
        noQueryResult = (TextView) view.findViewById(R.id.frag_food_query);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(2);
        foodRecyclerView.setLayoutManager(gridLayoutManager);
        foodRecyclerView.setHasFixedSize(true);
        hideRecycler();
        showText(noFoodAdded);
        hideText(noQueryResult);


        foodZoneAdapter = new FoodZoneAdapter(new ArrayList<CardFoodZone>(), getContext());
        foodRecyclerView.setAdapter(foodZoneAdapter);
        swipe.setRefreshing(true);
        getFood();

        foodZoneAdapter.setOnCardFoodClickedListener(new OnCardFoodClickedListener() {
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
        final View rootView = view;
        foodZoneAdapter.setOnAnnounceReportedListener(new OnAnnounceReported() {
            @Override
            public void onAnnounceReported(final String ID) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.desc_dialog_report_food))
                        .setSingleChoiceItems(getResources().getStringArray(R.array.array_checkbox_food), 0, null)
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utils.getInstance().isConnectedToNetwork(getContext())){
                                    String[] list = getResources().getStringArray(R.array.array_checkbox_food);
                                    int poz = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                    composeEmail(list[poz], ID);
                                }else{
                                    Snackbar.make(rootView, getResources().getString(R.string.error_no_network_connection), Snackbar.LENGTH_LONG).show();
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

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        foodRecyclerView.addItemDecoration(new SpaceGridItemDecoration(spacingInPixels));

        colorPrimary = Utils.getInstance().getColorPrimary(getContext());
        colorPrimaryDark = Utils.getInstance().getColorPrimaryDark(getContext());
        colorAccent = Utils.getInstance().getColorAccent(getContext());
        swipe.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark);
    }

    public void hideText(TextView text){
        text.setVisibility(View.GONE);
    }

    public void showText(TextView text){
        text.setVisibility(View.VISIBLE);
    }

    public void hideRecycler(){
        foodRecyclerView.setVisibility(View.GONE);
    }

    public void showRecycler(){
        foodRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Intent for creating report-email
     * @param extra : type of report
     * @param ID : announce ID
     */
    public void composeEmail(String extra, String ID){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        final String body = "ID-ul anuntului: " + ID + ".\nTip: " + extra + ".\nRaport trimis de catre utilizatorul: " + FirebaseAuth.getInstance().getCurrentUser().getUid();
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ytgabi98@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, DEV_TITLE + extra + ", ID: " + ID);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        Intent chooser = Intent.createChooser(emailIntent, getResources().getString(R.string.email_intent));
        startActivity(chooser);
    }

    /**
     * Downloading food announces from firebase database
     * @return a list with current foods in database
     */
    @SuppressWarnings("unchecked")
    public void getFood(){
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
            final ArrayList<CardFoodZone> cardFoodZones = new ArrayList<>();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_FOOD);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            CardFoodZone card = ds.getValue(CardFoodZone.class);
                            card.setFoodID(ds.getKey());
                            cardFoodZones.add(card);
                        }
                        foodZoneAdapter.loadNewData(cardFoodZones);
                        foodZoneAdapter.notifyDataSetChanged();
                        fullList = cardFoodZones;
                        swipe.setRefreshing(false);
                        if (foodZoneAdapter.getItemCount() == 0) {
                            hideRecycler();
                            showText(noFoodAdded);
                        } else {
                            showRecycler();
                            hideText(noFoodAdded);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Snackbar.make(rootView, getResources().getString(R.string.error_no_network_connection), Snackbar.LENGTH_LONG).show();
            swipe.setRefreshing(false);
        }
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

                if(foodZoneAdapter.getItemCount() == 0){
                    showText(noQueryResult);
                }else{
                    hideText(noQueryResult);
                }
            }
        });
    }
}