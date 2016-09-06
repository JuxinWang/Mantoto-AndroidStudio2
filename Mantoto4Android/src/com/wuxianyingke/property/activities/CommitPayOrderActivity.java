package com.wuxianyingke.property.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.mantoto.property.R;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.PushAgent;
import com.wuxianying.alipay.sdk.pay.PayResult;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class CommitPayOrderActivity extends BaseActivity {

	// 支付宝 初始化信息
	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++++++++++++++
	 */
	// 商户PID
	public static final String PARTNER = "";
	// 商户收款账号
	public static final String SELLER = "";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	private Context context;
//	private OrderItem order;
//	private OrderItem order = new OrderItem();
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {

					LocalStore.setSaveOrderSequenceNumber(
							CommitPayOrderActivity.this, OrderSequenceNumber);
					Toast.makeText(CommitPayOrderActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					String CTime = orders.Ctime;
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String date = sdf.format(new Date(Long.parseLong(CTime)));
					Intent intent = null;
					if (SaleTypeId == 1) {
						intent = new Intent(getApplicationContext(),
								CommitVoucherContentDetailsActivity.class);
						intent.putExtra("OrderID", orders.OrderID);
						intent.putExtra("Number", orders.Number);
						intent.putExtra("CTime", date);
						intent.putExtra("Total", orders.Total);
						intent.putExtra("TelNumber", orders.TelNumber);
						// 用于对实物和券码进行判断
						intent.putExtra("SaleTypeID",
								orders.ThePromotion.SaleTypeID);
					} else {
						intent = new Intent(getApplicationContext(),
								CommitVoucherContentActivity.class);

						intent.putExtra("ordersequencenumber",
								orders.OrderSequenceNumber);
					}
					intent.putExtra("OrderID", orders.OrderID);
					intent.putExtra("header", orders.ThePromotion.header);
					intent.putExtra("body", orders.ThePromotion.body);
					intent.putExtra("price", orders.ThePromotion.Price);
					intent.putExtra("path", orders.ThePromotion.path);
					intent.putExtra("mode", 1);

					startActivity(intent);
					finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(CommitPayOrderActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(CommitPayOrderActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				// Toast.makeText(CommitPayOrderActivity.this, "检查结果为：" +
				// msg.obj,
				// Toast.LENGTH_SHORT).show();
				break;
			}

			case 4:
				Toast.makeText(CommitPayOrderActivity.this, "订单创建失败："+mErrorInfo, Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		};
	};

	// 以上为支付宝初始化信息
	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * ++++++++++++++
	 */
	// private int mOrderItemID=0;
	// private OrderItem mOrderItem;
	/** 订单名称，订单数量，订单总价，订单地址 */
	private TextView name, number, totalPrice, address;
	/** 选择支付方式 */
	private ImageView select;
	/** 确认支付按钮 */
	private Button pay;
	/** 商品名，收货地址 */
	private String goodsName, goodsAddress;
	/** 标题（提交订单） */
	private TextView topbar_txt;
	/** 返回按钮 */
	private Button topbar_left;
	/** 跳转标志 */
	private int favorite_flag;
	/** 邮寄地址id */
	private long addressID;
	/** 优惠id */
	private long promotionID;
	/** 购买商品数量 */
	private int goodsNumber;
	/** 订单备注 */
	private String comment = "暂不支持备注！";
	/** 商品总价格 */
	private Double goodsTotalPrice;
	/** 阿里支付订单信息 */
	private String aliOrderStr;
	/** 通知对话 */
	private ProgressDialog mWaitLoading = null;
	/** 错误信息 */
	private String mErrorInfo = "";
	private ImageView check;
	/** 支付订单号 */
	private long OrderSequenceNumber;
	// private boolean isExist;
	private int SaleTypeId;
	private String header;
	private OrderItem orders = new OrderItem();
	private LinearLayout llOrderItems;
	private int Saletype;
	private String aName;
	private ImageView zhifubao, weixin;
	private LinearLayout zhifubaoPay, weixinPay;
	private String payType;
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_pay_entity);
		setImmerseLayout(findViewById(R.id.common_back));
		favorite_flag = getIntent().getIntExtra(Constants.FAVORITE_FLAT, 0);
		api = WXAPIFactory.createWXAPI(CommitPayOrderActivity.this,
				Constants.WEICHAT_APPID);
		// 获得订单数据
		Bundle bundle = getIntent().getExtras();
		goodsName = bundle.getString("name");
		goodsNumber = (int) bundle.getLong("number");
		goodsTotalPrice = bundle.getDouble("totlePrice");
		goodsAddress = bundle.getString("address");
		addressID = bundle.getLong("addressId");
		promotionID = bundle.getLong("promotionid");
		Saletype = bundle.getInt("SaleTypeId");
		aName = bundle.getString("Aname");
		// 初始化控件
		initView();
		// 初始化事件监听器
		initListener();

		// 创建订单并支付
//		crateOrderPay();
	}

	/*
	 * (非 Javadoc) <p>Title: onKeyDown</p> <p>Description: 返回键返回上一个界面</p>
	 * 
	 * @param keyCode 返回键
	 * 
	 * @param event 返回事件
	 * 
	 * @return
	 * 
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


	private void initListener() {
		// 左侧返回菜单处理事件
		topbar_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		weixinPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				payType = "wx";
				weixin.setBackgroundResource(R.drawable.ring);
				zhifubao.setBackgroundResource(R.drawable.ring_normal);
			}
		});

		zhifubaoPay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				payType = "ali";
				zhifubao.setBackgroundResource(R.drawable.ring);
				weixin.setBackgroundResource(R.drawable.ring_normal);
			}
		});
	}

	private void crateOrderPay() {

		User info = LocalStore.getUserInfo();
		/** 内部线程请求处理网络数据获得创建订单信息 */
		Thread createOrderThread = new Thread() {
			User info = LocalStore.getUserInfo();

			// Promotion promotion=LocalStore.getPromotion();
			// AddressItem address=LocalStore.getAddressId();
			@Override
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				Log.i("MyLog", "promotion======" + promotionID);
				Log.i("MyLog", "userId======" + info.userId);
				Log.i("MyLog", "addressid======" + addressID);
				Log.i("MyLog", "comment======" + comment);
				Log.i("MyLog", "countNum======" + goodsNumber);
				OrderItem order = remote.createOrder(CommitPayOrderActivity.this,
						promotionID, info.userId, addressID, comment,
						goodsNumber, payType, 0, 0, 0);
				Log.i("MyLog", "创建订单的参数为----");
				Message msg = new Message();
				if (order == null) {
					msg.what = 4;
					// }else if (200==order.netInfo.code) {
					// msg.what=3;
					// }else {
					// msg.what=2;
					// mErrorInfo=order.netInfo.desc;
				} else {
					msg.what = 3;
					aliOrderStr = order.AliOrderStr;
					OrderSequenceNumber = order.OrderSequenceNumber;
					SaleTypeId = order.ThePromotion.SaleTypeID;
					orders = order;
					header = orders.ThePromotion.header;
					// LocalStore.setSaveOrderSequenceNumber(getApplicationContext(),
					// 10);
					// long
					// s=LocalStore.getOrderSequenceNumber(CommitPayOrderActivity.this);
					// Log.i("MyLog",
					// "sssssss------------------**********************&&&&&&&>"+s);
					Log.i("MyLog", "***********aliOrderStr支付宝订单信息为=saletypeid="
							+ order.ThePromotion.SaleTypeID + "saletype:"
							+ SaleTypeId);
				}
				mHandler.sendMessage(msg);
			}
		};
		createOrderThread.start();
	}


	private void initView() {
		name = (TextView) findViewById(R.id.order_Commit_NameId);// 商品名称
		number = (TextView) findViewById(R.id.order_Commit_NumbersId);// 商品数量
		totalPrice = (TextView) findViewById(R.id.order_Commit_TotalPrice2Id);// 商品总价
		llOrderItems = (LinearLayout) findViewById(R.id.ll_order_entityId);// 收货地址
		zhifubao = (ImageView) findViewById(R.id.zhifubaoSelect);
		weixin = (ImageView) findViewById(R.id.weixinSelect);
		zhifubaoPay = (LinearLayout) findViewById(R.id.zhifubaoLinearLayout);
		weixinPay = (LinearLayout) findViewById(R.id.weixinLinearLayout);
		payType = "ali";
		zhifubao.setBackgroundResource(R.drawable.ring);
		weixin.setBackgroundResource(R.drawable.ring_normal);
		if (Saletype == 2) {
			llOrderItems.setVisibility(View.GONE);
			address = (TextView) findViewById(R.id.order_Commit_Address2Id);// 地址
		} else {
			llOrderItems.setVisibility(View.VISIBLE);
			address = (TextView) findViewById(R.id.order_Commit_Address2Id);// 地址
		}

		// 设置订单数据
		name.setText(goodsName);
		number.setText("" + goodsNumber);
		totalPrice.setText("" + goodsTotalPrice);

		address.setText(goodsAddress + "," + aName + "收。");

		select = (ImageView) findViewById(R.id.order_Commit_PaySelectId);// 选择支付方式
		pay = (Button) findViewById(R.id.order_Commit2Id);
		check = (ImageView) findViewById(R.id.order_Commit_PaySelect2Id);

		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("支付订单");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
	}

	/**
	 * 以下为支付宝调用的各种方法++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * +++++++++++++++++
	 */

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View v) {
		LocalStore.setPayFlag(CommitPayOrderActivity.this, 1);
		mWaitLoading = ProgressDialog.show(CommitPayOrderActivity.this, null, "获取订单中......",
                 true);
		User info = LocalStore.getUserInfo();
		/** 内部线程请求处理网络数据获得创建订单信息 */
		Thread createOrderThread = new Thread() {
			User info = LocalStore.getUserInfo();

			// Promotion promotion=LocalStore.getPromotion();
			// AddressItem address=LocalStore.getAddressId();
			@Override
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				Log.i("MyLog", "promotion======" + promotionID);
				Log.i("MyLog", "userId======" + info.userId);
				Log.i("MyLog", "addressid======" + addressID);
				Log.i("MyLog", "comment======" + comment);
				Log.i("MyLog", "countNum======" + goodsNumber);
				OrderItem order = remote.createOrder(CommitPayOrderActivity.this,
						promotionID, info.userId, addressID, comment,
						goodsNumber, payType, 0, 0, 0);
				Log.i("MyLog", "创建订单的参数为----");
				Log.i("MyLog", "order="+order);
				Message msg = new Message();
				if (order.netInfo.code!= 200) {
					mErrorInfo=order.netInfo.desc;
					CommitPayOrderActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        	mWaitLoading.dismiss();
                        }
                    });
					msg.what = 4;
					// }else if (200==order.netInfo.code) {
					// msg.what=3;
					// }else {
					// msg.what=2;
					// mErrorInfo=order.netInfo.desc;
				} else {
					CommitPayOrderActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        	mWaitLoading.dismiss();
                        }
                    });
					msg.what = 3;
					aliOrderStr = order.AliOrderStr;
					OrderSequenceNumber = order.OrderSequenceNumber;
					SaleTypeId = order.ThePromotion.SaleTypeID;
					orders = order;
					header = orders.ThePromotion.header;
					
					final String payInfo = aliOrderStr;
					if (payType.equals("wx")) {
						Log.i("MyLog", "LocalStore.setPayFlag(CommitPayOrderActivity.this, 1)="+LocalStore.getPayFlag(CommitPayOrderActivity.this));
						PayReq req = new PayReq();
						req.appId = order.WxPayInfo.Appid;
						req.partnerId = order.WxPayInfo.PartnerId;
						req.prepayId = order.WxPayInfo.PrepayId;
						req.nonceStr = order.WxPayInfo.NonceStr;
						req.timeStamp = order.WxPayInfo.TimeStamp;
						req.packageValue = order.WxPayInfo.Package;
						req.sign = order.WxPayInfo.Sign;

						Log.i("MyLog", "req.appId2=" + req.appId + "req.partnerId="
								+ req.partnerId + "req.prepayId=" + req.prepayId
								+ "req.nonceStr=" + req.nonceStr + "req.timeStamp="
								+ req.timeStamp + "req.packageValue=" + req.packageValue
								+ "req.sign=" + req.sign);
						// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
						api.sendReq(req);
						finish();
					} else {
						Runnable payRunnable = new Runnable() {

							@Override
							public void run() {
								// 构造PayTask 对象
								PayTask alipay = new PayTask(CommitPayOrderActivity.this);
								// 调用支付接口，获取支付结果
								// String result = alipay.pay(payInfo);
								String result = alipay.pay(payInfo, true);

								Message msg = new Message();
								msg.what = SDK_PAY_FLAG;
								msg.obj = result;
								mHandler.sendMessage(msg);
							}
						};

						// 必须异步调用
						Thread payThread = new Thread(payRunnable);
						payThread.start();
					}
					// LocalStore.setSaveOrderSequenceNumber(getApplicationContext(),
					// 10);
					// long
					// s=LocalStore.getOrderSequenceNumber(CommitPayOrderActivity.this);
					// Log.i("MyLog",
					// "sssssss------------------**********************&&&&&&&>"+s);
					Log.i("MyLog", "***********aliOrderStr支付宝订单信息为=saletypeid="
							+ order.ThePromotion.SaleTypeID + "saletype:"
							+ SaleTypeId);
				}
				mHandler.sendMessage(msg);
			}
		};
		createOrderThread.start();
		// check(check);
		// if (isExist) {
		
		// }else{
		// Toast.makeText(getApplicationContext(), "将要跳转到网页支付", 1).show();
		// Intent intent = new Intent(CommitPayOrderActivity.this,
		// WebPayActivity.class);
		// intent.putExtra("aliOrderStr", aliOrderStr);
		// startActivity(intent);
		//
		// }

	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(CommitPayOrderActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息 商品名称subject 商品数量 count 商品总价 price 购物者payer
	 * 联系人电话 phone 收件人地址address
	 */
	// public String getOrderInfo(String subject, int count, Double price,String
	// address) {
	// // 签约合作者身份ID
	// String orderInfo = "partner=" + "\"" + PARTNER + "\"";
	//
	// // 签约卖家支付宝账号
	// orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
	//
	// // 商户网站唯一订单号
	// orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
	//
	// // 商品名称
	// orderInfo += "&subject=" + "\"" + subject + "\"";
	//
	// // 商品数量
	// orderInfo += "&count=" + "\"" + count + "\"";
	//
	// // 商品总价
	// orderInfo += "&total_fee=" + "\"" + price + "\"";
	//
	// // 收件人地址
	// orderInfo += "&address=" + "\"" + address + "\"";
	//
	// // 服务器异步通知页面路径
	// orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
	// + "\"";
	//
	// // 服务接口名称， 固定值
	// orderInfo += "&service=\"mobile.securitypay.pay\"";
	//
	// // 支付类型， 固定值
	// orderInfo += "&payment_type=\"1\"";
	//
	// // 参数编码， 固定值
	// orderInfo += "&_input_charset=\"utf-8\"";
	//
	// // 设置未付款交易的超时时间
	// // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
	// // 取值范围：1m～15d。
	// // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
	// // 该参数数值不接受小数点，如1.5h，可转换为90m。
	// orderInfo += "&it_b_pay=\"30m\"";
	//
	// // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
	// // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
	//
	// // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
	// orderInfo += "&return_url=\"m.alipay.com\"";
	//
	// // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
	// // orderInfo += "&paymethod=\"expressGateway\"";
	//
	// return orderInfo;
	// }

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	// public String getOutTradeNo() {
	// SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
	// Locale.getDefault());
	// Date date = new Date();
	// String key = format.format(date);
	// Random r = new Random();
	// key = key + r.nextInt();
	// key = key.substring(0, 15);
	// return key;
	// }


	// public String sign(String content) {
	// return SignUtils.sign(content, RSA_PRIVATE);
	// }

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	// public String getSignType() {
	// return "sign_type=\"RSA\"";
	// }

}
