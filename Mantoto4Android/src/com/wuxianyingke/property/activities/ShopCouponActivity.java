package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;

public class ShopCouponActivity extends BaseActivity {
    private Button topbarLeft;
    private TextView topbarTxt;
    private LinearLayout shopCoupon;
    private ScrollView mAllViewSv = null;

    private int mLivingItemID = 0;
    private int propertyid;
    private String source = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_coupon);

        setImmerseLayout(findViewById(R.id.common_back));
        propertyid= LocalStore.getUserInfo().PropertyID;

        shopCoupon = (LinearLayout) findViewById(R.id.shop_coupon);
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

        mAllViewSv = (ScrollView) findViewById(R.id.all_view_sv);

        if(savedInstanceState != null){
            mLivingItemID=savedInstanceState.getInt("mLivingItemID");
            source=savedInstanceState.getString("source");


        }else{
            mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);
            source = getIntent().getStringExtra(Constants.CANYIN_SOURCE_ACTION);

        }


    }
}
