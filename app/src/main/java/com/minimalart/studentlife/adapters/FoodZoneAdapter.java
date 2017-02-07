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
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.minimalart.studentlife.R;
import com.minimalart.studentlife.activities.MainActivity;
import com.minimalart.studentlife.interfaces.OnCardFoodClickedListener;
import com.minimalart.studentlife.interfaces.SwipeAdapter;
import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;

import java.util.ArrayList;

/**
 * Created by ytgab on 11.01.2017.
 */

public class FoodZoneAdapter extends RecyclerView.Adapter<FoodZoneAdapter.FoodZoneViewHolder> implements SwipeAdapter{

    private ArrayList<CardFoodZone> foodList;
    private Context context;

    private OnCardFoodClickedListener listener;
    public View.OnLongClickListener longListener;

    public void setOnLongClickListener(View.OnLongClickListener longListener){
        this.longListener = longListener;
    }

    public void setOnCardFoodClickedListener(OnCardFoodClickedListener listener) {
        this.listener = listener;
    }

    public FoodZoneAdapter(ArrayList<CardFoodZone> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @Override
    public FoodZoneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View foodCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_frag_food_zone, parent, false);

        return new FoodZoneViewHolder(foodCard);
    }

    @Override
    public void onBindViewHolder(final FoodZoneViewHolder holder, int position) {
        final CardFoodZone cardFoodZone = foodList.get(position);
        holder.updateUI(cardFoodZone);

        ViewCompat.setTransitionName(holder.foodImage, String.valueOf(position) + "_food");
        MaterialRippleLayout mlr = (MaterialRippleLayout)holder.itemView.findViewById(R.id.food_ripple);
        mlr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((MainActivity)context).openFoodAnnounce(cardFoodZone);
                if(listener != null){
                    listener.onCardClicked(cardFoodZone, holder.foodImage, holder.getAdapterPosition());
                }
            }
        });

        mlr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longListener != null){
                    longListener.onLongClick(v);
                    return true;
                }else
                    return false;
            }
        });
    }

    public void loadNewData(ArrayList<CardFoodZone> list){
        foodList = list;
    }

    public ArrayList<CardFoodZone> getList(){
        return foodList;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void remove(int position){
        foodList.remove(position);
        notifyItemRemoved(position);
    }

    public class FoodZoneViewHolder extends RecyclerView.ViewHolder {

        private TextView foodTitle;
        private TextView foodPrice;
        private TextView foodDesc;
        private ImageView foodImage;
        private Uri uri;

        private static final String REF_FOOD_IMAGES = "food-images";

        public FoodZoneViewHolder(View itemView) {
            super(itemView);
            foodTitle = (TextView)itemView.findViewById(R.id.card_food_title);
            foodPrice = (TextView)itemView.findViewById(R.id.card_food_price);
            foodDesc = (TextView)itemView.findViewById(R.id.card_food_description);
            foodImage = (ImageView)itemView.findViewById(R.id.card_food_img);
        }

        public void updateUI(CardFoodZone cardFoodZone){
            getImageURL(cardFoodZone.getFoodID());
            foodTitle.setText(cardFoodZone.getFoodTitle());
            foodPrice.setText(context.getResources().getString(R.string.open_food_price, cardFoodZone.getFoodPrice()));
            foodDesc.setText(context.getResources().getString(R.string.open_rent_description, cardFoodZone.getFoodDesc()));
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
