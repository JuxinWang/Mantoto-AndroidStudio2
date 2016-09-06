package com.wuxianyingke.property.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HPagerAdapter extends FragmentPagerAdapter{
	private List<Fragment> viewPagers ;
	public HPagerAdapter(FragmentManager fm ,List<Fragment> viewPagers ) {
		super(fm);
		this.viewPagers = viewPagers;
	}
	@Override
	public Fragment getItem(int position) {
		return viewPagers.get(position);
	}
	@Override
	public int getCount() {
		return viewPagers.size();
	}
}
