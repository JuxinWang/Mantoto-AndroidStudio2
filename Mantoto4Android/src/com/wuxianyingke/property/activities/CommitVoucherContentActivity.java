package com.wuxianyingke.property.activities;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.GetVoucherQCodeListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetPromotionCodeThread;

/**
 * 消费券详情
 */
public class CommitVoucherContentActivity extends BaseActivity {

	// 顶部导航
	private TextView topbar_txt;
	private Button topbar_left;
	private int mode; // 1:支付后的详情; 0:订单列表详情
	/**订单名称，描述，单价，订单号码，*/
	private TextView header, describe, price, promotionCodeFirst,
			promotionCodeSecond,orderCommitCodeId;
	private String orderId;
	private RemoteApi.PromotionCode mPromotionCode;
	/**顶面图片*/
	private ImageView image, DimisionCodeFirst, DimisionCodeSecond;
	private long ordersequencenumber;
	private GetPromotionCodeThread mThread;
	private ListView mListView;
	private String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mantoto";
	private GetVoucherQCodeListAdapter mAdapter;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
				mAdapter=new GetVoucherQCodeListAdapter(getApplicationContext(), mThread.mPromotionCode, orderId);
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				break;
			case Constants.MSG_NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接出错请刷新", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.order_commit_item_voucher2_content);
		// 初始化布局控件
		initView();
		setImmerseLayout(findViewById(R.id.common_back));
		Intent intent = getIntent();
		ordersequencenumber = intent.getLongExtra("ordersequencenumber", 0);
		mThread = new GetPromotionCodeThread(getApplicationContext(), mHandler,
				ordersequencenumber);
		mThread.start();

		/*GetOrderListAdapter
		intent.putExtra("ordersequencenumber",items.OrderSequenceNumber);

		*/
		String url = intent.getStringExtra("path");
		header.setText(intent.getStringExtra("header"));
		describe.setText(intent.getStringExtra("body"));
		price.setText("" + intent.getDoubleExtra("price", 0));
		orderId=intent.getStringExtra("OrderID");
		Log.i("MyLog", "orderId@#$="+orderId+""+url);
		mode = intent.getIntExtra("mode", 0);
//		promotionCodeFirst.setText(mList.get(1).Code);

		Bitmap bitmap = SDCardUtils.readImage(url); // 获取SDCard中的图片
		Log.i("MyLog", "Sd图片的urlwei ------>" + url);
		if (bitmap != null)
			image.setImageBitmap(bitmap);
		else {
			new ImageAsyncTask().execute(url);
		}

		
		// 左侧返回菜单处理事件
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(mode == 1)
				{
					Intent intent =new Intent();
					intent.setClass(CommitVoucherContentActivity.this, CanyinDetailOwnActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
					
				}else
				{
					finish();
				}
			}
		});
//		//生成二维码的方法
//		CreateImageCode();

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK&&mode==1) {
			Intent intent =new Intent();
			intent.setClass(CommitVoucherContentActivity.this, CanyinDetailOwnActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		}else{
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

//	private void CreateImageCode() {
//		final String qRcode="";
//		 //二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                boolean success = QRCodeUtils.createQRImage(qRcode, 800, 800,filePath);
//
//                if (success) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                        	DimisionCodeFirst.setImageBitmap(BitmapFactory.decodeFile(filePath));
//                        }
//                    });
//                }
//            }
//        }).start();

//	}

	private void initView() {
		header = (TextView) findViewById(R.id.tv_goodId);// 订单名称
		describe = (TextView) findViewById(R.id.tv_TotalPriceId);// 描述
		price = (TextView) findViewById(R.id.tv_PriceId);// 单价
	
		image = (ImageView) findViewById(R.id.image_NameId);
		mListView=(ListView) findViewById(R.id.quan_Ma_listViewId);
		
		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("消费券详情");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);

		orderCommitCodeId = (TextView) findViewById(R.id.order_Commit_CodeId);
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
