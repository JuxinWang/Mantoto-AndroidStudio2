package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.LocalStore;

/**
 * Created by Administrator on 2015/1/6 .
 */
public class PayFailActivity extends BaseActivity implements View.OnClickListener{
    private Button topBar_left;
    private TextView topBar_text;
    private Button  confirmBtn;
    private ViewPager mViewpager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_fail);
        setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
        PushAgent.getInstance(getApplicationContext()).onAppStart();

        initView();
    }

    private void initView(){
        //初始化标题
        topBar_left=(Button)findViewById(R.id.topbar_left);
        topBar_text=(TextView)findViewById(R.id.topbar_txt);
        topBar_left.setVisibility(View.VISIBLE);
        topBar_text.setVisibility(View.VISIBLE);
        topBar_text.setText("支付失败");
        confirmBtn=(Button)findViewById(R.id.payButton);
    }

    public void onClick(View view){
    	if (LocalStore.getPayFlag(getApplicationContext())!=1) {
    		 Intent intent = new Intent();
    	       intent.setClass(PayFailActivity.this, RechargeActivity.class);
    	       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	       intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	       startActivity(intent);
		}
      
      switch (view.getId()){
          case R.id.topbar_left:
              
              finish();
              break;
          case R.id.payButton:
              finish();
              break;
      }
    }
}
