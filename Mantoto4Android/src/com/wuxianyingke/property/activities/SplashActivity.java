package com.wuxianyingke.property.activities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mantoto.property.R;
import com.testin.agent.TestinAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.HttpComm;
import com.wuxianyingke.property.remote.RemoteApi.Loading;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.service.SendLocation;

public class SplashActivity extends Activity {
	private final static String FIRST_CONFIG_MESSAGE = "first_config_message";
	private final static String CHANNEL_ID = "channel_id";
	public final static int MSG_GOTO_MAIN = 1;
	private boolean is_exit = false;

	private LinearLayout mainLl;
	private int logoId;
	private int propertyid;
	private Loading loading = null;
	private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
	private String url;
	private ImageView image;

	private byte[] bytes;
	private boolean downloadFinished = false;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationClient mLocClient;
	private static SendLocation info = new SendLocation();

	final Handler handler = new Handler() {

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOTO_MAIN:
				Log.i("MyLog", "downloadFinished in MSG_GOTO_MAIN:-----------"
						+ downloadFinished);
				if (!downloadFinished) {

					// SharedPreferences saving = SplashActivity.this
					// .getSharedPreferences(LocalStore.USER_INFO, 0);
					// 验证码是否为空
					if (LocalStore.getUserInfo().userId != 0) {
						// 用户id是否为空
						if (LocalStore.getUserInfo().userId != 0) {
							// 用户是否选中自动登录
							if (LocalStore.getUserStatus(SplashActivity.this)) {
								Intent intent = new Intent();
								intent.setClass(SplashActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent();
								intent.setClass(SplashActivity.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						} else {
							Intent intent = new Intent();
							intent.setClass(SplashActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						}

					} else {
						if (LocalStore.getWelcomeId(getApplicationContext()) == 0) {
							Intent intent = new Intent();
							intent.setClass(SplashActivity.this,
									WelcomeActivty.class);
							startActivity(intent);
							finish();
						} else {
							Intent intent = new Intent();
							intent.setClass(SplashActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						}
					}

				} else

				{
					Intent intent = new Intent();
					// intent.putExtra("bytes", bytes);
					intent.putExtra("url", url);
					// Log.i("MyLog", "goto splash2 in splash1--"+bytes);
					intent.setClass(SplashActivity.this, SplashActivity2.class);
					startActivity(intent);
					finish();
				}
				break;

			case 3:
				if (logoId != loading.logoId) {
					Log.i("MyLog", "网络请求的logoId为" + loading.logoId + logoId);
					downloading(url);
					// Bitmap bitmap = SDCardUtils.readImage(url);
					// setBackgraoundDrawable(bitmap);
				}
				// } else {
				// SDCardUtils.clearCache();
				// downloading(url);
				// Bitmap bitmap = SDCardUtils.readImage(url);
				// setBackgraoundDrawable(bitmap);
				// }
				break;
			}
			super.handleMessage(msg);
		}

		private void setBackgraoundDrawable(Bitmap bitmap) {
			if (bitmap != null) {
				@SuppressWarnings("deprecation")
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				mainLl.setBackgroundDrawable(bd);
			} else {
				mainLl.setBackgroundResource(R.drawable.splash2);
			}
		}

		private void downloading(final String url) {
			// 将网络请求处理的Runnable增加到线程池中
			mExecutor.execute(new Runnable() {
				@Override
				public void run() {

					try {
						HttpClient client = new DefaultHttpClient();
						HttpGet get = new HttpGet(url);
						HttpResponse response = client.execute(get);
						Log.i("MyLog",
								"response.getStatusLine().getStatusCode():-----------"
										+ response.getStatusLine()
												.getStatusCode());
						if (response.getStatusLine().getStatusCode() == 200) {
							bytes = EntityUtils.toByteArray(response
									.getEntity());
							Log.i("MyLog", "bytes in splash1  downloading--"
									+ bytes);
							// 保存图片到本地
							SDCardUtils.saveImage(url, bytes);
							downloadFinished = true;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		mPushAgent.enable();
		LocalStore.setIsUpload(SplashActivity.this, true);
		mLocClient = new LocationClient(getApplicationContext());

		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		String device_token = UmengRegistrar
				.getRegistrationId(getApplicationContext());
		Log.i("MyLog", "device_token=" + device_token);
		TestinAgent.init(this, Constants.TESTIN_APPKEY, "YiXiuDaoJia");
		setContentView(R.layout.splash);

		// remote();
		logoId = LocalStore.getLoadingId(getApplicationContext());
		propertyid = LocalStore.getUserInfo().PropertyID;
		LogUtil.d("MyLog", "SplashActivity.onCreate---------------------"
				+ propertyid);
		mainLl = (LinearLayout) findViewById(R.id.Splashmain_ll);

		image = (ImageView) findViewById(R.id.SplashImage);
		LogUtil.d("TAG", "SplashActivity.onCreate---------------------");
		if (LocalStore.getLoadingId(getApplicationContext()) == -1) {
			mainLl.setBackgroundResource(R.drawable.splash2);
		} else {
			Bitmap bitmap = SDCardUtils.readImage(LocalStore
					.getLoadingUrl(getApplicationContext()));
			setBackgraoundDrawable(bitmap);
		}
		if (!LocalStore.getUserStatus(this)) {
			LocalStore.logout(this);
		}
		if (Util.getNetworkState(this) != 0) {
			Thread loadingThread = new Thread() {
				@Override
				public void run() {
					Log.i("MyLog", "请求的propertyid===" + propertyid);
					RemoteApiImpl rai = new RemoteApiImpl();

					loading = rai.getLoadingInfo(logoId, propertyid);
					Log.i("MyLog", "splash-propertyid=" + propertyid);
					if (loading != null) {
						url = loading.logoImgUrl;

					} else {

						return;
					}

					if (!url.equals("")) {
						LocalStore.setLoadingUrl(getApplicationContext(), url);
					} else {
						return;
					}
					LocalStore.setLoadingId(getApplicationContext(),
							loading.logoId);
					Log.i("MyLog", "loading------->" + "    " + "----"
							+ loading.logoImgUrl);
					Message msg = new Message();
					msg.what = 3;
					handler.sendMessage(msg);
				}
			};
			loadingThread.start();

		}

		Thread t = new Thread() {
			@SuppressWarnings("finally")
			public void run() {
				try {
					Thread.sleep(2000);

					// LogUtil.d("MyTag",
					// "downloadFinished in Thread.t = ----------" +
					// downloadFinished);
					// while(!downloadFinished)
					// {
					// Thread.sleep(1000);
					// LogUtil.d("MyTag", "Thread.sleep(1000)");
					// }

					Message msg = new Message();
					msg.what = MSG_GOTO_MAIN;
					handler.sendMessage(msg);
				} catch (Exception ex) {
					LogUtil.d("MyTag", "Splash error:" + ex.getMessage());
				}
			}
		};
		t.start();

//		Intent intent11 = new Intent(this, AlarmService.class);
//		intent11.putExtra("action", "manually");
//		startService(intent11);
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;

			info.latitude = location.getLatitude();
			info.longitude = location.getLongitude();
			info.city = location.getCity();

			Log.i("MyLog", "百度定位获得的当前的位置为------------》" + info.city + "----"
					+ location.getCity());
			LocalStore.cityInfo.city_name = info.city;
			LocalStore.setCityInfo(SplashActivity.this, LocalStore.cityInfo);

			/*
			 * Message msg = new Message(); msg.what =
			 * Constants.MSG_LOCATION_READY; msg.obj = info;
			 * mHandler.sendMessage(msg);
			 */
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * 设置图片到本地
	 */
	private void setBackgraoundDrawable(Bitmap bitmap) {
		if (bitmap != null) {
			@SuppressWarnings("deprecation")
			BitmapDrawable bd = new BitmapDrawable(bitmap);
			mainLl.setBackgroundDrawable(bd);
		} else {
			mainLl.setBackgroundResource(R.drawable.splash2);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			is_exit = true;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void remote() {
		Thread remoteThread = new Thread() {
			public void run() {
				try {
					JSONObject lastest = new JSONObject();
					String url = "http://221.0.78.196:8080/propertymanagement/tmp/apklist.json";
					JSONObject response = HttpComm.sendJSONToServer(url,
							lastest, Constants.HTTP_SO_TIMEOUT);
					Log.d("MyTag", "remote" + response.toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		remoteThread.start();

	}
}