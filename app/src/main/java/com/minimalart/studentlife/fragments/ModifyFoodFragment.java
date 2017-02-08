package com.minimalart.studentlife.fragments;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.interfaces.OnCardDismissedListener;
import com.minimalart.studentlife.interfaces.OnCardFoodClickedListener;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.others.CardSwipeItemTouchHelper;
import com.minimalart.studentlife.others.SpaceGridItemDecoration;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

import static android.view.View.GONE;

public class ModifyFoodFragment extends Fragment {
    private static final String ARG_USER_UID = "useruid";
    private static final String REF_FOOD = "food-announces";
    private static final String REF_FOOD_IMAGES = "food-images";
    private static final int SLIDE_DURATION = 200;
    private String userUID;
    private RecyclerView foods;
    private FoodZoneAdapter adapter;
    private SwipeRefreshLayout swipe;

    private TextView error_text;

    @ColorInt
    int colorPrimary;
    @ColorInt
    int colorPrimaryDark;
    @ColorInt
    int colorAccent;

    public ModifyFoodFragment() {
        // Required empty public constructor
    }

    public static ModifyFoodFragment newInstance(String param1) {
        ModifyFoodFragment fragment = new ModifyFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_UID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userUID = getArguments().getString(ARG_USER_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_food, container, false);
        error_text = (TextView) view.findViewById(R.id.frag_modify_no_food);
        error_text.setVisibility(GONE);
        foods = (RecyclerView) view.findViewById(R.id.frag_modify_food_recyclerview);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.frag_modify_food_swipe);
        swipe.setRefreshing(true);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.modif_food_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back, getActivity().getTheme()));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.setSpanCount(2);
        foods.setLayoutManager(gridLayoutManager);

        adapter = new FoodZoneAdapter(new ArrayList<CardFoodZone>(), getContext());
        foods.setAdapter(adapter);
        downloadFoods();
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.card_grid_spacing);
        foods.addItemDecoration(new SpaceGridItemDecoration(spacingInPixels));

        addDismissBehavior();
        adapter.setOnCardFoodClickedListener(new OnCardFoodClickedListener() {
            @Override
            public void onCardClicked(CardFoodZone card, ImageView image, int poz) {
                /**
                 * Canvas: trying to use a recycled bitmap android exception thrown at runtine
                 * have to solve this
                 */
                /*image.buildDrawingCache(true);
                Bitmap bitmap = image.getDrawingCache(true);

                OpenFoodAnnounceFragment newFragment = OpenFoodAnnounceFragment.newInstanceWithImage(card, bitmap, poz);

                newFragment.setEnterTransition(new Slide().setDuration(SLIDE_DURATION));
                newFragment.setExitTransition(new Slide().setDuration(SLIDE_DURATION));

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_main_without_toolbar, newFragment)
                        .addToBackStack(null)
                        .commit();*/
            }
        });


        colorPrimary = Utils.getInstance().getColorPrimary(getContext());
        colorPrimaryDark = Utils.getInstance().getColorPrimaryDark(getContext());
        colorAccent = Utils.getInstance().getColorAccent(getContext());
        swipe.setColorSchemeColors(colorPrimary, colorAccent, colorPrimaryDark);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadFoods();
            }
        });
        return view;
    }

    public void addDismissBehavior(){
        CardSwipeItemTouchHelper callback = new CardSwipeItemTouchHelper(adapter);
        callback.setOnCardDismissedListener(new OnCardDismissedListener() {
            @Override
            public void onCardRemoved(final RecyclerView.ViewHolder holder) {

                new AlertDialog.Builder(getContext())
                        .setTitle(getResources().getString(R.string.desc_delele_f))
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utils.getInstance().isConnectedToNetwork(getContext())){
                                    final int poz = holder.getAdapterPosition();
                                    final String ID = adapter.getList()
                                            .get(poz)
                                            .getFoodID();

                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                    dbRef.child(REF_FOOD)
                                            .child(ID)
                                            .removeValue();

                                    StorageReference ref = FirebaseStorage.getInstance().getReference();
                                    ref.child(REF_FOOD_IMAGES)
                                            .child(ID)
                                            .delete();

                                    DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference();
                                    deleteRef.child("users-details").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot != null){
                                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                                    DatabaseReference d = FirebaseDatabase.getInstance().getReference();
                                                    d.child("users-details").child(ds.getKey())
                                                            .child("food-favorites")
                                                            .child(ID)
                                                            .removeValue();
                                                }
                                            }
                                            adapter.remove(poz);
                                            if(adapter.getItemCount() == 0){
                                                foods.setVisibility(GONE);
                                                error_text.setVisibility(View.VISIBLE);
                                            }else{
                                                foods.setVisibility(View.VISIBLE);
                                                error_text.setVisibility(GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    final DatabaseReference deleteFoodNr = FirebaseDatabase.getInstance().getReference()
                                            .child("users-details")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("food-numbers");
                                    deleteFoodNr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.v("FAVTEST", deleteFoodNr.toString());
                                            if(dataSnapshot.getValue(Long.class) == null || dataSnapshot.getValue(Long.class) == 0){
                                                long val = 0;
                                                deleteFoodNr.setValue(val);
                                            }else{
                                                long currAnnounces = dataSnapshot.getValue(Long.class);
                                                deleteFoodNr.setValue(currAnnounces-1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else{

                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                downloadFoods();
                            }
                        })
                        .show();
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(foods);
    }

    @SuppressWarnings("unchecked")
    public void downloadFoods(){
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
            error_text.setVisibility(GONE);
            final ArrayList<CardFoodZone> cardFoodZones = new ArrayList<>();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_FOOD);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            CardFoodZone card = ds.getValue(CardFoodZone.class);
                            card.setFoodID(ds.getKey());
                            if (card.getUserUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                cardFoodZones.add(card);
                        }
                    }
                    adapter.loadNewData(cardFoodZones);
                    adapter.notifyDataSetChanged();
                    swipe.setRefreshing(false);
                    if(adapter.getItemCount() == 0){
                        error_text.setVisibility(View.VISIBLE);
                        foods.setVisibility(GONE);
                    }else{
                        error_text.setVisibility(GONE);
                        foods.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    swipe.setRefreshing(false);
                }
            });
        }else {
            error_text.setVisibility(View.VISIBLE);
        }
    }

}
