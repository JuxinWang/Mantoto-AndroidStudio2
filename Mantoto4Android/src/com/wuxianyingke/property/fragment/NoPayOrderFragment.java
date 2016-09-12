package com.wuxianyingke.property.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CommitVoucherContentDetailsActivity;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetUnOrderListThread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Handler;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoPayOrderFragment extends Fragment {
    /** 订单列表适配器 */
    private GetOrderListAdapter mAdapter;
    /** 订单列表线程用于获得订单数据 */
    private GetUnOrderListThread mThread;

    /** 用于展示数据 */
    private ListView listView;

    private TextView mTextView;

    /**
     * 分页相关成员
     */
    private int pageIndex=1;
    private int pageCount;

    /**
     * 加载数据相关成员
     */
    private boolean isLoading = false;

    private LoadingDialog loadingDialog;
    private NetworkUtils nUtils;
    private int flags;
    ArrayList<RemoteApi.OrderItem> mdata=new ArrayList<RemoteApi.OrderItem>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_GET_CANYIN_LIST_FINISH:
                    Log.i("MyLog", "我的订单列表为---------------------》"+mThread.mOrders);
                    if(mThread.mOrders == null){
                        return;
                    }
                    if (mAdapter==null) {
                        mAdapter=new GetOrderListAdapter(getActivity(),mThread.mOrders,flags);
                        listView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }else{
                        mdata.addAll(mThread.mOrders);
                        mAdapter=new GetOrderListAdapter(getActivity(),mdata,flags);
                        listView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetInvalidated();
                    }
                    pageCount=mAdapter.getCount();
                    Log.i("MyLog", "@#￥%我的订单总数为————————————————————————》"+pageCount);
                    isLoading = false;
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String CTime=mdata.get(position).Ctime;
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String date=sdf.format(new Date(Long.parseLong(CTime)));
                            Intent intent = new Intent(getActivity(), CommitVoucherContentDetailsActivity.class);
                            intent.putExtra("header", mdata.get(position).ThePromotion.header);
                            intent.putExtra("body", mdata.get(position).ThePromotion.body);
                            intent.putExtra("price", mdata.get(position).ThePromotion.Price);
                            intent.putExtra("Total",mdata.get(position).Total);
                            intent.putExtra("OrderID", mdata.get(position).OrderID);
                            intent.putExtra("Number", mdata.get(position).Number);
                            intent.putExtra("CTime", date);
                            intent.putExtra("ordersequencenumber", mdata.get(position).OrderSequenceNumber);
                            intent.putExtra("Total", mdata.get(position).Total);
                            intent.putExtra("TelNumber", mdata.get(position).TelNumber);
                            intent.putExtra("path", mdata.get(position).ThePromotion.path);
                            intent.putExtra("flag", 2);
                            //用于对实物和券码进行判断
                            intent.putExtra("SaleTypeID", mdata.get(position).ThePromotion.SaleTypeID);
                            startActivity(intent);
                        }
                    });

                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (!isLoading&&scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                isLoading = true;
                                Toast.makeText(getActivity(), "数据加载中，请稍后...",
                                        Toast.LENGTH_LONG).show();
                                pageIndex++;
                                RemoteApi.User use = LocalStore.getUserInfo();
                                mThread = new GetUnOrderListThread(getActivity(),
                                        handler, use.userId, pageIndex);

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
                    listView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
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
        mThread = new GetUnOrderListThread(getActivity(),
                handler, use.userId, pageIndex);
        mThread.start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_chat, container, false);
        listView = (ListView)chatView.findViewById(R.id.product_list_view);
        listView.setDividerHeight(10);
        listView.setItemsCanFocus(true);
        listView.setVerticalScrollBarEnabled(false);
        mTextView=(TextView)chatView.findViewById(R.id.empty_tv);

        //listView.setAdapter(arrayAdapter);
        return chatView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
