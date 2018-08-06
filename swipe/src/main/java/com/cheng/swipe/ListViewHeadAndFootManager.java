package com.cheng.swipe;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

class ListViewHeadAndFootManager implements Swipe.OnChangeViewHeight, Swipe.OnChangeViewTip {
    private ViewGroup footView;
    private TextView tvFootTip;
    private ViewGroup headView;
    private TextView tvHeadTip;
    private Context mContext;
    private int refreshViewHeight;
    private ImageView ivHeadArrow;
    private ImageView ivHeadRefresh;
    private ImageView ivFootRefresh;
    private TextView tvRefreshTime;
    private SwipeRefreshLoadLayout mySwipe;

    ListViewHeadAndFootManager(Context context, SwipeRefreshLoadLayout mySwipe) {
        this.mContext = context;
        this.mySwipe = mySwipe;
        if (mySwipe != null) {
            refreshViewHeight = mySwipe.REFRESH_VIEW_HEIGHT_DP;
            mySwipe.setOnChangeViewTip(this);
            mySwipe.setOnChangeViewHeight(this);
        }
    }

    public View getHeadView() {
        headView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.srll_head, null, false);
        headChildView = headView.getChildAt(0);
        headChildViewLayoutParams = headChildView.getLayoutParams();
        headChildViewLayoutParams.height = 0;
        headChildView.setLayoutParams(headChildViewLayoutParams);
        headViewLayoutParams = new AbsListView.LayoutParams(-1, 0);
        headView.setLayoutParams(headViewLayoutParams);
        tvHeadTip = headView.findViewById(R.id.tv_head_tip);
        ivHeadArrow = headView.findViewById(R.id.iv_head_arrow);
        ivHeadArrow.animate().setInterpolator(new LinearInterpolator());
        ivHeadRefresh = headView.findViewById(R.id.iv_head_refresh);
        ivHeadRefresh.animate().setInterpolator(new LinearInterpolator());
        tvRefreshTime = headView.findViewById(R.id.tv_refresh_time);
        if (!TextUtils.isEmpty(Swipe.getLastRefreshTime(mContext))) {
            this.tvRefreshTime.setText("最后更新：" + Swipe.getLastRefreshTime(mContext));
        } else {
            this.tvRefreshTime.setVisibility(View.GONE);
        }
        return headView;
    }

    public View getFootView() {
        footView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.srll_foot, null, false);
        footView.setVisibility(mySwipe.footViewVisibility);
        View childAt = footView.getChildAt(0);
        childAt.setVisibility(mySwipe.footViewVisibility);
        ViewGroup.LayoutParams childLayoutParams = childAt.getLayoutParams();
        childLayoutParams.height = refreshViewHeight;
        //设置底部加载子视图高度
        childAt.setLayoutParams(childLayoutParams);
        footViewLayoutParams = new AbsListView.LayoutParams(-1, 0);
        //设置底部加载父视图高度
        footView.setLayoutParams(footViewLayoutParams);
        tvFootTip = footView.findViewById(R.id.tv_foot_tip);
        ivFootRefresh = footView.findViewById(R.id.iv_foot_refresh);
        ivFootRefresh.animate().setInterpolator(new LinearInterpolator());
        return footView;
    }

    public void setHeadView(ViewGroup headView) {
        /*
         * 如果想让listview的item高度为0，只有当该viewgroup本身高度和其所有子view高度为0才能实现，
         * 所以，为了视觉效果，强烈建议自定义头view时让布局包裹一个viewgroup，再在该viewgroup中进行布局
         */
        this.headView = headView;
        if (headView.getChildCount() == 1) {
            headChildView = headView.getChildAt(0);
            headChildViewLayoutParams = headChildView.getLayoutParams();
            headChildViewLayoutParams.height = 0;
            headChildView.setLayoutParams(headChildViewLayoutParams);
            headViewLayoutParams = new AbsListView.LayoutParams(-1, 0);
        } else {
            headViewLayoutParams = new AbsListView.LayoutParams(-1, 1);
        }
        headView.setLayoutParams(headViewLayoutParams);
    }

    public void setFootView(ViewGroup footView) {
        this.footView = footView;
        footViewLayoutParams = new AbsListView.LayoutParams(-1, refreshViewHeight);
        footView.setLayoutParams(footViewLayoutParams);
    }

    private ViewGroup.LayoutParams headViewLayoutParams;
    private ViewGroup.LayoutParams headChildViewLayoutParams;
    private View headChildView;
    private int tempHeight;
    private AbsListView.LayoutParams footViewLayoutParams;

    @Override
    public void changeHeadViewHeight(int headViewHeight) {
        if (headView != null && headViewHeight >= 0) {
            if (headChildView == null && headViewHeight == 0) {
                headViewHeight = 1;
            }
            headViewLayoutParams.height = headViewHeight;
            headView.setLayoutParams(headViewLayoutParams);
        }
        if (headChildView != null && headViewHeight >= 0) {
            headChildViewLayoutParams.height = headViewHeight == 0 ? 0 : refreshViewHeight;
            if (tempHeight != headChildViewLayoutParams.height) {
                headChildView.setLayoutParams(headChildViewLayoutParams);
                tempHeight = headChildViewLayoutParams.height;
            }
        }
    }

    @Override
    public void changeFootViewHeight(int footViewHeight) {
        if (footView != null && footViewHeight >= 0) {
            footViewLayoutParams.height = footViewHeight + refreshViewHeight;
            footView.setLayoutParams(footViewLayoutParams);
        }
    }

    private String currentHeadTips = "";
    private String currentFootTips = "";

    @Override
    public void changeFootTips(String tips) {
        if (!currentFootTips.equals(tips)) {
            currentFootTips = tips;
            if (tvFootTip != null) {
                tvFootTip.setText(tips);
            }
            if (ivFootRefresh != null) {
                switch (tips) {
                    case SwipeRefreshLoadLayout.LOADING:
                        ivFootRefresh.animate().rotation(360 * 60 * 10).setDuration(10 * 60 * 1000);
                        break;
                    default:
                        ivFootRefresh.animate().cancel();
                        break;
                }
            }
        }
    }

    @Override
    public void changeHeadTips(String tips) {
        if (!currentHeadTips.equals(tips)) {
            currentHeadTips = tips;
            if (tvHeadTip != null) {
                tvHeadTip.setText(tips);
            }
            if (ivHeadArrow != null && ivHeadRefresh != null) {
                switch (tips) {
                    case SwipeRefreshLoadLayout.PULL_DOWN:
                        ivHeadArrow.setVisibility(View.VISIBLE);
                        ivHeadRefresh.setVisibility(View.GONE);
                        ivHeadArrow.animate().rotation(0);
                        break;
                    case SwipeRefreshLoadLayout.RELEASE_REFRESH:
                        ivHeadArrow.setVisibility(View.VISIBLE);
                        ivHeadRefresh.setVisibility(View.GONE);
                        ivHeadArrow.animate().rotation(180);
                        break;
                    case SwipeRefreshLoadLayout.REFRESHING:
                        ivHeadArrow.setVisibility(View.GONE);
                        ivHeadRefresh.setVisibility(View.VISIBLE);
                        ivHeadArrow.animate().rotation(0);
                        ivHeadRefresh.animate().rotation(360 * 60 * 10).setDuration(10 * 60 * 1000);
                        break;
                    case SwipeRefreshLoadLayout.REFRESH_FINISH:
                        String time = Swipe.saveLastRefreshTime(mContext);
                        if (tvRefreshTime.getVisibility() != View.VISIBLE) {
                            tvRefreshTime.setVisibility(View.VISIBLE);
                        }
                        tvRefreshTime.setText("最后更新：" + time);
                        ivHeadRefresh.animate().cancel();
                        break;
                    default:
                        ivHeadArrow.animate().cancel();
                        ivHeadRefresh.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
}
