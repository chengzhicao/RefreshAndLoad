package com.cheng.swipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

class LvHeadAndFootView implements Swipe.OnChangeViewHeight, Swipe.OnChangeViewTip {
    private ViewGroup footView;
    private TextView tvFootTip;
    private ViewGroup headView;
    private TextView tvHeadTip;
    private Context mContext;
    private int refreshViewHeight;
    private ImageView ivHeadArrow;
    private ProgressBar headProgressBar;
    private ProgressBar footProgressBar;

    LvHeadAndFootView(Context context, SwipeRefreshLoadLayout mySwipe) {
        this.mContext = context;
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
        headProgressBar = headView.findViewById(R.id.head_progressbar);
        ivHeadArrow.animate().setInterpolator(new LinearInterpolator());
        return headView;
    }

    public View getFootView() {
        footView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.srll_foot, null, false);
        View childAt = footView.getChildAt(0);
        ViewGroup.LayoutParams childLayoutParams = childAt.getLayoutParams();
        childLayoutParams.height = refreshViewHeight;
        //设置底部加载子视图高度
        childAt.setLayoutParams(childLayoutParams);
        footViewLayoutParams = new AbsListView.LayoutParams(-1, refreshViewHeight);
        //设置底部加载父视图高度
        footView.setLayoutParams(footViewLayoutParams);
        tvFootTip = footView.findViewById(R.id.tv_foot_tip);
        footProgressBar = footView.findViewById(R.id.foot_progressbar);
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
            if (footProgressBar != null) {
                switch (tips) {
                    case SwipeRefreshLoadLayout.LOADING:
                        footProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_refresh));
                        footProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_refresh));
                        break;
                    default:
                        footProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.refresh));
                        footProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.refresh));
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
            if (ivHeadArrow != null && headProgressBar != null) {
                switch (tips) {
                    case SwipeRefreshLoadLayout.PULL_DOWN:
                        ivHeadArrow.setVisibility(View.VISIBLE);
                        headProgressBar.setVisibility(View.GONE);
                        ivHeadArrow.animate().rotation(0);
                        break;
                    case SwipeRefreshLoadLayout.RELEASE_REFRESH:
                        ivHeadArrow.setVisibility(View.VISIBLE);
                        headProgressBar.setVisibility(View.GONE);
                        ivHeadArrow.animate().rotation(180);
                        break;
                    case SwipeRefreshLoadLayout.REFRESHING:
                        ivHeadArrow.setVisibility(View.GONE);
                        headProgressBar.setVisibility(View.VISIBLE);
                        break;
                    default:
                        ivHeadArrow.animate().cancel();
                        headProgressBar.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }
}
