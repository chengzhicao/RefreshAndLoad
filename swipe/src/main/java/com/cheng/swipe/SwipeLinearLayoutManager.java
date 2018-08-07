package com.cheng.swipe;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

public class SwipeLinearLayoutManager extends LinearLayoutManager {
    private SwipeRefreshLoadLayout mySwipe;

    public SwipeLinearLayoutManager(Context context, SwipeRefreshLoadLayout mySwipe) {
        super(context);
        this.mySwipe = mySwipe;
    }

    public SwipeLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SwipeLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        if (mySwipe != null && child.getId() == mySwipe.headViewId) {
            return 1 + getBottomDecorationHeight(child);
        }
        return super.getDecoratedBottom(child);
    }
}
