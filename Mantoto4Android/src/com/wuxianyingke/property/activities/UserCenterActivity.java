package com.wuxianyingke.property.activities;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Property;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.umeng.message.proguard.L;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.UpdateManger;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.UpdateInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.views.StatusBarCompat;

public class UserCenterActivity extends BaseActivity {

	private LinearLayout mwodedingdanLinearLayout, mxinxiLinearLayout,dizhiguanliLinearLayout,
			mxiaofeiquanLinearLayout, setting,mdaijinquanLinearLayout,wanshanxinxiLinearLayout,
			mguanyuLinearLayout,mycollectionLinearLayout;
	private Button mBohaoLinearLayout;
	private Button mtuichudengluButton;
	private Button topbar_left;
	private TextView topbar_txt,userName,userPlot;
	private ImageView remindImg;
	private UpdateInfo updateInfo;
	private String desc;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constants.MSG_GET_INDEX_INFO_FINISH:
				case 5:
					Toast.makeText(getApplicationContext(), "请查看网络连接是否正常", Toast.LENGTH_SHORT)
							.show();
					break;
				case 9:
				/*if (LocalStore.getIsUpload(getApplicationContext())) {
					UpdateManger updateManger = new UpdateManger(UserCenterActivity.this, updateInfo.url, updateInfo.updateInfo, updateInfo.versionCode,updateInfo.appversion);
					updateManger.checkUpdate();
				}*/
					if (getVersionCode()<updateInfo.versionCode) {
						remindImg.setVisibility(View.VISIBLE);
					}
					break;
				case 10:
					Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT)
							.show();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_center);
		super.init(1);
		userPlot = (TextView) findViewById(R.id.user_plot);
		userName = (TextView) findViewById(R.id.user_name);
		initWidget();
		String username= LocalStore.getUserInfo().userName;
		userName.setText(username);





	}



	public void initWidget() {
		//底栏
		mBohaoLinearLayout = (Button) findViewById(R.id.BohaoLinearLayout);

		mBohaoLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {//拨号

				showPopwindow();

			}
		});
		//关于
		mguanyuLinearLayout = (LinearLayout) findViewById(R.id.guanyuLinearLayout);
		mguanyuLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UserCenterActivity.this,
						AboutActivity.class);
				startActivity(intent);
			}
		});
		//设置
		setting = (LinearLayout) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						|Intent.FLAG_ACTIVITY_CLEAR_TOP);

				intent.setClass(UserCenterActivity.this,
						SetActivity.class);
				startActivity(intent);
			}
		});

		//优惠券
		mxiaofeiquanLinearLayout = (LinearLayout) findViewById(R.id.xiaofeiquanLinearLayout);
		mxiaofeiquanLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|
						Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UserCenterActivity.this,
						CommitVoucherListActivity.class);
				startActivity(intent);
			}
		});
		//代金券
		mdaijinquanLinearLayout = (LinearLayout) findViewById(R.id.daijinquanLinearLayout);
		mdaijinquanLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UserCenterActivity.this,
						CouponActivity.class);
				startActivity(intent);
			}
		});

		//地址管理
		dizhiguanliLinearLayout = (LinearLayout) findViewById(R.id.dizhiguanliLinearLayout);
		dizhiguanliLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|
				Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UserCenterActivity.this,
						AddressActivity.class);
				startActivity(intent);
			}
		});

		//我的订单
		mwodedingdanLinearLayout = (LinearLayout) findViewById(R.id.wodedingdanLinearLayout);
		mwodedingdanLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|
						Intent.FLAG_ACTIVITY_CLEAR_TOP);
				/*intent.setClass(UserCenterActivity.this,
						CommitOrderListActivity.class);*/
				intent.setClass(UserCenterActivity.this,
						CommitOrderTestActivity.class);
				startActivity(intent);

			}
		});
		//我的收藏
		mycollectionLinearLayout = (LinearLayout) findViewById(R.id.mycollectionLinearLayout);
		mycollectionLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UserCenterActivity.this, CollectionListActivity.class);
				startActivity(intent);
			}
		});

		//完善信息
		//
		wanshanxinxiLinearLayout = (LinearLayout) findViewById(R.id.wanshanxinxiLinearLayout);
		wanshanxinxiLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(UserCenterActivity.this, SetMessageActivity.class);
				startActivity(intent);
			}
		});





		//标题

		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setBackgroundResource(R.drawable.arrow_left);
		topbar_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("我");
		topbar_txt.setTextColor(Color.parseColor("#ffffff"));



	}

	//方法:拨号弹出框Popwindow
	private void showPopwindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popupwindow,null);
		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		final PopupWindow window = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		window.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);
		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		// 在底部显示
		window.showAtLocation(UserCenterActivity.this.findViewById(R.id.BohaoLinearLayout),
				Gravity.BOTTOM,0,0);
		// 这里检验popWindow里的button是否可以点击
		Button callopen = (Button) view.findViewById(R.id.call_open);
		Button callcancle = (Button) view.findViewById(R.id.call_cancle);
		callopen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"拨号",Toast.LENGTH_SHORT);
				Uri uri = Uri.parse("tel:" + LocalStore.getUserInfo().phone);
				Log.i("MyLog", "MainActivity  tel="
						+ LocalStore.getUserInfo().phone + " number="
						+ LocalStore.getUserInfo().telnumber);
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
			}
		});
		callcancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"取消",Toast.LENGTH_SHORT);
				window.dismiss();

			}
		});
		//popWindow消失监听方法
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				Toast.makeText(getApplicationContext(),"消失",Toast.LENGTH_SHORT);
			}
		});

	}

	private int getVersionCode() {
		int versionCode = 0;
		try {
			//对应AndroidManifest.xml下android:versionCode
			versionCode = getPackageManager().getPackageInfo(
					"com.mantoto.property", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}





}