package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.SalesPromotionAdapter;
import com.wuxianyingke.property.adapter.ShopCouponsAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

//商家劵
public class ShopCouponsActivity extends BaseActivity {
    private Button topbarLeft;
    private TextView topbarTxt,emptyTv;
    private ListView shopCouponsList;
    private int mLivingItemID = 0;

    private GetCanyinOwnListThread mOwnListThread = null;
    private ShopCouponsAdapter shopCouponsAdapter;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //22-GetCanyinOwnListThread
                case Constants.MSG_GET_ACTIVITY_FINISH:
                    if(mOwnListThread!=null&&mOwnListThread.getPromotionDetail()!=null){
                        shopCouponsAdapter = new ShopCouponsAdapter(getApplicationContext(),
                                mOwnListThread.getPromotionDetail().promotionList);
                        shopCouponsList.setAdapter(shopCouponsAdapter);
                        shopCouponsAdapter.notifyDataSetChanged();
                    }
                    break;
                case Constants.MSG_GET_ACTIVITY_IMG_FINISH://22-GetCanyinOwnListThread

                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_coupons);
        setImmerseLayout(findViewById(R.id.common_back));

        initWidget();


        if(savedInstanceState != null){
            mLivingItemID=savedInstanceState.getInt("mLivingItemID");

        }else{
            mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);

        }

        mOwnListThread = new GetCanyinOwnListThread(this, mHandler,
                mLivingItemID);
        mOwnListThread.start();

    }

    private void initWidget() {
        topbarTxt = (TextView) findViewById(R.id.topbar_txt);
        topbarTxt.setText("商家劵");
        topbarTxt.setVisibility(View.VISIBLE);
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarLeft.setVisibility(View.VISIBLE);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        emptyTv = (TextView) findViewById(R.id.empty_tv);
        shopCouponsList = (ListView) findViewById(R.id.shop_coupon_list);
    }
}
