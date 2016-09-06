package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.PromotionCode;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetUnusePromotionListThread;

/**
优惠券
 *
 */
public class CommitVoucherListActivity extends BaseActivity {

	// 顶部导航
	private TextView topbar_txt;
	private Button topbar_left;
	private int favorite_flag;
	/** 订单列表适配器 */
	private GetOrderListAdapter mAdapter;
	/** 订单列表线程用于获得订单数据 */
	private GetUnusePromotionListThread mThread;
	/** 用于展示数据 */
	private ListView mListView;
	/** 分页设置 */
	private int pageIndex = 1;
	private int pageCount = 1;
	private TextView mTextview;
	private int flags=5;
	private ArrayList<OrderItem> mData = new ArrayList<OrderItem>();
	private ArrayList<PromotionCode> promotionCode = new ArrayList<PromotionCode>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
				Log.i("MyLog", "###我的优惠券列表---------------------》"
						+ mThread.mOrders);
				
				mData.addAll(mThread.mOrders);
				mAdapter = new GetOrderListAdapter(getApplicationContext(),
						mData,flags);
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();

				mListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						Intent intent = new Intent(getApplicationContext(),
								CommitVoucherContentActivity.class);
						intent.putExtra(
								"header",
								mData.get(position).ThePromotion.header);
						intent.putExtra("body",
								mData.get(position).ThePromotion.body);
						intent.putExtra(
								"price",
								mData.get(position).ThePromotion.Price);

						intent.putExtra("path",
								mData.get(position).ThePromotion.path);
						intent.putExtra("ordersequencenumber",mData
								.get(position).OrderSequenceNumber);
						intent.putExtra("OrderID", mData.get(position).OrderID);

						startActivity(intent);
					}
				});
				pageCount=mAdapter.getCount();
				((AbsListView) mListView)
						.setOnScrollListener(new OnScrollListener() {
							boolean isBottom = false;// 表示每页数据已经加载完，一个标志位
							
							@Override
							public void onScrollStateChanged(AbsListView view,
									int scrollState) {
								
								if (isBottom
										&& scrollState == OnScrollListener.SCROLL_STATE_IDLE
										&& pageIndex <= pageCount) {

									Toast.makeText(
											CommitVoucherListActivity.this,
											"数据加载中，请稍后...", Toast.LENGTH_LONG)
											.show();
									isBottom = false;
									pageIndex++;
									User use = LocalStore.getUserInfo();
									mThread = new GetUnusePromotionListThread(
											CommitVoucherListActivity.this,
											mHandler, use.userId, pageIndex);
									mThread.start();
								} else if (pageIndex > pageCount) {
									Toast.makeText(
											CommitVoucherListActivity.this,
											"数据已经加载完毕....", Toast.LENGTH_LONG)
											.show();
								}

							}

							@Override
							public void onScroll(AbsListView view,
									int firstVisibleItem, int visibleItemCount,
									int totalItemCount) {
								if (firstVisibleItem + visibleItemCount == totalItemCount) {
									isBottom = true;

								}
							}
						});

				break;
			case Constants.MSG_NETWORK_ERROR:
				mListView.setVisibility(View.GONE);
				mTextview.setVisibility(View.VISIBLE);
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_myvoucher);
		// 初始化控件
		initViews();
		setImmerseLayout(findViewById(R.id.common_back));
		// 初始化事件监听
		initListener();
		// mThread = new GetPromotionCodeThread(CommitVoucherListActivity.this,
		// mHandler, 179);
		// mThread.start();
		User user = LocalStore.getUserInfo();
		Log.i("MyLog", "我的消费券列表--------------------------------》" + user.userId
				+ "pageindex" + pageIndex);
		mThread = new GetUnusePromotionListThread(
				CommitVoucherListActivity.this, mHandler, user.userId,
				pageIndex);
		mThread.start();

	}



	private void initListener() {
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
		topbar_txt.setText("优惠券");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		mTextview=(TextView) findViewById(R.id.empty_tv);
		mListView = (ListView) findViewById(R.id.myorder_ListViewId);
	}
}
