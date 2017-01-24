package com.minimalart.studentlife.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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
 * Created by ytgab on 23.01.2017.
 */

public class HomeUserAnnouncesAdapter extends RecyclerView.Adapter<HomeUserAnnouncesAdapter.HomeUserAnnouncesViewHolder>{

    private ArrayList<CardRentAnnounce> cardRentAnnounceArrayList;
    private Context context;

    public HomeUserAnnouncesAdapter(ArrayList<CardRentAnnounce> cardRentAnnounceArrayList, Context context) {
        this.cardRentAnnounceArrayList = cardRentAnnounceArrayList;
        this.context = context;
    }

    @Override
    public HomeUserAnnouncesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View announceCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home_user_announces, parent, false);

        return new HomeUserAnnouncesAdapter.HomeUserAnnouncesViewHolder(announceCard);
    }

    @Override
    public void onBindViewHolder(HomeUserAnnouncesViewHolder holder, int position) {
        final CardRentAnnounce cardRentAnnounce = cardRentAnnounceArrayList.get(position);
        holder.updateUI(cardRentAnnounce);

        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.search_home_ripple);
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

    @Override
    public int getItemCount() {
        return cardRentAnnounceArrayList.size();
    }

    public class HomeUserAnnouncesViewHolder extends RecyclerView.ViewHolder{

        private TextView rentTitle;
        private TextView rentRooms;
        private TextView rentPrice;
        private TextView rentLocation;
        private ImageView rentImage;
        private Uri uri;
        StorageReference storageReference;

        private static final String REF_RENT_IMAGES = "rent-images";

        public HomeUserAnnouncesViewHolder(View itemView) {
            super(itemView);
            rentTitle = (TextView)itemView.findViewById(R.id.card_home_user_announces_title);
            rentImage = (ImageView)itemView.findViewById(R.id.card_home_user_announces_image);
        }

        public void updateUI(CardRentAnnounce cardRentAnnounce){
            getImageURL(cardRentAnnounce.getAnnounceID());
            rentTitle.setText(cardRentAnnounce.getTitle());
        }

        public void getImageURL(String announceID){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child(REF_RENT_IMAGES).child(announceID);

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
