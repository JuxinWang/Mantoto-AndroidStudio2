package com.wuxianyingke.property.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/8/23.
 */
public class TestAdapter  extends BaseAdapter {
    private ArrayList<RemoteApi.Promotion> mList;
    private Context mContext;
    private boolean mStoped;
    private int mCount;
    /**启用线程池下载网络数据*/
    private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
    public TestAdapter(Context ctx, ArrayList<RemoteApi.Promotion> list)
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
        return mList.get(position);
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
        if (convertView == null){

            View v =  LayoutInflater.from(mContext).inflate(
                    R.layout.canyin_detail_own_content, null);
            productItem = new ProductItem();
            productItem.mProductTitle = (TextView) v.findViewById(R.id.canyin_title);
            productItem.mProductPic = (ImageView) v.findViewById(R.id.canyinImg);
            productItem.mProductDesc = (TextView) v.findViewById(R.id.canyin_desc);
            productItem.mProductPrice = (TextView) v.findViewById(R.id.canyin_price);
            productItem.mProductbuy = (ImageView) v.findViewById(R.id.goumai);
            v.setTag(productItem);
            convertView = v;


        }else {
            productItem = (ProductItem) convertView.getTag();
            productItem.mProductPic.setImageResource(R.drawable.login_top); // 重置图片控件
        }

        productItem.mProductTitle.setText(info.header);
        productItem.mProductDesc.setText(info.body);

     /*   if(productItem.mProductPic==null){
            productItem.mProductPic.setImageResource(R.drawable.peisong);
        }

        Bitmap bm = BitmapFactory.decodeFile(info.path);
       // Drawable drawable =new BitmapDrawable(bm);
        //productItem.mProductPic.setImageDrawable(drawable);
        productItem.mProductPic.setImageBitmap(bm);*/
        String url = info.path;
        if(url==null){
            productItem.mProductPic.setImageResource(R.drawable.login_top);
        }else{
            // 从SDCard中读取图片
            Bitmap bitmap = SDCardUtils.readImage(url);
            if (bitmap != null) {
                productItem.mProductPic.setImageBitmap(bitmap);
            }else {
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

    class ProductItem
    {
        LinearLayout mItemBackground;
        ImageView mProductPic;
        TextView mProductTitle;
        TextView  mProductPrice;
        TextView  mProductDesc;
        ImageView mProductbuy;



    }
}
