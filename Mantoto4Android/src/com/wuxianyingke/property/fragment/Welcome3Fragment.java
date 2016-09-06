package com.wuxianyingke.property.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantoto.property.R;

public class Welcome3Fragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_welcome3, container,false);
//		enterHome = (HImageButton) view.findViewById(R.id.ib_welcome3_enter);
//	
//			enterHome.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(getActivity(),MainActivity.class);
//					startActivity(intent);
//
//					//保存进入欢迎界面的状态
//					SPUtils.set(getActivity(), "isEnter", true);
//					
//					getActivity().finish();
//				}
//			});
		return view;
	}
}
