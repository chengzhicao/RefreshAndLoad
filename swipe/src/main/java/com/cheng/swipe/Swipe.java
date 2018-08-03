package com.cheng.swipe;

public class Swipe {
    /**
     * 上拉和下拉时滑动监听
     */
    public interface OnSlideActionListener {
        /**
         * 释放刷新
         */
        void releaseRefresh();

        /**
         * 下拉刷新
         */
        void downRefresh();

        /**
         * 释放加载更多
         */
        void releaseLoad();

        /**
         * 上拉加载
         */
        void upLoad();
    }

    /**
     * 改变头尾提示信息，仅限本包类
     */
    interface OnChangeViewTip {
        /**
         * 改变底部view提示
         *
         * @param tips
         */
        void changeFootTips(String tips);

        /**
         * 改变头view提示
         *
         * @param tips
         */
        void changeHeadTips(String tips);
    }

    /**
     * 监听刷新和加载更多
     */
    public interface OnRefreshAndLoadListener {
        /**
         * 下拉刷新
         */
        void refresh();

        /**
         * 上拉加载
         */
        void loadMore();

    }

    /**
     * 监听头尾view的高度变化
     */
    interface OnChangeViewHeight {
        /**
         * 改变头view高度
         *
         * @param headViewHeight
         */
        void changeHeadViewHeight(int headViewHeight);

        /**
         * 改变底部view高度
         *
         * @param footViewHeight
         */
        void changeFootViewHeight(int footViewHeight);
    }
}
