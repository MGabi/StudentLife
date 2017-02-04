package com.minimalart.studentlife.others;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Decorator class for recyclerview
 * Adds spaces between cards
 */
public class SpaceHorizontalItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceHorizontalItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildLayoutPosition(view);

        outRect.top = space;
        outRect.bottom = space + 5;
        outRect.right = space + 5;

        if (position == 0){
            outRect.left = space + 5;
        } else{
            outRect.left = 0;
        }
    }
}
