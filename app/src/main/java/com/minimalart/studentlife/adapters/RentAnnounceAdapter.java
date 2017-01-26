package com.minimalart.studentlife.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 17.12.2016.
 */

public class RentAnnounceAdapter extends RecyclerView.Adapter<RentAnnounceAdapter.RentAnnounceViewHolder> {

    private ArrayList<CardRentAnnounce> cardRentAnnounceArrayList;
    private Context context;

    public RentAnnounceAdapter(ArrayList<CardRentAnnounce> cardRentAnnounceArrayList, Context context) {
        this.cardRentAnnounceArrayList = cardRentAnnounceArrayList;
        this.context = context;
    }

    @Override
    public RentAnnounceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View announceCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_frag_search_rent, parent, false);

        return new RentAnnounceViewHolder(announceCard);
    }

    @Override
    public void onBindViewHolder(RentAnnounceViewHolder holder, int position) {

        final CardRentAnnounce cardRentAnnounce = cardRentAnnounceArrayList.get(position);
        holder.updateUI(cardRentAnnounce);

        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.search_ripple);
        mlr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)context).openRentAnnounceFragment(cardRentAnnounce);
            }
        });
    }

    public void loadNewData(ArrayList<CardRentAnnounce> list){
        cardRentAnnounceArrayList = list;
    }

    public ArrayList<CardRentAnnounce> getList(){
        return cardRentAnnounceArrayList;
    }

    @Override
    public int getItemCount() {
        return cardRentAnnounceArrayList.size();
    }

    public class RentAnnounceViewHolder extends RecyclerView.ViewHolder {

        private TextView rentTitle;
        private TextView rentRooms;
        private TextView rentPrice;
        private TextView rentLocation;
        private ImageView rentImage;
        private Uri uri;

        private static final String REF_RENT_IMAGES = "rent-images";

        public RentAnnounceViewHolder(View itemView) {
            super(itemView);
            rentTitle = (TextView)itemView.findViewById(R.id.card_search_rent_title);
            rentRooms = (TextView)itemView.findViewById(R.id.card_search_rent_rooms);
            rentPrice = (TextView)itemView.findViewById(R.id.card_search_rent_price);
            rentLocation = (TextView)itemView.findViewById(R.id.card_search_rent_location);
            rentImage = (ImageView)itemView.findViewById(R.id.card_search_rent_image);
        }

        public void updateUI(CardRentAnnounce currentAnnounce){
            getImageURL(currentAnnounce.getAnnounceID());
            rentTitle.setText(currentAnnounce.getTitle());
            rentRooms.setText(context.getResources().getString(R.string.open_rent_rooms, currentAnnounce.getRooms()));
            rentPrice.setText(context.getResources().getString(R.string.open_rent_price, currentAnnounce.getPrice()));
            rentLocation.setText(context.getResources().getString(R.string.open_rent_location, currentAnnounce.getLocation()));
        }

        public void getImageURL(String announceID){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child(REF_RENT_IMAGES).child(announceID);

            Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(rentImage);
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }
    }
}
