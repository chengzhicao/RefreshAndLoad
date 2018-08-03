package com.cheng.swipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by cheng on 2018/3/27.
 */

public abstract class RefreshRecycleViewAdapter extends RecyclerView.Adapter implements Swipe.OnChangeViewTip, Swipe.OnChangeViewHeight {
    /**
     * 底部加载类型
     */
    private final int VIEW_TYPE_FOOT = 0xFFFF;
    /**
     * 顶部刷新类型
     */
    private final int VIEW_TYPE_HEAD = 0xAAAA;
    private Context mContext;
    private int itemCounts;
    private int refreshViewHeight;
    private ViewGroup.LayoutParams headViewLayoutParams;
    private ViewGroup.LayoutParams footViewLayoutParams;
    private SwipeRefreshLoadLayout mySwipe;
    private HeadViewHolder headViewHolder;
    private FootViewHolder footViewHolder;

    public abstract int getCounts();

    public abstract RecyclerView.ViewHolder onNewViewHolder(@NonNull ViewGroup parent, int viewType);

    public abstract int getItemType(int position);

    public abstract void onSetViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    public RefreshRecycleViewAdapter(Context context, SwipeRefreshLoadLayout mySwipe) {
        this.mContext = context;
        this.mySwipe = mySwipe;
        if (mySwipe != null) {
            refreshViewHeight = mySwipe.REFRESH_VIEW_HEIGHT_DP;
            mySwipe.setOnChangeViewTip(this);
            mySwipe.setOnChangeViewHeight(this);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOT) {
            if (footViewHolder == null) {
                if (mySwipe.getFootView() == null) {
                    footViewHolder = new FootViewHolder(LayoutInflater.from(mContext).inflate(R.layout.srll_foot, null, false), true);
                } else {
                    footViewHolder = new FootViewHolder(mySwipe.getFootView(), false);
                }
            }
            return footViewHolder;
        } else if (viewType == VIEW_TYPE_HEAD) {
            if (headViewHolder == null) {
                if (mySwipe.getHeadView() == null) {
                    headViewHolder = new HeadViewHolder(LayoutInflater.from(mContext).inflate(R.layout.srll_head, null, true), true);
                } else {
                    headViewHolder = new HeadViewHolder(mySwipe.getHeadView(), false);
                }
            }
            return headViewHolder;
        }
        return onNewViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position != 0 && position != itemCounts - 1) {
            onSetViewHolder(holder, position - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == itemCounts - 1) {
            return VIEW_TYPE_FOOT;
        } else if (position == 0) {
            return VIEW_TYPE_HEAD;
        }
        return getItemType(position - 1);
    }

    @Override
    public int getItemCount() {
        itemCounts = getCounts() + 2;
        return itemCounts;
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFootTip;
        private ViewGroup footView;
        private ProgressBar footProgressBar;


        private FootViewHolder(View itemView, boolean isFromSelf) {
            super(itemView);
            this.footView = (ViewGroup) itemView;
            if (isFromSelf) {
                ViewGroup childAt = (ViewGroup) footView.getChildAt(0);
                ViewGroup.LayoutParams childLayoutParams = childAt.getLayoutParams();
                childLayoutParams.height = refreshViewHeight;
                //设置底部加载子视图高度
                childAt.setLayoutParams(childLayoutParams);
                this.tvFootTip = footView.findViewById(R.id.tv_foot_tip);
                this.footProgressBar = footView.findViewById(R.id.foot_progressbar);
                LinearLayout llLoadMore = footView.findViewById(R.id.ll_load_more);
                llLoadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mySwipe.doLoadMore();
                    }
                });
            }
            //设置底部加载父视图高度
            footViewLayoutParams = new RecyclerView.LayoutParams(-1, refreshViewHeight);
            footView.setLayoutParams(footViewLayoutParams);
        }
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup headView;
        private TextView tvHeadTip;
        private ImageView ivHeadArrow;
        private ProgressBar headProgressBar;

        private HeadViewHolder(View itemView, boolean isFromSelf) {
            super(itemView);
            this.headView = (ViewGroup) itemView;
            if (isFromSelf) {
                ViewGroup childAt = (ViewGroup) headView.getChildAt(0);
                ViewGroup.LayoutParams childLayoutParams = childAt.getLayoutParams();
                childLayoutParams.height = refreshViewHeight;
                childAt.setLayoutParams(childLayoutParams);
                this.tvHeadTip = headView.findViewById(R.id.tv_head_tip);
                this.ivHeadArrow = headView.findViewById(R.id.iv_head_arrow);
                this.ivHeadArrow.animate().setInterpolator(new LinearInterpolator());
                this.headProgressBar = headView.findViewById(R.id.head_progressbar);
            }
            headViewLayoutParams = new RecyclerView.LayoutParams(-1, 0);
            headView.setLayoutParams(headViewLayoutParams);
        }
    }

    @Override
    public void changeFootTips(String tips) {
        if (!currentFootTips.equals(tips)) {
            currentFootTips = tips;
            if (footViewHolder.tvFootTip != null) {
                footViewHolder.tvFootTip.setText(tips);
            }
            if (footViewHolder.footProgressBar != null) {
                switch (tips) {
                    case SwipeRefreshLoadLayout.LOADING:
                        footViewHolder.footProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_refresh));
                        footViewHolder.footProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_refresh));
                        break;
                    default:
                        footViewHolder.footProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.refresh));
                        footViewHolder.footProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.refresh));
                        break;
                }
            }
        }
    }

    private String currentHeadTips = "";
    private String currentFootTips = "";

    @Override
    public void changeHeadTips(String tips) {
        if (!currentHeadTips.equals(tips)) {
            currentHeadTips = tips;
            if (headViewHolder.tvHeadTip != null) {
                headViewHolder.tvHeadTip.setText(tips);
            }
            if (headViewHolder.ivHeadArrow != null && headViewHolder.headProgressBar != null) {
                switch (tips) {
                    case SwipeRefreshLoadLayout.PULL_DOWN:
                        headViewHolder.ivHeadArrow.setVisibility(View.VISIBLE);
                        headViewHolder.headProgressBar.setVisibility(View.GONE);
                        headViewHolder.ivHeadArrow.animate().cancel();
                        headViewHolder.ivHeadArrow.animate().rotation(0);
                        break;
                    case SwipeRefreshLoadLayout.RELEASE_REFRESH:
                        headViewHolder.ivHeadArrow.setVisibility(View.VISIBLE);
                        headViewHolder.headProgressBar.setVisibility(View.GONE);
                        headViewHolder.ivHeadArrow.animate().cancel();
                        headViewHolder.ivHeadArrow.animate().rotation(180);
                        break;
                    case SwipeRefreshLoadLayout.REFRESHING:
                        headViewHolder.ivHeadArrow.setVisibility(View.GONE);
                        headViewHolder.headProgressBar.setVisibility(View.VISIBLE);
                        break;
                    default:
                        headViewHolder.ivHeadArrow.animate().cancel();
                        headViewHolder.headProgressBar.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    @Override
    public void changeHeadViewHeight(int headViewHeight) {
        if (headViewHolder.headView != null && headViewHeight >= 0) {
            headViewLayoutParams.height = headViewHeight;
            headViewHolder.headView.setLayoutParams(headViewLayoutParams);
        }
    }

    @Override
    public void changeFootViewHeight(int footViewHeight) {
        if (footViewHolder.footView != null && footViewHeight >= 0) {
            footViewLayoutParams.height = footViewHeight + refreshViewHeight;
            footViewHolder.footView.setLayoutParams(footViewLayoutParams);
        }
    }
}
