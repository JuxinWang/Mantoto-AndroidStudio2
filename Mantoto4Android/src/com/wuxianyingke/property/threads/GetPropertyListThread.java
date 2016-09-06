package com.wuxianyingke.property.threads;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.Propertys;
import com.wuxianyingke.property.remote.RemoteApiImpl;
//定位得到小区列表
public class GetPropertyListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	
	private float latitude;
	private float longitude;
	private int mPageIndex;
	private boolean running = true;
	private boolean isRunning = true;

	private ArrayList<Propertys> mPropertyList;


	public GetPropertyListThread(Context mContext, Handler mHandler,
			float latitude, float longitude, int mPageIndex) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mPageIndex = mPageIndex;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<Propertys> getPropertyList() {
		return mPropertyList;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			ArrayList<Propertys> propertyList = rai.getPropertyList(mContext,
					latitude, longitude, mPageIndex);
			Log.i("MyLog", "response=Location2="+latitude+"  "+longitude+"  "+mPageIndex);
			if (propertyList != null) {
				
				mPropertyList = propertyList;
				mHandler.sendEmptyMessage(2);//列表正常显示
			} else {
				if (!running)
					return;
				mHandler.sendEmptyMessage(3);
				return;
			}

		} catch (Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
			ex.getMessage();
			if (!running)
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			mHandler.sendMessage(msg);
		}
	}

}
