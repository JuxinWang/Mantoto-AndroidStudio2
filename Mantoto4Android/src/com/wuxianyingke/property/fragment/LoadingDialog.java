package com.wuxianyingke.property.fragment;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog extends ProgressDialog{

	public LoadingDialog(Context context){
		super(context);
		
		setTitle("提示");
		setMessage("正在拼命加载...");
		setCanceledOnTouchOutside(false);
	}
	
	
	
	
}
