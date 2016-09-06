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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetCanyinOwnListThread;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SalesPromotionActivity extends BaseActivity {
    private Button topbarLeft;
    private TextView topbarTxt;
    private LinearLayout salePromotion;
    private ScrollView mAllViewSv = null;
    private int mLivingItemID = 0;
    private long promotionId;
    private  int promotionTypeID;
    private GetCanyinOwnListThread mOwnListThread = null;
    private ArrayList<ImageView> activityImgList = new ArrayList<ImageView>();

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //22-GetCanyinOwnListThread
                case Constants.MSG_GET_ACTIVITY_FINISH:
                    if (mOwnListThread != null
                            && mOwnListThread.getActivityDetail() != null) {
                        RemoteApi.PromotionList list = mOwnListThread.getActivityDetail();
                        for (int i = 0; i < list.promotionList.size(); i++) {
                            final RemoteApi.Promotion promotion = list.promotionList.get(i);
                            promotionId = list.promotionList.get(i).PromotionID;
                            promotionTypeID = list.promotionList.get(i).PromotionTypeID;
                            Log.i("MyLog", "MSG_GET_ACTIVITY_FINISH.PromotionID=" + list.promotionList.get(i).PromotionID);

                                View v = getLayoutInflater().inflate(
                                        R.layout.canyin_detail_own_content, null);
                                int serverImageWidth = promotion.Width;
                                int serverImageHeight = promotion.Height;

                                TextView canyin_title = (TextView) v
                                        .findViewById(R.id.canyin_title);
                                canyin_title.setText(promotion.header);

                            /*图片*/
                                ImageView canyinImg = (ImageView) v
                                        .findViewById(R.id.canyinImg);
                                canyinImg.setScaleType(ImageView.ScaleType.FIT_XY);
                                Display display = getWindowManager()
                                        .getDefaultDisplay();
                                ViewGroup.LayoutParams params = canyinImg.getLayoutParams();
                                params.width = display.getWidth() - 20;
                                if (serverImageWidth != 0)
                                    params.height = (display.getWidth() - 20)
                                            * serverImageHeight / serverImageWidth;

                                Log.i("MyTag", "图片尺寸: 宽度 = " + params.width + "高度 = :"
                                        + params.height);
                                canyinImg.setLayoutParams(params);
                                canyinImg.setVisibility(View.GONE);

                                activityImgList.add(canyinImg);

                                TextView canyin_price = (TextView) v
                                        .findViewById(R.id.canyin_price);
                                Button goumai = (Button) v.findViewById(R.id.goumaiImg);
                                if (promotion.ForSal) {
                                    DecimalFormat df = new DecimalFormat("0.00");
                                    Log.i("MyLog", "promotion.Price=" + promotion.Price);
                                    canyin_price
                                            .setText("￥ " + df.format(promotion.Price));

                                    goumai.setVisibility(View.VISIBLE);
                                    goumai.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (LocalStore
                                                    .getIsVisitor(getApplicationContext())) {
                                                Toast.makeText(getApplicationContext(),
                                                        "游客或者未认证用户无法进行购买",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(
                                                        SalesPromotionActivity.this,
                                                        LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(
                                                        SalesPromotionActivity.this,
                                                        CommitOrderActivity.class);

                                                Bundle bundle = new Bundle();
                                                bundle.putDouble("price",
                                                        promotion.Price);
                                                bundle.putString("name",
                                                        promotion.header);
                                                bundle.putLong("promotionid", promotion.PromotionID);
                                                bundle.putInt("SaleTypeId", promotion.SaleTypeID);

                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }

                                TextView canyin_desc = (TextView) v.findViewById(R.id.canyin_desc);
                                canyin_desc.setText(promotion.body);

                            //if(promotionTypeID==2) {
                                salePromotion.addView(v);
                           // }
                        }
                    }
                    break;
                case Constants.MSG_GET_ACTIVITY_IMG_FINISH://22-GetCanyinOwnListThread
                {
                    ImageView canyinImg = activityImgList.get(msg.arg1);
                    // Log.i("ACTIVITY_IMG_FINISH", "图片尺寸: 宽度 = " +
                    // canyinImg.getWidth() + "高度 = :" + canyinImg.getHeight());
                    ViewGroup.LayoutParams pr = canyinImg.getLayoutParams();

                    canyinImg
                            .setImageDrawable(mOwnListThread.getDrawable(msg.arg2));
                    canyinImg.setLayoutParams(pr);
                    canyinImg.setVisibility(View.VISIBLE);
                }
                break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_promotion);
        setImmerseLayout(findViewById(R.id.common_back));

        salePromotion = (LinearLayout) findViewById(R.id.product_sale_promotion);
        topbarTxt = (TextView) findViewById(R.id.topbar_txt);
        topbarTxt.setText("商家活动");
        topbarTxt.setVisibility(View.VISIBLE);
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarLeft.setVisibility(View.VISIBLE);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAllViewSv = (ScrollView) findViewById(R.id.all_view_svs);

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
