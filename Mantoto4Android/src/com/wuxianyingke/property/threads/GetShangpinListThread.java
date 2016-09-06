package com.wuxianyingke.property.threads;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/21 0021.
 */
public class GetShangpinListThread extends Thread{
    private Context mContext;
    private Handler mHandler;
    private RemoteApi.PromotionList mProductItem;// 促销商品

    private RemoteApi.LivingItemPictureInfo mLivingItemPictureInfo;//生活项图片

    private boolean isRuning = true;
    private int fleaid;

    public ArrayList<Drawable> imgDwList = new ArrayList<Drawable>();

    public GetShangpinListThread(Context context, Handler handler,
                                 int mFleaid) {
        this.mContext = context;
        this.mHandler = handler;
        this.fleaid = mFleaid;



    }
    public RemoteApi.PromotionList getProductDetail() {
        return mProductItem;
    }

    public Drawable getDrawable(int id) {
        return imgDwList.get(id);
    }

    public void stopRun() {
        isRuning = false;
    }


    @Override
    public void run() {
        RemoteApiImpl rai = new RemoteApiImpl();
        mProductItem= rai.getProductByLivingItemId(mContext,fleaid);
        if (!isRuning)
            return;
        if (mProductItem != null ) {
            mHandler.sendEmptyMessage(Constants.MSG_GET_PRODUCT_FINISH);
        }

        int dwid = 0;


        if(mProductItem!= null )
        {
            for (int i = mProductItem.promotionList.size() - 1; i >= 0; --i)

            {
                if (!isRuning)
                    return;
                if (mProductItem.promotionList.get(i).path != null) {
                    Drawable dw = null;
                    try {
                        dw = Util.getDrawableFromCache(mContext,
                                mProductItem.promotionList.get(i).path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (dw != null) {
                        imgDwList.add(dw);
                        Message msg = new Message();
                        msg.what = Constants.MSG_GET_PRODUCT_IMG_FINISH;
                        msg.arg1 = i;
                        msg.arg2 = dwid;
                        dwid++;
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }



    }
}
