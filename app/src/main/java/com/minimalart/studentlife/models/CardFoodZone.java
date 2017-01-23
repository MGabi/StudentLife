package com.minimalart.studentlife.models;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ytgab on 11.01.2017.
 */

public class CardFoodZone {

    private String userUID;
    private String foodTitle;
    private String foodPrice;
    private String foodDesc;
    private String foodLoc;
    private boolean checked;

    private String foodID;

    public CardFoodZone() {

    }

    public CardFoodZone(String userUID, String foodTitle, String foodPrice, String foodDesc, String foodLoc, boolean checked) {
        this.userUID = userUID;
        this.foodTitle = foodTitle;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodLoc = foodLoc;
        this.checked = checked;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getFoodTitle() {
        return foodTitle;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public String getFoodLoc() {
        return foodLoc;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }
}
