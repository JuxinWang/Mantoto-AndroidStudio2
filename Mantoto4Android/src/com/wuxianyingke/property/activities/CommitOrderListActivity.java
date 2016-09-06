package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.secneo.mp.util.LogUtil;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetUnderwayListThread;

/*
 *支付订单列表
 */
public class CommitOrderListActivity extends BaseActivity {
	// 顶部导航
	private TextView topbar_txt;
	private Button topbar_left;
	private int favorite_flag;
	private Button completedBtn, uncompleteBtn,allOrder;
	private LinearLayout titleLayout,orderLine1,orderLine2,orderLine3;
	private ListView mListView;
	/** 订单列表适配器 */
	private static GetOrderListAdapter mAdapter;
	/** 订单列表线程用于获得订单数据 */
    //private GetUnOrderListThread mThread;
	private GetUnderwayListThread mThread;
	private User use;
	private int pagerIndex=1;
	private int page = 0;
	private ProgressDialog mProgressDialog;
	private int flag=0;
	private TextView mTextView;
	private ArrayList<OrderItem> mUnderwayDataList = new ArrayList<OrderItem>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constants.MSG_GET_CANYIN_LIST_FINISH:
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					mTextView.setVisibility(View.GONE);
					if (mThread != null)
						showLogsListView(mThread.mOrders, 0);
					break;
				case Constants.MSG_NETWORK_ERROR:
					if (mProgressDialog != null && mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					mListView.setVisibility(View.GONE);
					mTextView.setVisibility(View.VISIBLE);
					mTextView.setText("暂无订单");
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_myentity);

		// 初始化控件
		initViews();
		setImmerseLayout(findViewById(R.id.common_back));
		// 设置每一个模块标题的点击事件
		setTitleEvent();

		// 初始化事件监听
		initListener();

	}


	private void initListener() {
		// 左侧返回按钮事件监听
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (0 != favorite_flag) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getApplicationContext(),
							UserCenterActivity.class);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});

	}


	private void initViews() {
		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("我的订单");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		mTextView=(TextView)findViewById(R.id.empty_tv);
		//三条底线
		orderLine1 = (LinearLayout) findViewById(R.id.order_line1);
		orderLine2 = (LinearLayout) findViewById(R.id.order_line2);
		orderLine3 = (LinearLayout) findViewById(R.id.order_line3);

		// 基础
		uncompleteBtn = (Button) findViewById(R.id.btn_UncompletedId);// 未完成
		completedBtn = (Button) findViewById(R.id.btn_CompletedId);// 已完成
		allOrder = (Button) findViewById(R.id.btn_all_order);//全部订单
		//uncompleteBtn.setTextColor(Color.parseColor("#ff7e00"));
		orderLine1.setVisibility(View.VISIBLE);
		//completedBtn.setBackgroundResource(R.drawable.switch_button_right_on);// 初始化时将完成背景设置成蓝色

		mListView=(ListView) findViewById(R.id.order_list);
		mListView.setVerticalScrollBarEnabled(false);
		/*mListView.setDivider(getResources().getDrawable(
				R.drawable.list_line));*/
		mListView.setOnScrollListener(mScrollListner);
		use=LocalStore.getUserInfo();
		initResource();

	}

	// 设置每一个模块标题的点击事件
	private void setTitleEvent() {
		View view = null;
		titleLayout = (LinearLayout) findViewById(R.id.titleLayoutId);

		for (int i = 0, len = titleLayout.getChildCount(); i < len; i++) {
			view = titleLayout.getChildAt(i); // 获取容器控件中指定位置的子控件
			view.setTag(i);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag();
//					initResource();
					select(position);
				}
			});
		}
	}

	// 选择某一标题，设置其字体颜色为红色
	private void select(int position) {
		for (int i = 0, len = titleLayout.getChildCount(); i < len; i++) {
			//completedBtn = (Button) titleLayout.getChildAt(i);
			if (i == position) {
				//完成按钮被点击
				flag=1;
				uncompleteBtn.setTextColor(Color.parseColor("#000000"));
				completedBtn.setTextColor(Color.parseColor("#ff7e00"));
				orderLine2.setVisibility(View.VISIBLE);
				orderLine1.setVisibility(View.GONE);
				orderLine3.setVisibility(View.GONE);
				/*uncompleteBtn
						.setBackgroundResource(R.drawable.switch_button_left_on);
				completedBtn
						.setBackgroundResource(R.drawable.switch_button_right_default);*/
				initResource();
			} else {
				//未完成订单
				flag=0;
				uncompleteBtn.setTextColor(Color.parseColor("#ff7e00"));
				completedBtn.setTextColor(Color.parseColor("#000000"));
				orderLine1.setVisibility(View.VISIBLE);
				orderLine2.setVisibility(View.GONE);
				orderLine3.setVisibility(View.GONE);
				/*uncompleteBtn
						.setBackgroundResource(R.drawable.switch_button_left_default);
				completedBtn
						.setBackgroundResource(R.drawable.switch_button_right_on);*/
				initResource();
			}
		}
	}
	public void showLogsListView(ArrayList<OrderItem> list, int p) {
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			return;
		}
		mListView.setVisibility(View.VISIBLE);
		if (mPageNum == 1) {

			if (page == 0) {
				mAdapter=new GetOrderListAdapter(CommitOrderListActivity.this, mThread.mOrders,flag);
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				mListView.setAdapter(mAdapter);
			}
		} else {
			mAdapter.appandAdapter(list);
			mAdapter.notifyDataSetChanged();
			int test12 = mItemSum % 10;
			if (test12 == 0)
				mListView.setSelection(mItemSum - 10);
			else
				mListView.setSelection(mItemSum - test12);
		}
		setAllowGetPageAgain();
	}

	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;

	public static void setAllowGetPageAgain() {
		if (mAdapter != null)
			mItemSum = mAdapter.getCount();
		int test12 = mItemSum % 10;
		if ((test12 != 0) || (mItemSum >= 96)) {
			mAllowGetLogAgain = false;
		} else {
			mAllowGetLogAgain = true;
		}
	}

	private OnScrollListener mScrollListner = new OnScrollListener() {
		private int lastItem = 0;
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount - 1;
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mAdapter != null && lastItem >= (mAdapter.getCount() - 2)) {
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "InformDetailActivity.this onScrollStateChanged");
//				mThread=new GetUnOrderListThread(CommitOrderListActivity.this, mHandler, use.userId, pagerIndex);
				mThread=new GetUnderwayListThread(CommitOrderListActivity.this, mHandler, mPageNum, use.userId,flag);
				mThread.start();
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		endChildrenThreads();
		super.onDestroy();
	}
	protected void onRestart()
	{
		if(mListView!=null)
			mListView.invalidateViews();
		super.onRestart();
	}


	private void showDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
//		mListView_underway.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(CommitOrderListActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void endChildrenThreads() {
		if (mThread != null) {
			mThread.stopRun();
			mThread = null;
		}
		mListView.setAdapter(null);
		mAllowGetLogAgain = true;
		mPageNum = 1;
		mItemSum = 0;
	}
	public void freeResource() {
//		int count = mUnderwayDataList.size();
		int count = mUnderwayDataList.size();
		for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mUnderwayDataList.get(i).imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}
		mThread = null ;
		showDialog();
		endChildrenThreads();
	}

	void initResource() {
		freeResource() ;
		if (mThread == null)
			mThread=new GetUnderwayListThread(CommitOrderListActivity.this, mHandler, mPageNum, use.userId,flag);
			mThread.start();
	}

}
