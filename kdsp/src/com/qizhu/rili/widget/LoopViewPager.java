package com.qizhu.rili.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.qizhu.rili.utils.LogUtils;

/**
 * Created by lindow
 * 自定义可手动，自动滑动，循环滑动以及可嵌套滑动的ViewPager
 */
public class LoopViewPager extends ViewPager {

    public LoopViewPager(Context context) {
        super(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {

        if(v != this && v instanceof ViewPager) {
            LogUtils.d("--->  viewpager return true = ");
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}
