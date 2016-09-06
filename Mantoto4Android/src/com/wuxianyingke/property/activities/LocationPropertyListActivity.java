package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.Propertys;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetPropertyListThread;

public class LocationPropertyListActivity extends BaseActivity {

	private Button topbar_left;
	/**
	 * 小区列表
	 */
	private ListView propertyListView;
	/**
	 * 小区集合
	 */
	private int pageIndex = 1;
	private int pageCount = 1;
	private GetPropertyListThread mThread;
	private String[] propertys = new String[] {};
	private ArrayAdapter<String> adapter = null;
	private ArrayList<Propertys> mList = new ArrayList<Propertys>();
	private ArrayList<Propertys> propertysList = new ArrayList<Propertys>();
	private ProgressDialog mProgressBar = null;
	private float latitude,longitude;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {
			// 查找小区
			case 2:

				propertysList = mThread.getPropertyList();
				Log.i("MyLog", "当前小区信息为-----" + propertysList);

				mList.addAll(propertysList);

				String[] propertys = new String[mList.size()];

				for (int i = 0; i < mList.size(); i++) {

					propertys[i] = mList.get(i).PropertyName;
					Log.i("MyLog", "当前集合的内容为————————"
							+ mList.get(i).PropertyName);
				}

				adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.activity_list_item, R.id.tv_ListItem,
						propertys);

				propertyListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				propertyListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

								User user=new User();
								user.userId=LocalStore.getUserInfo().userId;
								user.userName=LocalStore.getUserInfo().userName;
								user.PropertyID=(int) mList.get(position).PropertyID;
								LocalStore.setUserInfo(LocationPropertyListActivity.this, user);
								
								Log.i("MyLog", "当前的小区idshi"+LocalStore.getUserInfo().PropertyID);
									Intent intent2 = new Intent();
									intent2.setClass(
											LocationPropertyListActivity.this,
											RegisterActivity.class);
									startActivity(intent2);
									finish();
//								}

							}
						});
				break;

			// 通讯错误
			case 3:
				// propertysList = mThread.getPropertyList();
				Intent intent2 = new Intent();
				intent2.putExtra("key", propertysList);
				if (propertysList.size() != 0) {
					intent2.setClass(LocationPropertyListActivity.this,
							LocationPropertyListActivity.class);
				} else {
					intent2.setClass(LocationPropertyListActivity.this,
							NoPropertyActivity.class);
				}
				startActivity(intent2);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neighborhood_list);
		setImmerseLayout(findViewById(R.id.common_back));
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		latitude=getIntent().getFloatExtra("latitude", 0);
		longitude=getIntent().getFloatExtra("longitude", 0);
		Log.i("MyLog", "当前的定位信息为-----）"+latitude+longitude);
		// 初始化控件
		initWidgets();

		// 获得小区列表数据源
		final ArrayList<Propertys> propertysList = (ArrayList<Propertys>) getIntent()
				.getSerializableExtra("key");
		mList.addAll(propertysList);
		if (mList == null) {
			Toast.makeText(getApplicationContext(), "附近暂无小区可供选择", Toast.LENGTH_SHORT).show();
		} else {
			String[] propertys = new String[mList.size()];
			for (int i = 0; i < mList.size(); i++) {
				propertys[i] = mList.get(i).PropertyName;
				Log.i("MyLog", "当前集合的内容为————————"
						+ mList.get(i).PropertyName);
			}
			adapter = new ArrayAdapter<String>(getApplicationContext(),
					R.layout.activity_list_item, R.id.tv_ListItem, propertys);
			propertyListView.setAdapter(adapter);
			propertyListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					User user=new User();
					user.userId=LocalStore.getUserInfo().userId;
					user.userName=LocalStore.getUserInfo().userName;
					user.PropertyID=(int) mList.get(position).PropertyID;
					LocalStore.setUserInfo(LocationPropertyListActivity.this, user);
					
					Log.i("MyLog", "当前的小区idshi"+LocalStore.getUserInfo().PropertyID);
						Intent intent2 = new Intent();
						intent2.setClass(LocationPropertyListActivity.this,
								RegisterActivity.class);
						startActivity(intent2);
						finish();
//					}

				}
			});

			propertyListView.setOnScrollListener(new OnScrollListener() {
				boolean isBottom = false;// 表示每页数据已经加载完，一个标志位

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					if (isBottom
							&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& pageIndex <= pageCount) {

						Toast.makeText(LocationPropertyListActivity.this, "数据加载中，请稍后...",
								Toast.LENGTH_SHORT).show();
						isBottom = false;
						pageIndex++;
						User use = LocalStore.getUserInfo();
						mThread = new GetPropertyListThread(
								LocationPropertyListActivity.this, mHandler, latitude,
								longitude,
								pageIndex);
						Log.i("MyLog", "定位的经纬度为--------"+getIntent().getFloatExtra("latitude", 0)+getIntent().getFloatExtra("longitude", 0));
						mThread.start();
					} else if (pageIndex > pageCount) {
						Toast.makeText(LocationPropertyListActivity.this, "数据已经加载完毕",
								Toast.LENGTH_SHORT).show();
					}

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

					if (firstVisibleItem + visibleItemCount == totalItemCount) {
						isBottom = true;
					}
				}
			});
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Intent intent=new Intent();
		intent.setClass(LocationPropertyListActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}

	private void initWidgets() {
		topbar_left=(Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent();
				intent.setClass(LocationPropertyListActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

			}
		});

		propertyListView = (ListView) findViewById(R.id.lv_PropertyList);

	}
}
