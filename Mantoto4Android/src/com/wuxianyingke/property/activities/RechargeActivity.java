package com.wuxianyingke.property.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;

public class RechargeActivity extends BaseActivity implements OnClickListener{
	private Button topbar_left;
	private TextView topbar_text;
	private int responseCode;
	private Button tenBtn,twentyBtn,fiftyBtn,otherBtn;
	private LinearLayout rechargeMoneyLinearLayout;
	private Button rechargeConfirmLinearLayout;
	private TextView rechargeMoneyTextView;
	private Button payButton;
	private double payMoney;
	private EditText PhoneEditText;
	private String[] results;
	private TextView rechargeWifiTime;//上网时长
	private TextView rechargeMoney;//账号金额
	private ProgressDialog mDialog;
	private Button rechargeCash,rechargeCoupon;
	private Runnable runnable2;
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
				mDialog.dismiss();
				String leftTime;
				String leftMoney;
				String result=msg.getData().getString("value");
				try {
					JSONObject object = new JSONObject(result);
					JSONArray array = object.getJSONArray("list");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						leftTime = obj.getString("lefttime");
						leftMoney = obj.getString("leftmoney");
						Log.i("MyLog", "Money = "+Double.valueOf(leftMoney)+" ___"+Double.valueOf(leftTime) + "value of = "+(Double.valueOf(leftMoney)*100+Double.valueOf(leftTime)));
						rechargeWifiTime.setText(""+(int)(Double.valueOf(leftMoney)*100+Double.valueOf(leftTime))/60+"小时"+String.format("%.0f", (Double.valueOf(leftMoney)*100+Double.valueOf(leftTime))%60)+"分");
						rechargeMoney.setText(leftMoney+"元");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				/*results = result.split("\t");//根据"/t"区分
				rechargeMoney.setText(results[4]+"元");
				String times = results[4];
				rechargeWifiTime.setText(""+Long.parseLong(times.replace(".", ""))/60+"小时"+Long.parseLong(times.replace(".", ""))%60+"分钟");
				Log.i("MyLog", "results=="+results[0]);
				Log.i("MyLog", "results="+results[0]+results[1]+results[2]+results[3]+results[4]+results[5]+results[6]+results[7]+results[8]);*/
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		setImmerseLayout(findViewById(R.id.common_back));
		mDialog = new ProgressDialog(RechargeActivity.this);
		mDialog.setMessage("加载中......");
		mDialog.setCanceledOnTouchOutside(true);
		 /* mDialog = ProgressDialog.show(BalenceActivity.this, null, "加载中......",
                  true);*/
		mDialog.show();
		tenBtn = (Button) findViewById(R.id.rechargeTenYuanButton);
		twentyBtn = (Button) findViewById(R.id.rechargetwentyYuanButton);
		fiftyBtn = (Button) findViewById(R.id.rechargefiftyButton);
		otherBtn = (Button) findViewById(R.id.rechargeotherYuanButton);
		rechargeMoneyLinearLayout = (LinearLayout) findViewById(R.id.rechargeMoneyLinearLayout);
		payButton = (Button) findViewById(R.id.payButton);
		rechargeMoneyTextView = (TextView) findViewById(R.id.rechargeCountTextView);
		PhoneEditText = (EditText) findViewById(R.id.PhoneEditText);
		rechargeMoneyTextView.setText(LocalStore.getFreeWifi().WIFIAccount);
		rechargeWifiTime = (TextView) findViewById(R.id.rechargeWifiTime);
		rechargeMoney = (TextView) findViewById(R.id.rechargeMoneyTextView);
		rechargeCoupon = (Button) findViewById(R.id.rechargeCouponPay);
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_text = (TextView) findViewById(R.id.topbar_txt);
		topbar_text.setText("充值");
		topbar_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		payMoney=10.0;
		rechargeMoneyLinearLayout.setVisibility(View.GONE);
		tenBtn.setBackgroundResource(R.drawable.white_btn_normal2);
		tenBtn.setTextColor(Color.WHITE);
		
		rechargeCoupon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CouponActivity.class);
				startActivity(intent);
			}
		});
		
		runnable2 = new Runnable() {
			@Override
			public void run() {
				
				String urlNew = "http://localhost:8080/DrcomSrv/DrcomServlet?business=";
				String datsNew = "S07"+LocalStore.getFreeWifi().WIFIAccount;
				Log.i("MyLog", "datasNew = "+datsNew);
				String dataBase = Base64.encodeToString(datsNew.getBytes(), Base64.NO_WRAP);
				Log.i("MyLog", "dataBase = "+dataBase);
				String result = "";
				String str_data = "";
/*
				String data = "091" + LocalStore.getFreeWifi().WIFIAccount;

				String datas = Base64.encodeToString(data.getBytes(),
						Base64.NO_WRAP);
				Log.i("MyLog", "datas2==" + datas);

				String url = LocalStore.getWifiApUrl(getApplicationContext())
						+ datas;*/
				String url = LocalStore.getWifiApUrl(getApplicationContext())+dataBase;
				Log.i("MyLog", "url = "+url);
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse response = new DefaultHttpClient().execute(httpGet);
					result = getResponseResult(response);
					
					str_data = str_data + "第二次请求返回结果: " + result;
					Log.i("MyLog", "断开返回result===" + result);
					Message msg = new Message();
					Bundle data2 = new Bundle();
//					data2.putCharArray("value", results);
					data2.putString("value", result);
					msg.setData(data2);
					handler2.sendMessage(msg);
				} catch (Exception e) {

					/*String err = e.toString();
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("value", err);
					msg.setData(data);*/
//					handler.sendMessage(msg);

					e.printStackTrace();
				}
			}
		};
		new Thread(runnable2).start();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(runnable2).start();
	}

	/**
	 * 显示响应结果到命令行和TextView
	 * 
	 * @param response
	 */
	private String getResponseResult(HttpResponse response) {
		if (null == response) {
			return "";
		}
		String result = "";
		HttpEntity httpEntity = response.getEntity();
		try {
			InputStream inputStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

			String line = "";
			while (null != (line = reader.readLine())) {
				result += line;

			}

			System.out.println(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rechargeTenYuanButton:
			payMoney=10.0;
			rechargeMoneyLinearLayout.setVisibility(View.GONE);
			tenBtn.setBackgroundResource(R.drawable.white_btn_normal2);
			tenBtn.setTextColor(Color.WHITE);
			twentyBtn.setBackgroundResource(R.drawable.white_btn_normal);
			twentyBtn.setTextColor(Color.parseColor("#000000"));
			fiftyBtn.setBackgroundResource(R.drawable.white_btn_normal);
			fiftyBtn.setTextColor(Color.parseColor("#000000"));
			otherBtn.setBackgroundResource(R.drawable.white_btn_normal);
			otherBtn.setTextColor(Color.parseColor("#000000"));
			break;

		case R.id.rechargetwentyYuanButton:
			tenBtn.setBackgroundResource(R.drawable.white_btn_normal);
			tenBtn.setTextColor(Color.parseColor("#000000"));
			twentyBtn.setBackgroundResource(R.drawable.white_btn_normal2);
			twentyBtn.setTextColor(Color.WHITE);
			fiftyBtn.setBackgroundResource(R.drawable.white_btn_normal);
			fiftyBtn.setTextColor(Color.parseColor("#000000"));
			otherBtn.setBackgroundResource(R.drawable.white_btn_normal);
			otherBtn.setTextColor(Color.parseColor("#000000"));
			rechargeMoneyLinearLayout.setVisibility(View.GONE);
			payMoney=20.0;
			break;
			
		case R.id.rechargefiftyButton:
			fiftyBtn.setBackgroundResource(R.drawable.white_btn_normal2);
			fiftyBtn.setTextColor(Color.WHITE);
			tenBtn.setBackgroundResource(R.drawable.white_btn_normal);
			tenBtn.setTextColor(Color.parseColor("#000000"));
			twentyBtn.setBackgroundResource(R.drawable.white_btn_normal);
			twentyBtn.setTextColor(Color.parseColor("#000000"));
			otherBtn.setBackgroundResource(R.drawable.white_btn_normal);
			otherBtn.setTextColor(Color.parseColor("#000000"));
			rechargeMoneyLinearLayout.setVisibility(View.GONE);
			payMoney=50.0;
			break;
			
		case R.id.rechargeotherYuanButton:
			otherBtn.setBackgroundResource(R.drawable.white_btn_normal2);
			otherBtn.setTextColor(Color.WHITE);
			tenBtn.setBackgroundResource(R.drawable.white_btn_normal);
			tenBtn.setTextColor(Color.parseColor("#000000"));
			twentyBtn.setBackgroundResource(R.drawable.white_btn_normal);
			twentyBtn.setTextColor(Color.parseColor("#000000"));
			fiftyBtn.setBackgroundResource(R.drawable.white_btn_normal);
			fiftyBtn.setTextColor(Color.parseColor("#000000"));
			rechargeMoneyLinearLayout.setVisibility(View.VISIBLE);
			break;
			
		case R.id.rechargeMoneyLinearLayout:
			
			break;
		case R.id.payButton:
//			Toast.makeText(RechargeActivity.this,"充值成功", Toast.LENGTH_LONG).show();
			if (payMoney!=10.0||payMoney!=20.0||payMoney!=50.0) {
				try {
					payMoney = new Double(PhoneEditText.getText().toString());
				} catch (Exception e) {
					
				}
			}
			Intent intent = new Intent();
			intent.putExtra("payMoney", payMoney);
			LocalStore.setWifiPayMoney(RechargeActivity.this, (int)payMoney);
			intent.setClass(RechargeActivity.this, PayActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		
	}

}
