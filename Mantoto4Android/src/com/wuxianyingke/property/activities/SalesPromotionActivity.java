package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.SalesPromotionAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

import java.text.DecimalFormat;
import java.util.ArrayList;
//商家活动
public class SalesPromotionActivity extends BaseActivity {
    private Button topbarLeft;
    private TextView topbarTxt,emptyTv;
    private ListView salePromotionList;
    private int mLivingItemID = 0;

    private SalesPromotionAdapter salesPromotionAdapter;
    private GetCanyinOwnListThread mOwnListThread = null;
    private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //22-GetCanyinOwnListThread
                case Constants.MSG_GET_ACTIVITY_FINISH:
                    if(mOwnListThread!=null&&mOwnListThread.getActivityDetail()!=null){
                        salesPromotionAdapter = new SalesPromotionAdapter(getApplicationContext(),
                                mOwnListThread.getActivityDetail().promotionList);
                        salePromotionList.setAdapter(salesPromotionAdapter);
                        salesPromotionAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_sales_promotion);
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
        salePromotionList = (ListView) findViewById(R.id.sales_activity_list);
        topbarTxt = (TextView) findViewById(R.id.topbar_txt);
        topbarTxt.setText("商家活动");
        topbarTxt.setTextSize(18);
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
        salePromotionList.setVerticalScrollBarEnabled(false);
        salePromotionList.setDivider(getResources().getDrawable(R.drawable.list_line));

    }
}
