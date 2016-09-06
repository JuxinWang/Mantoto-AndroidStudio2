package com.wuxianyingke.property.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.mantoto.property.R;

/**
 * 自定义listView
 * @author Administrator
 *
 */
public class LoadListView extends ListView implements OnScrollListener{
	private View footer;//底部布局
	private int totalItemCount;//总数量
	private int lastVisibleItem;//最后一个可见的item
	private boolean isLoading;//正在加载
	private ILoadListener iLoadListener;
	public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public LoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public LoadListView(Context context) {
		super(context);
		initView(context);
	}
	//添加底部加载布局到listView
	private void initView(Context context){
	LayoutInflater inflater=LayoutInflater.from(context);
	footer=inflater.inflate(R.layout.complete_footer_layout, null);
	footer.findViewById(R.id.footer_layout).setVisibility(View.GONE);
	this.addFooterView(footer);
	this.setOnScrollListener(this);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.lastVisibleItem=firstVisibleItem+visibleItemCount;
		this.totalItemCount=totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (totalItemCount==lastVisibleItem&&scrollState==SCROLL_STATE_IDLE) {
			if(!isLoading){
				isLoading=true;
			footer.findViewById(R.id.footer_layout).setVisibility(View.VISIBLE);	
			//加载更多数据
			iLoadListener.onLoad();
			}
		}
		
	} 
	
	public void onComplete(){
		isLoading=false;
		footer.findViewById(R.id.footer_layout).setVisibility(View.GONE);
	}
	
	public void setInterface(ILoadListener iLoadListener){
		this.iLoadListener=iLoadListener;
	}
	
	//加载更多数据的回调接口
	public interface ILoadListener{
		public void onLoad();
	}

}
