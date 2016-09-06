package com.wuxianyingke.property.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.service.MyService;
import com.wuxianyingke.property.threads.CouponThread;

public class WIFILoginActivity extends BaseActivity {

	public static final int CMD_STOP_SERVICE = 0;
	private String TAG = "http";
	private ProgressDialog mProgressDialog = null;
	private EditText mDDDDDText = null;
	private EditText mUpassText = null;

	private Button postButton = null;

	private TextView mResult = null;

	private String qs_url1 = null;
	private String ip_url1 = null;
	private TextView settingWifi;
	private LinearLayout wifiLinearLayout;
	private LinearLayout openLinearLayout;
	private LinearLayout breakLinearLayout;
	private Button breakWifibtn,openWifibtn;
	private String portalIP;
	private String portalPort;
	private int responseCode;
	private int flag = 1;
	private String resultBreak;
	private TextView topbar_btn_Ringht;

	private String[] ssid = null;
	private String ip;
	private double leftTimes;
	private Runnable runnableBalance;
    //余额查询
	private Button topbar_left,topbar_left1,topbar_left2;
	private WifiManager wifiManager;
	private TextView topbar_text;
	private String[] results;
	private TextView presentWifiTime;//赠送时长
	private TextView rechargeWifiTime;//上网时长
	private TextView rechargeMoney;//账号金额
	private ProgressDialog mDialog;
	private LinearLayout balaenceLinearLayout,balaenceLinearLayout2;

	Handler handlerBalance = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mProgressDialog.dismiss();
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
						Log.i("MyLog", "Money = "+Double.valueOf(leftMoney));
						leftTimes = Double.valueOf(leftMoney)*100+Double.valueOf(leftTime);
						rechargeWifiTime.setText(""+(int)(Double.valueOf(leftMoney)*100+Double.valueOf(leftTime))/60+"小时"+String.format("%.0f", (Double.valueOf(leftMoney)*100+Double.valueOf(leftTime))%60)+"分");
						rechargeMoney.setText(leftMoney+"元");
						presentWifiTime.setText(leftTime+"分");
					}
					if (leftTimes <=0) {
						showNoticeDialog();
						}else{
						mProgressDialog = new ProgressDialog(WIFILoginActivity.this);
						mProgressDialog.setMessage("连接中，请稍候...");
						mProgressDialog.setCancelable(true);
						mProgressDialog.show();
						new Thread(runnable_rq1).start();
						}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				/*String result=msg.getData().getString("value");
				results = result.split("\t");//根据"/t"区分
				rechargeMoney.setText(results[4]+"元");
				String times = results[4];
				rechargeWifiTime.setText(""+Long.parseLong(times.replace(".", ""))/60+"小时"+Long.parseLong(times.replace(".", ""))%60+"分钟");
				Log.i("MyLog", "results=="+results[0]);
				Log.i("MyLog", "results="+results[0]+results[1]+results[2]+results[3]+results[4]+results[5]+results[6]+results[7]+results[8]);*/
		
		}
	};
	@SuppressLint("ServiceCast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_login);

		ssid = getIntent().getExtras().getStringArray("ssid");
		Log.i("MyLog", "ssid[]=" + ssid[0]);
		// 获得Scheme名称
		this.getIntent().getScheme();
		// 获得Uri全部路径
		this.getIntent().getDataString();
		mProgressDialog = new ProgressDialog(WIFILoginActivity.this);
		mProgressDialog.setMessage("网络环境检测中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
		
		//获取wifi服务
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
        //判断wifi是否开启  
        if (!wifiManager.isWifiEnabled()) {  
        wifiManager.setWifiEnabled(true);    
        }  
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
        int ipAddress = wifiInfo.getIpAddress();   
        ip = intToIp(ipAddress);   
		Log.i("MyLog", "ip = "+ip);
		
		Log.i("MyLog",
				"获取的wifi登录信息为wifiAccount="
						+ LocalStore.getFreeWifi().WIFIAccount);

		mDDDDDText = (EditText) findViewById(R.id.DDDDD);//登录页面账号
		mUpassText = (EditText) findViewById(R.id.Upass);//登录页面密码
		mResult = (TextView) findViewById(R.id.result);//登录按钮下方的一个框

		postButton = (Button) findViewById(R.id.submit_request);//登录页面登录按钮

		//余额
		rechargeWifiTime = (TextView) findViewById(R.id.rechargeWifiTime);
		presentWifiTime = (TextView) findViewById(R.id.presentWifiTime);
		rechargeMoney = (TextView) findViewById(R.id.rechargeMoneyTextView);
		balaenceLinearLayout = (LinearLayout) findViewById(R.id.balaenceLinearLayout);
		balaenceLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(WIFILoginActivity.this, RechargeActivity.class);
				startActivity(intent);
			}
		});
		balaenceLinearLayout2 = (LinearLayout) findViewById(R.id.balaenceLinearLayout2);
		balaenceLinearLayout2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(WIFILoginActivity.this, RechargeActivity.class);
				startActivity(intent);
			}
		});




		postButton.setOnClickListener(mPostClickListener);
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left1 = (Button) findViewById(R.id.topbar_left1);
		topbar_left2 = (Button) findViewById(R.id.topbar_left2);
		topbar_text = (TextView) findViewById(R.id.topbar_txt);
		//点击右上角充值
		topbar_btn_Ringht = (TextView) findViewById(R.id.topbar_right);
		topbar_btn_Ringht.setText("充值");
		topbar_btn_Ringht.setVisibility(View.VISIBLE);
		topbar_btn_Ringht.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(WIFILoginActivity.this, RechargeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setBackgroundResource(R.drawable.arrowtest);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent myIntent = new Intent();// 创建Intent对象
				myIntent.setAction("wyf.wpf.MyService");
				myIntent.putExtra("cmd", CMD_STOP_SERVICE);
				sendBroadcast(myIntent);// 发送广播

				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setClass(WIFILoginActivity.this, MainActivity.class);
				finish();
			}
		});
		topbar_left1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent();// 创建Intent对象
				myIntent.setAction("wyf.wpf.MyService");
				myIntent.putExtra("cmd", CMD_STOP_SERVICE);
				sendBroadcast(myIntent);// 发送广播

				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setClass(WIFILoginActivity.this, MainActivity.class);
				finish();
			}
		});
		topbar_left2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent();// 创建Intent对象
				myIntent.setAction("wyf.wpf.MyService");
				myIntent.putExtra("cmd", CMD_STOP_SERVICE);
				sendBroadcast(myIntent);// 发送广播

				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.setClass(WIFILoginActivity.this, MainActivity.class);
				finish();
			}
		});
		topbar_text.setVisibility(View.VISIBLE);
		topbar_text.setText("WIFI");
		topbar_text.setTextColor(Color.BLACK);
		//带背景wifi
		wifiLinearLayout = (LinearLayout) findViewById(R.id.wifi_LinearLayout);
		//连接wifi
		openLinearLayout = (LinearLayout) findViewById(R.id.openWifi_LinearLayout);
		//断开wifi
		breakLinearLayout = (LinearLayout) findViewById(R.id.breakWifi_LinearLayout);

		openWifibtn = (Button) findViewById(R.id.openwifi);//连接按钮图片
		breakWifibtn = (Button) findViewById(R.id.cutwifi);//断开按钮图片
		settingWifi = (Button)findViewById(R.id.wifi_setting);//设置连接自家wifi按钮
		// new Thread(runnable_rq12).start();
		settingWifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openWifi();
			}
		});

		openWifibtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread(runnableBalance).start();
			}
		});

		breakWifibtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mProgressDialog = new ProgressDialog(WIFILoginActivity.this);
				mProgressDialog.setMessage("断开中，请稍候...");
				mProgressDialog.setCancelable(true);
				mProgressDialog.show();
				new Thread(runnable2).start();
			}
		});


		//打开wifi时启动
		runnableBalance = new Runnable() {
			@Override
			public void run() {
				
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
				Log.i("MyLog", "datas2==" + datas+"data="+ data);
				String url = LocalStore.getWifiApUrl(BalenceActivity.this)
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
					handlerBalance.sendMessage(msg);
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


	}
	@Override
	protected void onResume() {
		runnable_CheckConnectable.setHttpError(false);
		new Thread(runnable_CheckConnectable).start();
		Log.i("MyLog", "onWindowFocus erro");
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

	// 打开wifi设置
	private void openWifi() {
		Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
		startActivity(wifiSettingsIntent);
	}

	private OnClickListener mPostClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Log.i(TAG, "POST request");

			new Thread(runnable_rq1).start();
		}
	};

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


	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			flag = 3;
			switch (msg.what) {
			case 2:
				String  resultHtml = msg.getData().getString("value");
				Log.i("MyLog", "what2 login finish");
				if (!resultHtml.contains("Dr.COMWebLoginID_3.htm")) {
					Toast.makeText(getApplicationContext(), "登录失败请重试", Toast.LENGTH_SHORT).show();
				}
				break;

			case 4:
				boolean IsConnectable = msg.getData().getBoolean(
						"IsConnectable");
				boolean hasHttpError = msg.getData().getBoolean(
						"hasHttpError");
				ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = manager.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					String netType = networkInfo.getTypeName();
					if (netType.equalsIgnoreCase("WIFI")) {
						WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
						WifiInfo info = wifiManager.getConnectionInfo();
						Log.i("MyLog",
								"what4 wifiname=" + info.getSSID().replace("\"", "")
										+ Arrays.toString(ssid));
						String wifiName = info.getSSID().replace("\"", "");
						String s = "\"drcom\"";
						if (Arrays.toString(ssid).contains(wifiName)) {
							if (IsConnectable) {
								wifiLinearLayout.setVisibility(View.GONE);
								openLinearLayout.setVisibility(View.GONE);
								breakLinearLayout.setVisibility(View.VISIBLE);
							} else {
								wifiLinearLayout.setVisibility(View.GONE);
								breakLinearLayout.setVisibility(View.GONE);
								openLinearLayout.setVisibility(View.VISIBLE);
								if(hasHttpError){
									Toast.makeText(getApplicationContext(), "登录失败请重试", Toast.LENGTH_SHORT).show();
								}
							}
						} else {
							wifiLinearLayout.setVisibility(View.VISIBLE);
						}
					} else if (netType.equalsIgnoreCase("MOBILE")) {
						wifiLinearLayout.setVisibility(View.VISIBLE);
					}
				}
				if(mProgressDialog!=null&&mProgressDialog.isShowing())
				{
					mProgressDialog.dismiss();
				}
			}

			// mResult.setText("Response Content from server: " + result);

		}
	};

	Runnable runnable_rq1 = new Runnable() {
		@Override
		public void run() {
			String url1 = "http://www.wuxianying.com/checkWan.html";
			String str_data = "";
			try {
				URL u = new URL(url1);
				HttpURLConnection openConnection = (HttpURLConnection) u
						.openConnection();
				openConnection.setInstanceFollowRedirects(false);

				String result = "";
				responseCode = openConnection.getResponseCode();
				str_data = str_data + "第一次请求返回值: " + responseCode
						+ "--------------------";
				Log.i("MyLog", "responseCode===" + responseCode);
				result = openConnection.getHeaderField("Location");
				portalIP = result.substring(0, result.lastIndexOf("/"));
				portalPort = "80";
				Log.i("MyLog", "result --- -- port = " +result);
				if(portalIP.substring(0,portalIP.lastIndexOf(":")).contains(":"))
				{
					portalPort = portalIP.substring(portalIP.lastIndexOf(":") + 1);
					portalIP = portalIP.substring(0, portalIP.lastIndexOf(":"));
				}
				Log.i("MyLog", "result===" + result + "\t portalPort:" + portalPort);
				Log.i("MyLog", "result===" + result + "\t portalIP:" + portalIP);

				if (responseCode != 302)
					return;
				if (result.contains("?")) {
					qs_url1 = result.substring(result.indexOf("?") + 1);
					String[] arr_qs_url1 = qs_url1.split("&");
					qs_url1 = "";
					Log.i("MyLog", "qs = "+qs_url1);
					for (String qs : arr_qs_url1) {
						if (qs.contains("wlan") || qs.contains("vslanusermac") ) {
							qs_url1 = qs_url1 + qs + "&";
							Log.i("MyLog", " qs_url1= "+qs_url1);	
						}
					}
					
					if(qs_url1.contains("&"))
					{
						String paraStr = qs_url1.lastIndexOf("&")+1 == qs_url1.length()?"port=":"&port=";
						qs_url1 = qs_url1 + paraStr + portalPort;
					}
					
//					qs_url1记入共享参数，参数名称：RadiusParam
//					portalIP记入共享参数，参数名称：PortalIP
					LocalStore.setRadiusParam(getApplicationContext(), qs_url1);
					LocalStore.setPortalIp(getApplicationContext(), portalIP);
					str_data = str_data + "第一次请求返回QueryString: " + qs_url1
							+ "--------------------";

					str_data = str_data + "第一次请求返回: " + result
							+ "--------------------";
					Log.i("MyLog", "urlurl1===" + str_data);
					new Thread(runnable).start();
				} else {
					ip_url1 = result.replace("http://", "");
					if (ip_url1.contains("/"))
						ip_url1 = ip_url1
								.substring(0, ip_url1.indexOf("/") - 1);
					str_data = str_data + "第一次请求返回ip: " + ip_url1
							+ "--------------------";
					Log.i("MyLog", "urlurl===" + str_data);
					new Thread(runnable_post).start();
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", str_data);
				msg.setData(data);
				handler.sendMessage(msg);
			} catch (Exception e) {
				String err = e.toString();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", err);
				msg.setData(data);
				handler.sendMessage(msg);

				e.printStackTrace();
			}
		}
	};
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mProgressDialog != null&&mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			
			String location = msg.getData().getString("location");
			int retResponseCode = msg.getData().getInt("retResponseCode");
			
			if (retResponseCode == 302 && location.contains("/2.htm?")) {
				Toast.makeText(getApplicationContext(), "网络断开成功",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "网络断开失败，请重试",
						Toast.LENGTH_LONG).show();
			}
			Intent intent = new Intent();
			intent.setClass(WIFILoginActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}
	};
	
//	Runnable runnable014 = new Runnable() {
//		@Override
//		public void run() {
//			String result = "";
//			String str_data = "";
//			Log.i("MyLog", "runnable014== 014" );
//			String data1 = "014" + LocalStore.getFreeWifi().WIFIAccount + "\t"
//					+ "6666" + "\t" + "200902190014";
//
//			String datas = Base64.encodeToString(data1.getBytes(),
//					Base64.NO_WRAP);
//
//			String url3 = LocalStore.getWifiApUrl(WIFILoginActivity.this)
//					+ datas;
//			Log.i("MyLog", "url3==" + url3);
//			Log.i("MyLog", "url3=" + data1);
//			HttpGet httpGet2 = new HttpGet(url3);
//			try {
//				HttpResponse response3 = new DefaultHttpClient()
//						.execute(httpGet2);
//				result = getResponseResult(response3);
//				str_data = str_data + "第二次请求返回结果: " + result;
//				Log.i("MyLog", "断开返回result===" + result);
//				resultBreak = result;
//
//				Message msg = new Message();
//				Bundle data = new Bundle();
//				data.putString("value", str_data);
//				msg.setData(data);
//				handler2.sendMessage(msg);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Log.i("MyLog", "登陆 DDDDD="+LocalStore.getFreeWifi().WIFIAccount+",Upass="+LocalStore.getFreeWifi().WIFIPwd);
			String result = "";
			String str_data = "";
			try {
				String url2 = portalIP
						+ ":801/eportal/?c=ACSetting&a=Login&DDDDD=[account]&upass=[password]&";
				url2 = url2.replace("[account]",
						LocalStore.getFreeWifi().WIFIAccount);
				url2 = url2.replace("[password]",
						LocalStore.getFreeWifi().WIFIPwd);
				url2 = url2 + qs_url1;
				str_data = str_data + "第二次请求url: " + url2
						+ "------------------------------";
				// URL u2 = new URL(url2);
				// HttpURLConnection openConnection2 = (HttpURLConnection)
				// u2.openConnection();
				// openConnection2.setInstanceFollowRedirects(false);
				// int responseCode2 = openConnection2.getResponseCode();
				// result = openConnection2.getResponseMessage();
				Log.i("MyLog", " runnable url2=" +url2);
				
				HttpGet httpGet2 = new HttpGet(url2);
				HttpResponse response2 = new DefaultHttpClient()
						.execute(httpGet2);
				Log.i("MyLog", " runnable 登陆成功时返回结果result=" +response2);
				result = getResponseResult(response2);
				
				str_data = str_data + "第二次请求返回结果: " + result;
				Log.i("MyLog", " runnable 登陆成功时返回结果result=" + result);
				runnable_CheckConnectable.setHttpError(false);
				// LocalStore.setWifiId2(getApplicationContext(), 2);
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", str_data);
				msg.setData(data);
				msg.what = 2;
				handler.sendMessage(msg);
			} catch (Exception e) {
				runnable_CheckConnectable.setHttpError(true);
				Log.i("MyLog", "exception:"+e.getMessage());
				String err = e.toString();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", err);
				msg.setData(data);
				handler.sendMessage(msg);

				e.printStackTrace();
			} finally {				
				Thread runnableCheckWan = new Thread(runnable_CheckConnectable);
				runnableCheckWan.start();
			}
		}
	};


	//断开连接时启动
	Runnable runnable2 = new Runnable() {
		@Override
		public void run() {			
			try {
				String portalIP = LocalStore.getPortalIp(getApplicationContext());
			    String radiusParam = LocalStore.getRadiusParam(getApplicationContext());
						
				String url2 = portalIP
						+ ":801/eportal/?c=ACSetting&a=Logout&" + radiusParam;
				
				Log.i("MyLog", " runnable 注销请求=" +url2);
				
				URL u = new URL(url2);
				HttpURLConnection openConnection = (HttpURLConnection) u
						.openConnection();
				openConnection.setInstanceFollowRedirects(false);

				String location = "";
				int retResponseCode = 0;
				retResponseCode = openConnection.getResponseCode();
				location = openConnection.getHeaderField("Location");
				Log.i("MyLog", "retResponseCode===" + retResponseCode);
				Log.i("MyLog", "Location===" + location);				
								
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("location", location);
				data.putInt("retResponseCode", retResponseCode);
				
				msg.setData(data);
				handler2.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("MyLog", "exception :"+e.getMessage());
			}
		}
	};

	Runnable runnable_post = new Runnable() {
		@Override
		public void run() {
			//
			// TODO: http request.
			//
			Log.i("MyLog", "runnable2== post" );
			String baseURL = "http://" + ip_url1;
			String DDDDD = mDDDDDText.getText().toString();
			String Upass = mUpassText.getText().toString();

			NameValuePair pair1 = new BasicNameValuePair("DDDDD", DDDDD);
			NameValuePair pair2 = new BasicNameValuePair("upass", Upass);
			NameValuePair pair3 = new BasicNameValuePair("0MKKey", "1");
			NameValuePair pair4 = new BasicNameValuePair("port", portalPort);

			List<NameValuePair> pairList = new ArrayList<NameValuePair>();
			pairList.add(pair1);
			pairList.add(pair2);
			pairList.add(pair3);
			pairList.add(pair4); 

			String str_data = "";
			str_data = str_data + "串接模式--------第二次请求url: " + baseURL
					+ "------------------------------";
			try {
				HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
						pairList);
				// URL使用基本URL即可，其中不需要加参数
				HttpPost httpPost = new HttpPost(baseURL);
				// 将请求体内容加入请求中
				httpPost.setEntity(requestHttpEntity);
				// 需要客户端对象来发送请求
				HttpClient httpClient = new DefaultHttpClient();
				// 发送请求
				HttpResponse response = httpClient.execute(httpPost);

				// 显示响应
				String result = getResponseResult(response);

				str_data = str_data + "串接模式--------第二次请求返回结果: " + result
						+ "------------------------------";
				runnable_CheckConnectable.setHttpError(false);
				new Thread(runnable_CheckConnectable).start();

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", str_data);
				msg.setData(data);
				msg.what = 2;
				handler.sendMessage(msg);

			} catch (Exception e) {

				String err = e.toString();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", err);
				msg.setData(data);
				handler.sendMessage(msg);

				e.printStackTrace();
			}
		}
	};

	CheckConnectable runnable_CheckConnectable = new CheckConnectable() {
		@Override
		public void run() {
			Log.i("MyLog", "runnable_CheckConnectable run");
			boolean IsConnectable;
			String url1 = "http://www.wuxianying.com/checkWan.html";
			try {
				URL u = new URL(url1);
				HttpURLConnection openConnection = (HttpURLConnection) u
						.openConnection();
				openConnection.setInstanceFollowRedirects(false);
				responseCode = openConnection.getResponseCode();
				if (responseCode == 200) {
					IsConnectable = true;
				} else {
					IsConnectable = false;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putBoolean("IsConnectable", IsConnectable);
				data.putBoolean("hasHttpError", this.httpError);
				msg.setData(data);
				msg.what = 4;
				handler.sendMessage(msg);

			} catch (Exception e) {
				Log.i("MyLog", "CheckConnectable exception"+e.getMessage());
				
				e.printStackTrace();
			}
		}
	};

	private void ControlWiFiView() {
		runnable_CheckConnectable.setHttpError(false);
		new Thread(runnable_CheckConnectable).start();
	}
	
	 private String intToIp(int i) {       
         
         return (i & 0xFF ) + "." +       
       ((i >> 8 ) & 0xFF) + "." +       
       ((i >> 16 ) & 0xFF) + "." +       
       ( i >> 24 & 0xFF) ;  
    }   

	class CheckConnectable implements Runnable
	{ 
		boolean httpError;	
		public void setHttpError(boolean httpError){
			this.httpError=httpError;
		}
		
		public CheckConnectable(){
		this.httpError=false;
		
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void showNoticeDialog() {
		final AlertDialog alertDialog = new AlertDialog.Builder(WIFILoginActivity.this)
				.create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_banlance_zero);
		Button concle = (Button) window.findViewById(R.id.recharge_concel_btn);
		concle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				alertDialog.dismiss();
			}
		});
		Button confirm = (Button) window.findViewById(R.id.recharge_confirm_btn);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Toast.makeText(getApplicationContext(), "余额不足，请充值", Toast.LENGTH_SHORT).show();	
				Intent intent = new Intent();
				intent.setClass(WIFILoginActivity.this, RechargeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				alertDialog.dismiss();
			}
		});
	}

}

// 0MKKey = 1