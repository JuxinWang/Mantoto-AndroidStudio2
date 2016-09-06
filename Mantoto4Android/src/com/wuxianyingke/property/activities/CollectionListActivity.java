package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.CanYinListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.GetCollectionListThread;

public class CollectionListActivity extends BaseActivity{

	private enum ProductListTab {
		LIST, GRID
	}

	private enum DataSource {
		CATE_SOURCE,
	}

	private ListView mLogsListView;
	private ArrayList<Flea> mDataList = new ArrayList<Flea>();
	private static CanYinListAdapter mListAdapter; // TODO 写重复了一个adapter
	private Button mLeftTopButton,topbar_button_right;
	private TextView topbar_txt;
	private GetCollectionListThread mThread = null;

	private ProgressDialog mProgressDialog;
	private int propertyid;
    private int choucang_flag=0;
    
	// 品牌商品列表

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if(mThread!=null)
					showLogsListView(mThread.getProductList());

				break;
			case Constants.MSG_GET_CANYIN_LIST_EMPTY:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				((TextView) findViewById(R.id.empty_tv))
						.setVisibility(View.VISIBLE);
				break;
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				break;
			case Constants.MSG_GET_SEARCH_ICON_FINISH:
			case Constants.MSG_GET_CANYIN_DETAIL_IMG_FINISH:
				if (mListAdapter != null)
					mListAdapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.canyin_list);
		setImmerseLayout(findViewById(R.id.common_back));
		choucang_flag= getIntent().getIntExtra(Constants.SHOUCANG_FLAT , 0) ;
//		SharedPreferences saving = this.getSharedPreferences(
//				LocalStore.USER_INFO, 0);
//		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		propertyid=LocalStore.getUserInfo().PropertyID;
		ImageView cartImageview = (ImageView) findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this, cartImageview);
		topbar_txt= (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("收藏店铺");

		LogUtil.d("MyTag", "收藏店铺 onCreate");

		mLeftTopButton = (Button) findViewById(R.id.topbar_left);
		mLeftTopButton.setVisibility(View.VISIBLE);
		mLeftTopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				LogUtil.d("MyTag", "topbar_button_right on click listener");
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.setClass(CollectionListActivity.this, CollectionListActivity.class);
//				intent.putExtra(Constants.SHOUCANG_FLAT,choucang_flag);
//				startActivity(intent);
				finish();
			}
		});
		
		mLogsListView = (ListView) findViewById(R.id.product_list_view);
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
		showDialog();
		mThread = new GetCollectionListThread(CollectionListActivity.this,
				mHandler, propertyid,choucang_flag);
		mThread.start();
		
	}
	protected void onRestart(){
		if(mLogsListView!=null)
			mLogsListView.invalidateViews();
		super.onRestart();
	}
	@Override
	protected void onNewIntent(Intent intent){
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			showDialog();
			endChildrenThreads();
			mThread = new GetCollectionListThread(CollectionListActivity.this,
					mHandler, propertyid,choucang_flag);
			mThread.start();
			LogUtil.d("MyTag", "InformDetailActivity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy(){
		endChildrenThreads();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//startThread();
		super.onResume();
	}

	private void showDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mLogsListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(CollectionListActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}


	public void freeResource() {
		int count = mDataList.size();
		for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mDataList.get(i).frontCover.imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}
		mThread = null ;
		showDialog();
		endChildrenThreads();
	}

	public void showLogsListView(List<LivingItem> list){

		if (list == null){

			if (mProgressDialog != null){

				mProgressDialog.dismiss();
				mProgressDialog = null;
				
			}
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		mListAdapter = new CanYinListAdapter(this, list,mHandler,choucang_flag,1);

//		JSONArray ja = new JSONArray(list);
		LogUtil.d("MyTag","CollectionList-list"+list);

		for (int i = 0; i < list.size(); i++){
			LogUtil.d("MyTag", "CollectionList-list distance" + list.get(i).distance);
			LogUtil.d("MyTag", "CollectionList-list LivingItemID" + list.get(i).LivingItemID);
			LogUtil.d("MyTag", "CollectionList-list LivingItemName" + list.get(i).LivingItemName);
			LogUtil.d("MyTag", "CollectionList-list address" + list.get(i).address);
			LogUtil.d("MyTag", "CollectionList-list telephone" + list.get(i).telephone);

		}

		LogUtil.d("MyTag","CollectionList-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mListAdapter.getCount());
			if (mProgressDialog != null){
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mLogsListView.setAdapter(mListAdapter);
		    mListAdapter.notifyDataSetChanged();

		mLogsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
				LayoutInflater inflater =LayoutInflater.from(getApplicationContext());
				LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.user_address_dialog,null);
				final Dialog dialog = new AlertDialog.Builder(CollectionListActivity.this).create();
				dialog.setCancelable(false);
				dialog.show();
				dialog.getWindow().setContentView(layout);
				WindowManager.LayoutParams params =
						dialog.getWindow().getAttributes();
				params.width = 700;
				params.height = 700;
				dialog.getWindow().setAttributes(params);

				TextView dialog_msg = (TextView) layout.findViewById(R.id.remind_messagesId);
				TextView btnOK = (TextView) layout.findViewById(R.id.btn_yesId);
				btnOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Thread deleteThread = new Thread(){
							@Override
							public void run() {
								RemoteApiImpl rai = new RemoteApiImpl();
								RemoteApi.NetInfo netInfo = rai.deleteFromFavorite(CollectionListActivity.this
								,LocalStore.getUserInfo().userId,propertyid);
								mThread = new GetCollectionListThread(CollectionListActivity.this,
										mHandler, propertyid,choucang_flag);
								mThread.start();
							}
						};
						deleteThread.start();
						dialog.dismiss();
					}
				});

				return true;
			}
		});

	}
	

	
	private void endChildrenThreads(){

		if (mThread != null){

			mThread.stopRun();
			mThread = null;
		}

		mLogsListView.setAdapter(null);
		
	}

	void initResource() {

		LogUtil.d("MyTag", "InformDetailActivity.this initResource");
		freeResource() ;
		if (mThread == null) 
		showDialog();
		mThread = new GetCollectionListThread(CollectionListActivity.this,
				mHandler, propertyid,choucang_flag);
		mThread.start();
	}
}

