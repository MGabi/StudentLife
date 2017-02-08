package com.minimalart.studentlife.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.interfaces.OnCardAnnounceClickedListener;
import com.minimalart.studentlife.interfaces.OnFavRemoved;
import com.minimalart.studentlife.interfaces.OnImageReadyListener;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 23.01.2017.
 */

public class HomeUserAnnouncesAdapter extends RecyclerView.Adapter<HomeUserAnnouncesAdapter.HomeUserAnnouncesViewHolder>{

    public OnCardAnnounceClickedListener listener;
    public OnImageReadyListener imageListener;
    public OnFavRemoved longListener;

    private ArrayList<CardRentAnnounce> cardRentAnnounceArrayList;
    private Context context;

    public void setOnCardAnnounceClickedListener(OnCardAnnounceClickedListener listener){
        this.listener = listener;
    }

    public void setOnImageReadyListener(OnImageReadyListener listener){
        imageListener = listener;
    }

    public void setOnFavRemovedListener(OnFavRemoved longListener){
        this.longListener = longListener;
    }

    public HomeUserAnnouncesAdapter(ArrayList<CardRentAnnounce> cardRentAnnounceArrayList, Context context) {
        this.cardRentAnnounceArrayList = cardRentAnnounceArrayList;
        this.context = context;
    }

    public void remove(int poz){
        cardRentAnnounceArrayList.remove(poz);
        notifyItemRemoved(poz);
    }

    @Override
    public HomeUserAnnouncesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View announceCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home_user_announces, parent, false);

        return new HomeUserAnnouncesAdapter.HomeUserAnnouncesViewHolder(announceCard);
    }

    @Override
    public void onBindViewHolder(final HomeUserAnnouncesViewHolder holder, int position) {
        final CardRentAnnounce cardRentAnnounce = cardRentAnnounceArrayList.get(position);
        holder.updateUI(cardRentAnnounce);

        ViewCompat.setTransitionName(holder.rentImage, String.valueOf(position) + "_rent");
        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.search_home_ripple);
        mlr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onCardClicked(cardRentAnnounce, holder.rentImage, holder.getAdapterPosition());
                }
            }
        });

        mlr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longListener != null) {
                    longListener.onFavRemoved(cardRentAnnounce.getAnnounceID(), holder.getAdapterPosition());
                    return true;
                }else
                    return false;
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
            rentImage = (ImageView)itemView.findViewById(R.id.detailed_image);
        }

        public void updateUI(CardRentAnnounce cardRentAnnounce){
            getImageURL(cardRentAnnounce.getAnnounceID());
            rentTitle.setText(cardRentAnnounce.getTitle());
        }

        public void getImageURL(String announceID){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child(REF_RENT_IMAGES).child(announceID);

            Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(rentImage);
            /*storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(context).load(uri).placeholder(R.drawable.apartment_inside).into(rentImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.v("TRANSITIONTEST", "Success");
                            if(imageListener != null)
                                imageListener.onImageReady();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            });*/
        }

        public ImageView getRentImage(){
            return rentImage;
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }
    }
}
