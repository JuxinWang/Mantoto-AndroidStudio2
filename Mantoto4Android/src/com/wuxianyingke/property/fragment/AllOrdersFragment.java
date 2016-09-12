package com.wuxianyingke.property.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CommitVoucherContentDetailsActivity;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetOrderListThread;
import com.wuxianyingke.property.threads.GetUnOrderListThread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllOrdersFragment extends Fragment {
    /** 订单列表适配器 */
    private GetOrderListAdapter mAdapter;
    /** 订单列表线程用于获得订单数据 */
    private GetUnOrderListThread unThread;
    /** 订单列表线程用于获得订单数据 */
    private GetOrderListThread doThread;

    private ArrayList<RemoteApi.OrderItem> unData=new ArrayList<RemoteApi.OrderItem>();
    private ArrayList<RemoteApi.OrderItem> doData=new ArrayList<RemoteApi.OrderItem>();


    private ListView unListView;
    private ListView doListView;
    private TextView mTextView;
    private LoadingDialog loadingDialog;

    /**
     * 加载数据相关成员
     */
    private boolean isLoading = false;
    private int flags;
    /**
     * 分页相关成员
     */
    private int pageIndex = 1;
    private int pageCount = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case Constants.MSG_GET_CANYIN_LIST_FINISH:
                   Log.i("MyLog", "我的订单列表为---------------------》"
                           + doData);
                   if(doThread.mOrders==null && unThread.mOrders==null)
                   {
                       return;
                   }

                   /*doData.addAll(doThread.mOrders);
                   unData.addAll(unThread.mOrders);*/

                   /*if(mAdapter == null){
                       mAdapter = new GetOrderListAdapter(getActivity(),unThread.mOrders,flags);
                       mAdapter = new GetOrderListAdapter(getActivity(),doData,flags);
                       unListView.setAdapter(mAdapter);
                       mAdapter.notifyDataSetChanged();
                   }else {
                       unData.addAll(unThread.mOrders);
                       doData.addAll(doThread.mOrders);
                       mAdapter = new GetOrderListAdapter(getActivity(),unData,flags);
                       mAdapter = new GetOrderListAdapter(getActivity(),doData,flags);
                       unListView.setAdapter(mAdapter);
                       mAdapter.notifyDataSetChanged();
                   }
*/
                   unData.addAll(unThread.mOrders);
                   doData.addAll(doThread.mOrders);
                   mAdapter = new GetOrderListAdapter(getActivity(),unData,flags);
                   mAdapter = new GetOrderListAdapter(getActivity(),doData,flags);
                   unListView.setAdapter(mAdapter);
                   mAdapter.notifyDataSetChanged();

                   pageCount=mAdapter.getCount();
                   Log.i("MyLog", "@#￥%我的订单总数为————————————————————————》"+pageCount);

                   doListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                       @Override
                       public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                           if (isLoading&&scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                               isLoading = true;
                               Toast.makeText(getActivity(), "数据加载中，请稍后...",
                                       Toast.LENGTH_LONG).show();
                               pageIndex++;
                               RemoteApi.User use = LocalStore.getUserInfo();
                               doThread = new GetOrderListThread(getActivity(),
                                       mHandler, use.userId, pageIndex);
                               doThread.start();
                           }else if (pageIndex > pageCount) {
                               Toast.makeText(getActivity(), "数据已经加载完毕....",
                                       Toast.LENGTH_LONG).show();
                           }

                       }

                       @Override
                       public void onScroll(AbsListView absListView, int firstVisibleItem,
                                            int visibleItemCount, int totalItemCount) {
                           if (firstVisibleItem + visibleItemCount == totalItemCount) {
                           }

                       }
                   });

                   unListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view,
                                               int position, long id) {
                           String CTime=unData.get(position).Ctime;
                           SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                           String date=sdf.format(new Date(Long.parseLong(CTime)));
                           Intent intent = new Intent(getActivity(), CommitVoucherContentDetailsActivity.class);
                           intent.putExtra("header", unData.get(position).ThePromotion.header);
                           intent.putExtra("body", unData.get(position).ThePromotion.body);
                           intent.putExtra("price", unData.get(position).ThePromotion.Price);
                           intent.putExtra("OrderID", unData.get(position).OrderID);
                           intent.putExtra("Number", unData.get(position).Number);
                           intent.putExtra("CTime", date);
                           intent.putExtra("ordersequencenumber", unData.get(position).OrderSequenceNumber);
                           intent.putExtra("Total", unData.get(position).Total);
                           intent.putExtra("TelNumber", unData.get(position).TelNumber);
                           intent.putExtra("path", unData.get(position).ThePromotion.path);
                           intent.putExtra("flag", 2);
                           //用于对实物和券码进行判断
                           intent.putExtra("SaleTypeID", unData.get(position).ThePromotion.SaleTypeID);
                           startActivity(intent);
                       }
                   });


                   unListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                       @Override
                       public void onScrollStateChanged(AbsListView view, int scrollState) {
                           if (!isLoading&&scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                               isLoading = true;
                               Toast.makeText(getActivity(), "数据加载中，请稍后...",
                                       Toast.LENGTH_LONG).show();
                               pageIndex++;
                               RemoteApi.User use = LocalStore.getUserInfo();
                               unThread = new GetUnOrderListThread(getActivity(),
                                       mHandler, use.userId, pageIndex);

                               unThread.start();
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
                   unListView.setVisibility(View.GONE);
                   doListView.setVisibility(View.GONE);
                   mTextView.setVisibility(View.VISIBLE);
                   mTextView.setText("暂无订单信息");
                   break;


           }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RemoteApi.User use = LocalStore.getUserInfo();
        loadingDialog = new LoadingDialog(getActivity());
        doThread = new GetOrderListThread(getActivity(), mHandler, use.userId,
                pageIndex);
        unThread = new GetUnOrderListThread(getActivity(),
                mHandler, use.userId, pageIndex);
        doThread.start();
        unThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View contactView = inflater.inflate(R.layout.fragment_contacts, container, false);
        unListView = (ListView)contactView.findViewById(R.id.un_product_list_view);
        doListView = (ListView)contactView.findViewById(R.id.do_product_list_view);
        unListView.setVerticalScrollBarEnabled(false);
        doListView.setVerticalScrollBarEnabled(false);
        mTextView=(TextView) contactView.findViewById(R.id.empty_tv);
        return contactView;
    }


}
