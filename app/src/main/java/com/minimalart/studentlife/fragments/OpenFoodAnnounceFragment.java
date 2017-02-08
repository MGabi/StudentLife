package com.minimalart.studentlife.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.User;

public class OpenFoodAnnounceFragment extends Fragment {

    private static final String FOOD_PARAM = "parameter_food";
    private static final String REF_FOOD_IMAGES = "food-images";
    private static final String TRANS_ID_PARAM = "parameter_image_transition_name";
    private static final String TRANS_POZ_PARAM = "parameter_position";

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
    private FloatingActionButton fab;
    private Button email;
    private Button phone;
    private View rootView;
    private Bitmap currentImageBitmap;
    private int pozition;


    public OpenFoodAnnounceFragment() {
        // Required empty public constructor
    }

    public static OpenFoodAnnounceFragment newInstanceWithImage(CardFoodZone currentFood, Bitmap bitmap, int poz){
        OpenFoodAnnounceFragment fragment = new OpenFoodAnnounceFragment();
        Bundle args = new Bundle();
        args.putSerializable(FOOD_PARAM, currentFood);
        args.putParcelable(TRANS_ID_PARAM, bitmap);
        args.putInt(TRANS_POZ_PARAM, poz);
        fragment.setArguments(args);
        return fragment;
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
            this.currentImageBitmap = getArguments().getParcelable(TRANS_ID_PARAM);
            this.pozition = getArguments().getInt(TRANS_POZ_PARAM);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_open_food_announce, container, false);
        rootView = v;
        foodImg = (ImageView)v.findViewById(R.id.food_detailed_image);
        foodImg.setImageBitmap(currentImageBitmap);
        foodImg.setTransitionName(String.valueOf(pozition) + "_food");
        initViews(v);
        setViews();
        return v;
    }

    /**
     * Initializing views
     * @param v : context for views to be able to find reference for every view
     */
    public void initViews(View v){
        toolbar = (Toolbar)v.findViewById(R.id.food_detailed_toolbar);
        description = (TextView)v.findViewById(R.id.food_detailed_description);
        price = (TextView)v.findViewById(R.id.food_detailed_price);
        location = (TextView)v.findViewById(R.id.food_detailed_location);
        seller = (TextView)v.findViewById(R.id.food_detailed_seller);
        discount = (TextView)v.findViewById(R.id.food_detailed_discount);
        backBtn = (ImageButton)v.findViewById(R.id.food_detailed_back_btn);
        email = (Button)v.findViewById(R.id.food_detailed_contact_user_email);
        phone = (Button)v.findViewById(R.id.food_detailed_contact_user_phone);
        fab = (FloatingActionButton) v.findViewById(R.id.food_detailed_fab);
        foodUser = null;
    }

    /**
     * Setting up the views
     * adding bold style, colors to few of them at runtime
     */
    public void setViews(){
        if(isAdded()) {
            toolbar.setTitle(currentFood.getFoodTitle());
            description.setText(getResources().getString(R.string.open_rent_description, currentFood.getFoodDesc()));
            location.setText(getResources().getString(R.string.open_rent_location, currentFood.getFoodLoc()));
            price.setText(getResources().getString(R.string.open_rent_price, currentFood.getFoodPrice()));
            seller.setText(getResources().getString(R.string.open_rent_seller, "error"));

            if (currentFood.isChecked()) {
                discount.setTextColor(ContextCompat.getColor(getContext(), R.color.cyan_accent_dark));
                //discount.setTextColor(getResources().getColor(R.color.cyan_accent_dark, getActivity().getTheme()));
                discount.setText(getResources().getString(R.string.open_food_discount_checked));
            } else {
                discount.setTextColor(ContextCompat.getColor(getContext(), R.color.blueDark));
                //discount.setTextColor(getResources().getColor(R.color.blueDark, getActivity().getTheme()));
                discount.setText(getResources().getString(R.string.open_food_discount_unchecked));
            }
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = rootView;
                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users-details")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("food-favorites")
                        .child(currentFood.getFoodID());

                ref.setValue(currentFood.getFoodID()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, getResources().getString(R.string.fav_added), Snackbar.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, getResources().getString(R.string.fav_error), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });

        //setImage(currentFood.getFoodID());
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

    /**
     * Downloading the seller data from Firebase
     * @param userUID : user which will have to be retrieved
     */
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

        DatabaseReference refPhone = FirebaseDatabase.getInstance().getReference().child("users-details").child(userUID).child("phone");
        refPhone.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    setPhone(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setPhone(final String phoneNumber){
        final View view = rootView;
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phoneNumber != null) {
                    if(phoneNumber.length() == 10) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    }
                }else{
                    Snackbar.make(view, getResources().getString(R.string.error_bad_phone), Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).show();
                }
            }
        });
    }

    /**
     * Setting up the current seller after finishing the download
     * @param user : downloaded user
     */
    public void setSeller(User user){
        this.foodUser = user;
        if(isAdded()) {
            seller.setText(getResources().getString(R.string.open_food_seller, foodUser.getName() + " " + foodUser.getSecName()));

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

            StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
            ForegroundColorSpan colorAcc = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.cyan_accent_second));
            ForegroundColorSpan colorPrimary = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.blueDark));
            SpannableStringBuilder sp = new SpannableStringBuilder(seller.getText().toString().replace(":", ""));
            int index = seller.getText().toString().indexOf(":");
            sp.setSpan(colorAcc, index, seller.getText().length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            sp.setSpan(bold, index, seller.getText().length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            sp.setSpan(colorPrimary, 0, index, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            seller.setText(sp);
        }
    }

    /**
     * Downloading corresponding image from FirebaseStorage for current food announce and setting it
     * up to the imageView
     * @param ID : image ID
     */
    public void setImage(String ID){
        Log.v("SETIMAGE", ID);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(REF_FOOD_IMAGES).child(ID);
        Log.v("SETIMAGE", storageReference.toString());
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(storageReference).centerCrop().into(foodImg);
    }
}
