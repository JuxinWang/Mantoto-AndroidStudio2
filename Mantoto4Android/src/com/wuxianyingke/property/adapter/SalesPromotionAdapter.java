package com.wuxianyingke.property.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CommitOrderActivity;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.remote.RemoteApi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/9/7 0007.
 */
public class SalesPromotionAdapter extends BaseAdapter{
    private ArrayList<RemoteApi.Promotion> mList;
    private Context mContext;
    private boolean mStoped;
    /**启用线程池下载网络数据*/
    private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
    public SalesPromotionAdapter(Context ctx, ArrayList<RemoteApi.Promotion> list)
    {
        this.mContext = ctx;
        this.mList = list;
        this.mStoped = false;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return  mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mStoped)
            return convertView;

        ProductItem productItem;
        final RemoteApi.Promotion info = mList.get(position);

        if (convertView == null) {
            View v = LayoutInflater.from(mContext).inflate(
                    R.layout.sales_promotion_item, null);
            productItem = new ProductItem();
            productItem.mProductTitle = (TextView) v.findViewById(R.id.canyin_title);
            productItem.mProductPic = (ImageView) v.findViewById(R.id.canyinImg);
            productItem.mProductDesc = (TextView) v.findViewById(R.id.canyin_desc);
            productItem.mProductPrice = (TextView) v.findViewById(R.id.canyin_price);
            productItem.mProductbuy = (Button) v.findViewById(R.id.goumaiImg);
            productItem.mItemBackground = (LinearLayout) v.findViewById(R.id.mItemBackground);
            v.setTag(productItem);
            convertView = v;
        } else {
            productItem = (ProductItem) convertView.getTag();
            productItem.mProductPic.setImageResource(R.drawable.login_top); // 重置图片控件
        }


            productItem.mProductTitle.setText("【" + info.header + "】");
            productItem.mProductDesc.setText(info.body);
            if (info.ForSal) {
                DecimalFormat df = new DecimalFormat("0.00");
                Log.i("MyLog", "productItem.mProductPrice" + productItem.mProductPrice);
                productItem.mProductPrice.setText("¥" + df.format(info.Price));
                productItem.mProductbuy.setVisibility(View.VISIBLE);
                productItem.mProductbuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LocalStore.setPromotionId(mContext, info.PromotionID);
                        Log.i("MyLog", "所有的活动商品信息-----PromotionID=" + info.PromotionID);
                        Intent intent = new Intent(
                                mContext,
                                CommitOrderActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("price",
                                info.Price);
                        bundle.putString("name",
                                info.header);
                        bundle.putLong("promotionid",
                                info.PromotionID);
                        bundle.putInt("SaleTypeId", info.SaleTypeID);
                        Log.i("MyLog", "当前的SaleTypeId的标记为：----promotion:" + info.SaleTypeID);
                        intent.putExtras(bundle);

                        //mContext.startActivity(intent);
                        Log.i("MyLog",
                                "promotionid------" + info.PromotionID);
                    }
                });

            }

            Log.d("MyTags", info.PromotionID + "+++++++PromotionID+++hahahhahhahahah++++++++++++++++");
            Log.d("MyTags", info.PromotionID + "+++++++++PromotionID+hahahahhahahahahahah++++++++++++++");

            String url = info.path;
            if (url == null) {
                productItem.mProductPic.setImageResource(R.drawable.login_top);
            } else {
                // 从SDCard中读取图片
                Bitmap bitmap = SDCardUtils.readImage(url);
                if (bitmap != null) {
                    productItem.mProductPic.setImageBitmap(bitmap);
                } else {
//				String url=imageUrl;
                    productItem.mProductPic.setImageResource(R.drawable.login_top);
                    productItem.mProductPic.setTag(url); // 将图片的地址作为图片控件的标签

                    // 网络下载
                    download(url);
                }
            }


            return convertView;
        }


    public void download(final String url) {

        // 将网络请求处理的Runnable增加到线程池中
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {

                        byte[] bytes = EntityUtils.toByteArray(response
                                .getEntity());
                        //保存图片到本地
                        SDCardUtils.saveImage(url, bytes);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ProductItem{
        LinearLayout mItemBackground;
        ImageView mProductPic;
        TextView mProductTitle;
        TextView  mProductPrice;
        TextView  mProductDesc;
        Button mProductbuy;



    }
}
