package com.wuxianyingke.property.threads;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.Propertys;
import com.wuxianyingke.property.remote.RemoteApiImpl;
//通过名字模糊查询小区
public class GetPropertyByNameListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private String description;
	private int mPageIndex;
	private boolean running = true;
	private boolean isRunning = true;

	private ArrayList<Propertys> mPropertyByNameList;

	public GetPropertyByNameListThread(Context mContext, Handler mHandler,
			String description, int mPageIndex) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.description = description;
		this.mPageIndex = mPageIndex;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<Propertys> getPropertyList() {
		return mPropertyByNameList;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			ArrayList<Propertys> propertyList = rai.getPropertyList(mContext,
					description, mPageIndex);

			 if (propertyList != null) {
		
			 if (!running)
			 return;
			 mPropertyByNameList = propertyList;
			 mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_LIST_FINISH);
			 } else {
			 if (!running)
			 return;
			 mHandler.sendEmptyMessage(Constants.MSG_NETWORK_ERROR);
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
