package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.qizhu.rili.R;
import com.qizhu.rili.adapter.PagerItemAdapter;
import com.qizhu.rili.listener.RefreshListener;
import com.qizhu.rili.utils.UIUtils;

/**
 * 引入的翻页库
 * 地址:https://github.com/skypanda100/BezierCurve
 * 这个库是对bitmap进行操作，有一定概率OOM
 */
public class PagerFactory {
    private Context mContext;                           //上下文
    private Bitmap mBg = null;                          //背景
    private int count;                                  //页数

    private int mBackColor = R.color.transparent;       // 背景颜色

    private Paint            mPaint;                               //画笔
    private PagerItemAdapter adapter;                   //适配器
    private Pager            pager;                                //封装的页
    private SparseArray<View> mViews = new SparseArray<View>();     //view
    private RefreshListener mRefreshListener;      //当前项刷新

    public PagerFactory(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Align.LEFT);
        mPaint.setColor(ContextCompat.getColor(mContext, mBackColor));
    }

    public void onDraw(Canvas c) {
        if (mBg == null) {
            c.drawColor(ContextCompat.getColor(mContext, mBackColor));
        } else {
            c.drawBitmap(mBg, 0, 0, null);
        }
    }

    public void onDraw(Canvas canvas, Bitmap bitmap) {
        try {
            //清空canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(bitmap, 0, 0, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setBgBitmap(Bitmap BG) {
        mBg = BG;
    }

    private Bitmap currentBitmap, mCurPageBitmap, mNextPageBitmap;
    private Canvas mCurPageCanvas, mNextPageCanvas;
    private int mWidth;
    private int mHeight;

    public void initPager(final Pager pager, PagerItemAdapter adapter, int width, int height) {
        this.pager = pager;
        this.adapter = adapter;
        count = adapter.getCount();
        mWidth = width;
        mHeight = height;

        mCurPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);

        pager.setBitmaps(mCurPageBitmap, mCurPageBitmap);
        loadImage(mCurPageCanvas, 0);

        pager.setOnTouchListener(new View.OnTouchListener() {

            private int currentIndex = 0;
            private int lastIndex = 0;
            private Bitmap lastBitmap = null;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                boolean ret = false;
                if (v == pager) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        pager.calcCornerXY(e.getX(), e.getY());

                        lastBitmap = currentBitmap;
                        lastIndex = currentIndex;

                        onDraw(mCurPageCanvas, currentBitmap);
                        if (pager.DragToRight()) {    // 向右滑动，显示前一页
                            if (currentIndex == 0) return false;
                            pager.abortAnimation();
                            currentIndex--;
                            loadImage(mNextPageCanvas, currentIndex);
                        } else {        // 向左滑动，显示后一页
                            if (currentIndex + 1 == count) return false;
                            pager.abortAnimation();
                            currentIndex++;
                            loadImage(mNextPageCanvas, currentIndex);
                        }
                    } else if (e.getAction() == MotionEvent.ACTION_MOVE) {

                    } else if (e.getAction() == MotionEvent.ACTION_UP) {
                        if (!pager.canDragOver()) {
                            currentIndex = lastIndex;
                            currentBitmap = lastBitmap;
                        }
                    }
                    if (mRefreshListener != null) {
                        mRefreshListener.refresh(currentIndex);
                    }

                    ret = pager.doTouchEvent(e);
                    return ret;
                }
                return false;
            }

        });
    }

    private void loadImage(Canvas canvas, int index) {
        View view;
        if (mViews.get(index) != null) {
            view = mViews.get(index);
        } else {
            view = adapter.getView(index);
            mViews.put(index, view);
        }

        Bitmap bitmap = UIUtils.getViewBitmap(view, mWidth, mHeight);
        currentBitmap = bitmap;

        // 把bitmap绘制到canvas中的Bitmap中
        onDraw(canvas, bitmap);
        pager.setBitmaps(mCurPageBitmap, mNextPageBitmap);
        pager.postInvalidate();
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        mRefreshListener = refreshListener;
    }
}
