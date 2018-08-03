package com.cheng.swipe;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

public class MyRecyclerViewLayoutManager extends LinearLayoutManager {

    public MyRecyclerViewLayoutManager(Context context) {
        super(context);
    }

    public MyRecyclerViewLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyRecyclerViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 覆写此方法，防止头View的高为0时无法判断是否滑到最顶部，只要遍历到头View时返回值大于0即可
     *
     * @param child
     * @return
     */
    @Override
    public int getDecoratedBottom(View child) {
        if (child.getId() == R.id.ll_head) {
            return 1 + getBottomDecorationHeight(child);
        }
        return super.getDecoratedBottom(child);
    }
}
