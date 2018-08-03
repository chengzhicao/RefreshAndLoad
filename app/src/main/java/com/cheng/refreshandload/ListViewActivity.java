package com.cheng.refreshandload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.cheng.swipe.SwipeRefreshLoadLayout;

public class ListViewActivity extends AppCompatActivity {
    ListView listView;
    SwipeRefreshLoadLayout mySwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mySwipe = findViewById(R.id.srll);
        listView = findViewById(R.id.listview);
        listView.setAdapter(new BaseAdapter() {
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
        });
    }

    public void doRefresh(View view) {
        mySwipe.doRefresh();
    }

    public void stopRefresh(View view) {
        mySwipe.finishRefresh();
    }

    public void stopLoadMore(View view) {
        mySwipe.finishLoadMore();
    }
}
