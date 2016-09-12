package com.wuxianyingke.property.activities;


import android.graphics.Color;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.adapter.FragmentAdapter;
import com.wuxianyingke.property.fragment.NoPayOrderFragment;
import com.wuxianyingke.property.fragment.AllOrdersFragment;
import com.wuxianyingke.property.fragment.DoPayOrderFragment;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends FragmentActivity {
    private Button topbarLeft;
    private TextView topbarTxt;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;

    private ViewPager mPageVp;

    /**
     * Tab显示内容TextView
     */
    private TextView unTabTv, doTabTv, allTabTv;
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLineIv;
    /**
     * Fragment
     */
    private NoPayOrderFragment noPayOrderFragment;
    private DoPayOrderFragment payOrderFragment;
    private AllOrdersFragment allOrdersFragment;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        findById();
        init();
        initTabLineWidth();
    }

    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 3;
        mTabLineIv.setLayoutParams(lp);
    }


    private void findById() {
        topbarLeft = (Button) findViewById(R.id.topbar_left);
        topbarTxt = (TextView) findViewById(R.id.topbar_txt);
        topbarLeft.setVisibility(View.VISIBLE);
        topbarTxt.setVisibility(View.VISIBLE);
        topbarLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topbarTxt.setText("我的订单");

        doTabTv = (TextView) this.findViewById(R.id.id_contacts_tv);
        unTabTv = (TextView) this.findViewById(R.id.id_chat_tv);
        allTabTv = (TextView) this.findViewById(R.id.id_friend_tv);

        mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);

        mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);
    }

    private void init() {
        payOrderFragment = new DoPayOrderFragment();
        allOrdersFragment = new AllOrdersFragment();
        noPayOrderFragment = new NoPayOrderFragment();
        mFragmentList.add(noPayOrderFragment);
        mFragmentList.add(payOrderFragment);
        mFragmentList.add(allOrdersFragment);

        mFragmentAdapter = new FragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        mPageVp.setAdapter(mFragmentAdapter);
        mPageVp.setCurrentItem(0);
        mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();

                Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */
                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));

                }else if (currentIndex == 1 && position == 1) // 1->2
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                } else if (currentIndex == 2 && position == 1) // 2->1
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 3) + currentIndex
                            * (screenWidth / 3));
                }

                mTabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        unTabTv.setTextColor(Color.rgb(255,165,0));
                        break;
                    case 1:
                        allTabTv.setTextColor(Color.rgb(255,165,0));
                        break;
                    case 2:
                        doTabTv.setTextColor(Color.rgb(255,165,0));
                        break;
                }
                currentIndex = position;
            }

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        unTabTv.setTextColor(Color.BLACK);
        allTabTv.setTextColor(Color.BLACK);
        doTabTv.setTextColor(Color.BLACK);
    }


}
