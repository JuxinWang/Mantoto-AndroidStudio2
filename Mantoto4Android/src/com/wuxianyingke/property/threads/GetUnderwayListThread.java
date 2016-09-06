package com.wuxianyingke.property.threads;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.OrderInfo;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.Promotion;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetUnderwayListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private int mPageIndex;
	private boolean running = true;
	private boolean isRunning = true;
	private int flag=0;
	private long userid;
	public ArrayList<OrderItem>mOrders;
	private OrderInfo retInfo ;
	
	public GetUnderwayListThread(Context mContext, Handler mHandler,
			int mPageIndex, long userid,int flag) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.mPageIndex = mPageIndex;
		this.userid=userid;
		this.flag = flag;
	}
	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<OrderItem> getProductList() {
		return mOrders;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			if(0==flag)//得到未完成订单
				retInfo = rai.getUncompletedOrder(mContext,  userid, mPageIndex);
			else //得到已完成订单
				retInfo =rai.getCompletedOrder(mContext,  userid, mPageIndex);


			if (retInfo != null) {
				/*if (retInfo.netInfo.code != 200
						&& retInfo.netInfo.desc.equals("空数据")) {
					mHandler.sendEmptyMessage(Constants.MSG_NETWORK_ERROR);
					return;
				}*/
				if (!running)
					return;
				mOrders = retInfo.orderInfo;//订单集合
				mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_FINISH);
			} else {
				if (!running)
					return;
				mHandler.sendEmptyMessage(Constants.MSG_NETWORK_ERROR);
				return;
			}
			int count = retInfo.orderInfo.size();//订单个数
			for (int i = 0; i < count; ++i) {
				if (!isRunning)
					return;
				OrderItem orderInfo = retInfo.orderInfo.get(i);
				if (orderInfo.path != null) {
					Drawable dw = null;
					try {
						dw = Util.getDrawableFromCache(mContext, orderInfo.path);
						Log.i("MyLog", "Constants.URL pic.path/"+orderInfo.path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (dw != null) {
						orderInfo.imgDw = dw;
						mHandler.sendEmptyMessage(Constants.MSG_GET_GOODS_IMG_FINISH);
					}
				}
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
