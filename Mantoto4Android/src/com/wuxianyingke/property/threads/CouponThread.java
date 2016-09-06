package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

import java.util.ArrayList;
import java.util.List;

public class CouponThread extends Thread {
	private final static String TAG = "MyTag";
	private Handler handler;
	private Context ctx;
	private int pageIndex;
	private int propertyid;
	private long userId;
	private List<RemoteApi.UserCashCoupon> mActivityList;
	private RemoteApi.CashCouponList cashCouponList;
	private boolean running = true;

	public CouponThread(Context ctx, Handler handler, long userId) {
		this.handler = handler;
		this.ctx = ctx;
		this.userId = userId;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public RemoteApi.CashCouponList getActivitys() {
		return cashCouponList;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl remoteApi = new RemoteApiImpl();
			cashCouponList = remoteApi.getListUserCashCoupon(ctx,
					Integer.parseInt("" + userId), 2);
			Message msg = new Message();
			if (cashCouponList == null) {
				msg.what = Constants.MSG_NETWORK_ERROR;
				return;
			} else {
				if (cashCouponList.netInfo.code == 201) {
					msg.what = 3;
				} else {
					mActivityList = cashCouponList.userCashCouponList;
					msg.what = Constants.MSG_MESSAGE_OUT_BOX_FINISH;
					
				}
			}
			handler.sendMessage(msg);
			/*
			 * if (!running) return; Radio3Activity.setAllowGetBoxAgain();
			 */

		} catch (Exception ex) {
			LogUtil.d(TAG, "ActivityThread::Run() error = " + ex.getMessage());
			ex.getMessage();
			if (!running)
				return;
			Message msg = new Message();
			msg.what = Constants.MSG_NETWORK_ERROR;
			handler.sendMessage(msg);
		}
	}
}
