package com.minimalart.studentlife.others;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Decorator class for recyclerview
 * Adds spaces between cards
 */
public class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceGridItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildLayoutPosition(view);

        outRect.right = space;
        outRect.bottom = space/2;

        // Add top margin only for the first item to avoid double space between items
        if (position == 0 || position == 1){
            outRect.top = space;
        } else{
            outRect.top = 0;
        }

        if(position % 2 != 0){
            outRect.left = 0;
        } else{
            outRect.left = space;
        }
    }
}
