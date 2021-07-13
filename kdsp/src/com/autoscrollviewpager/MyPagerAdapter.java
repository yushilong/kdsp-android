package com.autoscrollviewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.qizhu.rili.utils.LogUtils;

import java.util.List;

/**
 * 无限循环ViewPager的通用适配器
 */
public class MyPagerAdapter extends PagerAdapter {

    private List<? extends View> mListViews;

    public MyPagerAdapter(List<? extends View> mListViews) {
        this.mListViews = mListViews;           //构造方法，参数是我们的页卡，这样比较方便。
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(Object object) {
        int position = mListViews.indexOf(object);
        if (position >= 0) {
            return super.getItemPosition(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        if (position == 0) {
            view = (View) mListViews.get(mListViews.size() - 1).getTag();
        } else if (position == mListViews.size() + 1) {
            view = (View) mListViews.get(0).getTag();
        } else {
            view = mListViews.get(LoopViewPager.toRealPosition(position, mListViews.size()));
        }

        if (null != view) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        LogUtils.d("--->  destroyItem = " + position);
        container.removeView((View) object);
    }
}
