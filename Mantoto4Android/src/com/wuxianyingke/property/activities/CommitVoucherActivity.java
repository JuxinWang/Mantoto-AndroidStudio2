package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.remote.RemoteApi.AddressInfo;
import com.wuxianyingke.property.remote.RemoteApi.User;

public class CommitVoucherActivity extends BaseActivity {
	/**请求码*/
	private static final int REQUEST_CODE = 0;
	/**返回码*/
	private static final int Result_CODE = 1;
	/**商品名称、价格、数量、总价、地址*/
	private TextView goodsName, tvPrice, count, totalPrice, address;
	/**增加减少商品数量*/
	private ImageView minusImg, addImg, rightImg;
	/**联系人，手机号，联系人地址，选择联系人*/
	private TextView linkMan, phoneNumber, linkedManAddress, selectLinkman;
	/**需要隐藏的布局*/
	private LinearLayout llNameAndPhone, llAddress;
	/**提交订单*/
	private Button commitImg;
	/**传递过来的商品名称*/
	private String name;
	/**传递过来的商品价格*/
	private double price;
	/**提交订单标题*/
	private TextView topbar_txt;
	/**左侧返回按钮*/
	private Button topbar_left;
	/**是否收藏标志*/
	private int favorite_flag;
	/**商品数量*/
	private int countNum = 1;
	/**商品总价*/
	private double totalPrices;
	/**地址信息*/
	private AddressInfo addressInfo;
	/**通知对话*/
	private ProgressDialog mWaitLoading = null;
	/**错误信息*/
	private String mErrorInfo = "";
	/**得到数据请求信息*/
	private String desc = "";
	// promotionid long 优惠id
	// userid long 用户id
	// addressid long 邮寄地址id
	// comment string 备注
	// number int 购买商品数量
	/**优惠id*/
	private long promotionid;// 优惠id
	/**备注信息*/
	private String comment = "暂不支持备注！";// 备注
	/**购买的数量*/
	private int number;// 购买数量
	/**支付宝签名信息*/
	private long AliOrderStr;// zhifu信息
	/**地址id*/
	private long addressid;
	/**从共享参数中取出用户id*/
	private User info =null;
	/**handle信息处理*/
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
				Toast.makeText(CommitVoucherActivity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;
			// 注册成功
			case 3:
				// RemoteApi.LoginInfo logininfo = new RemoteApi.LoginInfo();
				// logininfo.U_ID=U_ID;
				// logininfo.U_PASS=U_PASS;
				// logininfo.autoLogin = false;
				// LocalStore.setLoginInfo(CommitOrderActivity.this,logininfo )
				// LocalStore.setUserStatus(CommitOrderActivity.this, true);
				// Toast.makeText(CommitOrderActivity.this, "注册成功啦~",
				// Toast.LENGTH_SHORT).show();
				// Intent intent = new Intent();
				// intent.setClass(CommitOrderActivity.this,
				// CommitPayOrderActivity.class);
				//
				// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
//				User info = LocalStore.getUserInfo();
//				Toast.makeText(
//						getApplicationContext(),
//						"恭喜订单提交成功--" +"promotionid="+promotionid + "addressid=" + addressid
//								+ "comment=" + comment + "number=" + countNum+comment+"userid"+info.userId, 1)
//						.show();
			
				// finish();
				break;
			// 通讯错误
			case 4:
				Toast.makeText(CommitVoucherActivity.this, "通信错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(CommitVoucherActivity.this, desc,
						Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(CommitVoucherActivity.this, "网络超时，请重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/* (非 Javadoc) 
	* <p>Title: onKeyDown</p> 
	* <p>Description: 返回键返回上一个界面</p> 
	* @param keyCode  返回键
	* @param event	  返回事件
	* @return 
	* @see android.app.Activity#onKeyDown(int, android.view.KeyEvent) 
	*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_voucher);
		setImmerseLayout(findViewById(R.id.common_back));
//		//取出数据
//		info = LocalStore.getUserInfo();
//		//获得购物界面传递过来的数据
//		Bundle bundle = getIntent().getExtras();
//		name = bundle.getString("name");
//		price = bundle.getDouble("price");
//		promotionid = bundle.getLong("promotionid");
//		Log.i("MyLog", "commitorderActivity=====" + "name=" + name + "price="
//				+ price + "promotionid=" + promotionid);
//		/**初始化控件*/
//		initView();
//		// 初始化事件监听器
//		initListener();
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
							CanyinDetailOwnActivity.class);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});

		// 点击减少设置商品数量
		minusImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (countNum < 1) {
					Toast.makeText(getApplicationContext(), "商品数量不能为负数----", Toast.LENGTH_SHORT)
							.show();
				} else {
					count.setText("" + --countNum);
					totalPrices = countNum * price;
					totalPrice.setText("" + totalPrices + "元");
				}
			}
		});

		// 点击增加商品的数量
		addImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				countNum++;
				Toast.makeText(getApplicationContext(),
						"商品数量为——————" + countNum, Toast.LENGTH_SHORT).show();
				count.setText("" + countNum);
				totalPrices = countNum * price;
				totalPrice.setText("" + totalPrices + "元");
			}
		});

		// 点击进入地址选择界面
		rightImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "你将要跳转到地址选择栏---", Toast.LENGTH_SHORT)
						.show();
				// selectLinkman.setVisibility(View.GONE);
				// llAddress.setVisibility(View.VISIBLE);
				// llNameAndPhone.setVisibility(View.VISIBLE);
				//
				// mWaitLoading = ProgressDialog.show(CommitOrderActivity.this,
				// null, "注册中，请稍候......", true);
				Intent intent2 = new Intent(CommitVoucherActivity.this,
						AddressActivity.class);

				int requestCode = REQUEST_CODE;
				startActivityForResult(intent2, requestCode);

				// startActivity(intent2);
			}
		});
		// 点击提交订单
commitImg.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
		//将要创建的订单所需要的参数传递过去
		Intent intent = new Intent(CommitVoucherActivity.this,
				CommitPayOrderActivity.class);
		Bundle bundle = new Bundle();
		//订单数据
		bundle.putString("name", name);
		bundle.putLong("number", countNum);
		bundle.putDouble("totlePrice", countNum * price);
		bundle.putString("address", address.getText().toString());
		//优惠id,地址id
		bundle.putLong("promotionid", promotionid);
		bundle.putLong("addressId", addressid);
		intent.putExtras(bundle);
		int requestCode = REQUEST_CODE;
		startActivityForResult(intent, requestCode);
		Log.v("msg", "-------------" + bundle);
		
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == Result_CODE) {
			selectLinkman.setVisibility(View.GONE);
			llAddress.setVisibility(View.VISIBLE);
			llNameAndPhone.setVisibility(View.VISIBLE);
			// int indexs=bundle.getInt("indexs");
			// int indexs=getIntent().getIntExtra("index", 0);
			// addressInfo = ListUtils.address.get(indexs);
			String aName = data.getStringExtra("aname");
			String aPhone = data.getStringExtra("aphone");
			String aAddress = data.getStringExtra("aaddress");
			addressid = data.getLongExtra("addressid", 0);
			// 联系人
			linkMan = (TextView) findViewById(R.id.order_Commit_LinkmanId);
			linkMan.setText(aName);
			Log.i("MyLog",
					"bundle.getString(name)------------="
							+ data.getStringExtra("aname"));
			phoneNumber = (TextView) findViewById(R.id.order_Commit_PhoneNumberId);
			phoneNumber.setText(aPhone);
			linkedManAddress = (TextView) findViewById(R.id.order_Commit_AddressId);
			linkedManAddress.setText(aAddress);

		}
	}

	private void initView() {
		goodsName = (TextView) findViewById(R.id.order_Commit_NameId);// 商品名
		tvPrice = (TextView) findViewById(R.id.order_Commit_PriceId);// 商品价格
		count = (TextView) findViewById(R.id.order_Commit_NumberId);// 商品数量
		totalPrice = (TextView) findViewById(R.id.order_Commit_TotalPriceId);// 商品的总价
		minusImg = (ImageView) findViewById(R.id.order_Commit_MinusId);// 减少商品数量
		addImg = (ImageView) findViewById(R.id.order_Commit_AddId);// 增加商品数量
		rightImg = (ImageView) findViewById(R.id.order_Commit_RightId);// 地址栏
		commitImg = (Button) findViewById(R.id.order_CommitId);// 提交订单按钮
		address = (TextView) findViewById(R.id.order_Commit_AddressId);// 选择地址
		selectLinkman = (TextView) findViewById(R.id.order_Commit_Select_LinkmanId);// 请选择联系人
		// LinearLayout
		llNameAndPhone = (LinearLayout) findViewById(R.id.order_Commit_LL_NameandPhone);
		llAddress = (LinearLayout) findViewById(R.id.order_Commit_LL_Address);

		count.setText("" + countNum);
		goodsName.setText(name);
		tvPrice.setText("" + price);
		totalPrice.setText("" + price);

		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("提交订单");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);

	}

}
