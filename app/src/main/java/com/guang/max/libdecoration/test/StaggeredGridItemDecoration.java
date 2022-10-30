package com.guang.max.libdecoration.test;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class StaggeredGridItemDecoration extends RecyclerView.ItemDecoration {

    private int dividerWidth;
    private int maxSpan;
    private int topEdge;

    /**
     * 瀑布流分割线
     *
     * @param dividerWidth 分割线大小
     * @param maxSpan      每行/每列最大元素个数
     * @param topEdge      top edge
     */
    public StaggeredGridItemDecoration(int dividerWidth, int maxSpan, int topEdge) {
        this.dividerWidth = dividerWidth;
        this.maxSpan = maxSpan;
        this.topEdge = topEdge;
    }

    public StaggeredGridItemDecoration(int dividerWidth, int maxSpan) {
        this.dividerWidth = dividerWidth;
        this.maxSpan = maxSpan;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int position = parent.getChildAdapterPosition(view);
            int spanIndex = layoutParams.getSpanIndex();
            int halfDividerWidth = dividerWidth / 2;
            outRect.left = halfDividerWidth;
            outRect.right = halfDividerWidth;
            outRect.top = position < maxSpan ? topEdge : dividerWidth;
            if (spanIndex == 0) {
                // left
                outRect.left += halfDividerWidth;
            } else if (spanIndex == maxSpan - 1) {
                outRect.right += halfDividerWidth;
            }
        }
    }
}