package com.minimalart.studentlife.fragments;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
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
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.interfaces.OnCardDismissedListener;
import com.minimalart.studentlife.others.CardSwipeItemTouchHelper;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.others.Utils;

import java.util.ArrayList;

import static android.view.View.GONE;

public class ModifyRentsFragment extends Fragment{

    private static final String ARG_USER_UID = "useruid";
    private static final String REF_RENT = "rent-announces";
    private static final String REF_RENT_IMAGES = "rent-images";
    private static final int SLIDE_DURATION = 200;

    private String userUID;
    private RecyclerView rents;
    private RentAnnounceAdapter adapter;
    private SwipeRefreshLayout swipe;

    private TextView error_text;

    @ColorInt
    int colorPrimary;
    @ColorInt
    int colorPrimaryDark;
    @ColorInt
    int colorAccent;

    public ModifyRentsFragment() {
        // Required empty public constructor
    }

    public static ModifyRentsFragment newInstance(String param1) {
        ModifyRentsFragment fragment = new ModifyRentsFragment();
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
        View view = inflater.inflate(R.layout.fragment_modify_rents, container, false);
        error_text = (TextView) view.findViewById(R.id.frag_modify_no_rent);
        error_text.setVisibility(View.GONE);

        rents = (RecyclerView) view.findViewById(R.id.frag_modify_rents_recyclerview);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.frag_modify_rents_swipe);
        swipe.setRefreshing(true);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.modif_rents_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back, getActivity().getTheme()));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rents.setLayoutManager(llm);

        adapter = new RentAnnounceAdapter(new ArrayList<CardRentAnnounce>(), getContext());
        rents.setAdapter(adapter);
        downloadAnnounces();
        rents.addItemDecoration(new DividerItemDecoration(rents.getContext(), llm.getOrientation()));

        addDismissBehavior();
        adapter.setOnCardAnnounceClickedListener(new OnCardAnnounceClickedListener() {
            @Override
            public void onCardClicked(CardRentAnnounce card, ImageView imageView, int poz) {
                /**
                 * Canvas: trying to use a recycled bitmap android exception thrown at runtine
                 * have to solve this
                 */
                /*imageView.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache(true));

                OpenRentAnnounceFragment newFragment = OpenRentAnnounceFragment.newInstanceWithImage(card, bitmap, poz);

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
                downloadAnnounces();
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
                        .setTitle(getResources().getString(R.string.desc_delete_r))
                        .setPositiveButton(getResources().getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(Utils.getInstance().isConnectedToNetwork(getContext())){
                                    final int poz = holder.getAdapterPosition();
                                    final String ID = adapter.getList()
                                            .get(poz)
                                            .getAnnounceID();

                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                                    dbRef.child(REF_RENT)
                                            .child(ID)
                                            .removeValue();

                                    StorageReference ref = FirebaseStorage.getInstance()
                                            .getReference();
                                    ref.child(REF_RENT_IMAGES)
                                            .child(ID)
                                            .delete();

                                    final DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference();
                                    deleteRef.child("users-details").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot != null) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    DatabaseReference d = FirebaseDatabase.getInstance().getReference();
                                                    d.child("users-details").child(ds.getKey())
                                                            .child("rent-favorites")
                                                            .child(ID)
                                                            .removeValue();
                                                }
                                            }
                                            adapter.remove(poz);
                                            if(adapter.getItemCount() == 0){
                                                rents.setVisibility(GONE);
                                                error_text.setVisibility(View.VISIBLE);
                                            }else{
                                                rents.setVisibility(View.VISIBLE);
                                                error_text.setVisibility(GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    final DatabaseReference deleteRentNr = FirebaseDatabase.getInstance().getReference()
                                            .child("users-details")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("rent-numbers");
                                    deleteRentNr.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.v("FAVTEST", deleteRentNr.toString());
                                            if(dataSnapshot.getValue(Long.class) == null || dataSnapshot.getValue(Long.class) == 0){
                                                long val = 0;
                                                deleteRentNr.setValue(val);
                                            }else{
                                                long currAnnounces = dataSnapshot.getValue(Long.class);
                                                deleteRentNr.setValue(currAnnounces-1);
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
                                downloadAnnounces();
                            }
                        })
                        .show();
            }
        });
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rents);
    }

    @SuppressWarnings("unchecked")
    public void downloadAnnounces() {
        if(Utils.getInstance().isConnectedToNetwork(getContext())) {
            error_text.setVisibility(View.GONE);
            final ArrayList<CardRentAnnounce> cardRentAnnounces = new ArrayList<>();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_RENT);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            CardRentAnnounce card = ds.getValue(CardRentAnnounce.class);
                            card.setAnnounceID(ds.getKey());
                            if (card.getUserUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                cardRentAnnounces.add(card);
                        }
                        adapter.loadNewData(cardRentAnnounces);
                        adapter.notifyDataSetChanged();
                        swipe.setRefreshing(false);
                        if(adapter.getItemCount() == 0){
                            error_text.setVisibility(View.VISIBLE);
                            rents.setVisibility(View.GONE);
                        }else{
                            error_text.setVisibility(View.GONE);
                            rents.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    swipe.setRefreshing(false);
                }
            });
        }else{
            error_text.setVisibility(View.VISIBLE);
        }

    }
}
