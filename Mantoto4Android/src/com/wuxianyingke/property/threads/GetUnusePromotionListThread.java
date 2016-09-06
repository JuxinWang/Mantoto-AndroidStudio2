package com.wuxianyingke.property.threads;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.OrderInfo;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetUnusePromotionListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private long userid;
	private int pageIndex;
	private boolean running = true;
	public ArrayList<OrderItem>mOrders;

	public GetUnusePromotionListThread(Context mContext, Handler mHandler, long userid,
			int pageIndex) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.userid = userid;
		this.pageIndex = pageIndex;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<OrderItem> getOrders() {
		return mOrders;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			
			OrderInfo orderInfo = rai.getUnUsePromotion(mContext, userid, pageIndex);
			if (orderInfo != null) {
				Log.i("MyLog", "获得已完成订单+GetOrderListActivity---------------->"+orderInfo.toString());
				
				if (!running)
				{
					return;
				}
					
				mOrders=orderInfo.orderInfo;
				Log.i("MyLog","GetOrderListActivity+orderInfo=-----------------"+orderInfo);
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
