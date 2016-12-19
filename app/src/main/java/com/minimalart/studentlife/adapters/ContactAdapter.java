package com.minimalart.studentlife.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.holders.ContactViewHolder;
import com.minimalart.studentlife.models.CardContact;

import java.util.ArrayList;

/**
 * Created by ytgab on 13.12.2016.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private ArrayList<CardContact> cardContactArrayList;

    public ContactAdapter(ArrayList<CardContact> cardContactArrayList) {
        this.cardContactArrayList = cardContactArrayList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contactCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_frag_contact, parent, false);

        return new ContactViewHolder(contactCard);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final CardContact cardContact = cardContactArrayList.get(position);
        holder.updateUI(cardContact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return cardContactArrayList.size();
    }
}
