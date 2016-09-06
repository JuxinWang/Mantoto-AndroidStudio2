package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.threads.GetPlotListThread;
import com.wuxianyingke.property.widget.FilpperListvew;

public class PlotLocationActivity extends BaseActivity {
    // 自定义ListView
    private FilpperListvew listView;
    private Button topbar_left;
    private TextView topbar_txt,topbar_right;
    //获取小区外部线程
    private  GetPlotListThread mThread=null;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_location);

        // 初始化控件
        initView();
        setImmerseLayout(findViewById(R.id.common_back));
        // 初始化事件监听器
        initListener();
    }

    private void initListener() {
        topbar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(getApplicationContext(),
                        PlotControlActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onResume() {
//		init();
        super.onResume();
    }


    private void initView() {

        // 顶部导航
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_txt.setText("小区管理");
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        //将此处按钮改为保存：
        topbar_right = (TextView) findViewById(R.id.topbar_right);
        topbar_right.setVisibility(View.VISIBLE);
        topbar_right.setText("保存");
        topbar_right.setTextSize(16);
        topbar_right.setTextColor(Color.rgb(255,165,0));


        listView = (FilpperListvew) findViewById(R.id.lv_addressId);

    }
}
