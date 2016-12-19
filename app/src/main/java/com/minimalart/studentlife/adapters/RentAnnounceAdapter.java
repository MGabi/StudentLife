package com.minimalart.studentlife.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.holders.RentAnnounceViewHolder;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 17.12.2016.
 */

public class RentAnnounceAdapter extends RecyclerView.Adapter<RentAnnounceViewHolder> {

    private ArrayList<CardRentAnnounce> cardRentAnnounceArrayList;

    public RentAnnounceAdapter(ArrayList<CardRentAnnounce> cardRentAnnounceArrayList) {
        this.cardRentAnnounceArrayList = cardRentAnnounceArrayList;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public void loadNewData(ArrayList<CardRentAnnounce> list){
        cardRentAnnounceArrayList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cardRentAnnounceArrayList.size();
    }
}
