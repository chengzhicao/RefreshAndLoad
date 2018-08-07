# SwipeRefreshLoadLayout，一个能同时支持RecyclerView和ListView的下拉刷新和上拉加载的布局

![使用RecyclerView的延展样式](https://upload-images.jianshu.io/upload_images/6753190-a64d567102c7a3a1.gif?imageMogr2/auto-orient/strip)

![使用RecyclerView并使用SwipeRefreshLayout的原生样式](https://upload-images.jianshu.io/upload_images/6753190-380b7377d116c5ef.gif?imageMogr2/auto-orient/strip)

![使用ListView的延展样式](https://upload-images.jianshu.io/upload_images/6753190-fb54b6b50cf7c2e6.gif?imageMogr2/auto-orient/strip)

![使用ListView并使用SwipeRefreshLayout的原生样式](https://upload-images.jianshu.io/upload_images/6753190-d24bd451355a98c7.gif?imageMogr2/auto-orient/strip)

# 1.功能

* 支持RecyclerView和ListView下拉刷新和上拉加载

* 支持主动刷新

* 下拉刷新能使用SwipeRefresh的原生样式或自定义的拉伸样式

* 可自定义ViewGroup定制刷新样式和加载样式

# 2.类和方法

|类|描述|
|---|---|
|SwipeRefreshLoadLayout|继承SwipeRefreshLayout，增加上拉加载功能以及各种事件的处理，整个项目的和心类|
|RecycleViewAdapter|如果要用RecyclerView实现功能，使用此类代替原RecyclerView.Adapter|
|SwipeLinearLayoutManager|RecyclerView布局管理器，继承LinearLayoutManager，一个辅助类，使用RecyclerView时必须使用此类|
|Swipe|内部有公开和非公开的监听接口|
|ListViewHeadAndFootManager|ListView的headView和footView管理器|

|SwipeRefreshLoadLayout方法|描述|
|---|---|
|void setHeadViewRefreshingHeight(int headViewRefreshingHeight)|设置处于刷新状态时的头view高度。同时，头view可允许刷新时滑动的距离为此值+20dp|
|void setFootViewAccessLoadDistance(int footViewAccessLoadDistance)|设置底部view可允许加载更多时滑过的距离|
|void setListViewHeadAndFoot(ListView listView)|设置ListView的头和尾View，必须在setAdapter之前调用|
|boolean isRefreshing()|获取刷新状态|
|boolean isLoading()|获取加载状态|
|void setFootViewVisibility(int visibility)|设置底部footView的可见性|
|void setHeadView(ViewGroup headView, int id)|自定义头View|
|void setFootView(ViewGroup footView)|自定义底部footView|
|void setRefreshStyle(int refreshStyle)|设置刷新样式，CIRCLE或SPREAD|
|finishLoadMore()|完成加载|
|doRefresh()|主动刷新|
|finishRefresh()|完成刷新|

|Swipe.OnSlideActionListener接口方法|描述|
|---|---|
|void downRefreshAction()|下拉刷新行为|
|void releaseRefreshAction()|释放刷新行为|
|void upLoadAction()|上拉加载行为|
|void releaseLoadAction()|释放加载行为|

|Swipe.OnRefreshAndLoadListener接口方法|描述|
|---|---|
|void refresh()|下拉刷新|
|void loadMore()|上拉加载|

# 3.使用

* 使用RecyclerView

```
<com.cheng.swipe.SwipeRefreshLoadLayout
	android:id="@+id/srl"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<android.support.v7.widget.RecyclerView
		android:id="@+id/rcv"
		android:layout_width="wrap_content"
		android:layout_height="wrap_content"
		android:scrollbars="vertical" />
</com.cheng.swipe.SwipeRefreshLoadLayout>
```

```
mySwipe = findViewById(R.id.srll);
mySwipe.setRefreshStyle(SwipeRefreshLoadLayout.SPREAD);
mySwipe.setOnRefreshAndLoadListener(this);
mySwipe.setOnSlideActionListener(this);
//mySwipe.setFootViewVisibility(View.GONE);
//mySwipe.setFootView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.foot_layout,null));
//mySwipe.setHeadView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.head_layout, null), R.id.headId);

recyclerView = findViewById(R.id.rcv);
recyclerView.setNestedScrollingEnabled(true);
recyclerView.setLayoutManager(new SwipeLinearLayoutManager(this, mySwipe));

recyclerView.setAdapter(new RecycleViewAdapter(this,mySwipe) {
	@Override
	public int getCounts() {
		return 0;
	}

	@Override
	public RecyclerView.ViewHolder onNewViewHolder(@NonNull ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public int getItemType(int position) {
		return 0;
	}

	@Override
	public void onSetViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

	}
});
```

* 使用ListView
```
<com.cheng.swipe.SwipeRefreshLoadLayout
	android:id="@+id/srll"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<ListView
		android:id="@+id/listview"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:scrollbars="vertical" />

</com.cheng.swipe.SwipeRefreshLoadLayout>
```

```
mySwipe = findViewById(R.id.srll);
listView = findViewById(R.id.listview);
//mySwipe.setFootView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.foot_layout, null));
//mySwipe.setHeadView((ViewGroup) LayoutInflater.from(this).inflate(R.layout.head_layout, null),R.id.headId);
mySwipe.setListViewHeadAndFoot(listView);
mySwipe.setOnRefreshAndLoadListener(this);

//注意顺序，设置headView和footView要在setAdapter之前
mySwipe.setListViewHeadAndFoot(listView);
listView.setAdapter(new BaseAdapter() {
	@Override
	public int getCount() {
		return 0;
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
		return null;
	}
});
```

# 4.详细介绍

> https://www.jianshu.com/p/c92e666dd14d
