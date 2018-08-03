package com.cheng.refreshandload;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheng.swipe.MyRecyclerViewLayoutManager;
import com.cheng.swipe.RefreshRecycleViewAdapter;
import com.cheng.swipe.Swipe;
import com.cheng.swipe.SwipeRefreshLoadLayout;

public class RecyclerViewActivity extends AppCompatActivity implements Swipe.OnRefreshAndLoadListener, Swipe.OnSlideActionListener {
    private RecyclerView rvData;
    private SwipeRefreshLoadLayout mySwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        mySwipe = findViewById(R.id.srl);
        mySwipe.setRefreshStyle(SwipeRefreshLoadLayout.SPREAD);
        mySwipe.setOnRefreshAndLoadListener(this);
        mySwipe.setOnSlideActionListener(this);

        rvData = findViewById(R.id.rcv);
        rvData.setNestedScrollingEnabled(true);
        rvData.setLayoutManager(new MyRecyclerViewLayoutManager(this));
        RefreshRecycleViewAdapter recycleViewRefreshAdapter = new RefreshRecycleViewAdapter(this, mySwipe) {

            @Override
            public int getCounts() {
                return 50;
            }

            @Override
            public RecyclerView.ViewHolder onNewViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == 1) {
                    return new TextViewHolder(LayoutInflater.from(RecyclerViewActivity.this).inflate(R.layout.rrv_item_text, parent, false));
                } else if (viewType == 2) {
                    return new ImageViewHolder(LayoutInflater.from(RecyclerViewActivity.this).inflate(R.layout.rrv_item_image, parent, false));
                }
                return null;
            }

            @Override
            public int getItemType(int position) {
                if (position % 2 == 0) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            public void onSetViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (position % 2 == 0) {
                    ((TextViewHolder) holder).tvTitle.setText("第" + position + "章");
                    ((TextViewHolder) holder).tvIntroduce.setText(position + "");
                } else {
                    ((ImageViewHolder) holder).ivPhoto.setImageResource(R.mipmap.ic_launcher);
                }
            }

            class ImageViewHolder extends RecyclerView.ViewHolder {
                ImageView ivPhoto;

                public ImageViewHolder(View itemView) {
                    super(itemView);
                    ivPhoto = itemView.findViewById(R.id.iv_photo);
                }
            }

            class TextViewHolder extends RecyclerView.ViewHolder {
                TextView tvTitle, tvIntroduce;

                public TextViewHolder(View itemView) {
                    super(itemView);
                    tvIntroduce = itemView.findViewById(R.id.tv_introduce);
                    tvTitle = itemView.findViewById(R.id.tv_title);
                }
            }
        };
        rvData.setAdapter(recycleViewRefreshAdapter);
    }

    /**
     * 主动刷新
     *
     * @param view
     */
    public void doRefresh(View view) {
        Log.i("jfiowejigwe", "主动刷新");
        mySwipe.doRefresh();
    }

    /**
     * 停止刷新
     *
     * @param view
     */
    public void stopRefresh(View view) {
        Log.i("jfiowejigwe", "停止刷新");
        mySwipe.finishRefresh();
    }

    /**
     * 停止加载
     *
     * @param view
     */
    public void stopLoadMore(View view) {
        Log.i("jfiowejigwe", "停止加载更多");
        mySwipe.finishLoadMore();
    }

    @Override
    public void refresh() {
        Log.i("jfiowejigwe", "正在刷新。。。");
    }

    @Override
    public void loadMore() {
        Log.i("jfiowejigwe", "正在加载更多。。。");
    }

    @Override
    public void releaseRefresh() {
        Log.i("jfiowejigwe", "释放刷新");
    }

    @Override
    public void downRefresh() {
        Log.i("jfiowejigwe", "下拉刷新");
    }

    @Override
    public void releaseLoad() {
        Log.i("jfiowejigwe", "释放加载更多");
    }

    @Override
    public void upLoad() {
        Log.i("jfiowejigwe", "上拉加载更多");
    }
}
