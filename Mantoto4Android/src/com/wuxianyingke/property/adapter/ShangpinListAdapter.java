package com.wuxianyingke.property.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.remote.RemoteApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/21 0021.
 */
public class ShangpinListAdapter extends BaseAdapter{
    private boolean mStoped;
    private ArrayList<RemoteApi.Promotion> mList;
    private Context mContext;

    public ShangpinListAdapter(Context ctx, ArrayList<RemoteApi.Promotion> list){
        this.mContext = ctx;
        this.mList = list;
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
        if(convertView==null) {
            View v = LayoutInflater.from(mContext).inflate(
                    R.layout.canyin_detail_own_content, null);
            productItem = new ProductItem();
            productItem.title = (TextView) v.findViewById(R.id.canyin_title);
            productItem.pic = (ImageView) v.findViewById(R.id.canyinImg);
            productItem.price = (TextView) v.findViewById(R.id.canyin_price);
            productItem.desc = (TextView) v.findViewById(R.id.canyin_desc);
            productItem.goumai = (Button) v.findViewById(R.id.goumaiImg);
            v.setTag(productItem);
            convertView = v;
        }else {
            productItem = (ProductItem) convertView.getTag();
        }

        productItem.title.setText(info.header);
        //productItem.pic.setImageDrawable(info.Pric);
       // productItem.price.setD(info.Price);
        productItem.desc.setText(info.body);



        return convertView;
    }


    class ProductItem {
        TextView title;
        ImageView pic;
        TextView price;
        TextView desc;
        Button goumai;


    }
}
