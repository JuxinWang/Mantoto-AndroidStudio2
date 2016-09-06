package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.Propertys;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetPropertyByNameListThread;

public class PropertyListActivity extends BaseActivity {
	/**
	 * 查找小区
	 */
	private EditText inputProperty;
	/**
	 * 小区列表
	 */
	private ListView propertyListView;
	/**
	 * 小区集合
	 */
	/**
	 * 分页相关成员
	 */
	private int pageIndex = 1;
	private int pageCount = 1;
	// private ArrayList<Propertys> propertyList=new ArrayList<Propertys>();
	private String[] propertys = new String[] {};
	private Button topbar_left;
	private TextView topRightTxt;
	private ArrayAdapter<String> adapter = null;
	private GetPropertyByNameListThread mByNameThread;
	private ArrayList<Propertys> mList=new ArrayList<Propertys>();
	private ArrayList<Propertys> propertysList=new ArrayList<Propertys>();
	private ProgressDialog mProgressBar = null;
	private String mErrorInfo = "";
	private String desc = "";
	java.io.File file = null;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {
			// 查找小区
			case Constants.MSG_GET_PRODUCT_LIST_FINISH:
				propertysList = mByNameThread.getPropertyList();
				Log.i("MyLog", "当前小区信息为-----" + propertysList);
				mList.addAll(propertysList);
				String[] propertys = new String[mList.size()];
				for (int i = 0; i < mList.size(); i++) {
					propertys[i] = mList.get(i).PropertyName;
				Log.i("MyLog", "当前集合的内容为————————"+mList.get(i).PropertyName);
				}
				adapter = new ArrayAdapter<String>(getApplicationContext(),
						R.layout.activity_list_item,R.id.tv_ListItem, propertys);
				propertyListView.setAdapter(adapter);
				propertyListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
//						LocalStore.setPropertys(PropertyListActivity.this, propertysList.get(position));
						User user=new User();
						user.userId=LocalStore.getUserInfo().userId;
						user.userName=LocalStore.getUserInfo().userName;
						user.PropertyID=(int) mList.get(position).PropertyID;
						LocalStore.setUserInfo(PropertyListActivity.this, user);
						
						Log.i("MyLog", "当前的小区idshi"+LocalStore.getUserInfo().PropertyID);
//						SharedPreferences saving=getSharedPreferences(LocalStore.PROPERTY_ID, 0);
//						saving.edit().putInt(LocalStore.PROPERTY_ID, Integer.parseInt(""+mList.get(position).PropertyID));
						if (mList.get(position).OrganizationID==0) {
							Intent intent=new Intent();
							intent.setClass(PropertyListActivity.this, RegisterActivity.class);
							startActivity(intent);
						}else {
							Intent intent2=new Intent();
							intent2.setClass(PropertyListActivity.this,RegisterActivity.class);
							startActivity(intent2);
						}
						
					}
				});
				break;

			// 通讯错误
			case 2:
//				propertysList = mThread.getPropertyList();
				Intent intent2 = new Intent();
				intent2.putExtra("key", propertysList);
				if (propertysList.size()!=0) {
					intent2.setClass(PropertyListActivity.this,
							LocationPropertyListActivity.class);
				}else {
					intent2.setClass(PropertyListActivity.this,
							NoPropertyActivity.class);
					intent2.putExtra("flag", "flag");
				}
				startActivity(intent2);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
private String description;
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_neighborhood_list);
		setImmerseLayout(findViewById(R.id.common_back));
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		description=getIntent().getStringExtra("et_InputContent");
		// 初始化控件
		initWidgets();
		// 获得小区列表数据源
		final ArrayList<Propertys> propertysList = (ArrayList<Propertys>) getIntent().getSerializableExtra("key");
		mList.addAll(propertysList);
		String[] propertys = new String[mList.size()];
		for (int i = 0; i < mList.size(); i++) {
			propertys[i] = mList.get(i).PropertyName;
		Log.i("MyLog", "当前集合的内容为————————"+mList.get(i).PropertyName);
		}
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.activity_list_item,R.id.tv_ListItem, propertys);
		propertyListView.setAdapter(adapter);
		propertyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				LocalStore.setPropertys(PropertyListActivity.this, propertysList.get(position));
				User user=new User();
				user.userId=LocalStore.getUserInfo().userId;
				user.userName=LocalStore.getUserInfo().userName;
				user.PropertyID=(int) mList.get(position).PropertyID;
				LocalStore.setUserInfo(PropertyListActivity.this, user);
				
				Log.i("MyLog", "当前的小区idshi"+LocalStore.getUserInfo().PropertyID);
				if (propertysList.get(position).OrganizationID==0) {
					Intent intent=new Intent();
					intent.setClass(PropertyListActivity.this, RegisterActivity.class);
					startActivity(intent);
				}else {
					Intent intent2=new Intent();
					intent2.setClass(PropertyListActivity.this,RegisterActivity.class);
					startActivity(intent2);
				}
				
			}
		});
		propertyListView.setOnScrollListener(new OnScrollListener() {
			boolean isBottom = false;//表示每页数据已经加载完，一个标志位
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Toast.makeText(PropertyListActivity.this, "数据加载中，请稍后...",
						Toast.LENGTH_SHORT).show();
				if (isBottom&&scrollState==OnScrollListener.SCROLL_STATE_IDLE&&pageIndex <= pageCount) {
					
					isBottom = false;
					pageIndex++;
					User use = LocalStore.getUserInfo();
					mByNameThread=new GetPropertyByNameListThread(getApplicationContext(), mHandler, description, pageIndex);
					mByNameThread.start();
				}else if (pageIndex > pageCount) {
					Toast.makeText(PropertyListActivity.this, "数据已经加载完毕！",
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

	private void initWidgets() {

		topbar_left=(Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

		topRightTxt = (TextView) findViewById(R.id.topbar_right);
		topRightTxt.setVisibility(View.VISIBLE);
		topRightTxt.setText("保存");
		topRightTxt.setTextColor(Color.rgb(255,165,0));

		inputProperty = (EditText) findViewById(R.id.et_InputNeiborhoodNameId);
		propertyListView = (ListView) findViewById(R.id.lv_PropertyList);

	}
}
