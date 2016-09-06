package com.wuxianyingke.property.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.AddressAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.CreateAddress;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.GetAddressListThread;
import com.wuxianyingke.property.widget.FilpperListvew;

public class AddressListActivity extends BaseActivity {

	// 顶部导航
	private TextView topbar_txt, topbar_right;
	private Button topbar_left;
	private int favorite_flag;

	// 自定义ListView
	private FilpperListvew listView;
	// 地址列表适配器
	private static AddressAdapter mListAdapter;
	private int width;
	private int userid;
	// 获取所有地址外部线程
	private GetAddressListThread mThread = null;
	private ProgressDialog mProgressDialog;
	
	private Handler mHandler = new Handler() {

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case Constants.MSG_GET_CANYIN_LIST_FINISH:
			mListAdapter=new AddressAdapter(getApplicationContext(), mThread.getAddress());
			listView.setAdapter(mListAdapter);
			mListAdapter.notifyDataSetChanged();
			Log.i("MyLog","MSG_GET_CANYIN_LIST_FINISH---"+mThread.getAddress().get(0).Recipient);
			
			
			listView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, final int position, long id) {
					// 1. 布局文件转换为View对象
					LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
					LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.user_address_dialog, null);
					final Dialog dialog = new AlertDialog.Builder(AddressListActivity.this).create();
					dialog.setCancelable(false);
					dialog.show();
					dialog.getWindow().setContentView(layout);
					WindowManager wm=getWindowManager();
					int width= wm.getDefaultDisplay().getWidth();
					int height= wm.getDefaultDisplay().getHeight();
					WindowManager.LayoutParams params =
							dialog.getWindow().getAttributes();
							params.width = width*2/3;
							params.height = height*4/11 ;
							dialog.getWindow().setAttributes(params);
					TextView dialog_msg = (TextView) layout.findViewById(R.id.remind_messagesId);
					TextView btnOK = (TextView) layout.findViewById(R.id.btn_yesId);
					btnOK.setOnClickListener(new OnClickListener()
					{
					    @Override
					    public void onClick(View v)
					    {
					    	Thread DeleteThread=new Thread(){
								@Override
								public void run() {
									RemoteApiImpl rai=new RemoteApiImpl();
									CreateAddress cAddress=rai.deleteAddress(AddressListActivity.this, mThread.getAddress().get(position).AddressID);
									User info=LocalStore.getUserInfo();
									mThread=new GetAddressListThread(AddressListActivity.this, mHandler, info.userId);
									mThread.start();
								};
							};
							DeleteThread.start();
					        dialog.dismiss();
					    }
					});
					 
					// 5. 取消按钮
					TextView btnCancel = (TextView) layout.findViewById(R.id.btn_noId);
					btnCancel.setOnClickListener(new OnClickListener()
					{
					 
					    @Override
					    public void onClick(View v)
					    {
					        dialog.dismiss();
					    }
					});
					
					
					return true;
				}
			});
			
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent=new Intent(AddressListActivity.this,AddressDetailActivity.class);
					intent.putExtra("aname", mThread.getAddress().get(position).Recipient);
					Log.i("MyLog", "bundle.getString(aname)------------="+mThread.getAddress().get(position).Recipient);
					intent.putExtra("aphone", mThread.getAddress().get(position).TelNumber);
					intent.putExtra("aaddress", mThread.getAddress().get(position).Detail);
					intent.putExtra("area", mThread.getAddress().get(position).CityArea);
					intent.putExtra("addressId",mThread.getAddress().get(position).AddressID);
					Log.i("MyLog","dangqian地址的IDwei ____"+mThread.getAddress().get(position).AddressID);
					startActivity(intent);
//					finish();
				}
			});
			
			
//		case Constants.MSG_GET_CANYIN_LIST_EMPTY:
//			if (mProgressDialog != null && mProgressDialog.isShowing()) {
//				mProgressDialog.dismiss();
//				}
//			((TextView) findViewById(R.id.empty_tv))
//					.setVisibility(View.VISIBLE);
			break;
		case Constants.MSG_NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接出错请刷新---", Toast.LENGTH_SHORT)
						.show();
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
		setContentView(R.layout.user_address);
		User info = LocalStore.getUserInfo();
		Log.i("MyLog", "-----userid" + userid);
		
		// 初始化控件
		initView();
//		init();
		setImmerseLayout(findViewById(R.id.common_back));
		// 初始化事件监听器
		initListener();
		mThread=new GetAddressListThread(AddressListActivity.this, mHandler, info.userId);
		mThread.start();

		
	}

	@Override
	protected void onResume() {
//		init();
		super.onResume();
	}


	private void initListener() {
		// 左侧返回菜单处理事件
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (0 != favorite_flag) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getApplicationContext(),
							CommitOrderActivity.class);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});

		// 右侧按钮处理事件 进入地址编辑界面

		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(AddressListActivity.this,
						AddressEditActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
	}


	private void initView() {

		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("地址管理");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setText("新建");
		topbar_right.setTextSize(22);

		listView = (FilpperListvew) findViewById(R.id.lv_addressId);

	}

}
