package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;

public class CopyOfUserCenterActivity extends Activity {

	private boolean mFromMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.help);
		initWidget();
	}

	public void initWidget() {
		Button backbutton = (Button) findViewById(R.id.topbar_left);
		if (!mFromMore) {
			backbutton.setText(R.string.txt_back);
		}
		backbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}
