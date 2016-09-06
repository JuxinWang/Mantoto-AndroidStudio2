package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Propertys;
import com.wuxianyingke.property.service.SendLocation;
import com.wuxianyingke.property.threads.GetPropertyByNameListThread;
import com.wuxianyingke.property.threads.GetPropertyListThread;

/***
 * 通过定位查找获得小区
 */
public class LocationActivity extends BaseActivity {

	private View TopbarView;
	private TextView topbar_txt, topbar_right, remind;
	private Button topbar_left;
	private ImageButton clear;

	/**
	 * 定位、查找
	 */
	private Button btn_Loacation, btn_Find;
	/**
	 * 输入小区
	 */
	private EditText et_Input;
	private ListView mListView;

	private LinearLayout llRemindMessage, llLocation, llEditText, llFindName;
	private LocationClient mLocClient;
	private static SendLocation info = new SendLocation();
	public MyLocationListenner myListener = new MyLocationListenner();
	private GetPropertyListThread mThread;
	private GetPropertyByNameListThread mByNameThread;
	private int mPageIndex = 1;
	private ArrayList<Propertys> propertysList = new ArrayList<Propertys>();

	private ArrayAdapter<String> adapter = null;

	private ProgressDialog mProgressBar = null;
	private String mErrorInfo = "";
	private String desc = "";
	java.io.File file = null;
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
			case Constants.MSG_GET_PRODUCT_LIST_FINISH:
				propertysList = mByNameThread.getPropertyList();
				Log.i("MyLog", "当前小区信息为-----" + propertysList);
				/*
				 * llRemindMessage.setVisibility(View.GONE);
				 * llLocation.setVisibility(View.GONE);
				 * llFindName.setVisibility(View.GONE);
				 * mListView.setVisibility(View.VISIBLE);
				 */
				Intent intent = new Intent();
				intent.putExtra("key", propertysList);
				intent.putExtra("et_InputContent", et_Input.getText().toString());
				if (propertysList.size()!=0) {
					intent.setClass(LocationActivity.this,
							PropertyListActivity.class);
				}else {
					intent.setClass(LocationActivity.this, NoPropertyActivity.class);
				}
				startActivity(intent);
				finish();
				break;

			case 2:
				//定位得到小区列表
				propertysList = mThread.getPropertyList();
				Intent intent2 = new Intent();
				intent2.putExtra("key", propertysList);
				intent2.putExtra("latitude", latitude);
				intent2.putExtra("longitude", longitude);
				if (propertysList.size()!=0) {
					intent2.setClass(LocationActivity.this,
							LocationPropertyListActivity.class);
				}else {
					intent2.setClass(LocationActivity.this,
							NoPropertyActivity.class);
				}
				startActivity(intent2);
				finish();
				break;
			case 3:
				Toast.makeText(LocationActivity.this, "附近暂无小区，请输入小区名称查找！",
						Toast.LENGTH_LONG).show();
				break;
			case 9:
				Toast.makeText(LocationActivity.this, "网络超时，请重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_neighborhood);

		PushAgent.getInstance(getApplicationContext()).onAppStart();
		/**
		 * 初始化布局
		 */
		initWidgets();

		setImmerseLayout(findViewById(R.id.common_back));
		/**
		 * 定位识别小区
		 */
		initLocation();

		/**
		 * 处理点击事件
		 */
		initListening();
		

		
		et_Input.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String hint;
				if (arg1) {
					hint=et_Input.getHint().toString();
					et_Input.setTag(hint);
					et_Input.setHint("");
				}else {
					hint=et_Input.getTag().toString();
					et_Input.setHint(hint);
				}
				
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		initWidgets();
		Intent intent=new Intent();
		intent.setClass(LocationActivity.this, SetMessageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 处理按钮点击事件
	 */
	private void initListening() {
		// 点击定位识别小区
		btn_Loacation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				latitude=Float.parseFloat("" + info.latitude);
				longitude=Float
						.parseFloat("" + info.longitude);
				Log.i("MyLog", "当前的定位信息为：---->" + mPageIndex + "经纬度："
						+ info.latitude + ":" + info.longitude);
				
				mThread = new GetPropertyListThread(LocationActivity.this,
						mHandler, latitude, longitude, mPageIndex);
				Log.i("MyLog", "response=Location="+latitude+"  "+longitude+"  "+mPageIndex);
				btn_Loacation.setEnabled(false);
				mThread.start();
			
			}
		});
		//点击查询小区
		btn_Find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 提交查询小区的名称
				propertyNameSend();
				
			}

		});

	}

	/**
	 * 定位识别小区
	 */
	private void initLocation() {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(10000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();

	}

	/**
	 * 初始化布局控件
	 */
	private void initWidgets() {
		topbar_left=(Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent();
				intent.setClass(LocationActivity.this, SetMessageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

			}
		});

		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setVisibility(View.VISIBLE);
		topbar_txt.setText("小区管理");
		topbar_txt.setTextSize(18);


		remind = (TextView) findViewById(R.id.tv_RemindMessage);//上方提示语
		btn_Loacation = (Button) findViewById(R.id.Location_Neborhood);// 定位
		btn_Find = (Button) findViewById(R.id.Find_Neborhood);// 查找
		et_Input = (EditText) findViewById(R.id.et_InputNeiborhoodNameId);// 输入模糊小区(门牌号)
		/*
		 * mListView = (ListView) findViewById(R.id.lv_PropertyList);// 小区列表
		 * llRemindMessage = (LinearLayout) findViewById(R.id.ll_RemindMessage);
		 * llLocation = (LinearLayout)
		 * findViewById(R.id.ll_Location_Neiborhood); llEditText =
		 * (LinearLayout) findViewById(R.id.ll_InputNeiborHoodName); llFindName
		 * = (LinearLayout) findViewById(R.id.ll_FindNorhood);
		 * llRemindMessage.setVisibility(View.VISIBLE);
		 * llLocation.setVisibility(View.VISIBLE);
		 * llEditText.setVisibility(View.VISIBLE);
		 * llFindName.setVisibility(View.VISIBLE);
		 */
	}

	// 提交查询小区的名称
	private void propertyNameSend() {
		// 获得搜索内容
		String et_InputContent = et_Input.getText().toString();
		if (Util.isEmpty(et_Input)) {
			Toast.makeText(getApplicationContext(), "请输入小区名称", Toast.LENGTH_SHORT).show();
			return;
		}
		mByNameThread = new GetPropertyByNameListThread(LocationActivity.this,
				mHandler, et_InputContent, mPageIndex);
		btn_Find.setEnabled(false);
		mByNameThread.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置

			if (location == null)
				return;

			info.latitude = (float) location.getLatitude();
			/*
			 * Log.i("MyLog", "当前定位的经纬度---：" + location.getLatitude() + ":" +
			 * location.getLongitude());
			 */
			info.longitude = (float) location.getLongitude();
			info.city = location.getCity();
			Log.i("MyLog","info.cyty=="+location.getCity());

			LocalStore.cityInfo.city_name = info.city;
			LocalStore.setCityInfo(LocationActivity.this, LocalStore.cityInfo);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}