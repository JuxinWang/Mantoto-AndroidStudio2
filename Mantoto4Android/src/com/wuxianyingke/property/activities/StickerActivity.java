package com.wuxianyingke.property.activities;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.BianqianAdapter;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LocalStore.bianqian;

public class StickerActivity extends BaseActivity {
	 private TextView topbar_txt,topbar_right;
	 private Button topbar_left;
	 private Button mSaveButton;
	 private JSONObject response = null;
	 private ListView mLogsListView;
	 private BianqianAdapter mListAdapter;
	 private LocalStore localstore;
	 
	 private Handler mHandler = new Handler() {
		 public void handleMessage(Message msg){
			 switch (msg.what){
				 case 0:
				 default:
					 mListAdapter.notifyDataSetChanged();
			 }
		 }
	 };
		
     @Override
     protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		  super.onCreate(savedInstanceState);
		  PushAgent.getInstance(getApplicationContext()).onAppStart();
		  setContentView(R.layout.sticker);
		  setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		  localstore = new LocalStore();
		  localstore.initBianqian(this);
		  initWidgets();
	
	 }
	 @Override
	 protected void onNewIntent(Intent intent){
		  if(mListAdapter!=null){
			mListAdapter.notifyDataSetChanged();
		  }
			super.onNewIntent(intent);
	 }
     private void initWidgets() {
		  topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		  topbar_left = (Button) findViewById(R.id.topbar_left);
		  topbar_txt.setText("生活便签");
		  topbar_left.setVisibility(View.VISIBLE);
		  String title=LocalStore.getBianqianTitle(this);
		  String content=LocalStore.getBianqianContent(this);
		  topbar_left.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  // TODO Auto-generated method stub
				  finish();
			  }

     });

		  topbar_right = (TextView) findViewById(R.id.topbar_right);
		  topbar_right.setText("新建");
		  topbar_right.setVisibility(View.VISIBLE);
		  topbar_right.setTextColor(Color.rgb(255,165,0));
		  topbar_right.setTextSize(16);
		  topbar_right.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(StickerActivity.this, AddBianqianActivity.class);
				startActivityForResult(intent, 0);
		}
	 });
		  mLogsListView = (ListView) findViewById(R.id.bianqian_list_view);
		  mLogsListView.setVerticalScrollBarEnabled(false);//实现滚动条隐藏
	      showLogsListView(localstore.bianqian_List);
		
     }
  	 public void showLogsListView(ArrayList<bianqian> arrayList){
  		  if(mListAdapter == null)
			  mListAdapter = new BianqianAdapter(this, arrayList,mHandler);
  			  mLogsListView.setVisibility(View.VISIBLE);
			  mLogsListView.setAdapter(mListAdapter);

	 }
  	 protected  void onActivityResult(int requestCode, int resultCode, Intent data)  {
  		  if(mListAdapter!=null){
			mListAdapter.notifyDataSetChanged();
		  }
          super.onActivityResult(requestCode, resultCode,  data);
  	 }
}
