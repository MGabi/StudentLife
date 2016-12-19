package com.minimalart.studentlife.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.models.CardRentAnnounce;

/**
 * Created by ytgab on 17.12.2016.
 */

public class RentAnnounceViewHolder extends RecyclerView.ViewHolder {

    private TextView rentTitle;
    private TextView rentRooms;
    private TextView rentPrice;
    private TextView rentLocation;
    private ImageView rentImage;

    public RentAnnounceViewHolder(View itemView) {
        super(itemView);

        rentTitle = (TextView)itemView.findViewById(R.id.card_search_rent_title);
        rentRooms = (TextView)itemView.findViewById(R.id.card_search_rent_rooms);
        rentPrice = (TextView)itemView.findViewById(R.id.card_search_rent_price);
        rentLocation = (TextView)itemView.findViewById(R.id.card_search_rent_location);
        rentImage = (ImageView)itemView.findViewById(R.id.card_search_rent_image);
    }

    public void updateUI(CardRentAnnounce cardRentAnnounce){
        rentTitle.setText(cardRentAnnounce.getTitle());
        rentRooms.setText(cardRentAnnounce.getRooms());
        rentPrice.setText(cardRentAnnounce.getPrice());
        rentLocation.setText(cardRentAnnounce.getLocation());
    }
}
