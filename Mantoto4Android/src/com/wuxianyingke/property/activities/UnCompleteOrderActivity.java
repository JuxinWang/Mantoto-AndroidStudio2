package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.GetOrderListAdapter2;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.threads.GetUnOrderListThread;
import com.wuxianyingke.property.threads.MessageOutBoxThread;
/**
 * 未完成订单
 */
public class UnCompleteOrderActivity extends BaseActivity {

	private ProgressDialog mProgressDialog = null;
	private ListView mListView = null;
	private static GetOrderListAdapter2 mAdapter = null;
	private int propertyid;
	private TextView topbar_txt,topbar_right;
	private Button topbar_left;
	private long userid = 0;
	private LinearLayout add_message_linearlayout,message_btn_linearlayout;
	private GetUnOrderListThread mThread = null;
	private boolean typeinited = false;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case Constants.MSG_NETWORK_ERROR:
					if (mProgressDialog != null)
					{
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
					if(mPageNum == 1)
					{
						View view = findViewById(R.id.view_network_error);
						view.setVisibility(View.VISIBLE);
					}
					else
					{
						LogUtil.d("MyTag","MSG_NETWORK_ERROR");
						mPageNum--;
						mAllowGetLogAgain = false;
						searchLayout.setVisibility(View.GONE);
					}
					break;
				case Constants.MSG_GET_MESSAGE_TYPE_FINISH:
					if(mThread!=null)
//					showMessageTypeView(mThread.mOrders);
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		userid = LocalStore.getUserInfo().userId;
		
		setContentView(R.layout.activity_uncompelete_order);
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);

		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setText(R.string.message_left);
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setClickable(true);
		topbar_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(userid == 0||LocalStore.getIsVisitor(getApplicationContext()))
				{
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(typeinited)
					add_message_linearlayout.setVisibility(View.VISIBLE);
				else
				{
					Toast.makeText(getApplicationContext(), "未获得消息类型列表，请稍后重试。",
					Toast.LENGTH_SHORT).show();
				}
			}
		});
		 topbar_left = (Button) findViewById(R.id.topbar_left);
	        topbar_txt.setText("未完成订单");
	        topbar_left.setVisibility(View.VISIBLE);
	        topbar_left.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	            	finish();
	            }
	        });
		SharedPreferences saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
//		propertyid=(int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		propertyid=LocalStore.getUserInfo().PropertyID;
		mListView = (ListView) findViewById(R.id.UnCompeleteOrderListView);
		// 设置不显示滚动条
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setDivider(getResources().getDrawable(R.drawable.list_line));
		mListView.addFooterView(showLayout());
		mListView.setOnScrollListener(mScrollListner);
		startProgressDialog();
		mThread = new GetUnOrderListThread(getApplicationContext(), mHandler, userid, mPageNum);
		mThread.start();
		
	}

	

	@Override
	protected void onRestart()
	{
		if(mListView!=null)
			mListView.invalidateViews();
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			startProgressDialog();
			endChildrenThreads();
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.VISIBLE);
			mThread = new GetUnOrderListThread(getApplicationContext(), mHandler, userid, mPageNum);
			mThread.start();
			LogUtil.d("MyTag", "InformDetailActivity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy()
	{
		endChildrenThreads();
		super.onDestroy();
	}
	

	private void startProgressDialog()
	{
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(UnCompleteOrderActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	
	private void endChildrenThreads()
	{

		if (mThread != null)
		{
			mThread.stopRun();
			mThread = null;
		}

		mListView.setAdapter(null);
		
		mAllowGetLogAgain = true;
		mPageNum = 1;
		mItemSum = 0;
	}
	
	private void stopProgressDialog() {

		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	//显示信息列表
	public void showLogsListView(ArrayList<OrderItem> mOrders) 
	{
		if (mOrders == null)
		{
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
				
			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mListView.setVisibility(View.VISIBLE);
		if(mPageNum == 1)
		{
			 
			mAdapter = new GetOrderListAdapter2(getApplicationContext(), mOrders, mItemSum);
			LogUtil.d("MyTag","1-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mAdapter.getCount());
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mListView.setAdapter(mAdapter);
		}
		else
		{
			mAdapter.appandAdapter(mOrders);
			mAdapter.notifyDataSetChanged();
			LogUtil.d("MyTag","1-mItemSum = mLogAdapter.getCount()="+mAdapter.getCount());
			int test12 = mItemSum % 10;
			if(test12 == 0)
				mListView.setSelection(mItemSum - 10);
			else
				mListView.setSelection(mItemSum - test12);
		}
		setAllowGetPageAgain();
	}
	
	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;
	public LinearLayout showLayout()
	{
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}
	
	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;
	public static void setAllowGetPageAgain()
	{
		LogUtil.d("MyTag", "mItemSummItemSum="+mItemSum);
		if(mAdapter!=null)
			mItemSum = mAdapter.getCount();
		int test12 = mItemSum % 10;
		if((test12 != 0) || (mItemSum >= 96))
		{
			LogUtil.d("MyTag", "mItemSummItemSum"+test12);
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.GONE);
		}
		else
		{
			mAllowGetLogAgain = true;
			LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"+test12);
		}
	}
	
	private OnScrollListener mScrollListner = new OnScrollListener()
	{
		private int lastItem = 0;
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			lastItem = firstVisibleItem + visibleItemCount - 1;
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			if (mAdapter != null && lastItem >= (mAdapter.getCount()-2))
			{
				LogUtil.d("MyTag", "InformDetailActivity.this mAllowGetLogAgain="+mAllowGetLogAgain);
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "InformDetailActivity.this onScrollStateChanged");
				mThread = new GetUnOrderListThread(getApplicationContext(), mHandler, scrollState, mPageNum);
				mThread.start();
			}
		}
	};

	void freeResource() {
		// TODO Auto-generated method stub
		mThread = null ;
		stopProgressDialog();
		endChildrenThreads();
	}

	void initResource() {
		// TODO Auto-generated method stub
		LogUtil.d("MyTag", "InformDetailActivity.this initResource");
		freeResource() ;
		if (mThread == null) 
		startProgressDialog();
		mThread = new GetUnOrderListThread(getApplicationContext(), mHandler, userid, mPageNum);
		mThread.start();
	}
}
