package com.wuxianyingke.property.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.views.UpdateProgressBar;

public class UpdateManger {

	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	/* 应用下载地址 */
	private String mUrl;
	/* 应用更新信息 */
	private String mUpdateInfo;
	/* 应用更新版本号 */
	private int mVersionCode;
	/* 新应用版本号 */
	private String mAppversion;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManger(Context mContext, String url, String updateInfo,
			int versionCode, String appversion) {
		super();
		this.mContext = mContext;
		this.mUrl = url;
		this.mUpdateInfo = updateInfo;
		this.mVersionCode = versionCode;
		this.mAppversion = appversion;
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate() {
		if (isUpdate()) {
			// 显示提示对话框
			showNoticeDialog();
		} else {
//			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG)
//					.show();
		}
	}

	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private boolean isUpdate() {
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		if (mVersionCode > versionCode) {
			return true;
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					"com.mantoto.property", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		/*
		 * // 构造对话框 AlertDialog.Builder builder = new Builder(mContext);
		 * builder.setTitle(R.string.soft_update_title);
		 * builder.setMessage(R.string.soft_update_info); // 更新
		 * builder.setPositiveButton(R.string.soft_update_updatebtn, new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * dialog.dismiss(); // 显示下载对话框 showDownloadDialog(); } }); // 稍后更新
		 * builder.setNegativeButton(R.string.soft_update_later, new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * dialog.dismiss(); } }); Dialog noticeDialog = builder.create();
		 * noticeDialog.show();
		 */
		final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.update_dialog);
		TextView updateVersion = (TextView) window
				.findViewById(R.id.update_find_new_version);
		updateVersion.setText("发现新版本" + mAppversion);
		TextView versionInfo = (TextView) window
				.findViewById(R.id.update_find_new_version_info);
		versionInfo.setText(mUpdateInfo);
		versionInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
		Button concle = (Button) window.findViewById(R.id.update_btn_cancel);
		Button confirm = (Button) window.findViewById(R.id.update_btn_confirm);
		concle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alertDialog.dismiss();
				LocalStore.setIsUpload(mContext, false);
			}
		});
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LocalStore.setIsUpload(mContext, false);
				alertDialog.dismiss();
				showDownloadDialog();
			}
		});
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder build = new Builder(mContext);
		build.setTitle(R.string.soft_updating);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		build.setView(v);
		build.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				cancelUpdate = true;
				LocalStore.setIsUpload(mContext, true);
			}
		});
		mDownloadDialog = build.create();
		mDownloadDialog.show();
		// 现在文件
		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mUrl);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, "mantoto");
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				Log.i("MyLog", "MalformedURLException = " + e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				Log.i("MyLog", "ixException = " + e.getMessage());
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, "mantoto");
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
