package com.minimalart.studentlife.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;

public class OpenFoodAnnounceFragment extends Fragment {

    private static final String FOOD_PARAM = "parameter_food";
    private static final String REF_FOOD_IMAGES = "food-images";
    private CardFoodZone currentFood;
    private ImageView foodImg;
    private TextView description;
    private TextView location;
    private TextView price;
    private TextView seller;
    private TextView discount;
    private Toolbar toolbar;
    private ImageButton backBtn;
    private User foodUser;
    private StorageReference storageReference;
    private Button email;
    private Button phone;


    public OpenFoodAnnounceFragment() {
        // Required empty public constructor
    }

    public static OpenFoodAnnounceFragment newInstance(CardFoodZone card) {
        OpenFoodAnnounceFragment fragment = new OpenFoodAnnounceFragment();
        Bundle args = new Bundle();
        args.putSerializable(FOOD_PARAM, card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.currentFood = (CardFoodZone)getArguments().getSerializable(FOOD_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_open_food_announce, container, false);
        initViews(v);
        setViews();
        return v;
    }

    public void initViews(View v){
        toolbar = (Toolbar)v.findViewById(R.id.food_detailed_toolbar);
        description = (TextView)v.findViewById(R.id.food_detailed_description);
        price = (TextView)v.findViewById(R.id.food_detailed_price);
        location = (TextView)v.findViewById(R.id.food_detailed_location);
        seller = (TextView)v.findViewById(R.id.food_detailed_seller);
        foodImg = (ImageView)v.findViewById(R.id.food_detailed_image);
        discount = (TextView)v.findViewById(R.id.food_detailed_discount);
        backBtn = (ImageButton)v.findViewById(R.id.food_detailed_back_btn);
        email = (Button)v.findViewById(R.id.food_detailed_contact_user_email);
        phone = (Button)v.findViewById(R.id.food_detailed_contact_user_phone);
        foodUser = null;
    }

    public void setViews(){
        toolbar.setTitle(currentFood.getFoodTitle());
        description.setText(getResources().getString(R.string.open_rent_description, currentFood.getFoodDesc()));
        location.setText(getResources().getString(R.string.open_rent_location, currentFood.getFoodLoc()));
        price.setText(getResources().getString(R.string.open_rent_price, currentFood.getFoodPrice()));
        seller.setText(getResources().getString(R.string.open_rent_seller, "error"));

        if(currentFood.isChecked()){
            discount.setTextColor(getResources().getColor(R.color.colorAccentDark, getActivity().getTheme()));
            discount.setText(getResources().getString(R.string.open_food_discount_checked));
        }else{
            discount.setTextColor(getResources().getColor(R.color.blueDark, getActivity().getTheme()));
            discount.setText(getResources().getString(R.string.open_food_discount_unchecked));
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, foodUser.getEmail());
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Alertă" + currentFood.getFoodTitle());
                Intent chooser = Intent.createChooser(emailIntent, "Trimite e-mail...");
                startActivity(chooser);
            }
        });

        setImage(currentFood.getFoodID());
        downloadSellerCredentials(currentFood.getUserUID());

        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        SpannableStringBuilder sp = new SpannableStringBuilder(description.getText());
        int index = description.getText().toString().indexOf(":");
        sp.setSpan(bold, 0, index+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        description.setText(sp);

        sp = new SpannableStringBuilder(location.getText());
        index = location.getText().toString().indexOf(":");
        sp.setSpan(bold, 0, index+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        location.setText(sp);

        sp = new SpannableStringBuilder(price.getText());
        index = price.getText().toString().indexOf(":");
        sp.setSpan(bold, 0, index+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        price.setText(sp);
    }

    public void downloadSellerCredentials(String userUID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userUID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                setSeller(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setSeller(User user){
        this.foodUser = user;
        seller.setText(getResources().getString(R.string.open_food_seller, foodUser.getName() + " " + foodUser.getSecName()));

        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        ForegroundColorSpan colorAcc = new ForegroundColorSpan(getResources().getColor(R.color.colorAccentSecond, getActivity().getTheme()));
        ForegroundColorSpan colorPrimary = new ForegroundColorSpan(getResources().getColor(R.color.blueDark, getActivity().getTheme()));
        SpannableStringBuilder sp = new SpannableStringBuilder(seller.getText().toString().replace(":", ""));
        int index = seller.getText().toString().indexOf(":");
        sp.setSpan(colorAcc, index, seller.getText().length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(bold, index, seller.getText().length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(colorPrimary, 0, index, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        seller.setText(sp);
    }

    public void setImage(String ID){
        Log.v("SETIMAGE", ID);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(REF_FOOD_IMAGES).child(ID);
        Log.v("SETIMAGE", storageReference.toString());
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(storageReference).centerCrop().into(foodImg);
    }
}
