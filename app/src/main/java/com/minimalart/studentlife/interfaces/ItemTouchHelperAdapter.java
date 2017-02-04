package com.minimalart.studentlife.interfaces;
/**
 * Interface to listen for a move or dismissal event from a ItemTouchHelper.Callback
 */
public interface ItemTouchHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     */
    boolean onItemMove(int fromPosition, int toPosition);


    /**
     * Called when an item has been dismissed by a swipe
     * @param position : the position of the item dismissed.
     */
    void onItemDismiss(int position);
}
