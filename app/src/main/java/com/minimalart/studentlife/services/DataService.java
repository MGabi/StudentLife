package com.minimalart.studentlife.services;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.models.CardContact;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 13.12.2016.
 */
public class DataService {

    private static final String REF_RENT = "rent-announces";
    private static final String REF_FOOD = "food-announces";

    private RentAnnounceAdapter callbackAdapterRent;
    private FoodZoneAdapter callbackAdapterFood;

    private static DataService ourInstance = new DataService();

    public static DataService getInstance() {
        return ourInstance;
    }

    private DataService() {

    }

    public void registerCallbackAdapterRent(RentAnnounceAdapter callbackAdapter) {
        this.callbackAdapterRent = callbackAdapter;
    }

    public void unregisterCallbackAdapterRent() {
        if (callbackAdapterRent != null) {
            callbackAdapterRent = null;
        }
    }

    public void registerCallbackAdapterFood(FoodZoneAdapter callbackAdapter) {
        this.callbackAdapterFood = callbackAdapter;
    }

    public void unregisterCallbackAdapterFood() {
        if (callbackAdapterFood != null) {
            callbackAdapterFood = null;
        }
    }

    /**
     * @return the list with contact cards for About fragment
     */
    public ArrayList<CardContact> getContactFragItems() {
        ArrayList<CardContact> list = new ArrayList<>();

        list.add(new CardContact("frag_contact_name", "dev_name", "profile_pic"));
        list.add(new CardContact("frag_contact_email", "frag_contact_email_val", "inbox_img"));
        list.add(new CardContact("frag_contact_site", "frag_contact_site_val", "google_plus_img"));
        list.add(new CardContact("frag_contact_gplay", "frag_contact_gplay_url", "google_playstore"));
        return list;
    }

    /**
     * Downloading rent announces from firebase database
     * @return a list with current announces in database
     */
    @SuppressWarnings("unchecked")
    public ArrayList<CardRentAnnounce> getRentAnnounces() {
        final ArrayList<CardRentAnnounce> cardRentAnnounces = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_RENT);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        CardRentAnnounce card = ds.getValue(CardRentAnnounce.class);
                        card.setAnnounceID(ds.getKey());
                        cardRentAnnounces.add(card);
                    }
                }
                if (callbackAdapterRent != null) {
                    callbackAdapterRent.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return cardRentAnnounces;
    }

    /**
     * Downloading food announces from firebase database
     * @return a list with current foods in database
     */
    @SuppressWarnings("unchecked")
    public ArrayList<CardFoodZone> getFood(){
        final ArrayList<CardFoodZone> cardFoodZones = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(REF_FOOD);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        CardFoodZone card = ds.getValue(CardFoodZone.class);
                        card.setFoodID(ds.getKey());
                        cardFoodZones.add(card);
                    }
                    if (callbackAdapterFood != null) {
                        callbackAdapterFood.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return cardFoodZones;
    }

    /**
     * Checking if app is connected to network
     * @param context : context of the current activity/fragment in which this method is called
     * @return the state of the network connection : true if it is connected, false if not
     */
    public boolean isConnectedToNetwork(Context context) {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }
}
