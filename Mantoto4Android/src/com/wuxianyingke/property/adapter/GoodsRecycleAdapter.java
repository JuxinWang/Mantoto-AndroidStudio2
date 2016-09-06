package com.wuxianyingke.property.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wuxianyingke.property.remote.RemoteApi;

import java.util.List;

/**
 * Created by Administrator on 2016/8/24 0024.
 */
public class GoodsRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /** item 的类型 */
    private static final int ITEM_TYPE_SHOPS = 0;//商品
    private static final int ITEM_TYPE_COUPON_NOPIC = 1;//商家券 （无图）
    private static final int ITEM_TYPE_COUPON = 2;//商家券 有图
    private static final int ITEM_TYPE_ACTIVE = 3; //活动
    private Context mContext;
    private int promotionType;
    private List<RemoteApi.Promotion> mList;

    public GoodsRecycleAdapter(Context mContext, int promotionType, List<RemoteApi.Promotion> mList) {
        this.mContext = mContext;
        this.promotionType = promotionType;
        this.mList = mList;
    }

    /**
     * 自定义ViewHold  商家商品ViewHolder
     */
    public static class ShopViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ShopViewHolder(View itemView) {
            super(itemView);
            //初始化Item布局文件绑定数据类似listView
        }
    }

    /**
     * 自定义ViewHold  商家券ViewHolder
     */
    public static class CouponViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public CouponViewHolder(View itemView) {
            super(itemView);
            //初始化Item布局文件绑定数据类似listView
        }
    }

    /**
     * 自定义ViewHold  商家券ViewHolder
     */
    public static class CouponNopicViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public CouponNopicViewHolder(View itemView) {
            super(itemView);
            //初始化Item布局文件绑定数据类似listView
        }
    }

    /**
     * 自定义ViewHold  商家活动ViewHolder
     */
    public static class ActiveViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ActiveViewHolder(View itemView) {
            super(itemView);
            //初始化Item布局文件绑定数据类似listView
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).PromotionTypeID==1){
            return ITEM_TYPE_SHOPS;
        }else if(mList.get(position).PromotionTypeID==2){
            return ITEM_TYPE_ACTIVE;
        }else {
            if (mList.get(position).path.equals("")||mList.get(position).path==null){
                return ITEM_TYPE_COUPON_NOPIC;
            }else {
                return ITEM_TYPE_COUPON;
            }
        }
    }

    @Override
    public int getItemCount() {
        return  mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }



}
