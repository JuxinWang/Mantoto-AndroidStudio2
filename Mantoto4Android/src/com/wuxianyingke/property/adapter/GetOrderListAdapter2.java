package com.wuxianyingke.property.adapter;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.fragment.NetworkUtils;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;

public class GetOrderListAdapter2 extends BaseAdapter {
	/**上下文参数的对象*/
	private Context mContext;
	/**数据源集合的对象*/
	private ArrayList<OrderItem>orderItems;
	/**网络操作工具类*/
	private NetworkUtils nUtils;
	/**启用线程池下载网络数据*/
	private boolean mStoped;
	private int mCount;
	private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
	public GetOrderListAdapter2(Context mContext,
			ArrayList<OrderItem> orderItems, int mCount) {
		super();
		this.mContext = mContext;
		this.orderItems = orderItems;
		this.mCount = mCount;
	}

	public void appandAdapter(ArrayList<OrderItem> orderItems) {
		for(int i=0; i<orderItems.size(); i++)
		{
			orderItems.add(orderItems.get(i));
			mCount++;
		}
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
		ViewHolder vHolder = null;
		if (convertView == null) { // 创建新的itemView
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.order_commit_list_item, null); // 加载item布局并生成View对象
			
			vHolder = new ViewHolder();
			vHolder.body = (TextView) convertView.findViewById(R.id.tv_goodId);
			vHolder.total = (TextView) convertView.findViewById(R.id.tv_TotalPriceId);
			vHolder.number = (TextView) convertView.findViewById(R.id.tv_NumberId);
			vHolder.imageView = (ImageView) convertView.findViewById(R.id.image_NameId);

			convertView.setTag(vHolder);
		} else { // 复用ListView滚出屏幕的itemView

			vHolder = (ViewHolder) convertView.getTag(); // 获取到可复用的item中的所有控件对象
			vHolder.imageView.setImageResource(R.drawable.code_logo); // 重置图片控件
		}
		vHolder.body.setText(orderItems.get(position).ThePromotion.header);
		vHolder.total.setText(""+orderItems.get(position).Total);
		vHolder.number.setText(""+orderItems.get(position).Number);
		
		
		String url = orderItems.get(position).ThePromotion.path;
		Log.i("MyLog", "imageUrl---------------------------------->"+url);
		
		if(url==null){
			vHolder.imageView.setImageResource(R.drawable.code_logo);
		}else{
			// 从SDCard中读取图片
			Bitmap bitmap = SDCardUtils.readImage(url);
			if (bitmap != null) {
				vHolder.imageView.setImageBitmap(bitmap);
			}else {
//				String url=imageUrl;
				vHolder.imageView.setTag(url); // 将图片的地址作为图片控件的标签

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
	
	class ViewHolder {
		public TextView body; // 标题
		public ImageView imageView; // 图片
		public TextView total; //总价
		public TextView number; //数量
	}


}
