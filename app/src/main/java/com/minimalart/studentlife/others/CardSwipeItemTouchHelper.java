package com.minimalart.studentlife.others;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.minimalart.studentlife.interfaces.OnCardDismissedListener;
import com.minimalart.studentlife.interfaces.SwipeAdapter;

/**
 * Created by ytgab on 05.02.2017.
 */

public class CardSwipeItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private SwipeAdapter swipeAdapter;

    private OnCardDismissedListener listener;

    public CardSwipeItemTouchHelper(SwipeAdapter adapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.swipeAdapter = adapter;
    }

    public void setOnCardDismissedListener(OnCardDismissedListener listener){
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener != null){
            listener.onCardRemoved(viewHolder);
        }
    }
}
