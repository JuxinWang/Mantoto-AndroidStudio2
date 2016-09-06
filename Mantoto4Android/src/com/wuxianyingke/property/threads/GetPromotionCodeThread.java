package com.wuxianyingke.property.threads;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.PromotionCode;
import com.wuxianyingke.property.remote.RemoteApi.PromotionCodeArray;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetPromotionCodeThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private long ordersequencenumber;
	private boolean running = true;
	public ArrayList<PromotionCode> mPromotionCode;

	public GetPromotionCodeThread(Context mContext, Handler mHandler,
			long ordersequencenumber) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.ordersequencenumber = ordersequencenumber;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<PromotionCode> getPromotionCode() {
		return mPromotionCode;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();

			PromotionCodeArray pArray = rai.getPromotionCodeArray(mContext,
					ordersequencenumber);
			if (pArray != null) {
				Log.i("MyLog",
						"GetOrderListActivity---------------->"
								+ pArray.toString());

				if (!running) {
					return;
				}

				mPromotionCode = pArray.proArray;

				Log.i("MyLog",
						"mPromotionCode=-----------------"
								+ mPromotionCode.get(0).PromotionStatus.PromotionCodeStatusName);
				mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_FINISH);
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
