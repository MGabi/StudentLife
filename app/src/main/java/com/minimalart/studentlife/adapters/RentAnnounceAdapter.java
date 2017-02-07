package com.minimalart.studentlife.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
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
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.interfaces.SwipeAdapter;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ytgab on 17.12.2016.
 */

public class RentAnnounceAdapter extends RecyclerView.Adapter<RentAnnounceAdapter.RentAnnounceViewHolder> implements SwipeAdapter{

    private ArrayList<CardRentAnnounce> cardRentAnnounceArrayList;
    private Context context;

    private OnCardAnnounceClickedListener listener;
    public View.OnLongClickListener longListener;

    public void setOnLongClickListener(View.OnLongClickListener longListener){
        this.longListener = longListener;
    }

    public void setOnCardAnnounceClickedListener ( OnCardAnnounceClickedListener listener ){
        this.listener = listener;
    }

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
    public void onBindViewHolder(final RentAnnounceViewHolder holder, final int position) {

        final CardRentAnnounce cardRentAnnounce = cardRentAnnounceArrayList.get(position);
        holder.updateUI(cardRentAnnounce);

        ViewCompat.setTransitionName(holder.rentImage, String.valueOf(position) + "_rent");
        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.search_ripple);
        mlr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((MainActivity)context).openRentAnnounceFragment(cardRentAnnounce, image);
                if(listener != null){
                    listener.onCardClicked(cardRentAnnounce, holder.rentImage, holder.getAdapterPosition());
                }
            }
        });

        mlr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longListener != null) {
                    longListener.onLongClick(v);
                    return true;
                }else
                    return false;
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

    public void remove(int position){
        cardRentAnnounceArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public class RentAnnounceViewHolder extends RecyclerView.ViewHolder{

        private TextView rentTitle;
        private TextView rentRooms;
        private TextView rentPrice;
        private TextView rentLocation;
        private ImageView rentImage;

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
    }
}
