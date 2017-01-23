package com.minimalart.studentlife.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.models.CardContact;

import java.util.ArrayList;

/**
 * Created by ytgab on 13.12.2016.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>{

    private ArrayList<CardContact> cardContactArrayList;
    Context finalContext;
    public ContactAdapter(ArrayList<CardContact> cardContactArrayList, Context c) {
        this.cardContactArrayList = cardContactArrayList;
        this.finalContext = c;
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
        final int pos = position;
        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.contact_ripple);
        mlr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalContext instanceof MainActivity)
                    switch(pos){
                        case 0:
                            break;
                        case 1:
                            ((MainActivity)finalContext).openEMAILSender();
                            break;
                        case 2:
                            ((MainActivity)finalContext).openGPLUS();
                            break;
                        case 3:
                            ((MainActivity)finalContext).openGPLAY();
                            break;
                    }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardContactArrayList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView description;
        private ImageView image;

        public ContactViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView)itemView.findViewById(R.id.card_frag_contact_title);
            this.description = (TextView)itemView.findViewById(R.id.card_frag_contact_desc);
            this.image = (ImageView)itemView.findViewById(R.id.card_frag_contact_image);
        }

        public void updateUI(CardContact cardContact){
            String uriTITLE = cardContact.getTitle();
            int t = title.getResources().getIdentifier(uriTITLE, null, title.getContext().getPackageName());
            title.setText(title.getContext().getString(t));

            String uriDESC = cardContact.getDescription();
            int d = description.getResources().getIdentifier(uriDESC, null, description.getContext().getPackageName());
            description.setText(description.getContext().getString(d));

            String uriIMG = cardContact.getImgUri();
            int img = image.getResources().getIdentifier(uriIMG, null, image.getContext().getPackageName());
            image.setImageResource(img);
        }

    }

}
