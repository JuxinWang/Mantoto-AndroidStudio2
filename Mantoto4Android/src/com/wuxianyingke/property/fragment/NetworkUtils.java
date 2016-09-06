package com.wuxianyingke.property.fragment;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.wuxianyingke.property.common.SDCardUtils;


public class NetworkUtils {

	private Handler mHandler; // 主线程的handler对象
	private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象

	public NetworkUtils(Handler handler) {
		this.mHandler = handler;
	}

	/**
	 * 下载功能方法url请求的地址reqType请求的类型mode请求的模块
	 */
	public void download(final String url, final int reqType) {

		Log.i("info", "---url---" + url);

		// 将网络请求处理的Runnable增加到线程池中
		mExecutor.execute(new Runnable() {

			@Override
			public void run() {

				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {

						byte[] bytes = EntityUtils.toByteArray(response
								.getEntity());

						Message message = Message.obtain();
						message.what = reqType; // 请求的类型

						// 判断请求是否为查询列表或查询详情
						if (reqType == 1
								|| reqType == 3) {
							String jsonTxt = new String(bytes, "utf-8");

							message.obj = jsonTxt; // 请求响应的数据

							Log.i("info", "---json---" + jsonTxt);

						} else {
							Bitmap bitmap = BitmapFactory.decodeByteArray(
									bytes, 0, bytes.length);
							message.obj = bitmap; // 请求响应的数据

							Bundle respData = new Bundle();
							respData.putString("url", url); // 请求的地址，用于查找图片控件

							message.setData(respData);

							// 将图片保存到本地(SDcard)
							SDCardUtils.saveImage(url, bytes);
						}

						mHandler.sendMessage(message); // 将请求响应的数据发送给主线程
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 下载功能方法url请求的地址reqType请求的类型mode请求的模块
	 */
	public void download(final String url, final int reqType, final int modeType) {
		// 将网络请求处理的Runnable增加到线程池中
		mExecutor.execute(new Runnable() {

			@Override
			public void run() {

				try {
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(url);
					HttpResponse response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {

						byte[] bytes = EntityUtils.toByteArray(response
								.getEntity());

						Message message = Message.obtain();
						message.what = reqType; // 请求的类型
						message.arg1 = modeType; // 请求模块

						// 判断请求是否为查询列表或查询详情
						if (reqType == 1
								|| reqType == 3) {
							String jsonTxt = new String(bytes, "utf-8");

							message.obj = jsonTxt; // 请求响应的数据

						} else {
							Bitmap bitmap = BitmapFactory.decodeByteArray(
									bytes, 0, bytes.length);
							message.obj = bitmap; // 请求响应的数据

							Bundle respData = new Bundle();
							respData.putString("url", url); // 请求的地址，用于查找图片控件

							message.setData(respData);

							// 将图片保存到本地(SDcard)
							SDCardUtils.saveImage(url, bytes);
						}

						mHandler.sendMessage(message); // 将请求响应的数据发送给主线程
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void checkNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			setDialog(context,"当前未连接任何网络哦 ╯﹏╰", "不连接", "马上连接", 1);
		}
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			setDialog(context,"继续浏览会消耗流量，是否切换为WIFI模式", "不切换", "马上切换", 2);
		}
	}

	
	/**
	 * 弹出对话框的方法message对话框显示的内容nbString取消按钮的文字pbString确定按钮的文字flag区分操作的标识
	 */
	private static void setDialog(final Context context,String message, String nbString, String pbString,
			final int flag) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("温馨提示");
		dialog.setMessage(message);
		dialog.setNegativeButton(nbString, null).setPositiveButton(pbString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (flag) {
						case 1:
							Intent intent1 = new Intent(
									Settings.ACTION_WIRELESS_SETTINGS);
							context.startActivity(intent1);
							break;
						case 2:
							Intent intent2 = new Intent(
									Settings.ACTION_WIFI_SETTINGS);
							context.startActivity(intent2);
							break;
						}
					}
				});
		dialog.create();
		dialog.show();
	}

	
}
