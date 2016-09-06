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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mantoto.property.R;
import com.testin.agent.TestinAgent;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.push.AlarmService;
import com.wuxianyingke.property.remote.HttpComm;
import com.wuxianyingke.property.remote.RemoteApi.Loading;

public class SplashActivity2 extends Activity {
	private final static String FIRST_CONFIG_MESSAGE = "first_config_message";
	private final static String CHANNEL_ID = "channel_id";
	public final static int MSG_GOTO_MAIN = 1;
	private boolean is_exit = false;

	private LinearLayout mainLl;
	private int logoId;
	private int propertyid;
	private Loading loading = null;
	private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
	private byte[] bytes;
	private String url;
	private ImageView image;
	final Handler handler = new Handler() {

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GOTO_MAIN:
				if (!is_exit) {
					// SharedPreferences saving = SplashActivity.this
					// .getSharedPreferences(LocalStore.USER_INFO, 0);
					// 验证码是否为空
					if (LocalStore.getUserInfo().PropertyID != 0) {
						// 用户id是否为空
						if (LocalStore.getUserInfo().userId != 0) {
							// 用户是否选中自动登录
							if (LocalStore.getUserStatus(SplashActivity2.this)) {
								Intent intent = new Intent();
								intent.setClass(SplashActivity2.this,
										MainActivity.class);
								startActivity(intent);
								finish();
							} else {
								Intent intent = new Intent();
								intent.setClass(SplashActivity2.this,
										LoginActivity.class);
								startActivity(intent);
								finish();
							}
						} else {
							Intent intent = new Intent();
							intent.setClass(SplashActivity2.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						}

					} else {
						Intent intent = new Intent();
						intent.setClass(SplashActivity2.this,
								WelcomeActivty.class);
						startActivity(intent);
						finish();
					}
				}
				break;

			case 3:
				if (logoId == loading.logoId) {
					Log.i("MyLog", "网络请求的logoId为" + loading.logoId + logoId);
					Bitmap bitmap = SDCardUtils.readImage(url);
					setBackgraoundDrawable(bitmap);
				} else {
					SDCardUtils.clearCache();
					downloading(url);
					Bitmap bitmap = SDCardUtils.readImage(url);
					setBackgraoundDrawable(bitmap);
				}
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
						if (response.getStatusLine().getStatusCode() == 200) {
							byte[] bytes = EntityUtils.toByteArray(response
									.getEntity());
							// 保存图片到本地
							SDCardUtils.saveImage(url, bytes);
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
		TestinAgent.init(this, Constants.TESTIN_APPKEY, "YiXiuDaoJia");
		setContentView(R.layout.splash);
		String url=LocalStore.getLoadingUrl(getApplicationContext());
		// remote();
		logoId = LocalStore.getLoadingId(getApplicationContext());
		propertyid = LocalStore.getUserInfo().PropertyID;
		mainLl = (LinearLayout) findViewById(R.id.Splashmain_ll);
		//添加的动态
		RotateAnimation animRotate = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animRotate.setDuration(1000);
		animRotate.setFillAfter(true);

		// 缩放
		ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animScale.setDuration(1000);
		animScale.setFillAfter(true);

		// 渐变
		AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
		animAlpha.setDuration(2000);
		animAlpha.setFillAfter(true);

		// 动画集合
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(animRotate);
		animSet.addAnimation(animScale);
		animSet.addAnimation(animAlpha);
		
		mainLl.startAnimation(animSet);
		
		
		image = (ImageView) findViewById(R.id.SplashImage);
		LogUtil.d("TAG", "SplashActivity.onCreate---------------------");
		
		bytes=getIntent().getByteArrayExtra("bytes");
		Log.i("MyLog", "bytes=="+bytes);
		Bitmap bitmap = SDCardUtils.readImage(url);
		//Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);		
		setBackgraoundDrawable(bitmap);

		if (!LocalStore.getUserStatus(this)) {
			LocalStore.logout(this);
		}
//		if (Util.getNetworkState(this) != 0) {
//			Thread loadingThread = new Thread() {
//				@Override
//				public void run() {
//					Log.i("MyLog", "请求的propertyid===" + propertyid);
//					RemoteApiImpl rai = new RemoteApiImpl();
//
//					loading = rai.getLoadingInfo(-1, propertyid);
//					url = loading.logoImgUrl;
//					LocalStore.setLoadingId(getApplicationContext(),
//							loading.logoId);
//					Log.i("MyLog", "loading------->" + loading + "    "
//							+ loading.logoId + "----" + loading.logoImgUrl);
//					Message msg = new Message();
//					msg.what = 3;
//					handler.sendMessage(msg);
//				}
//			};
//			loadingThread.start();
//		}

		Thread t = new Thread() {
			@SuppressWarnings("finally")
			public void run() {
				try {
					Thread.sleep(3000);

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