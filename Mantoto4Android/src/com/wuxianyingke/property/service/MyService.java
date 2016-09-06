package com.wuxianyingke.property.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.wuxianyingke.property.activities.WIFILoginActivity;

public class MyService extends Service{
        CommandReceiver cmdReceiver;
        boolean flag;
        private int flags=1;
        @Override
        public void onCreate() {//重写onCreate方法
                flag = true;
                cmdReceiver = new CommandReceiver();
                super.onCreate();
           
        }
        @Override
        public IBinder onBind(Intent intent) {//重写onBind方法
                // TODO Auto-generated method stub
                return null;
        }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {//重写onStartCommand方法
        	
                IntentFilter filter = new IntentFilter();//创建IntentFilter对象
                filter.addAction("wyf.wpf.MyService");
                registerReceiver(cmdReceiver, filter);//注册Broadcast Receiver
                doJob();//调用方法启动线程
                return super.onStartCommand(intent, flags, startId);
        }
        //方法：
        public void doJob(){
                new Thread(){
                        public void run(){
                                while(flag){
                                        try{//睡眠一段时间
                                                Thread.sleep(1000);
                                        }
                                        catch(Exception e){
                                                e.printStackTrace();
                                        }
                                        Intent intent = new Intent();//创建Intent对象
                                        intent.setAction("wyf.wpf.Sample_3_6");
                                        intent.putExtra("data",flags );
                                        sendBroadcast(intent);//发送广播
                                }                                
                        }
                        
                }.start();
        }        
        private class CommandReceiver extends BroadcastReceiver{//继承自BroadcastReceiver的子类
                @Override
                public void onReceive(Context context, Intent intent) {//重写onReceive方法
                   
                        int cmd = intent.getIntExtra("cmd", -1);//获取Extra信息
                        if(cmd == WIFILoginActivity.CMD_STOP_SERVICE){//如果发来的消息是停止服务                                
                                flag = false;//停止线程
                                stopSelf();//停止服务
                        }
                }                
        }
        @Override
        public void onDestroy() {//重写onDestroy方法
                this.unregisterReceiver(cmdReceiver);//取消注册的CommandReceiver
                super.onDestroy();
        }        
}