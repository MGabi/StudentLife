package com.minimalart.studentlife.holders;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.models.CardContact;

/**
 * Created by ytgab on 13.12.2016.
 */

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
