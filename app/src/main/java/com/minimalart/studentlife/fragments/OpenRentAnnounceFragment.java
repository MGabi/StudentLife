package com.minimalart.studentlife.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.models.CardRentAnnounce;

public class OpenRentAnnounceFragment extends Fragment {

    private static final String RENT_PARAM = "parameter_rent_announce";
    private static final String REF_RENT_IMAGES = "rent-images";
    private CardRentAnnounce currentAnnounce;
    private TextView description;
    private TextView location;
    private TextView price;
    private TextView rooms;
    private TextView discount;
    private Toolbar toolbar;
    private ImageView image;
    private ImageButton backBtn;
    private StorageReference imgRef;

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
    }

    public void setViews(){
        toolbar.setTitle(currentAnnounce.getTitle());
        description.setText("Descriere: " + currentAnnounce.getDescription());
        location.setText("Locație: " + currentAnnounce.getLocation());
        price.setText("Preț: " + currentAnnounce.getPrice());
        rooms.setText("Număr camere: " + currentAnnounce.getRooms());
        if(currentAnnounce.isChecked()){
            discount.setTextColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()));
            discount.setText("Acest vânzător oferă un discount dacă ești student!");
        }else{
            discount.setTextColor(getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
            discount.setText("Acest vânzător nu oferă un discount dacă ești student!");
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("FMANAGER", "in onClick");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        setImage(currentAnnounce.getAnnounceID());
    }

    public void setImage(String ID){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        imgRef = firebaseStorage.getReference().child(REF_RENT_IMAGES).child(ID);

        Glide.with(getContext()).using(new FirebaseImageLoader()).load(imgRef).into(image);
    }
}
