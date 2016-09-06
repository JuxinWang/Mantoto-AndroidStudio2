package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;

public class PlotControlActivity extends Activity {
    // 顶部导航
    private TextView topbar_txt,topbar_right;
    private Button topbar_left,plot_control_location;
    private int favorite_flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_control);
        initwidget();
    }

    public void initwidget(){
        //标题
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_right = (TextView) findViewById(R.id.topbar_right);
        topbar_right.setVisibility(View.VISIBLE);
        topbar_right.setText("保存");
        topbar_right.setTextColor(Color.rgb(255,165,0));

        //--中间标题
        topbar_txt.setText("小区管理");
        //-- 左侧返回菜单处理事件
        topbar_left.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (0 != favorite_flag) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setClass(getApplicationContext(),
                            SetMessageActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });
        //--右侧保存按钮


        plot_control_location = (Button) findViewById(R.id.plot_control_location);
        plot_control_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClass(getApplicationContext(),
                        PlotLocationActivity.class);
                startActivity(intent);
            }
        });
    }
}
