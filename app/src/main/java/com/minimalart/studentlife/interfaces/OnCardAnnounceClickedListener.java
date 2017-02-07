package com.minimalart.studentlife.interfaces;

import android.widget.ImageView;

import com.minimalart.studentlife.adapters.HomeUserAnnouncesAdapter;
import com.minimalart.studentlife.models.CardRentAnnounce;

/**
 * Created by ytgab on 07.02.2017.
 */

public interface OnCardAnnounceClickedListener {

    void onCardClicked(CardRentAnnounce card, ImageView imageView, int poz);

}
