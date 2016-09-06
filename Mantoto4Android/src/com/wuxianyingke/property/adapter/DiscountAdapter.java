package com.wuxianyingke.property.adapter;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CommitVoucherContentActivity;
import com.wuxianyingke.property.activities.CommitVoucherContentDetailsActivity;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.fragment.NetworkUtils;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;

public class DiscountAdapter extends BaseAdapter {
    /**上下文参数的对象*/
    private Context mContext;
    /**数据源集合的对象*/
    private List<OrderItem>orderItems;
    /**网络操作工具类*/
    private NetworkUtils nUtils;
    private int mCount;
    private boolean mStoped;
    private boolean mIsOnEdit;
    private int flags;
    /**启用线程池下载网络数据*/
    private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
    public DiscountAdapter(Context mContext, List<OrderItem> orderItems,int flags) {
        super();
        this.mContext = mContext;
        this.orderItems = orderItems;
        this.flags=flags;
    }

    public void onDataChanger(List<OrderItem> list){
        this.orderItems=list;
        this.notifyDataSetInvalidated();
    }

    public void addNewData(ArrayList<OrderItem> orderItems){
        orderItems.addAll(orderItems);
        notifyDataSetChanged();
    }

    public void appandAdapter(ArrayList<OrderItem> list) //每次滑动获取的数据都追加进来，页数加1
    {
        for(int i=0; i<list.size(); i++)
        {
            orderItems.add(list.get(i));
            mCount++;
        }
    }
    public void freeDrawable() {
        mStoped = true;
        for (int i = 0; i < orderItems.size(); i++) {
            BitmapDrawable a = (BitmapDrawable) orderItems.get(i).imgDw;
            if (a != null && !a.getBitmap().isRecycled())//回收释放资源
                a.getBitmap().recycle();
            orderItems.get(i).imgDw = null;
        }
        System.gc();
    }

    public void setIsOnEdit(boolean isOnEdit)
    {
        mIsOnEdit = isOnEdit;
    }

    public final boolean getIsOnEdit()
    {
        return mIsOnEdit;
    }

    @Override
    public int getCount() {
        return orderItems.size();//返回数据源大小
    }

    @Override
    public Object getItem(int position) {

        return orderItems.get(position);//返回指定数据源位置的对象
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mStoped)
            return convertView;
        ViewHolder vHolder = null;
        final OrderItem items=orderItems.get(position);
        Log.i("MyLog", "items="+items.OrderSequenceNumber);
        if (convertView == null) { // 创建新的itemView
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.discount_list_item, null); // 加载item布局并生成View对象

            vHolder = new ViewHolder();
            vHolder.body = (TextView) convertView.findViewById(R.id.tv_goodId);
            vHolder.Ctime = (TextView) convertView.findViewById(R.id.tv_Ctime);
            vHolder.number = (TextView) convertView.findViewById(R.id.tv_NumberId);
            vHolder.imageView = (ImageView) convertView.findViewById(R.id.image_NameId);
            //vHolder.orderNumber = (TextView) convertView.findViewById(R.id.tv_PriceId);

            vHolder.status = (TextView) convertView.findViewById(R.id.tv_PriceId);
            vHolder.mRelativeLayout= (LinearLayout) convertView.findViewById(R.id.ItemRelativeLayout);
            vHolder.couponCodeDetails = (TextView) convertView.findViewById(R.id.coupon_code_detail);
            convertView.setTag(vHolder);
        } else { // 复用ListView滚出屏幕的itemView

            vHolder = (ViewHolder) convertView.getTag(); // 获取到可复用的item中的所有控件对象
            vHolder.imageView.setImageResource(R.drawable.login_top); // 重置图片控件
        }
        vHolder.body.setText(orderItems.get(position).ThePromotion.header);
//		String body=orderItems.get(position).ThePromotion.header;
//		Log.i("MyLog", "imageUrl---------------------------------->"+body);
        String CTime=items.Ctime;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(new Date(Long.parseLong(CTime)));
        vHolder.Ctime.setText(""+date);
        vHolder.number.setText(""+orderItems.get(position).Number);
        //vHolder.orderNumber.setText(""+orderItems.get(position).OrderID);
        vHolder.status.setText(""+orderItems.get(position).Status);
        String url = orderItems.get(position).ThePromotion.path;
//		Log.i("MyLog", "imageUrl---------------------------------->"+url);

        if(url==null){
            vHolder.imageView.setImageResource(R.drawable.login_top);
        }else{
            // 从SDCard中读取图片
            Bitmap bitmap = SDCardUtils.readImage(url);
            if (bitmap != null) {
                vHolder.imageView.setImageBitmap(bitmap);
            }else {
//				String url=imageUrl;
                vHolder.imageView.setImageResource(R.drawable.login_top);
                vHolder.imageView.setTag(url); // 将图片的地址作为图片控件的标签

                // 网络下载
                download(url);
            }
        }

        vHolder.couponCodeDetails.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(flags==5){
                    Intent intent = new Intent(mContext,
                            CommitVoucherContentActivity.class);
                    intent.putExtra(
                            "header",
                            items.ThePromotion.header);
                    intent.putExtra("body",
                            items.ThePromotion.body);
                    intent.putExtra(
                            "price",
                            items.ThePromotion.Price);

                    intent.putExtra("path",
                            items.ThePromotion.path);
                    intent.putExtra("ordersequencenumber",items.OrderSequenceNumber);
                    intent.putExtra("OrderID", items.OrderID);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }else{
                    String CTime=items.Ctime;
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date=sdf.format(new Date(Long.parseLong(CTime)));
                    Intent intent = new Intent(mContext, CommitVoucherContentDetailsActivity.class);
                    intent.putExtra("header", items.ThePromotion.header);
                    intent.putExtra("body", items.ThePromotion.body);
                    intent.putExtra("price", items.ThePromotion.Price);
                    intent.putExtra("OrderID", items.OrderID);
                    intent.putExtra("Number", items.Number);
                    intent.putExtra("CTime", date);
                    intent.putExtra("ordersequencenumber", items.OrderSequenceNumber);
                    intent.putExtra("Total", items.Total);
                    intent.putExtra("TelNumber", items.TelNumber);
                    intent.putExtra("path", items.ThePromotion.path);
                    intent.putExtra("flag", 2);
                    //用于对实物和券码进行判断
                    intent.putExtra("SaleTypeID", items.ThePromotion.SaleTypeID);
                    if(flags==0){
                        intent.putExtra("OrderStatusID",items.Status.OrderStatusID);}
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });

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

    class ViewHolder {
        public TextView body; // 标题
        public ImageView imageView; // 图片
        public TextView Ctime; //订单时间
        public TextView number; //数量
        public  TextView orderNumber;//订单序号
        public  TextView status;//订单状态
        public LinearLayout mRelativeLayout;
        public TextView couponCodeDetails;
    }

}
