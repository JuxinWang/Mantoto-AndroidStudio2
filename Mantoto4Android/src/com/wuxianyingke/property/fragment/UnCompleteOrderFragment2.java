/*
package com.wuxianyingke.property.fragment;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.MessageActivity;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.adapter.GetOrderListAdapter2;
import com.wuxianyingke.property.adapter.PropertyMessageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.OrderInfo;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.GetUnOrderListThread;
import com.wuxianyingke.property.threads.MessageOutBoxThread;

public class UnCompleteOrderFragment2 extends ListFragment {
	
	*/
/** 订单列表适配器 *//*

	private static GetOrderListAdapter2 mAdapter;
	*/
/** 订单列表线程用于获得订单数据 *//*

	private GetUnOrderListThread mThread;
	*/
/**
	 * 分页相关成员
	 *//*

	private int pageIndex=1;
	private int pageCount;
	private ProgressDialog mProgressDialog = null;
	*/
/**
	 * 加载数据相关成员
	 *//*

	private boolean isLoading = false;
	private LoadingDialog loadingDialog;
	private NetworkUtils nUtils; 
	ArrayList<OrderItem>mdata=new ArrayList<OrderItem>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
				if(mdata==null)
				{
					return;
				}
				Log.i("MyLog", "getOrders"+mdata);
					*/
/**显示信息列表*//*

					showLogsListView(mdata);
				break;
			case Constants.MSG_NETWORK_ERROR:
				
				break;
			}
			super.handleMessage(msg);
		}

		private void showLogsListView(ArrayList<OrderItem> mdata) {
			if (mdata == null)
			{
				if (mProgressDialog != null)
				{
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				searchLayout.setVisibility(View.GONE);
				return;
			}
			
			if(pageCount == 1)
			{
				 
				mAdapter = new GetOrderListAdapter2(getActivity(), mdata,  pageCount);
				LogUtil.d("MyTag","1-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mAdapter.getCount());
				if (mProgressDialog != null)
				{
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				getListView().setAdapter(mAdapter);
			}
			else
			{
				mAdapter.appandAdapter(mdata);
				mAdapter.notifyDataSetChanged();
				LogUtil.d("MyTag","1-mItemSum = mLogAdapter.getCount()="+mAdapter.getCount());
				int test12 = mItemSum % 10;
				if(test12 == 0)
					getListView().setSelection(mItemSum - 10);
				else
					getListView().setSelection(mItemSum - test12);
			}
			setAllowGetPageAgain();
			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		User use = LocalStore.getUserInfo();
		loadingDialog = new LoadingDialog(getActivity());
//		mThread = new GetUnOrderListThread(getActivity(),
//				mHandler, use.userId, pageIndex);
//		mThread.start();
		Thread getOrderThread=new Thread(){
			@Override
			public void run() {
				RemoteApiImpl remote=new RemoteApiImpl();
				OrderInfo orderInfo=remote.getUncompletedOrder(getActivity(), LocalStore.getUserInfo().userId, pageIndex);
				if (orderInfo!=null) {
					mdata=orderInfo.orderInfo;
					Message msg=new Message();
					mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_FINISH);
				}
				
			}
		};
		getOrderThread.start();
	}
	
	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;
	public LinearLayout showLayout()
	{
		searchLayout = new LinearLayout(getActivity());
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(getActivity());
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(getActivity());
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(getActivity());
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
				mThread = new GetUnOrderListThread(getActivity(), mHandler, LocalStore.getUserInfo().userId, scrollState);
				mThread.start();
			}
		}
	};

}
*/
