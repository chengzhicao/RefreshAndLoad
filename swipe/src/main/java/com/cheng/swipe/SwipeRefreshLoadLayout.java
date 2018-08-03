package com.cheng.swipe;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SwipeRefreshLoadLayout extends SwipeRefreshLayout {
    private int mTotalUnconsumed, mTotalUnconsumed2;
    private Context mContext;
    /**
     * 刷新后恢复原始位置所用时间
     */
    private final int ANIMATOR_TIME = 300;

    /**
     * 处于刷新状态时头view的高度
     */
    private int headViewRefreshingHeight;

    /**
     * 类型为下拉刷新
     */
    private final int PULL_REFRESH = 0x21;

    /**
     * 类型为上拉加载
     */
    private final int LOAD_MORE = 0x22;

    /**
     * 头、尾高度，dp
     */
    public int REFRESH_VIEW_HEIGHT_DP = 50;

    /**
     * 刷新高度的偏移量，dp
     */
    private int refreshHeightOffset = 20;

    /**
     * 头view可允许下拉刷新时划过的距离
     */
    private int headViewAccessRefreshDistance;

    /**
     * 底部view可允许加载更多时划过的距离
     */
    private int footViewAccessLoadDistance;

    /**
     * 是否正在刷新
     */
    private boolean isRefreshing;

    /**
     * 是否正在加载更多
     */
    private boolean isLoading;

    private RecyclerView recyclerView;

    private ListView listView;

    /**
     * 无操作
     */
    private final int DRAG_ACTION_NULL = 0x00;

    /**
     * 下拉
     */
    private final int DRAG_ACTION_PULL_DOWN = 0x01;

    /**
     * 下拉返回
     */
    private final int DRAG_ACTION_PULL_DOWN_BACK = 0x02;

    /**
     * 上拉
     */
    private final int DRAG_ACTION_PULL_UP = 0x03;

    /**
     * 上拉返回
     */
    private final int DRAG_ACTION_PULL_UP_BACK = 0x04;

    /**
     * 拖拽行为动作
     */
    private int dragAction = DRAG_ACTION_NULL;

    /**
     * 圆形样式
     */
    public final static int CIRCLE = 0X00AA;

    /**
     * 展开样式
     */
    public final static int SPREAD = 0X00BB;

    /**
     * 刷新样式
     */
    private int refreshStyle = SPREAD;

    public SwipeRefreshLoadLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeRefreshLoadLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        REFRESH_VIEW_HEIGHT_DP = dp2px(mContext, REFRESH_VIEW_HEIGHT_DP);
        refreshHeightOffset = dp2px(mContext, refreshHeightOffset);
        headViewRefreshingHeight = REFRESH_VIEW_HEIGHT_DP;
        headViewAccessRefreshDistance = headViewRefreshingHeight + refreshHeightOffset;
        footViewAccessLoadDistance = headViewRefreshingHeight;
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                if (onRefreshAndLoadListener != null) {
                    onRefreshAndLoadListener.refresh();
                }
            }
        });
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (recyclerView == null && listView == null) {
            ensureTargetView();
        }
    }

    /**
     * 设置处于刷新状态时的头view高度。同时，头view可允许刷新时滑动的距离为此值+20dp
     */
    public void setHeadViewRefreshingHeight(int headViewRefreshingHeight) {
        if (headViewRefreshingHeight >= 0) {
            this.headViewRefreshingHeight = headViewRefreshingHeight;
            headViewAccessRefreshDistance = headViewRefreshingHeight + refreshHeightOffset;
        }
    }

    /**
     * 设置底部view可允许加载更多时滑过的距离
     */
    public void setFootViewAccessLoadDistance(int footViewAccessLoadDistance) {
        if (footViewAccessLoadDistance > 0) {
            this.footViewAccessLoadDistance = footViewAccessLoadDistance;
        }
    }

    private void ensureTargetView() {
        if (listView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof ListView) {
                    listView = (ListView) child;
                    initTargetView();
                    break;
                }
                if (child instanceof RecyclerView) {
                    recyclerView = (RecyclerView) child;
                    initTargetView();
                    break;
                }
            }
        }
    }

    private void initTargetView() {
        if (listView != null) {
            LvHeadAndFootView lvHeadAndFootView = new LvHeadAndFootView(mContext, this);
            if (headView == null) {
                headView = (ViewGroup) lvHeadAndFootView.getHeadView();
            } else {
                lvHeadAndFootView.setHeadView(headView);
            }
            if (footView == null) {
                footView = (ViewGroup) lvHeadAndFootView.getFootView();
            } else {
                lvHeadAndFootView.setFootView(footView);
            }
            listView.addHeaderView(headView);
            listView.addFooterView(footView);
        }
    }

    private ViewGroup headView;
    private ViewGroup footView;

    ViewGroup getHeadView() {
        return headView;
    }

    ViewGroup getFootView() {
        return footView;
    }

    /**
     * 自定义头View
     *
     * @param headView
     */
    public void setHeadView(ViewGroup headView) {
        this.headView = headView;
    }

    /**
     * 自定义底部View
     *
     * @param footView
     */
    public void setFootView(ViewGroup footView) {
        this.footView = footView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (action == null) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getY();
                int dy = (int) (currentY - initialDownY);
                if (dy >= 0) {
                    if (action == ACTION_PULL_DOWN && !isRefreshing) {
                        onPullDown(dy);
                    } else if (action == ACTION_PULL_UP && !isLoading) {
                        onPullUpBack(dy);
                        if (recyclerView != null) {
                            recyclerView.scrollBy(0, dy);
                        }
                    }
                } else {
                    if (action == ACTION_PULL_DOWN && !isRefreshing) {
                        onPullDownBack(dy);
                    } else if (action == ACTION_PULL_UP && !isLoading) {
                        onPullUp(dy);
                        if (recyclerView != null) {
                            recyclerView.scrollBy(0, -dy);
                        }
                        if (listView != null) {
                            ListViewCompat.scrollListBy(listView, -dy);
                        }
                    }
                }
                initialDownY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                action = null;
                release();
                break;
        }
        return action != null || super.onTouchEvent(ev);
    }

    private float initialDownY;
    private final int ACTION_PULL_DOWN = 0X00C1;
    private final int ACTION_PULL_UP = 0X00D1;
    private Integer action = null;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (recyclerView != null) {
            //当刷新类型为CIRCLE且处于刷新状态时，recyclerView的嵌套滑动不再响应，所以无法进行上拉加载，因此通过拦截事件利用onTouchEvent()实现上拉加载
            if (refreshStyle == CIRCLE && isRefreshing && !recyclerView.canScrollVertically(1)) {
                Boolean x = isIntercept(ev, true);
                if (x != null) return x;
            }
            //当recyclerView禁止嵌套滑动时，上拉加载需要使用onTouchEvent()实现，所以这里要进行拦截
            if (!recyclerView.isNestedScrollingEnabled() && !recyclerView.canScrollVertically(1)) {
                Boolean x = isIntercept(ev, true);
                if (x != null) return x;
            }
            //当刷新类型为SPREAD时，如果recyclerView禁止了嵌套滑动，那这里需要拦截事件让onTouchEvent()实现下拉刷新
            if (refreshStyle == SPREAD && !recyclerView.isNestedScrollingEnabled() && !recyclerView.canScrollVertically(-1)) {
                Boolean x = isIntercept(ev, false);
                if (x != null) return x;
            }
        }
        if (listView != null) {
            if (refreshStyle == CIRCLE && isRefreshing && !ListViewCompat.canScrollList(listView, 1)) {
                Boolean x = isIntercept(ev, true);
                if (x != null) return x;
            }
            if (!ListViewCompat.canScrollList(listView, 1)) {
                Boolean x = isIntercept(ev, true);
                if (x != null) return x;
            }
            if (refreshStyle == SPREAD && !ListViewCompat.canScrollList(listView, -1)) {
                Boolean x = isIntercept(ev, false);
                if (x != null) return x;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Nullable
    private Boolean isIntercept(MotionEvent ev, boolean type) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                if (type) {
                    if (y - initialDownY < 0) {
                        initialDownY = y;
                        action = ACTION_PULL_UP;
                        return true;
                    }
                } else {
                    if (y - initialDownY > 0) {
                        initialDownY = y;
                        action = ACTION_PULL_DOWN;
                        return true;
                    }
                }
                break;
        }
        return null;
    }

    /**
     * 设置刷新样式
     *
     * @param refreshStyle
     */
    public void setRefreshStyle(int refreshStyle) {
        this.refreshStyle = refreshStyle;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        mTotalUnconsumed = 0;
        mTotalUnconsumed2 = 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (refreshStyle == CIRCLE) {
            super.onNestedPreScroll(target, dx, dy, consumed);
        } else if (refreshStyle == SPREAD) {
            if (dy > 0 && mTotalUnconsumed > 0) {
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - mTotalUnconsumed;
                    mTotalUnconsumed = 0;
                    finishParentDrag();
                } else {
                    mTotalUnconsumed -= dy;
                    consumed[1] = dy;
                }
                onPullDownBack(dy);
            }
        }
        //上拉操作
        if (dy < 0 && mTotalUnconsumed2 < 0) {
            if (dy < mTotalUnconsumed2) {
                consumed[1] = mTotalUnconsumed2 - dy;
                mTotalUnconsumed2 = 0;
                finishParentDrag();
            } else {
                mTotalUnconsumed2 -= dy;
                consumed[1] = dy;
            }
            onPullUpBack(dy);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (refreshStyle == CIRCLE) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        } else if (refreshStyle == SPREAD) {
            if (dyUnconsumed < 0 && !isRefreshing) {
                mTotalUnconsumed += -dyUnconsumed;
                onPullDown(dyUnconsumed);
            }
        }
        //上拉操作
        if (dyUnconsumed > 0 && !isLoading) {
            mTotalUnconsumed2 += -dyUnconsumed;
            onPullUp(dyUnconsumed);
        }
    }

    @Override
    public void onStopNestedScroll(View target) {
        if (refreshStyle == CIRCLE) {
            super.onStopNestedScroll(target);
        } else if (refreshStyle == SPREAD) {
            if (mTotalUnconsumed > 0 && !isRefreshing) {
                release();
                mTotalUnconsumed = 0;
            }
        }
        //上拉操作
        if (mTotalUnconsumed2 < 0) {
            release();
            mTotalUnconsumed2 = 0;
        }
    }

    private int pullDownDistance, pullUpDistance;

    /**
     * 停止父view拖拽
     */
    private void finishParentDrag() {
        dragAction = DRAG_ACTION_NULL;
        pullDownDistance = 0;
        pullUpDistance = 0;
    }

    private void onPullDownBack(int dy) {
        dragAction = DRAG_ACTION_PULL_DOWN_BACK;
        pullDownDistance -= Math.abs(dy) / 2;
        changeTipsRefresh();
        if (onChangeViewHeight != null) {
            onChangeViewHeight.changeHeadViewHeight(pullDownDistance);
        }
    }

    private void onPullUp(int dy) {
        dragAction = DRAG_ACTION_PULL_UP;
        pullUpDistance += Math.abs(dy) / 2;
        changeTipsLoadMore();
        if (onChangeViewHeight != null) {
            onChangeViewHeight.changeFootViewHeight(pullUpDistance);
        }
    }

    private void onPullUpBack(int dy) {
        dragAction = DRAG_ACTION_PULL_UP_BACK;
        pullUpDistance -= Math.abs(dy) / 2;
        changeTipsLoadMore();
        if (onChangeViewHeight != null) {
            onChangeViewHeight.changeFootViewHeight(pullUpDistance);
        }
    }

    private void onPullDown(int dy) {
        dragAction = DRAG_ACTION_PULL_DOWN;
        pullDownDistance += Math.abs(dy) / 2;
        changeTipsRefresh();
        if (onChangeViewHeight != null) {
            onChangeViewHeight.changeHeadViewHeight(pullDownDistance);
        }
    }

    static final String REFRESHING = "正在刷新";
    static final String LOADING = "正在加载";
    static final String RELEASE_REFRESH = "释放刷新";
    static final String PULL_DOWN = "下拉刷新";
    static final String RELEASE_LOAD = "释放加载";
    static final String PULL_UP = "上拉加载";
    static final String LOAD_FINISH = "加载完成";
    static final String REFRESH_FINISH = "加载完成";

    /**
     * 手指释放
     */
    private void release() {
        if (dragAction == DRAG_ACTION_PULL_DOWN || dragAction == DRAG_ACTION_PULL_DOWN_BACK) {
            //如果拖拽的距离大于可刷新距离
            if (pullDownDistance >= headViewAccessRefreshDistance) {
                isRefreshing = true;
                adjustRefreshPosition(PULL_REFRESH);
                if (onRefreshAndLoadListener != null) {
                    onRefreshAndLoadListener.refresh();
                }
                if (onChangeViewTip != null) {
                    onChangeViewTip.changeHeadTips(REFRESHING);
                }
            } else {
                if (!isRefreshing) {
                    cancelRefresh();
                }
            }
        }
        if (dragAction == DRAG_ACTION_PULL_UP || dragAction == DRAG_ACTION_PULL_UP_BACK) {
            if (pullUpDistance >= footViewAccessLoadDistance) {
                isLoading = true;
                adjustRefreshPosition(LOAD_MORE);
                if (onRefreshAndLoadListener != null) {
                    onRefreshAndLoadListener.loadMore();
                }
                if (onChangeViewTip != null) {
                    onChangeViewTip.changeFootTips(LOADING);
                }
            } else {
                if (!isLoading) {
                    cancelLoadMore();
                }
            }
        }
    }

    private void changeTipsRefresh() {
        if (pullDownDistance >= headViewAccessRefreshDistance) {
            if (onChangeViewTip != null) {
                onChangeViewTip.changeHeadTips(RELEASE_REFRESH);
            }
            if (onSlideActionListener != null) {
                onSlideActionListener.releaseRefresh();
            }
        } else {
            if (onChangeViewTip != null) {
                onChangeViewTip.changeHeadTips(PULL_DOWN);
            }
            if (onSlideActionListener != null) {
                onSlideActionListener.downRefresh();
            }
        }
    }

    private void changeTipsLoadMore() {
        if (pullUpDistance >= footViewAccessLoadDistance) {
            if (onChangeViewTip != null) {
                onChangeViewTip.changeFootTips(RELEASE_LOAD);
            }
            if (onSlideActionListener != null) {
                onSlideActionListener.releaseLoad();
            }
        } else {
            if (onChangeViewTip != null) {
                onChangeViewTip.changeFootTips(PULL_UP);
            }
            if (onSlideActionListener != null) {
                onSlideActionListener.upLoad();
            }
        }
    }

    /**
     * 调整刷新位置
     */
    private void adjustRefreshPosition(int refreshStyle) {
        if (refreshStyle == PULL_REFRESH) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "pullDownDistance", pullDownDistance, headViewRefreshingHeight);
            objectAnimator.setDuration(ANIMATOR_TIME);
            objectAnimator.start();
        } else if (refreshStyle == LOAD_MORE) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "pullUpDistance", pullUpDistance, 0);
            objectAnimator.setDuration(ANIMATOR_TIME);
            objectAnimator.start();
        }
    }

    private void cancelRefresh() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "pullDownDistance", pullDownDistance, 0);
        objectAnimator.setDuration(ANIMATOR_TIME);
        objectAnimator.start();
    }

    private void cancelLoadMore() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "pullUpDistance", pullUpDistance, 0);
        objectAnimator.setDuration(ANIMATOR_TIME);
        objectAnimator.start();
    }

    /**
     * 完成加载
     */
    public void finishLoadMore() {
        isLoading = false;
        stopLoadMore();
        if (onChangeViewTip != null) {
            onChangeViewTip.changeFootTips(LOAD_FINISH);
        }
    }

    /**
     * 停止加载更多
     */
    private void stopLoadMore() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "pullUpDistance", pullUpDistance, 0);
        objectAnimator.setDuration(ANIMATOR_TIME);
        objectAnimator.start();
    }

    /**
     * 主动刷新
     */
    public void doRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            if (recyclerView != null) {
                recyclerView.scrollToPosition(0);
            }
            if (listView != null) {
                listView.setSelection(0);
            }
            if (onRefreshAndLoadListener != null) {
                onRefreshAndLoadListener.refresh();
            }
            if (refreshStyle == SPREAD) {
                adjustRefreshPosition(PULL_REFRESH);
                if (onChangeViewTip != null) {
                    onChangeViewTip.changeHeadTips(REFRESHING);
                }
            } else if (refreshStyle == CIRCLE) {
                setRefreshing(true);
            }
        }
    }

    /**
     * 加载更多
     */
    protected void doLoadMore() {
        if (!isLoading) {
            isLoading = true;
            if (onRefreshAndLoadListener != null) {
                onRefreshAndLoadListener.loadMore();
            }
            if (onChangeViewTip != null) {
                onChangeViewTip.changeFootTips(LOADING);
            }
        }
    }

    /**
     * 完成刷新
     */
    public void finishRefresh() {
        isRefreshing = false;
        if (refreshStyle == SPREAD) {
            stopRefresh();
            if (onChangeViewTip != null) {
                onChangeViewTip.changeHeadTips(REFRESH_FINISH);
            }
        } else if (refreshStyle == CIRCLE) {
            setRefreshing(false);
        }
    }

    /**
     * 停止刷新
     */
    private void stopRefresh() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this, "pullDownDistance", pullDownDistance, 0);
        objectAnimator.setDuration(ANIMATOR_TIME);
        objectAnimator.start();
    }

    private int getPullDownDistance() {
        return pullDownDistance;
    }

    private void setPullDownDistance(int pullDownDistance) {
        this.pullDownDistance = pullDownDistance;
        if (onChangeViewHeight != null) {
            onChangeViewHeight.changeHeadViewHeight(pullDownDistance);
        }
    }

    private int getPullUpDistance() {
        return pullUpDistance;
    }

    private void setPullUpDistance(int pullUpDistance) {
        this.pullUpDistance = pullUpDistance;
        if (onChangeViewHeight != null) {
            onChangeViewHeight.changeFootViewHeight(pullUpDistance);
        }
    }

    private Swipe.OnChangeViewHeight onChangeViewHeight;

    protected void setOnChangeViewHeight(Swipe.OnChangeViewHeight onChangeViewHeight) {
        this.onChangeViewHeight = onChangeViewHeight;
    }

    private Swipe.OnRefreshAndLoadListener onRefreshAndLoadListener;

    public void setOnRefreshAndLoadListener(Swipe.OnRefreshAndLoadListener onRefreshAndLoadListener) {
        this.onRefreshAndLoadListener = onRefreshAndLoadListener;
    }

    private Swipe.OnChangeViewTip onChangeViewTip;

    protected void setOnChangeViewTip(Swipe.OnChangeViewTip onChangeViewTip) {
        this.onChangeViewTip = onChangeViewTip;
    }

    private Swipe.OnSlideActionListener onSlideActionListener;

    public void setOnSlideActionListener(Swipe.OnSlideActionListener onSlideActionListener) {
        this.onSlideActionListener = onSlideActionListener;
    }

    /**
     * dp转px
     */
    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

}
