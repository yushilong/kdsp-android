package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by lindow
 * 竖直方向的ScrollView，此时不影响子view接收横向事件，增加监听滑动距离的操作
 */

public class VerticalScrollView extends ScrollView {

    private float xDistance, yDistance, xLast, yLast;
    private boolean mListenTopAndBottom;            //监听滑动到顶部和底部
    private boolean mListenScrollEnd;               //监听滑动停止

    private long delayMillis = 100;
    private ScrollDistanceListener scrollDistanceListener = null;

    //上次滑动的时间
    private long lastScrollUpdate = -1;

    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > delayMillis) {
                lastScrollUpdate = -1;
                if (scrollDistanceListener != null) {
                    scrollDistanceListener.onScrollEnd();
                }
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };

    public VerticalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 此方法由ScrollTo回调，比onTouch更准，但是会多次调用，用于返回ScrollView的精确的滑动距离，从而实现用户的自定义操作
     * l为X轴移动距离，t为Y轴移动距离
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollDistanceListener != null) {
            scrollDistanceListener.onScrollChanged(l, t, oldl, oldt);
            if (mListenTopAndBottom) {
                if (0 == t) {
                    scrollDistanceListener.onScrollTop(l, t, oldl, oldt);
                } else if (t + getHeight() >= computeVerticalScrollRange()) {
                    scrollDistanceListener.onScrollBottom(l, t, oldl, oldt);
                }
            }
            if (mListenScrollEnd && lastScrollUpdate == -1) {
                // 更新ScrollView的滑动时间
                lastScrollUpdate = System.currentTimeMillis();
                postDelayed(scrollerTask, delayMillis);
            }
        }
    }

    public void setScrollViewListener(ScrollDistanceListener scrollDistanceListener) {
        this.scrollDistanceListener = scrollDistanceListener;
    }

    /**
     * 设置是否监听到顶部和到底部的事件，减少无谓的回调与判断
     */
    public void setListenEvents(boolean listenTopAndBottom, boolean listenScrollEnd) {
        mListenTopAndBottom = listenTopAndBottom;
        mListenScrollEnd = listenScrollEnd;
    }

    public interface ScrollDistanceListener {
        //滑动距离
        void onScrollChanged(int l, int t, int oldl, int oldt);

        //滑到顶部
        void onScrollTop(int l, int t, int oldl, int oldt);

        //滑到底部
        void onScrollBottom(int l, int t, int oldl, int oldt);

        //滑动停止
        void onScrollEnd();
    }
}
