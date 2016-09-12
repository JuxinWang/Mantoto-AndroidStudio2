package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetPropertyByNameListThread;

import java.util.ArrayList;

public class SearchPlotActivity extends BaseActivity {
    private Button topbarLeft,searchPlot;
    private TextView topTxt,topRightTxt;
    private EditText etFindPlot;
    private int flag = 1;
    private ListView listView;
    private GetPropertyByNameListThread mByNameThread;
    private ArrayList<RemoteApi.Propertys> propertysList = new ArrayList<RemoteApi.Propertys>();
    private ArrayList<RemoteApi.Propertys> mList = new ArrayList<RemoteApi.Propertys>();
    private ArrayAdapter<String> adapter = null;
    private ProgressDialog mProgressBar = null;
    private int propertyId;
    private int mPageIndex = 1;
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (mProgressBar != null) {

                mProgressBar.dismiss();
                mProgressBar = null;
            }
            switch(msg.what){
                case Constants.MSG_GET_PRODUCT_LIST_FINISH:
                    if (flag==1) {
                        propertysList = mByNameThread.getPropertyList();
                        // propertyId = (int)propertysList.get(0).PropertyID;
                        //  Log.i("MyLog", "当前小区信息为-----" + propertyId);
                        mList.addAll(propertysList);

                        String[] propertys = new String[mList.size()];

                        for (int i = 0; i < mList.size(); i++) {

                            propertys[i] = mList.get(i).PropertyName;
                            Log.i("MyLog", "当前集合的内容为————————"
                                    + mList.get(i).PropertyName);
                        }

                        adapter = new ArrayAdapter<String>(getApplicationContext(),
                                R.layout.activity_list_item, R.id.tv_ListItem,
                                propertys);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        listView
                                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent,
                                                            View view, int position, long id) {

                                        RemoteApi.User user=new RemoteApi.User();
                                        user.userId= LocalStore.getUserInfo().userId;
                                        user.userName=LocalStore.getUserInfo().userName;
                                        user.PropertyID=(int) mList.get(position).PropertyID;
                                        LocalStore.setUserInfo(SearchPlotActivity.this, user);

                                        Log.i("MyLog", "当前的小区idshi"+LocalStore.getUserInfo().PropertyID);
                                        etFindPlot.setText(propertysList.get(position).PropertyName);
                                        /*Intent intent2 = new Intent();
                                        intent2.setClass(
                                                SearchPlotActivity.this,
                                                RegisterActivity.class);
                                        startActivity(intent2);
                                        finish();*/
//								   }

                                    }
                                });

                    }else{
                        propertysList = mByNameThread.getPropertyList();
                        Log.i("MyLog", "当前小区信息为-----" + propertysList);

                        Intent intent = new Intent();
                        intent.putExtra("key", propertysList);
                        intent.putExtra("et_InputContent", etFindPlot.getText().toString());
                        if (propertysList.size()!=0) {
                            intent.setClass(SearchPlotActivity.this,
                                    PropertyListActivity.class);
                        }else {
                            intent.setClass(SearchPlotActivity.this, NoPropertyActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_plot);
        setImmerseLayout(findViewById(R.id.common_back));
        PushAgent.getInstance(getApplicationContext()).onAppStart();
        initWidget();
        initListener();

        topRightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("key", propertysList);
                intent.putExtra("et_InputContent", etFindPlot.getText().toString());
                intent.setClass(SearchPlotActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });


    }



    private void initWidget() {
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarLeft.setBackgroundResource(R.drawable.style_topbar_left);
        topbarLeft.setVisibility(View.VISIBLE);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topTxt = (TextView) findViewById(R.id.topbar_txt);
        topTxt.setText("小区管理");
        topTxt.setTextColor(Color.parseColor("#000000"));
        topTxt.setVisibility(View.VISIBLE);


        topRightTxt = (TextView) findViewById(R.id.topbar_right);
        topRightTxt.setVisibility(View.VISIBLE);
        topRightTxt.setText("保存");
        topRightTxt.setTextColor(Color.rgb(255,165,0));

        etFindPlot = (EditText) findViewById(R.id.plot_name_edt);
        searchPlot = (Button) findViewById(R.id.search_plot_btn);

        listView = (ListView) findViewById(R.id.plot_list);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(getResources().getDrawable(R.drawable.list_line));

    }

    private void initListener() {
        etFindPlot.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint;
                if(b){
                    hint = etFindPlot.getHint().toString();
                    etFindPlot.setTag(hint);
                    etFindPlot.setHint("");
                }else{
                    hint = etFindPlot.getTag().toString();
                    etFindPlot.setHint(hint);
                }
            }
        });

        searchPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获得搜索内容
                String et_InputContent = etFindPlot.getText().toString();
                if (Util.isEmpty(etFindPlot)) {
                    Toast.makeText(getApplicationContext(), "请输入小区名称", Toast.LENGTH_SHORT).show();
                    return;
                }

                mByNameThread = new GetPropertyByNameListThread(SearchPlotActivity.this,
                        mHandler, et_InputContent, mPageIndex);
                mByNameThread.start();

            }
        });

      /*  etFindPlot.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // 获得搜索内容
                String  etInputContent = etFindPlot.getText().toString();
                if (Util.isEmpty(etFindPlot)){
                    Toast.makeText(getApplicationContext(),
                            "输入小区名称",Toast.LENGTH_SHORT).show();

                }

                flag = 2;
                mByNameThread = new GetPropertyByNameListThread(
                        SearchPlotActivity.this,mHandler,etInputContent,mPageIndex);
                mByNameThread.start();
                return false;
              }
        });*/


    }
}
