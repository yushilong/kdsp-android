package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 监听布局变化的RelativeLayout
 */
public class ResizeRelativeLayout extends RelativeLayout{

    private OnResizeListener mListener;

    public interface OnResizeListener{
        void onResize(int w, int h, int oldW, int oldH);
    }

    public ResizeRelativeLayout(Context context) {
        super(context);
    }

    public ResizeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnResizeListener(OnResizeListener pListener){
        this.mListener = pListener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.mListener != null){
            this.mListener.onResize(w,h,oldw,oldh);
        }
    }
}
