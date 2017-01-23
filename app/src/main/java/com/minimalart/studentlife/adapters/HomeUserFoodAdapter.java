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
import com.minimalart.studentlife.models.CardFoodZone;

import java.util.ArrayList;

/**
 * Created by ytgab on 23.01.2017.
 */

public class HomeUserFoodAdapter extends RecyclerView.Adapter<HomeUserFoodAdapter.HomeUserFoodViewHolder> {

    private ArrayList<CardFoodZone> foodList;
    private Context context;

    public HomeUserFoodAdapter(ArrayList<CardFoodZone> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @Override
    public HomeUserFoodAdapter.HomeUserFoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View foodCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home_user_food, parent, false);

        return new HomeUserFoodViewHolder(foodCard);
    }

    @Override
    public void onBindViewHolder(HomeUserFoodAdapter.HomeUserFoodViewHolder holder, int position) {
        final CardFoodZone cardFoodZone = foodList.get(position);
        holder.updateUI(cardFoodZone);

        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.food_home_ripple);
        mlr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("HOLDERCLICK", "Clicked;");
            }
        });
    }

    public void loadNewData(ArrayList<CardFoodZone> list){
        foodList = list;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class HomeUserFoodViewHolder extends RecyclerView.ViewHolder {

        private TextView foodTitle;
        private TextView foodPrice;
        private TextView foodDesc;
        private ImageView foodImage;
        private Uri uri;

        private static final String REF_FOOD_IMAGES = "food-images";

        public HomeUserFoodViewHolder(View itemView) {
            super(itemView);
            foodTitle = (TextView)itemView.findViewById(R.id.card_home_user_food_title);
            foodImage = (ImageView)itemView.findViewById(R.id.card_home_user_food_image);
        }

        public void updateUI(CardFoodZone cardFoodZone){
            getImageURL(cardFoodZone.getFoodID());
            foodTitle.setText(cardFoodZone.getFoodTitle());
        }

        public void getImageURL(String announceID){
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference().child(REF_FOOD_IMAGES).child(announceID);

            Glide.with(context).using(new FirebaseImageLoader()).load(storageReference).into(foodImage);
        }

        public Uri getUri() {
            return uri;
        }

        public void setUri(Uri uri) {
            this.uri = uri;
        }

    }
}
