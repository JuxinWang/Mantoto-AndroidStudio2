package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.HPagerAdapter;
import com.wuxianyingke.property.fragment.Welcome1Fragment;
import com.wuxianyingke.property.fragment.Welcome2Fragment;
import com.wuxianyingke.property.fragment.Welcome3Fragment;
import com.wuxianyingke.property.fragment.Welcome4Fragment;

/**
 *引导页面
 */
public class WelcomeActivty extends FragmentActivity implements OnClickListener {
	private ViewPager viewPager;
	private LinearLayout dotsLayout;
	private List<Fragment> viewPagers;
	private ImageButton[] images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.activity_welcome);
		initView();

		HPagerAdapter adapter = new HPagerAdapter(getSupportFragmentManager(),
				viewPagers);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new MyListener());
	}

	private class MyListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < images.length; i++) {
				if (i == position) {
					images[i].setImageResource(R.drawable.page);
				} else {
					images[i].setImageResource(R.drawable.page_now);
				}
			}
		}
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.vp_welcome_container);
		dotsLayout = (LinearLayout) findViewById(R.id.ll_welcome_dots);
		viewPagers = new ArrayList<Fragment>();
		viewPagers.add(new Welcome1Fragment());
		viewPagers.add(new Welcome2Fragment());
		viewPagers.add(new Welcome3Fragment());
		viewPagers.add(new Welcome4Fragment());
		images = new ImageButton[viewPagers.size()];
		for (int i = 0; i < images.length; i++) {
			images[i] = new ImageButton(this);
			images[i].setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			images[i].setPadding(10, 0, 10, 0);
			images[i].setBackgroundColor(Color.TRANSPARENT);
			if (i == 0) {
				images[i].setImageResource(R.drawable.page);
			} else {
				images[i].setImageResource(R.drawable.page_now);
			}
			images[i].setId(i);
			images[i].setOnClickListener(this);
			dotsLayout.addView(images[i]);
		}

	}

	@Override
	public void onClick(View v) {
		viewPager.setCurrentItem(v.getId());

	}

}
