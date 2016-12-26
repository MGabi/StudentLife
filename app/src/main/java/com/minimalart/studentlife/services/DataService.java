package com.minimalart.studentlife.services;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.models.CardContact;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 13.12.2016.
 */
public class DataService {

    private static final String RENT_TITLE = "title";
    private static final String RENT_PRICE = "price";
    private static final String RENT_ROOMS = "rooms";
    private static final String RENT_LOCATION = "location";
    private static final String RENT_DESCRIPTION = "description";
    private static final String RENT_CHECKED = "checked";
    private static final String RENT_UID = "userUID";

    private RentAnnounceAdapter callbackAdapter;

    private static DataService ourInstance = new DataService();

    public static DataService getInstance() {
        return ourInstance;
    }

    private DataService() {

    }

    public void registerCallbackAdapter(RentAnnounceAdapter callbackAdapter) {
        this.callbackAdapter = callbackAdapter;
    }

    public void unregisterCallbackAdapter() {
        if (callbackAdapter != null) {
            callbackAdapter = null;
        }
    }

    public ArrayList<CardContact> getContactFragItems() {
        ArrayList<CardContact> list = new ArrayList<>();

        list.add(new CardContact("frag_contact_name", "dev_name", "profile_pic"));
        list.add(new CardContact("frag_contact_email", "frag_contact_email_val", "inbox_img"));
        list.add(new CardContact("frag_contact_site", "frag_contact_site_val", "google_plus_img"));
        list.add(new CardContact("frag_contact_gplay", "frag_contact_gplay_url", "google_playstore"));
        return list;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<CardRentAnnounce> getRentAnnounces() {
        final ArrayList<CardRentAnnounce> cardRentAnnounces = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("rent-announces");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CardRentAnnounce card = ds.getValue(CardRentAnnounce.class);
                        cardRentAnnounces.add(card);
                    }
                }
                if (callbackAdapter != null) {
                    callbackAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return cardRentAnnounces;
    }
}
