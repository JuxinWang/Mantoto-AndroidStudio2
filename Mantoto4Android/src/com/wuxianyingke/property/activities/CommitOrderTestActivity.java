package com.wuxianyingke.property.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;
import com.wuxianyingke.property.common.ScreenUtil;
import com.wuxianyingke.property.fragment.SampleFragmentPagerAdapter;

public class CommitOrderTestActivity extends FragmentActivity {
    private Button topLeft;
    private TextView topTxt;

    private TabPageIndicator linePagerIndicator;
    private ViewPager viewPager;
    //private UnderlinePageIndicator mUnderlinePageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_order_test);

        initWidget();
        setImmerseLayout(findViewById(R.id.common_back));

        linePagerIndicator = (TabPageIndicator) findViewById(R.id.line_tab_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
       // mUnderlinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.underline_tab_indicator);

        viewPager.setAdapter(new SampleFragmentPagerAdapter(this,getSupportFragmentManager()));
       // mUnderlinePageIndicator.setViewPager(viewPager);

        linePagerIndicator.setViewPager(viewPager);

       // mUnderlinePageIndicator.setFades(false);
        //linePagerIndicator.setOnPageChangeListener(mUnderlinePageIndicator);
    }

    private void initWidget() {
        topLeft = (Button) findViewById(R.id.topbar_left);
        topLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        topTxt = (TextView) findViewById(R.id.topbar_txt);
        topTxt.setText("我的订单");
    }

    @SuppressLint("InlinedApi")
    protected void setImmerseLayout(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏变透明
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

           /* int statusBarHeight = ScreenUtil.getStatusBarHeight(this.getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);*/
        }
    }


}
