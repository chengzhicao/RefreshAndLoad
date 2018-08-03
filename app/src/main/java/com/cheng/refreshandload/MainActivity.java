package com.cheng.refreshandload;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressDrawable mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(0xff666666);
        ImageView iv = findViewById(R.id.iv_image);
        iv.setImageDrawable(mProgressDrawable);

        ImageView iv2 = findViewById(R.id.iv_image2);
        PathsDrawable mArrowDrawable = new PathsDrawable();
        mArrowDrawable.parserColors(0xff666666);
        mArrowDrawable.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
        iv2.setImageDrawable(mArrowDrawable);
    }

    public void toRecyclerView(View view) {
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

    public void toListView(View view) {
        startActivity(new Intent(this, ListViewActivity.class));
    }
}
