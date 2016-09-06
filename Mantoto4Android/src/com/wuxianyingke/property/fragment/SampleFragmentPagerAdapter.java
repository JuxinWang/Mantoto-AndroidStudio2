package com.wuxianyingke.property.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter{
    private static final String[] CONTENT = new String[]{
      "未完成","已完成","全部订单"
    };
    private Context mContext;
    public SampleFragmentPagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return SampleFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return new String[]{"未付订单","已付订单","全部订单"}[position];
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }
}
