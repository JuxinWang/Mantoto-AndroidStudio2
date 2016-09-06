package com.wuxianyingke.property.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 
 * @author liudongdong
 *
 */
public class HFFinishRelativeLayout extends RelativeLayout{

    public interface ScrollLeftFinishListener{
        public void finishPage();
    }

    FragmentActivity activity;

    private ScrollLeftFinishListener scrollLeftFinishListener;

    public void setScrollLeftFinishListener(ScrollLeftFinishListener scrollLeftFinishListener) {
        this.scrollLeftFinishListener = scrollLeftFinishListener;
    }

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    public HFFinishRelativeLayout(Context context) {
        super(context);
    }


    public HFFinishRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void attachToActivity(FragmentActivity activity) {
        this.activity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.windowBackground });
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        decor.addView(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:

        }

        return super.onInterceptTouchEvent(event);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = event.getX();
                yLast = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = event.getX();
                final float curY = event.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                if (curX > xLast && xDistance > yDistance && xDistance > 300) {
                    if(scrollLeftFinishListener != null){
                        xLast = curX;
                        yLast = curY;
                        scrollLeftFinishListener.finishPage();
                        return true;
                    }
                }
                xLast = curX;
                yLast = curY;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
//        <strong><span style="color:#ff6666;">return true;</span></strong>
		return false;
    }

    
}