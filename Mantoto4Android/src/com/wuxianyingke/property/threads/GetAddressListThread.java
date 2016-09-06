package com.wuxianyingke.property.threads;

import java.util.ArrayList;

import android.content.Context;
import android.location.Address;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.AddressAll;
import com.wuxianyingke.property.remote.RemoteApi.AddressItem;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetAddressListThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private long userid;
	private boolean running = true;
	private ArrayList<AddressItem> mAddress;



	public GetAddressListThread(Context mContext, Handler mHandler, long userid) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.userid = userid;
	}
	public GetAddressListThread(Context mContext, long userid) {
		super();
		this.mContext = mContext;

		this.userid = userid;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public ArrayList<AddressItem> getAddress() {
		return mAddress;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl rai = new RemoteApiImpl();
			AddressAll lAddress = rai.getAllAddress(mContext,userid);
			Log.i("MyLog","lAddress-----------------"+lAddress.addressItems.get(0).Recipient);
			if (lAddress != null) {

				if (!running)
				{
					return;
				}
					
				mAddress=lAddress.addressItems;
				Log.i("MyLog","mAddress-----------------"+mAddress.get(0).Recipient);
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
