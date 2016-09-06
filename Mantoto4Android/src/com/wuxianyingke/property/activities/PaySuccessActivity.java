package com.wuxianyingke.property.activities;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.LocalStore;

/**
 * Created by Administrator on 2015/1/6 .
 */
public class PaySuccessActivity extends BaseActivity implements View.OnClickListener{
    private Button topBar_left;
    private TextView topBar_text;
    private Button  confirmBtn;
    private TextView successMoney;
    private TextView successTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
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
        topBar_text.setText("支付成功");
        confirmBtn=(Button)findViewById(R.id.payButton);
        successMoney=(TextView)findViewById(R.id.successMoney);
        successMoney.setText(""+LocalStore.getWifiPayMoney(PaySuccessActivity.this)+"元");
      /*  successTime = (TextView) findViewById(R.id.successTime);
        Time time = new Time("GMT+8");       
        time.setToNow();      
        int year = time.year;      
        int month = time.month;      
        int day = time.monthDay;      
        int minute = time.minute;      
        int hour = time.hour;      
        int sec = time.second;  
        
        successTime.setText(""+year+"年"+month+1+"月"+day+"日"+" "+hour+"："+minute+":"+sec);*/
    }

    public void onClick(View view){
        	Intent intent = new Intent();
            intent.setClass(PaySuccessActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        
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
