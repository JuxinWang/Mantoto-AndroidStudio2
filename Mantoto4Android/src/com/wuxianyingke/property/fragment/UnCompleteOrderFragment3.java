/*
package com.wuxianyingke.property.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.wuxianyingke.property.activities.CommitVoucherContentDetailsActivity;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.adapter.GetOrderListAdapter2;
import com.wuxianyingke.property.adapter.GetOrderListAdapter3;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi.OrderItem;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetUnOrderListThread;

public class UnCompleteOrderFragment3 extends ListFragment {
	
	*/
/** 订单列表适配器 *//*

	private GetOrderListAdapter3 mAdapter;
	*/
/** 订单列表线程用于获得订单数据 *//*

	private GetUnOrderListThread mThread;
	*/
/** 用于展示数据 *//*

	private ListView mListView;
	
	*/
/**
	 * 分页相关成员
	 *//*

	private int pageIndex=1;
	private int pageCount;

	*/
/**
	 * 加载数据相关成员
	 *//*

	private boolean isLoading = false;

	private LoadingDialog loadingDialog;
	private NetworkUtils nUtils; 
	ArrayList<OrderItem>mdata=new ArrayList<OrderItem>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_CANYIN_LIST_FINISH:
//				Log.i("MyLog", "我的订单列表为---------------------》"+mThread.mOrders);
				if(mThread.mOrders==null)
				{
					return;
				}
				if (pageIndex==1) {
					mAdapter=new GetOrderListAdapter3(getActivity(), mdata);
					setListAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
				}else {
					mdata.addAll(mThread.mOrders);
					mAdapter.appandAdapter(mdata);
					setListAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					mdata.clear();
				}
				
//				mdata.clear();
				pageCount=mAdapter.getCount();
				Log.i("MyLog", "@#￥%我的订单总数为————————————————————————》"+pageCount);
				isLoading = false;
				getListView().setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Toast.makeText(getActivity(), "正在加载...", Toast.LENGTH_LONG).show();
						String CTime=mdata.get(position).Ctime;
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String date=sdf.format(new Date(Long.parseLong(CTime)));
						Intent intent = new Intent(getActivity(), CommitVoucherContentDetailsActivity.class);
						intent.putExtra("header", mdata.get(position).ThePromotion.header);
						intent.putExtra("body", mdata.get(position).ThePromotion.body);
						intent.putExtra("price", mdata.get(position).ThePromotion.Price);
						intent.putExtra("OrderID", mdata.get(position).OrderID);
						intent.putExtra("Number", mdata.get(position).Number);
						intent.putExtra("CTime", date);
						intent.putExtra("Total", mdata.get(position).Total);
						intent.putExtra("TelNumber", mdata.get(position).TelNumber);
						intent.putExtra("path", mdata.get(position).ThePromotion.path);
						//用于对实物和券码进行判断
						intent.putExtra("SaleTypeID", mdata.get(position).ThePromotion.SaleTypeID);
						startActivity(intent);
					}
				});
				
				getListView().setOnScrollListener(new OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
						if (!isLoading&&scrollState==OnScrollListener.SCROLL_STATE_IDLE) {
							isLoading = true;
							Toast.makeText(getActivity(), "数据加载中，请稍后...",
									Toast.LENGTH_LONG).show();
							pageIndex++;
							User use = LocalStore.getUserInfo();
							mThread = new GetUnOrderListThread(getActivity(),
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
							Log.i("MyLog","firstVisibleItem"+firstVisibleItem);
						}
					}
				});
				break;
			case Constants.MSG_NETWORK_ERROR:
				
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
		mThread = new GetUnOrderListThread(getActivity(),
				mHandler, use.userId, pageIndex);
		mThread.start();
	}

}
*/
