package com.cheng.refreshandload;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheng.swipe.RecycleViewAdapter;
import com.cheng.swipe.Swipe;
import com.cheng.swipe.SwipeLinearLayoutManager;
import com.cheng.swipe.SwipeRefreshLoadLayout;

public class RecyclerViewActivity extends AppCompatActivity implements Swipe.OnRefreshAndLoadListener, Swipe.OnSlideActionListener {
    private String TAG = this.getClass().getSimpleName();
    private RecyclerView recyclerView;
    private SwipeRefreshLoadLayout mySwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("使用RecyclerView");
        }

        mySwipe = findViewById(R.id.srll);
        mySwipe.setRefreshStyle(SwipeRefreshLoadLayout.SPREAD);
        mySwipe.setOnRefreshAndLoadListener(this);
        mySwipe.setOnSlideActionListener(this);
//        mySwipe.setFootViewVisibility(View.GONE);
//        mySwipe.setFootView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.foot_layout,null));
//        mySwipe.setHeadView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.head_layout,null));

        recyclerView = findViewById(R.id.rcv);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new SwipeLinearLayoutManager(this));
        RecycleViewAdapter recycleViewRefreshAdapter = new RecycleViewAdapter(this, mySwipe) {

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
        recyclerView.setAdapter(recycleViewRefreshAdapter);
    }

    /**
     * 主动刷新
     *
     * @param view
     */
    public void doRefresh(View view) {
        if (!mySwipe.isRefreshing()) {
            Log.i(TAG, "主动刷新");
            mySwipe.doRefresh();
        }
    }

    /**
     * 停止刷新
     */
    public void stopRefresh() {
        Log.i(TAG, "停止刷新");
        mySwipe.finishRefresh();
    }

    /**
     * 停止加载
     */
    public void stopLoadMore() {
        Log.i(TAG, "停止加载更多");
        mySwipe.finishLoadMore();
    }

    @Override
    public void refresh() {
        Log.i(TAG, "正在刷新。。。");
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopRefresh();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void loadMore() {
        Log.i(TAG, "正在加载更多。。。");
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopLoadMore();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void releaseRefreshAction() {
        Log.i(TAG, "释放刷新");
    }

    @Override
    public void downRefreshAction() {
        Log.i(TAG, "下拉刷新");
    }

    @Override
    public void releaseLoadAction() {
        Log.i(TAG, "释放加载更多");
    }

    @Override
    public void upLoadAction() {
        Log.i(TAG, "上拉加载更多");
    }
}
