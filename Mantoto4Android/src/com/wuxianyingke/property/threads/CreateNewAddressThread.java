package com.wuxianyingke.property.threads;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.CreateAddress;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class CreateNewAddressThread extends Thread {
	private final static String TAG = "MyTag";
	private Context mContext;
	private Handler mHandler;
	private long userid;
	private String recipient;
	private String telnumber;
	private String cityarea;
	private String detail;
	private boolean isdefault;
	private boolean running = true;
//	private boolean isRunning = true;
	private List<CreateAddress> mAddress;

	public CreateNewAddressThread(Context mContext, Handler mHandler,
			long userid, String recipient, String telnumber, String cityarea,
			String detail, boolean isdefault) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.userid = userid;
		this.recipient = recipient;
		this.telnumber = telnumber;
		this.cityarea = cityarea;
		this.detail = detail;
		this.isdefault = isdefault;
	}

	public synchronized void stopRun() {
		running = false;
		this.interrupt();

	}

	public CreateAddress getAddress() {
		return (CreateAddress) mAddress;
	}

	public void run() {
		try {
			running = true;
			RemoteApiImpl remote = new RemoteApiImpl();
		CreateAddress cAddress = remote.createNewAddress(
					mContext, userid, recipient,
					telnumber, cityarea, detail, isdefault);
			CreateAddress createAddress=new CreateAddress();
			if (cAddress != null) {
				if (createAddress.netInfo.code != 200
						&& createAddress.netInfo.desc.equals("空数据")) {
					mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_EMPTY);
					return;
				}
				if (!running)
					return;
				mAddress = (List<CreateAddress>) cAddress;
				mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_LIST_FINISH);
			} else {
				if (!running)
					return;
				mHandler.sendEmptyMessage(Constants.MSG_NETWORK_ERROR);
				return;
			}

//			int count = mAddress.size();
//			for (int i = 0; i < count; ++i) {
//				if (!isRunning)
//					return;
//				LivingItemPicture pic = mAddress.get(i).FrontCover;
//				if (pic.path != null) {
//					Drawable dw = null;
//					try {
//						dw = Util.getDrawableFromCache(mContext, pic.path);
//						Log.d("MyTag", "Constants.URL pic.path/"+pic.path);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					if (dw != null) {
//						pic.imgDw = dw;
//						mHandler.sendEmptyMessage(Constants.MSG_GET_CANYIN_DETAIL_IMG_FINISH);
//					}
//				}
//			}
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
