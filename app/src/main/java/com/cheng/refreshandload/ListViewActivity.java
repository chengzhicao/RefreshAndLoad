package com.cheng.refreshandload;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.cheng.swipe.Swipe;
import com.cheng.swipe.SwipeRefreshLoadLayout;

public class ListViewActivity extends AppCompatActivity implements Swipe.OnRefreshAndLoadListener {
    ListView listView;
    SwipeRefreshLoadLayout mySwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("使用ListView");
        }

        mySwipe = findViewById(R.id.srll);
        listView = findViewById(R.id.listview);
        mySwipe.setRefreshStyle(SwipeRefreshLoadLayout.CIRCLE);
//        mySwipe.setFootView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.rrv_bottom_load2, null));
//        mySwipe.setHeadView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.rrv_top_head2, null));
        mySwipe.setOnRefreshAndLoadListener(this);

        //注意顺序，设置headView和footView要在setAdapter之前
        mySwipe.setListViewHeadAndFoot(listView);
        listView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position % 2 == 0) {
                return LayoutInflater.from(ListViewActivity.this).inflate(R.layout.rrv_item_text, parent, false);
            } else {
                View view = LayoutInflater.from(ListViewActivity.this).inflate(R.layout.rrv_item_image, parent, false);
                ImageView imageView = view.findViewById(R.id.iv_photo);
                imageView.setImageResource(R.mipmap.ic_launcher);
                return view;
            }
        }
    }

    @Override
    public void refresh() {
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

    public void doRefresh(View view) {
        if (!mySwipe.isRefreshing()) {
            mySwipe.doRefresh();
        }
    }

    public void stopRefresh() {
        mySwipe.finishRefresh();
    }

    public void stopLoadMore() {
        mySwipe.finishLoadMore();
    }
}
