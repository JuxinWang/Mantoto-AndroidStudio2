package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.Propertys;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetPropertyByNameListThread;

public class NoPropertyActivity extends BaseActivity {
	private Button topbar_left, next;
	//private EditText et_FindNebohood;
	private TextView notFound,topTxt;
	private GetPropertyByNameListThread mByNameThread;
	private ArrayList<Propertys> propertysList = new ArrayList<Propertys>();
	private int flag =1;
	private int propertyId;
	private ProgressDialog mProgressBar = null;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressBar != null) {
				mProgressBar.dismiss();
				mProgressBar = null;
			}
			switch (msg.what) {


			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_notfound);
		setImmerseLayout(findViewById(R.id.common_back));
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		mByNameThread = new GetPropertyByNameListThread(NoPropertyActivity.this,
				mHandler, "附件未找到小区", 1);
		mByNameThread.start();
		/**
		 * 初始化布局控件
		 */
		initWidgets();


		/**
		 * 为查找到小区
		 */
		notFound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				/*// 1. 布局文件转换为View对象
				LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
				LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.unfind_property_dialog, null);
				final Dialog dialog = new AlertDialog.Builder(NoPropertyActivity.this).create();
				dialog.setCancelable(false);
				dialog.show();
				dialog.getWindow().setContentView(layout);
				WindowManager wm=getWindowManager();
				WindowManager.LayoutParams params =
						dialog.getWindow().getAttributes();
						params.width = wm.getDefaultDisplay().getWidth()*5/6;
						params.height = wm.getDefaultDisplay().getHeight()*4/11;
						dialog.getWindow().setAttributes(params);
				TextView dialog_msg = (TextView) layout.findViewById(R.id.remind_messagesId);
				TextView btnOK = (TextView) layout.findViewById(R.id.btn_yesId);
				btnOK.setOnClickListener(new OnClickListener()
				{
				    @Override
				    public void onClick(View v)
				    {
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
				    	User user=new User();
						user.userId=LocalStore.getUserInfo().userId;
						user.userName=LocalStore.getUserInfo().userName;
						user.PropertyID=propertyId;
						LocalStore.setUserInfo(NoPropertyActivity.this, user);
						
						Log.i("MyLog", "当前的小区idshi"+LocalStore.getUserInfo().PropertyID);
				       Intent intent=new Intent(NoPropertyActivity.this, RegisterActivity.class);
				       startActivity(intent);
				       finish();
				    }
				});*/

				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(NoPropertyActivity.this, LocationActivity.class);
				startActivity(intent);
		
			}
		});
	/*	et_FindNebohood.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String hint;
				if (arg1) {
					hint = et_FindNebohood.getHint().toString();
					et_FindNebohood.setTag(hint);
					et_FindNebohood.setHint("");
				} else {
					hint = et_FindNebohood.getTag().toString();
					et_FindNebohood.setHint(hint);
				}

			}
		});*/
	/*	et_FindNebohood.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// 获得搜索内容
				String et_InputContent = et_FindNebohood.getText().toString();
				if (Util.isEmpty(et_FindNebohood)) {
					Toast.makeText(getApplicationContext(), "请输入小区名称", Toast.LENGTH_SHORT).show();
					
				}
				flag=2;
				mByNameThread = new GetPropertyByNameListThread(NoPropertyActivity.this,
						mHandler, et_InputContent, 1);
				mByNameThread.start();
				return false;
			}
		});*/


	}

	/**
	 * 初始化布局控件
	 */
	private void initWidgets() {
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(NoPropertyActivity.this,LocationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
		topTxt = (TextView) findViewById(R.id.topbar_txt);
		topTxt.setText("小区管理");



		// next=(Button) findViewById(R.id.btn_Next);
		//et_FindNebohood = (EditText) findViewById(R.id.et_InputNeiborhoodNameId);
		notFound = (TextView) findViewById(R.id.NotFound);
	}
}
