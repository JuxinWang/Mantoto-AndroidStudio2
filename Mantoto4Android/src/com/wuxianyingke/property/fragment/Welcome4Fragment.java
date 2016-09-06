package com.wuxianyingke.property.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.mantoto.property.R;
import com.wuxianyingke.property.activities.LoginActivity;
import com.wuxianyingke.property.common.LocalStore;

public class Welcome4Fragment extends Fragment {
	private ImageView image;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_welcome4, container,false);
		/*//3秒自动跳
		final Intent intent = new Intent(getActivity(),LoginActivity.class);
		Timer timer = new Timer();
		TimerTask task = new TimerTask(){

			@Override
			public void run() {
				startActivity(intent);
				
			}
			
		};
		timer.schedule(task,1000*5);//5秒后跳
		*/
		image=(ImageView) view.findViewById(R.id.imgId);
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getActivity(),
						LoginActivity.class);
				startActivity(intent);
				//保存进入欢迎界面的状态
//				SPUtils.set(getActivity(), "isEnter", true);
				getActivity().finish();
				LocalStore.setWelcomeId(getActivity().getApplicationContext(), 1);
			}
		});
		return view;
	}
}
