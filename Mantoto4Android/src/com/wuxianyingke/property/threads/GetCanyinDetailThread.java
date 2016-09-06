package com.wuxianyingke.property.threads;

import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi.LivingItem;
import com.wuxianyingke.property.remote.RemoteApi.LivingItemPictureInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class GetCanyinDetailThread extends Thread {

	private Context mContext;
	private Handler mHandler;
	private LivingItem mLivingItem;
	private LivingItemPictureInfo mLivingItemPictureInfo;
	private boolean isRuning = true;
	private int fleaid;
	private int propertyid;
	private String source;
	private double latitude,longitude;


	public GetCanyinDetailThread(Context context, Handler handler,
								 int mPropertyid,int mFleaid,String mSource,double latitude,double longitude) {
		this.mContext = context;
		this.mHandler = handler;
		this.fleaid = mFleaid;
		this.source = mSource;
		this.propertyid = mPropertyid;
		this.latitude = latitude;
		this.longitude = longitude;
		Log.d("MyTag","GetProductDetailThread=fleaid="+fleaid+"/propertyid="+propertyid);
	}

	public LivingItem getProductDetail() {
		return mLivingItem;
	}

	public void stopRun() {
		isRuning = false;
	}

	public void run() {
		RemoteApiImpl rai = new RemoteApiImpl();
		mLivingItem = rai.getLivingItemsByLivingItemId(mContext,fleaid,source,propertyid,latitude,longitude) ;
		mLivingItemPictureInfo=rai.getLivingItemsPictureByLivingItemId(mContext,fleaid,source,propertyid) ;
		if (!isRuning)
			return;
		if (mLivingItem != null ) {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_FINISH);

		} else {
			mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR);
			return;
		}


		mLivingItem.livingItemPicture=mLivingItemPictureInfo.livingItemPicture;
		if(mLivingItemPictureInfo.livingItemPicture!=null) {
			int imgCount = mLivingItemPictureInfo.livingItemPicture.size();

		if(imgCount>0) {

			for (int i = imgCount - 1; i >= 0; --i) {
				if (!isRuning)
					return;
				if (mLivingItemPictureInfo.livingItemPicture.get(i).path != null) {
					Drawable dw = null;
					try {
						dw = Util.getDrawableFromCache(mContext,
								mLivingItemPictureInfo.livingItemPicture.get(i).path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (dw != null) {
						mLivingItem.livingItemPicture.get(i).imgDw = dw;
						Message msg = new Message();
						msg.what = Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH;
						msg.arg1 = i;
						mHandler.sendMessage(msg);
					}
				}
			}
			}
		}
	}
}
