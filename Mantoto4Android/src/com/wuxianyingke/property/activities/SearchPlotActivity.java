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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetPropertyByNameListThread;

import java.util.ArrayList;

public class SearchPlotActivity extends BaseActivity {
    private Button topbarLeft;
    private TextView topTxt,topRightTxt;
    private EditText etFindPlot;
    private int flag = 1;
    private GetPropertyByNameListThread mByNameThread;
    private ArrayList<RemoteApi.Propertys> propertysList = new ArrayList<RemoteApi.Propertys>();
    private ProgressDialog mProgressBar = null;
    private int propertyId;
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
                        propertyId = (int)propertysList.get(0).PropertyID;
                        Log.i("MyLog", "当前小区信息为-----" + propertyId);
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

        mByNameThread = new GetPropertyByNameListThread(SearchPlotActivity.this,
                mHandler, "未找到小区", 1);
        mByNameThread.start();
        initWidget();
        initListener();

    }



    private void initWidget() {
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarLeft.setBackgroundResource(R.drawable.arrow_left);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topTxt = (TextView) findViewById(R.id.topbar_txt);
        topTxt.setText("小区管理");
        topTxt.setTextColor(Color.parseColor("#ffffff"));


        topRightTxt = (TextView) findViewById(R.id.topbar_right);
        topRightTxt.setVisibility(View.VISIBLE);
        topRightTxt.setText("保存");
        topRightTxt.setTextColor(Color.rgb(255,165,0));

        etFindPlot = (EditText) findViewById(R.id.plot_name_edt);

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

        etFindPlot.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        SearchPlotActivity.this,mHandler,etInputContent,1);
                mByNameThread.start();
                return false;
              }
        });


    }
}
