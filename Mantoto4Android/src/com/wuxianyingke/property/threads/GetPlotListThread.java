package com.wuxianyingke.property.threads;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/11 0011.
 */
public class GetPlotListThread extends Thread{
    private final static String TAG = "MyTag";
    private Context mContext;
    private Handler mHandler;
    private float latitude;
    private float longitude;
    private int pageindex;
    private boolean running = true;
    private ArrayList<RemoteApi.Propertys>  myPropertys;

    public GetPlotListThread(Context mContext, Handler mHandler, float latitude,
     float longitude,int pageindex){
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pageindex = pageindex;
    }

    public synchronized void stopRun() {
        running = false;
        this.interrupt();

    }

    public ArrayList<RemoteApi.Propertys> getMyPropertys() {
        return myPropertys;
    }

    public void run(){
        running =true;
        RemoteApiImpl remoteApi = new RemoteApiImpl();
        ArrayList<RemoteApi.Propertys> myProperty =
                remoteApi.getPropertyList(mContext,latitude,longitude,pageindex);
        Log.i("MyLog","lAddress-----------------"+myPropertys.get(0).PropertyName);
        if(myPropertys!=null){
            if (!running)
            {
                return;
            }
            myPropertys = myProperty;
            Log.i("MyLog","mAddress-----------------"+myProperty.get(0).PropertyName);


        }else{

        }
    }

}
