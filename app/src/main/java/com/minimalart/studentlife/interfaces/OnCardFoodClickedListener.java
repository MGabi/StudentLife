package com.minimalart.studentlife.interfaces;

import android.widget.ImageView;

import com.minimalart.studentlife.adapters.HomeUserFoodAdapter;
import com.minimalart.studentlife.models.CardFoodZone;

/**
 * Created by ytgab on 07.02.2017.
 */

/**
 * Interface for click event on food announces
 */
public interface OnCardFoodClickedListener {

    void onCardClicked(CardFoodZone card, ImageView image, int poz);

}
