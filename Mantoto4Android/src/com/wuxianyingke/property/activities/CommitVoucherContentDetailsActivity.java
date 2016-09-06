package com.wuxianyingke.property.activities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

/**
 * 订单详情
 */
public class CommitVoucherContentDetailsActivity extends BaseActivity {
	/** 订单名称，描述，单价，订单号码，使用数量，下单时间，订单金额，手机号码 */
	private TextView header, describe, price, orderNumber, UseNumber,
			payOrderTime, total, phone, textPhone, refund,cauponstyle;
	/** 顶面图片 */
	private ImageView image;
	// 顶部导航
	private TextView topbar_txt;
	private Button topbar_left;
	private int favorite_flag;
	private int mode;
	private long ordersequencenumber;
	private NetInfo netInfo;
	private String desc;
	private int flag;// flag=1,表示已完成订单 flag=2，表示未完成订单
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 6:
				Toast.makeText(CommitVoucherContentDetailsActivity.this,
						"申请成功", Toast.LENGTH_SHORT).show();
				break;
			case 7:
				Toast.makeText(CommitVoucherContentDetailsActivity.this, desc,
						Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(CommitVoucherContentDetailsActivity.this,
						"确认收货成功", Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(CommitVoucherContentDetailsActivity.this, desc,
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_item_voucher_content);

		// 初始化布局控件
		InitViews();
		setImmerseLayout(findViewById(R.id.common_back));
		Intent intent = getIntent();
		int SaleTypeID = intent.getIntExtra("SaleTypeID", 2);
		int OrderStatusID = intent.getIntExtra("OrderStatusID", 0);
		mode = intent.getIntExtra("mode", 0);
		flag = intent.getIntExtra("flag", 0);
		ordersequencenumber = intent.getLongExtra("ordersequencenumber", -1);
		// 对商品进行判断
//		if (SaleTypeID == 1) {
//			phone.setVisibility(View.VISIBLE);
//			textPhone.setVisibility(View.VISIBLE);
//			refund.setVisibility(View.VISIBLE);
//		}

		String url = intent.getStringExtra("path");
		header.setText(intent.getStringExtra("header"));
		describe.setText(intent.getStringExtra("body"));
		price.setText("￥：" + intent.getDoubleExtra("price", 0));
		orderNumber.setText(intent.getStringExtra("OrderID"));
		UseNumber.setText("" + intent.getIntExtra("Number", 0));
		payOrderTime.setText(intent.getStringExtra("CTime"));
		total.setText("￥：" + intent.getDoubleExtra("Total", 0));
		phone.setText(intent.getStringExtra("TelNumber"));

		// //接收详情数据
		// Bundle bundle=getIntent().getExtras();
		//
		// header.setText(bundle.getString("header"));
		// describe.setText(bundle.getString("body"));
		// price.setText(""+bundle.getDouble("Price"));
		// orderNumber.setText(bundle.getString("OrderID"));
		// UseNumber.setText(bundle.getInt("Number"));
		// payOrderTime.setText(bundle.getString("CTime"));
		// total.setText(""+bundle.getDouble("Total"));
		// phone.setText(bundle.getString("TelNumber"));
		//

		Bitmap bitmap = SDCardUtils.readImage(url); // 获取SDCard中的图片
		if (bitmap != null)
			image.setImageBitmap(bitmap);
		else {
			new ImageAsyncTask().execute(url);
		}

		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mode == 1) {
					Intent intent = new Intent();
					intent.setClass(CommitVoucherContentDetailsActivity.this,
							CanyinDetailOwnActivity.class);
					intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});
		if (SaleTypeID == 1 && OrderStatusID == 6&&flag==2) {
			refund.setText("确认收货");
			phone.setVisibility(View.VISIBLE);
			textPhone.setVisibility(View.VISIBLE);
			refund.setVisibility(View.VISIBLE);
			refund.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Thread refundThread = new Thread() {
						@Override
						public void run() {
							RemoteApiImpl rai = new RemoteApiImpl();
							netInfo = rai.userConfirmGoods(
									CommitVoucherContentDetailsActivity.this,
									ordersequencenumber);
							Message msg = new Message();
							if (netInfo.code == 200) {
								msg.what = 8;
								mHandler.sendMessage(msg);
							} else {
								msg.what = 9;
								desc = netInfo.desc;
								mHandler.sendMessage(msg);
							}
						}
					};
					refundThread.start();
				}
			});

		} else if (SaleTypeID == 1 && OrderStatusID == 2&&flag==2) {
			phone.setVisibility(View.VISIBLE);
			textPhone.setVisibility(View.VISIBLE);
			refund.setVisibility(View.VISIBLE);
			refund.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Thread refundThread = new Thread() {
						@Override
						public void run() {
							RemoteApiImpl rai = new RemoteApiImpl();
							netInfo = rai.userReturnApply(
									CommitVoucherContentDetailsActivity.this,
									ordersequencenumber);
							Message msg = new Message();
							if (netInfo.code == 200) {
								msg.what = 6;
								mHandler.sendMessage(msg);
							} else {
								msg.what = 7;
								desc = netInfo.desc;
								mHandler.sendMessage(msg);
							}
						}
					};
					refundThread.start();
				}
			});
		} else if (SaleTypeID == 1 && OrderStatusID == 6&&flag==2) {
			phone.setVisibility(View.VISIBLE);
			textPhone.setVisibility(View.VISIBLE);
			refund.setVisibility(View.VISIBLE);
			refund.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Thread refundThread = new Thread() {
						@Override
						public void run() {
							RemoteApiImpl rai = new RemoteApiImpl();
							netInfo = rai.userReturnApply(
									CommitVoucherContentDetailsActivity.this,
									ordersequencenumber);
							Message msg = new Message();
							if (netInfo.code == 200) {
								msg.what = 6;
								mHandler.sendMessage(msg);
							} else {
								msg.what = 7;
								desc = netInfo.desc;
								mHandler.sendMessage(msg);
							}
						}
					};
					refundThread.start();
				}
			});
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mode == 1) {
			Intent intent = new Intent();
			intent.setClass(CommitVoucherContentDetailsActivity.this,
					CanyinDetailOwnActivity.class);
			// intent.setClass(CommitVoucherContentDetailsActivity.this,
			// CanYinListActivity.class);
			// Intent intent = new
			// Intent(CommitVoucherContentDetailsActivity.this,
			// CanYinListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		} else {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void InitViews() {
		header = (TextView) findViewById(R.id.tv_goodId);// 订单名称
		describe = (TextView) findViewById(R.id.tv_TotalPriceId);// 描述
		price = (TextView) findViewById(R.id.tv_PriceId);// 单价
		orderNumber = (TextView) findViewById(R.id.order_Commit_PriceId);// 订单编号
		UseNumber = (TextView) findViewById(R.id.order_Commit_NumberId);// 使用数量
		payOrderTime = (TextView) findViewById(R.id.order_Commit_TimeId);// 下单时间
		total = (TextView) findViewById(R.id.order_Commit_TotalPriceId);// 订单金额
		phone = (TextView) findViewById(R.id.order_Commit_PhoneNumberId);// 手机号码
		refund = (TextView) findViewById(R.id.tv_refund);
		textPhone = (TextView) findViewById(R.id.order_Commit_phoneId);

		image = (ImageView) findViewById(R.id.image_NameId);// 图片

		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("订单详情");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
	}

	class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(params[0]);
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == 200) {

					byte[] bytes = EntityUtils
							.toByteArray(response.getEntity());

					SDCardUtils.saveImage(params[0], bytes);

					return BitmapFactory
							.decodeByteArray(bytes, 0, bytes.length);
				}

			} catch (Exception e) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				image.setImageBitmap(result);
			} else {
				image.setImageResource(R.drawable.code_logo);
			}
		}
	}

}
