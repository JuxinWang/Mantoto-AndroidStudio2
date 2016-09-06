package com.wuxianyingke.property.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.UpdateManger;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.UpdateInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
/**
 * 关于

 */
public class AboutActivity extends BaseActivity {

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
					UpdateManger updateManger = new UpdateManger(AboutActivity.this, updateInfo.url, updateInfo.updateInfo, updateInfo.versionCode,updateInfo.appversion);
					updateManger.checkUpdate();
				
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
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.about);
		initWidgets();
		setImmerseLayout(findViewById(R.id.common_back));
		
		Thread getUpdateThread = new Thread(){
			@Override
			public void run() {
				RemoteApiImpl remoteApiImpl = new RemoteApiImpl();
				updateInfo = remoteApiImpl.getUpdateInfo(getApplicationContext(), "mantoto");
				Message msg = new Message();
				if (updateInfo == null) {
					msg.what = 5;
				}else
				{
				if (updateInfo.netInfo.code == 200)
					{
						msg.what = 9;
					}else{
						desc = updateInfo.netInfo.desc;
						msg.what = 10;
					}	
				}
				mHandler.sendMessage(msg);
			}
		};
		getUpdateThread.start();
	}

	private void initWidgets() {
		ImageView cartImageview=(ImageView)findViewById(R.id.cart_imageview);
		Util.modifyCarNumber(this,cartImageview);
		String version = Util.getPackageVersion(getApplicationContext(),
				Constants.GET_PACKAGENAME(getApplicationContext()));
		if (version != null) {
			TextView textVersion = (TextView) findViewById(R.id.versionTextView);
			/*if (Constants.URL.equals("http://dev.mantoto.com/")) {
				textVersion.setText("v"+version + "(测试版)");
			}else{*/
			textVersion.setText("v"+version);
			//}
		}

		TextView topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("个人中心");
		Button backbutton = (Button) findViewById(R.id.topbar_left);
		backbutton.setVisibility(View.VISIBLE);
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}


}