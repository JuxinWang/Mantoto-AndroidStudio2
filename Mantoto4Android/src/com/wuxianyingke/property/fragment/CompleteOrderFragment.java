package com.wuxianyingke.property.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CommitVoucherContentDetailsActivity;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetOrderListThread;

public class CompleteOrderFragment extends Fragment {

	/** 订单列表适配器 */
	private GetOrderListAdapter mAdapter;
	/** 订单列表线程用于获得订单数据 */
	private GetOrderListThread mThread;
	/**
	 * 分页相关成员
	 */
	private int pageIndex = 1;
	private int pageCount = 1;
	/**
	 * 加载数据相关成员
	 */
	private boolean isLoading = false;
	private int flags;

	private LoadingDialog loadingDialog;
	private TextView mTextView;
	private ArrayList<OrderItem>mData=new ArrayList<OrderItem>();
	private ListView listView;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
				Log.i("MyLog", "我的订单列表为---------------------》"
						+ mData);
				if(mThread.mOrders==null)
				{
					return;
				}
				mData.addAll(mThread.mOrders);
				mAdapter = new GetOrderListAdapter(getActivity(),
						mData,flags);
				listView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				pageCount=mAdapter.getCount();
				isLoading = false;
				Log.i("MyLog", "@#￥%我的订单总数为————————————————————————》"+pageCount);
				listView.setOnScrollListener(new OnScrollListener() {
					boolean isBottom = false;//表示每页数据已经加载完，一个标志位
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						if (isLoading&&scrollState==OnScrollListener.SCROLL_STATE_IDLE) {
							isLoading = true;
							Toast.makeText(getActivity(), "数据加载中，请稍后...",
									Toast.LENGTH_LONG).show();
							pageIndex++;
							User use = LocalStore.getUserInfo();
							mThread = new GetOrderListThread(getActivity(),
									mHandler, use.userId, pageIndex);
							mThread.start();
						}else if (pageIndex > pageCount) {
							Toast.makeText(getActivity(), "数据已经加载完毕....",
									Toast.LENGTH_LONG).show();
						}
						
					}
					
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
						if (firstVisibleItem + visibleItemCount == totalItemCount) {
						}
						
					}
				});
				
				break;
			case Constants.MSG_NETWORK_ERROR:
				listView.setVisibility(View.GONE);
				mTextView.setVisibility(View.VISIBLE);
				mTextView.setText("暂无已完成订单");
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		User use = LocalStore.getUserInfo();
		
		loadingDialog = new LoadingDialog(getActivity());
		mThread = new GetOrderListThread(getActivity(), mHandler, use.userId,
				pageIndex);
		mThread.start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View view= inflater.inflate(R.layout.complete_order_list , container, false);
	      listView = (ListView)view.findViewById(R.id.product_list_view);
	      mTextView=(TextView) view.findViewById(R.id.empty_tv);
//	      ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
//	          android.R.layout.simple_list_item_ ,getData());
//	      listView.setAdapter(arrayAdapter);
	     /* listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				String CTime=mData.get(position).Ctime;
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String date=sdf.format(new Date(Long.parseLong(CTime)));
				Intent intent = new Intent(getActivity(), CommitVoucherContentDetailsActivity.class);
				intent.putExtra("header", mData.get(position).ThePromotion.header);
				intent.putExtra("body", mData.get(position).ThePromotion.body);
				intent.putExtra("price", mData.get(position).ThePromotion.Price);
				intent.putExtra("OrderID", mData.get(position).OrderID);
				intent.putExtra("Number", mData.get(position).Number);
				intent.putExtra("CTime", date);
				intent.putExtra("Total", mData.get(position).Total);
				intent.putExtra("TelNumber", mData.get(position).TelNumber);
				intent.putExtra("flag", 1);
				intent.putExtra("path", mData.get(position).ThePromotion.path);
				//用于对实物和券码进行判断
				intent.putExtra("SaleTypeID", mData.get(position).ThePromotion.SaleTypeID);
				
				startActivity(intent);	
				
			}
		});*/
	      return view;
	}
	
	

/*	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
//		String time=DateUtils.stringDateFormat(mData.get(position).Ctime);
//		Log.i("MyLog", "获得当前的时间为--------》"+time);
		String CTime=mData.get(position).Ctime;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=sdf.format(new Date(Long.parseLong(CTime)));
		
		Toast.makeText(getActivity(), "正在加载...", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(getActivity(), CommitVoucherContentDetailsActivity.class);
		intent.putExtra("header", mData.get(position).ThePromotion.header);
		intent.putExtra("body", mData.get(position).ThePromotion.body);
		intent.putExtra("price", mData.get(position).ThePromotion.Price);
		intent.putExtra("OrderID", mData.get(position).OrderID);
		intent.putExtra("Number", mData.get(position).Number);
		intent.putExtra("CTime", date);
		intent.putExtra("Total", mData.get(position).Total);
		intent.putExtra("TelNumber", mData.get(position).TelNumber);
		
		intent.putExtra("path", mData.get(position).ThePromotion.path);
		//用于对实物和券码进行判断
		intent.putExtra("SaleTypeID", mData.get(position).ThePromotion.SaleTypeID);
		
		startActivity(intent);
		
	}*/

}
