package com.minimalart.studentlife.interfaces;

import com.minimalart.studentlife.models.CardFoodZone;
import com.minimalart.studentlife.models.CardRentAnnounce;

/**
 * Created by ytgab on 19.01.2017.
 */

/**
 * Interface which helps accessing methods from MainActivity inside every fragment
 */
public interface HelperInterface {
    void openGPLUS();
    void openEMAILSender();
    void openGPLAY();
    void openRentAnnounceFragment(CardRentAnnounce cardRentAnnounce);
    void openFoodAnnounce(CardFoodZone cardFoodZone);
}
