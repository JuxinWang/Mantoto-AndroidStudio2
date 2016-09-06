package com.wuxianyingke.property.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.AddressInfo;
import com.wuxianyingke.property.remote.RemoteApi.CreateAddress;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.GetAddressListThread;

public class AddressEditActivity extends BaseActivity {

	private GetAddressListThread mThread = null;
	private long AddressId;
	//点击列表里地址数据进行编辑
	/*private LocalStore localStore;
	private String recipientName = null;
	private  String recipientTel = null;
	private  String recipientAddressDetail = null;
	*/
	// 顶部导航
	private TextView topbar_txt,topbar_right;
	private Button topbar_left;
	private int favorite_flag;
	// 输入姓名 号码 地区 详细地址
	private EditText et_name, et_phoneNumber, et_area, et_address;

	private int[] provincesIDs = new int[3];
	// 是否可以直接返回
	private boolean isCanBack = true;
	private boolean isDefault = true;

	protected Activity mContext;
	public int userId;
	//
	//private AddressInfo addressInfo;
	// private List<AreaInfo>list;
	// private AddressItem mAddress;

	String address, name, area, TelPhone;
	private ProgressDialog mWaitLoading = null;
	private String mErrorInfo = "";
	private String desc = "";
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mWaitLoading != null) {
				mWaitLoading.dismiss();
				mWaitLoading = null;
			}
			switch (msg.what) {
			// 注册失败
			case 2:
				Toast.makeText(AddressEditActivity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;
			// 注册成功
			case 3:
				LocalStore.setAddressId(AddressEditActivity.this, AddressId);
				Toast.makeText(getApplicationContext(),
						"保存地址成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(AddressEditActivity.this,
						AddressActivity.class);
				intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("userid", userId);
				startActivity(intent);
				 finish();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(AddressEditActivity.this, "通信错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(AddressEditActivity.this, desc,
						Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(AddressEditActivity.this, "网络超时，请重新获取",
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
		//PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.user_edit_address);
		//addressInfo = new AddressInfo();//没用着
		/*localStore = new LocalStore();
		recipientName = getIntent().getStringExtra("name");
		recipientTel = getIntent().getStringExtra("telnumber");
		recipientAddressDetail = getIntent().getStringExtra("address");*/
		// 初始化控件
		initView();
		//setResult(0,getIntent());

		setImmerseLayout(findViewById(R.id.common_back));
		// 初始化事件监听器
		initListener();
		// 加载地址
		// onLoadDatas();
	}


	// 标题中左侧返回按钮 右侧保存按钮 处理事件
	private void initListener() {

		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (0 != favorite_flag) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getApplicationContext(),
							AddressActivity.class);
					startActivity(intent);
				} else {
					finish();
				}
			}
		});

		// 保存即原提交按钮
		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(checkInput()){
				address = et_address.getText().toString();
				name = et_name.getText().toString();
				area = et_area.getText().toString();
				TelPhone = et_phoneNumber.getText().toString();

				Thread createAddressThread = new Thread() {
					User info = LocalStore.getUserInfo();

					@Override
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						CreateAddress cAddress = remote.createNewAddress(
								AddressEditActivity.this, info.userId, name,
								TelPhone, area, address, isDefault);
					    Message msg = new Message();
						if (cAddress == null) {
							msg.what = 4;

						} else {
							msg.what = 3;
							userId = (int) cAddress.UserID;
							AddressId=cAddress.AddressID;
							LocalStore.setAddressId(AddressEditActivity.this, AddressId);
							Log.i("MyLog", "服务端传回的userid为=====" +"userid="+ userId+"addressid="+AddressId);
//							mErrorInfo = info.netInfo.desc;
						}
						mHandler.sendMessage(msg);
					};
				
				};

				createAddressThread.start();
				topbar_right.setEnabled(false);//设置右侧按钮不可用

				}	
		}
		});

	}

	// 保存地址
	// private void saveAddress() {
	// LivingDB db= new LivingDB(AddressEditActivity.this);
	// }

	//没用着---编辑过程中按返回键
	public boolean handleOptions() {

		if (!isCanBack
				|| (!TextUtils.isEmpty(et_name.getText().toString())
						|| !TextUtils.isEmpty(et_area.getText().toString())
						|| !TextUtils.isEmpty(et_phoneNumber.getText()
								.toString()) || !TextUtils.isEmpty(et_address
						.getText().toString()))) {
			showChooseDialog();
			return true;
		}
		return false;
	}

	//没用着---编辑过程中按返回键弹出的对话框
	private void showChooseDialog() {
		new AlertDialog.Builder(getApplicationContext()).setTitle("确认")
				.setMessage("是否放弃编辑？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						isCanBack = true;
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	// 没用着--对录入的数据进行实时监控
	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;

		// private int editStart;
		// private int editEnd;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			temp = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// mTextView.setText(s);//将输入的内容实时显示
		}

		@Override
		public void afterTextChanged(Editable s) {
			// editStart = address.getSelectionStart();
			// editEnd = address.getSelectionEnd();
			et_phoneNumber.setText(String.valueOf(temp.length()));
			if (temp.length() > 50) {
				Toast.makeText(getApplicationContext(), "您输入的手机号码不正确---", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	//判断输入框内容是否为空
	private boolean checkInput() {
		if (TextUtils.isEmpty(et_name.getText().toString())) {
			Toast.makeText(getApplicationContext(), "请输入联系人---", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(et_phoneNumber.getText().toString())) {
			Toast.makeText(getApplicationContext(), "请输入手机号---", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			// 验证手机号是否合法
			if (!isMobileNO(et_phoneNumber.getText().toString())) {
				Toast.makeText(getApplicationContext(), "请输入正确的手机号---", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		}
		if (TextUtils.isEmpty(et_area.getText().toString())) {
			Toast.makeText(getApplicationContext(), "请输入地区---", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(et_address.getText().toString())) {
			// showToast("请输入详细地址");
			Toast.makeText(getApplicationContext(), "请输入详细地址----", Toast.LENGTH_SHORT).show();
			return false;
		}

		// if (TextUtils.isEmpty(code.getText().toString())) {
		// showToast("请输入邮政编码");
		// return false;
		// }
		return true;
	}

	// 判断输入的手机号格式是否正确
	public static boolean isMobileNO(String mobiles) {
		// Pattern p = Pattern.compile("^\\d{11}$");
		Pattern p = Pattern
				.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}


	//未编辑完成 按返回键弹出一个对话框
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showBackDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    //弹出的对话框
	private void showBackDialog() {
		if (TextUtils.isEmpty(et_name.getText().toString().trim())
				&& TextUtils.isEmpty(et_phoneNumber.getText().toString().trim())
				&& TextUtils.isEmpty(et_area.getText().toString().trim())
				&& TextUtils.isEmpty(et_address.getText().toString().trim())) {
			AddressEditActivity.this.finish();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder
					.setMessage("是否放弃编辑")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									AddressEditActivity.this.finish();
									dialog.dismiss();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).setCancelable(true).show();
		}
	}

	// 初始化 顶部导航 地址信息
	private void initView() {

		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("添加地址");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setText("保存");
		topbar_right.setTextColor(Color.rgb(255,165,0));
		et_name = (EditText) findViewById(R.id.user_Address_nameId);
		et_phoneNumber = (EditText) findViewById(R.id.user_Address_PhoneNumberId);
		et_area = (EditText) findViewById(R.id.user_Address_arerId);
		et_address = (EditText) findViewById(R.id.user_Address_AddressId);

	}

}
