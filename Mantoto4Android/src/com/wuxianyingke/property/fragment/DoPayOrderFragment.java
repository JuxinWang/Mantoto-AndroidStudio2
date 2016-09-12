package com.wuxianyingke.property.fragment;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.GetOrderListAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetOrderListThread;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoPayOrderFragment extends Fragment {
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
    private ArrayList<RemoteApi.OrderItem> mData=new ArrayList<RemoteApi.OrderItem>();
    private ListView listView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
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
                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        boolean isBottom = false;//表示每页数据已经加载完，一个标志位
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                            if (isLoading&&scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                                isLoading = true;
                                Toast.makeText(getActivity(), "数据加载中，请稍后...",
                                        Toast.LENGTH_LONG).show();
                                pageIndex++;
                                RemoteApi.User use = LocalStore.getUserInfo();
                                mThread = new GetOrderListThread(getActivity(),
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RemoteApi.User use = LocalStore.getUserInfo();

        loadingDialog = new LoadingDialog(getActivity());
        mThread = new GetOrderListThread(getActivity(),handler, use.userId,
                pageIndex);
        mThread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View friendView = inflater.inflate(R.layout.fragment_friend, container, false);
        listView = (ListView)friendView.findViewById(R.id.product_list_view);
        mTextView=(TextView) friendView.findViewById(R.id.empty_tv);
        listView.setVerticalScrollBarEnabled(false);
        return friendView;
    }

}
