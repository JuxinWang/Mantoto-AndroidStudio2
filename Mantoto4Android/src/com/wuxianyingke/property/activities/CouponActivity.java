package com.wuxianyingke.property.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.CouponAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.MessageTypeInfo;
import com.wuxianyingke.property.remote.RemoteApi.Promotion;
import com.wuxianyingke.property.threads.CouponThread;
import com.wuxianyingke.property.threads.MessageTypeThread;

/**
 * 代金券
 */
public class CouponActivity extends BaseActivity {

	private ProgressDialog mProgressDialog = null;
	// private MessageOutBoxThread mLogsThread = null;
	private CouponThread couponThread = null;
	private ListView mLogsListView = null;
	private static CouponAdapter mLogAdapter = null;
	private int propertyid;
	private TextView topbar_txt, topbar_right, txt_btn3;
	private Button topbar_left;
	private long userid = 0;
	private LinearLayout add_message_linearlayout, message_btn_linearlayout;
	private MessageTypeThread mMessageTypeThread = null;
	private boolean typeinited = false;
	private LinearLayout confirm;// 确定
	private Button payButton;
	private String time;// 有效期
	private int flags;// `1:正常 2.选择情况 3.消费券过期
	private int ParValue;// 代金券面额
	private long UserCashCouponID;// 代金券id
	private int flag;
	private int selectFlag = 0;
	private TextView couponTextView;
	private Promotion promotion;
	private long promotionId;
	private String desc ;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 5:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				Toast.makeText(CouponActivity.this, "订单获取失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 6:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				Toast.makeText(CouponActivity.this, "充值成功",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 4:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				promotionId = promotion.PromotionID;
				break;

			case 3:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				searchLayout.setVisibility(View.GONE);
				mLogsListView.setVisibility(View.GONE);
				couponTextView.setVisibility(View.VISIBLE);
				break;
			case 7:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				Toast.makeText(CouponActivity.this, desc,
						Toast.LENGTH_SHORT).show();
				break;
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				searchLayout.setVisibility(View.GONE);
				if (mPageNum == 1) {
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				} else {
					LogUtil.d("MyTag", "MSG_NETWORK_ERROR");
					mPageNum--;
					mAllowGetLogAgain = false;
					searchLayout.setVisibility(View.GONE);
				}
				break;

			case Constants.MSG_MESSAGE_OUT_BOX_FINISH:
				if (couponThread != null)
					showLogsListView(couponThread.getActivitys().userCashCouponList);
				/*
				 * mLogsListView.setOnItemSelectedListener(new
				 * AdapterView.OnItemSelectedListener() {
				 * 
				 * @Override public void onItemSelected(AdapterView<?> parent,
				 * View view, int position, long id) {
				 * time=mLogsThread.getActivitys().get(position).cTime;
				 * Log.i("MyLog","点击选择时间time="+time); }
				 * 
				 * @Override public void onNothingSelected(AdapterView<?>
				 * parent) {
				 * Toast.makeText(CouponActivity.this,"未选择成功",Toast.LENGTH_SHORT
				 * ).show(); } });
				 */
				mLogsListView
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								 {
									mLogAdapter.changeSelected(position);
									/*
									 * UserCashCouponID = couponThread
									 * .getActivitys().userCashCouponList
									 * .get(position).UserCashCouponID;
									 */
									if (couponThread.getActivitys().userCashCouponList
											.get(position).cashCouponStatus.CashCouponStatusID == 1) {
										if (flag != 2) {
											/*Toast.makeText(getApplicationContext(), "代金券+"+couponThread.getActivitys().userCashCouponList
													.get(position).cashCoupon.ParValue, Toast.LENGTH_SHORT).show();*/
											showNoticeDialog(couponThread,position);
										} 
										ParValue = couponThread.getActivitys().userCashCouponList
												.get(position).cashCoupon.ParValue;
										UserCashCouponID = couponThread
												.getActivitys().userCashCouponList
												.get(position).UserCashCouponID;
										time = couponThread.getActivitys().userCashCouponList
												.get(position).BTime;
										Log.i("MyLog", "点击选择时间time=" + time);
										selectFlag = 0;
										/*
										 * Toast.makeText(CouponActivity.this,
										 * "选择成功", Toast.LENGTH_SHORT).show();
										 */
									} else {
										selectFlag = 1;
										Toast.makeText(CouponActivity.this,
												"您选择的代金券不可以使用",
												Toast.LENGTH_SHORT).show();
									}
								}
							}
						});

				break;

			case Constants.MSG_GET_MESSAGE_TYPE_FINISH:
				if (mMessageTypeThread != null)
					showMessageTypeView(mMessageTypeThread.getMessageTypeList());
				break;

			}
			super.handleMessage(msg);
		}
	};

	private void showMessageTypeView(List<MessageTypeInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			final MessageTypeInfo info = list.get(i);
			View v = LayoutInflater.from(this).inflate(R.layout.message_btn,
					null);
			TextView txt_btn = (TextView) v.findViewById(R.id.txt_btn);
			txt_btn.setText(info.messageTypeName);
			txt_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					newMessage(info.messageTypeID, info.messageTypeName);
				}
			});
			message_btn_linearlayout.addView(v);

		}
		typeinited = true;
	}

	private void newMessage(int type, String name) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(CouponActivity.this, AddMessageActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("name", name);
		startActivityForResult(intent, 0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		startProgressDialog();
		endChildrenThreads();
		mAllowGetLogAgain = false;
		searchLayout.setVisibility(View.VISIBLE);
		couponThread = new CouponThread(CouponActivity.this, mHandler, userid);
		couponThread.start();

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userid = LocalStore.getUserInfo().userId;

		if (getIntent().getExtras() != null) {
			flag = getIntent().getIntExtra("flag", 0);
			flags =1;
		}

		Thread getOrderThread = new Thread() {
			@Override
			public void run() {
				RemoteApiImpl remoteApi = new RemoteApiImpl();
				promotion = remoteApi.getPromotionId(CouponActivity.this,
						LocalStore.getUserInfo().PropertyID,
						(int) LocalStore.getUserInfo().userId, "wifi");

				Message msg = new Message();
				if (promotion == null) {
					msg.what = 5;
					Log.i("MyLog", "OrderPaymentThread3");
				} else {
					msg.what = 4;
				}
				mHandler.sendMessage(msg);
			}
		};
		getOrderThread.start();
		setContentView(R.layout.activity_coupon_listview);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		couponTextView = (TextView) findViewById(R.id.couponTextView);
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);

		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setText(R.string.message_left);
		topbar_right.setClickable(true);
		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (userid == 0
						|| LocalStore.getIsVisitor(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), "游客或者未认证用户无法完成此操作",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (typeinited)
					add_message_linearlayout.setVisibility(View.VISIBLE);
				else {
					Toast.makeText(getApplicationContext(), "未获得消息类型列表，请稍后重试。",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_txt.setText("选择代金券");
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		payButton = (Button) findViewById(R.id.payButton);

		payButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectFlag == 1) {
					Toast.makeText(CouponActivity.this, "请选择可以使用的代金券",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.setClass(CouponActivity.this, PayActivity.class);
					intent.putExtra("price", ParValue);
					intent.putExtra("usercashcouponid", UserCashCouponID);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});

		SharedPreferences saving = this.getSharedPreferences(
				LocalStore.USER_INFO, 0);
		// propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		mLogsListView = (ListView) findViewById(R.id.PropertyMessageListView);
		// 设置不显示滚动条
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setVisibility(View.VISIBLE);
		// mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
		mLogsListView.addFooterView(showLayout());
		mLogsListView.setOnScrollListener(mScrollListner);
		/*
		 * mLogsListView.setOnItemClickListener(new
		 * AdapterView.OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) {
		 * 
		 * } });
		 */
		startProgressDialog();
		/*
		 * mLogsThread = new MessageOutBoxThread(CouponActivity.this,
		 * mHandler,propertyid,userid, mPageNum); mLogsThread.start();
		 */
		couponThread = new CouponThread(CouponActivity.this, mHandler, userid);
		couponThread.start();

		txt_btn3 = (TextView) findViewById(R.id.txt_btn3);
		add_message_linearlayout = (LinearLayout) findViewById(R.id.add_message_linearlayout);
		message_btn_linearlayout = (LinearLayout) findViewById(R.id.message_btn_linearlayout);
		add_message_linearlayout.setVisibility(View.GONE);
		confirm = (LinearLayout) findViewById(R.id.couponConfirm);
		if (flag == 2) {
			confirm.setVisibility(View.VISIBLE);
		}
		/*
		 * confirm.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new Intent();
		 * intent.setClass(CouponActivity.this, PayActivity.class); double price
		 * = 5; intent.putExtra("price", price); setResult(RESULT_OK, intent);
		 * finish(); } });
		 */

		txt_btn3.setClickable(true);
		txt_btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				add_message_linearlayout.setVisibility(View.GONE);
			}
		});

		mMessageTypeThread = new MessageTypeThread(this, mHandler, propertyid);
		mMessageTypeThread.start();

		/*
		 * Thread CouponThread=new Thread(){
		 * 
		 * @Override public void run() { RemoteApiImpl remoteApi=new
		 * RemoteApiImpl(); RemoteApi.CashCouponList
		 * cashCouponList=remoteApi.getListUserCashCoupon
		 * (CouponActivity.this,Integer
		 * .parseInt(""+LocalStore.getUserInfo().userId));
		 * Log.i("MyLog","CouponActivity=+userId="
		 * +Integer.parseInt(""+LocalStore.getUserInfo().userId)); } };
		 * CouponThread.start();
		 */
	}

	@Override
	protected void onRestart() {
		if (mLogsListView != null)
			mLogsListView.invalidateViews();
		super.onRestart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if (needInit) {
			// 重新获取资源
			startProgressDialog();
			endChildrenThreads();
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.VISIBLE);
			/*
			 * mLogsThread = new MessageOutBoxThread(CouponActivity.this,
			 * mHandler,propertyid, userid, mPageNum); mLogsThread.start();
			 */
			couponThread = new CouponThread(CouponActivity.this, mHandler,
					userid);
			couponThread.start();
			LogUtil.d("MyTag", "InformDetailActivity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		endChildrenThreads();
		super.onDestroy();
	}

	private void startProgressDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(CouponActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}

	private void endChildrenThreads() {

		if (couponThread != null) {
			couponThread.stopRun();
			couponThread = null;
		}

		mLogsListView.setAdapter(null);

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

	public void showLogsListView(List<RemoteApi.UserCashCoupon> list) {
		if (list == null) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;

			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		if (mPageNum == 1) {

			mLogAdapter = new CouponAdapter(this, list, flags);
			LogUtil.d("MyTag",
					"1-mItemSum mPageNum == 1  mLogAdapter.getCount()="
							+ mLogAdapter.getCount());
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mLogsListView.setAdapter(mLogAdapter);
		} else {
			mLogAdapter.appandAdapter(list);
			mLogAdapter.notifyDataSetChanged();
			LogUtil.d("MyTag", "1-mItemSum = mLogAdapter.getCount()="
					+ mLogAdapter.getCount());
			int test12 = mItemSum % 10;
			if (test12 == 0)
				mLogsListView.setSelection(mItemSum - 10);
			else
				mLogsListView.setSelection(mItemSum - test12);
		}
		setAllowGetPageAgain();
	}

	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;

	public LinearLayout showLayout() {
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}

	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;

	public static void setAllowGetPageAgain() {
		LogUtil.d("MyTag", "mItemSummItemSum=" + mItemSum);
		if (mLogAdapter != null)
			mItemSum = mLogAdapter.getCount();
		int test12 = mItemSum % 10;
		if ((test12 != 0) || (mItemSum >= 96)) {
			LogUtil.d("MyTag", "mItemSummItemSum" + test12);
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.GONE);
		} else {
			mAllowGetLogAgain = true;
			LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"
					+ test12);
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
			if (mLogAdapter != null && lastItem >= (mLogAdapter.getCount() - 2)) {
				LogUtil.d("MyTag", "InformDetailActivity.this mAllowGetLogAgain="
						+ mAllowGetLogAgain);
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "InformDetailActivity.this onScrollStateChanged");
				/*
				 * mLogsThread = new MessageOutBoxThread(CouponActivity.this,
				 * mHandler,propertyid, userid, mPageNum); mLogsThread.start();
				 */
				couponThread = new CouponThread(CouponActivity.this, mHandler,
						userid);
				couponThread.start();
			}
		}
	};

	void freeResource() {
		// TODO Auto-generated method stub
		couponThread = null;
		stopProgressDialog();
		endChildrenThreads();
	}

	void initResource() {
		// TODO Auto-generated method stub
		LogUtil.d("MyTag", "InformDetailActivity.this initResource");
		freeResource();
		if (couponThread == null)
			startProgressDialog();
		/*
		 * mLogsThread = new MessageOutBoxThread(CouponActivity.this,
		 * mHandler,propertyid, userid, mPageNum); mLogsThread.start();
		 */
		couponThread = new CouponThread(CouponActivity.this, mHandler, userid);
		couponThread.start();
	}
	
	private void showNoticeDialog(final CouponThread couponThread,final int position) {
		final AlertDialog alertDialog = new AlertDialog.Builder(CouponActivity.this)
				.create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_recharge_pay);
		TextView rechargeCouponMoney = (TextView) window
				.findViewById(R.id.rechargeCouponMoney);
		String couponMoney = rechargeCouponMoney.getText().toString().replace("10", ""+couponThread.getActivitys().userCashCouponList
									.get(position).cashCoupon.ParValue);
		rechargeCouponMoney.setText(couponMoney);
		Button concel = (Button) window.findViewById(R.id.recharge_cancel_btn);
		concel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				alertDialog.dismiss();	
			}
		});
		Button confirm = (Button) window.findViewById(R.id.recharge_confirm_btn);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Runnable payRunnable = new Runnable() {
					@Override
					public void run() {
						RemoteApiImpl remoteApi = new RemoteApiImpl();
	                      RemoteApi.OrderItem orderItem2 = remoteApi.createOrder(CouponActivity.this, promotionId, LocalStore.getUserInfo().userId, 0, "支付WIFI费用", (int)couponThread.getActivitys().userCashCouponList
									.get(position).cashCoupon.ParValue, "wx",couponThread.getActivitys().userCashCouponList
									.get(position).UserCashCouponID, 0, 0);
	                      Log.i("MyLog", "promotionid = "+promotionId+"payValue = "+couponThread.getActivitys().userCashCouponList
									.get(position).cashCoupon.ParValue);
	                      Log.i("MyLog", "2userId=--------------" + LocalStore.getUserInfo().userId + "payMoney=" + couponThread.getActivitys().userCashCouponList
									.get(position).cashCoupon.ParValue + "payType=");
	                      Message msg = new Message();
	                      if (orderItem2 ==null) {
							return;
						}
	                      if (orderItem2.netInfo.code ==200) {
	                    	  msg.what = 6;
						}else{
							desc = orderItem2.netInfo.desc;
							msg.what = 7;
						}
	                      Log.i("MyLog", "create  code = " +orderItem2.netInfo.code);
	                     
	                      mHandler.sendMessage(msg);
					}
				};
				Thread payThread = new Thread(payRunnable);
				payThread.start();
				alertDialog.dismiss();
			}
		});
	}
}
