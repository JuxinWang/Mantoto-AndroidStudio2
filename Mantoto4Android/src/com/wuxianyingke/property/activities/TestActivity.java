package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.TestAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

import java.util.ArrayList;

public class TestActivity extends Activity {
    private GridView girdView;
    private TestAdapter testAdapter;
    private int mLivingItemID = 0;
    private ImageView canyinImg;

    private GetCanyinOwnListThread mOwnListThread = null;
    private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();
    private ArrayList<ImageView> productImgList = new ArrayList<ImageView>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_GET_PRODUCT_FINISH:
                    if (mOwnListThread != null && mOwnListThread.getProductDetail() != null) {
                        testAdapter = new TestAdapter(getApplicationContext(),
                                mOwnListThread.getProductDetail().promotionList);
                        girdView.setAdapter(testAdapter);
                        testAdapter.notifyDataSetChanged();
                    }
                        break;

            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        girdView = (GridView) findViewById(R.id.testlist);
        girdView.setNumColumns(2);
        canyinImg = (ImageView)findViewById(R.id.canyinImg);
        if(savedInstanceState != null){
            mLivingItemID=savedInstanceState.getInt("mLivingItemID");

        }else{
            mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);

        }

        mOwnListThread = new GetCanyinOwnListThread(this, mHandler,
                mLivingItemID);
        mOwnListThread.start();

    }
}
