package com.minimalart.studentlife.interfaces;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ytgab on 05.02.2017.
 */

/**
 * Interface for dismiss behavior callback
 */
public interface OnCardDismissedListener {

    void onCardRemoved(RecyclerView.ViewHolder holder);

}
