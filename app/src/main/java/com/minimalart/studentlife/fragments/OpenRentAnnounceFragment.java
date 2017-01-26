package com.minimalart.studentlife.fragments;

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
import com.minimalart.studentlife.models.CardRentAnnounce;
import com.minimalart.studentlife.models.User;

public class OpenRentAnnounceFragment extends Fragment {

    private static final String RENT_PARAM = "parameter_rent_announce";
    private static final String REF_RENT_IMAGES = "rent-images";
    private CardRentAnnounce currentAnnounce;
    private TextView description;
    private TextView location;
    private TextView price;
    private TextView rooms;
    private TextView discount;
    private TextView seller;
    private Toolbar toolbar;
    private ImageView image;
    private ImageButton backBtn;
    private StorageReference imgRef;
    private User sellerUser;
    public OpenRentAnnounceFragment() {
        // Required empty public constructor
    }

    public static OpenRentAnnounceFragment newInstance(CardRentAnnounce currentCard) {
        OpenRentAnnounceFragment fragment = new OpenRentAnnounceFragment();
        Bundle args = new Bundle();
        args.putSerializable(RENT_PARAM, currentCard);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.currentAnnounce = (CardRentAnnounce)getArguments().getSerializable(RENT_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_rent_announce, container, false);
        initViews(view);
        setViews();
        return view;
    }

    public void initViews(View view){
        toolbar = (Toolbar)view.findViewById(R.id.detailed_toolbar);
        image = (ImageView)view.findViewById(R.id.detailed_image);
        description = (TextView)view.findViewById(R.id.detailed_description);
        location = (TextView)view.findViewById(R.id.detailed_location);
        price = (TextView)view.findViewById(R.id.detailed_price);
        rooms = (TextView)view.findViewById(R.id.detailed_rooms);
        discount = (TextView)view.findViewById(R.id.detailed_discount);
        backBtn = (ImageButton)view.findViewById(R.id.detailed_back_btn);
        seller = (TextView)view.findViewById(R.id.detailed_seller);
        sellerUser = null;
    }

    public void setViews(){
        toolbar.setTitle(currentAnnounce.getTitle());
        description.setText(getResources().getString(R.string.open_rent_description, currentAnnounce.getDescription()));
        location.setText(getResources().getString(R.string.open_rent_location, currentAnnounce.getLocation()));
        price.setText(getResources().getString(R.string.open_rent_price, currentAnnounce.getPrice()));
        rooms.setText(getResources().getString(R.string.open_rent_rooms, currentAnnounce.getRooms()));
        seller.setText(getResources().getString(R.string.open_rent_seller, "error"));
        if(currentAnnounce.isChecked()){
            discount.setTextColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()));
            discount.setText(getResources().getString(R.string.open_rent_discount_checked));
        }else{
            discount.setTextColor(getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
            discount.setText(getResources().getString(R.string.open_rent_discount_unchecked));
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("FMANAGER", "in onClick");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        setImage(currentAnnounce.getAnnounceID());
        downloadSellerCredentials(currentAnnounce.getUserUID());

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

        sp = new SpannableStringBuilder(rooms.getText());
        index = rooms.getText().toString().indexOf(":");
        sp.setSpan(bold, 0, index+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        rooms.setText(sp);

        sp = new SpannableStringBuilder(discount.getText());
        index = discount.getText().toString().length();
        sp.setSpan(bold, 0, index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        discount.setText(sp);
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
        this.sellerUser = user;
        seller.setText(getResources().getString(R.string.open_rent_seller, sellerUser.getName() + " " + sellerUser.getSecName()));

        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        ForegroundColorSpan colorAcc = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent, getActivity().getTheme()));
        ForegroundColorSpan colorPrimary = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
        SpannableStringBuilder sp = new SpannableStringBuilder(seller.getText().toString().replace(":", ""));
        int index = seller.getText().toString().indexOf(":");
        sp.setSpan(colorAcc, index, seller.getText().length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(bold, index, seller.getText().length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(colorPrimary, 0, index, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        seller.setText(sp);
    }

    public void setImage(String ID){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        imgRef = firebaseStorage.getReference().child(REF_RENT_IMAGES).child(ID);

        Glide.with(getContext()).using(new FirebaseImageLoader()).load(imgRef).into(image);
    }
}
