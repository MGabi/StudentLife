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
import android.support.v7.widget.DividerItemDecoration;
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
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.fragments.OpenRentAnnounceFragment;
import com.minimalart.studentlife.interfaces.OnAnnounceReported;
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

public class SearchRentFragment extends Fragment{

    private RecyclerView rentRecyclerView;
    private RentAnnounceAdapter rentAnnounceAdapter;
    private SwipeRefreshLayout swipe;
    private FloatingSearchView floatingSearchView;
    private ArrayList<CardRentAnnounce> fullList;
    private TextView noRentsAdded;
    private TextView noQueryResult;
    private View rootView;

    private static final String DEV_TITLE = "STUDENTLIFE report TIP : ";

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
        rootView = view;
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
        noRentsAdded = (TextView) view.findViewById(R.id.frag_rent_no_rents);
        noQueryResult = (TextView) view.findViewById(R.id.frag_rent_query);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rentRecyclerView.setLayoutManager(llm);
        rentRecyclerView.setHasFixedSize(true);

        hideRecycler();
        showText(noRentsAdded);
        hideText(noQueryResult);

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

        final View rootView = view;
        rentAnnounceAdapter.setOnAnnounceReportedListener(new OnAnnounceReported() {
            @Override
            public void onAnnounceReported(final String ID) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.desc_dialog_report_rent))
                        .setSingleChoiceItems(getResources().getStringArray(R.array.array_checkbox_rent), 0, null)
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utils.getInstance().isConnectedToNetwork(getContext())){
                                    String[] list = getResources().getStringArray(R.array.array_checkbox_rent);
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
        rentRecyclerView.setVisibility(View.GONE);
    }

    public void showRecycler(){
        rentRecyclerView.setVisibility(View.VISIBLE);
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
     * Downloading rent announces from firebase database
     * @return a list with current announces in database
     */
    @SuppressWarnings("unchecked")
    public void getRentAnnounces() {
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
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
                    if (rentAnnounceAdapter.getItemCount() == 0) {
                        hideRecycler();
                        showText(noRentsAdded);
                    } else {
                        showRecycler();
                        hideText(noRentsAdded);
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

                if(rentAnnounceAdapter.getItemCount() == 0){
                    showText(noQueryResult);
                }else{
                    hideText(noQueryResult);
                }
            }
        });
    }
}
