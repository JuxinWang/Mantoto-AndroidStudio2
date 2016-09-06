package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.GetVoucherQCodeListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.threads.GetPromotionCodeThread;

public class CouponCodeActivity extends Activity {
    private Button topLeft;
    private TextView topTxt;
    private ListView mListView;

    private GetPromotionCodeThread mThread;
    private long ordersequencenumber;
    private GetVoucherQCodeListAdapter mAdapter;
    private String orderId;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_GET_CANYIN_LIST_FINISH:
                    mAdapter=new GetVoucherQCodeListAdapter(getApplicationContext(), mThread.mPromotionCode, orderId);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    break;
                case Constants.MSG_NETWORK_ERROR:
                    Toast.makeText(getApplicationContext(), "网络连接出错请刷新", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_code);

        initWidget();
        Intent intent = getIntent();
        ordersequencenumber = intent.getLongExtra("head",0);
        orderId=intent.getStringExtra("OrderID");
        mThread = new GetPromotionCodeThread(getApplicationContext(), mHandler,
                ordersequencenumber);
        mThread.start();


    }

    private void initWidget() {
        topLeft = (Button) findViewById(R.id.topbar_left);
        topTxt = (TextView) findViewById(R.id.topbar_txt);
        topLeft.setVisibility(View.VISIBLE);
        topTxt.setVisibility(View.VISIBLE);
        topLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topTxt.setText("二维码");

        mListView=(ListView) findViewById(R.id.quan_Ma_listViewId);

    }
}
