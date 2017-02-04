package com.minimalart.studentlife.others;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.adapters.FoodZoneAdapter;
import com.minimalart.studentlife.adapters.RentAnnounceAdapter;
import com.minimalart.studentlife.models.CardContact;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 13.12.2016.
 */
public class Utils {

    private static Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {

    }

    @ColorInt
    public int getColorPrimary(Context context){
        @ColorInt int colorPrimary;
        TypedValue t = new TypedValue();
        TypedArray ta = context.obtainStyledAttributes(t.data, new int[]{ R.attr.colorPrimary });
        colorPrimary = ta.getColor(0, Color.GRAY);
        ta.recycle();

        return colorPrimary;
    }

    @ColorInt
    public int getColorPrimaryDark(Context context){
        @ColorInt int colorPrimaryDark;
        TypedValue t = new TypedValue();
        TypedArray ta = context.obtainStyledAttributes(t.data, new int[]{ R.attr.colorPrimaryDark });
        colorPrimaryDark = ta.getColor(0, Color.GRAY);
        ta.recycle();

        return colorPrimaryDark;
    }

    @ColorInt
    public int getColorAccent(Context context){
        @ColorInt int colorAccent;
        TypedValue t = new TypedValue();
        TypedArray ta = context.obtainStyledAttributes(t.data, new int[]{ R.attr.colorAccent });
        colorAccent = ta.getColor(0, Color.GRAY);
        ta.recycle();

        return colorAccent;
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
     * Checking if app is connected to network
     * @param context : context of the current activity/fragment in which method is called
     * @return the state of the network connection : true if it is connected, false if not
     */
    public boolean isConnectedToNetwork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }
}
