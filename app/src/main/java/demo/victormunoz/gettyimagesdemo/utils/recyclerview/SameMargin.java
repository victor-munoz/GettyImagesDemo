package demo.victormunoz.gettyimagesdemo.utils.recyclerview;

import android.graphics.Rect;
import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Set bottom and right margin to every item and top margin to the first row only.
 * The left margin for the first column will be added by the recyclerview left margin
 */
public class SameMargin extends RecyclerView.ItemDecoration {
    private final int columns;
    private final int marginPixels;

    public SameMargin(@IntRange(from = 0) int marginPixels, @IntRange(from = 0) int columns){
        this.marginPixels = marginPixels;
        this.columns = columns;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state){
        int itemPosition = recyclerView.getChildLayoutPosition(view);
        outRect.right = marginPixels - view.getPaddingEnd();
        outRect.bottom = marginPixels - view.getPaddingBottom();
        if (isInFirstRow(itemPosition)) {
            outRect.top = marginPixels;
        }
    }

    private boolean isInFirstRow(@IntRange(from = 0) int position){
        return position < columns;
    }
}
