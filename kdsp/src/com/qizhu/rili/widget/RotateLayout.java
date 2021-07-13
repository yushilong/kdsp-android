package com.qizhu.rili.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.qizhu.rili.R;

/**
 * Created by lindow on 10/26/15.
 * 偏移一定角度的Relativelayout
 */
public class RotateLayout extends RelativeLayout {
    private static float DEFAULT_DEGREES = 0.0f;
    private float mDegrees;

    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateLayout);
        mDegrees = a.getFloat(R.styleable.RotateLayout_rotateDegree, DEFAULT_DEGREES);
        a.recycle();
        setWillNotDraw(false);
    }

    public RotateLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateLayout);
        mDegrees = a.getFloat(R.styleable.RotateLayout_rotateDegree, DEFAULT_DEGREES);
        a.recycle();
    }

    public RotateLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegrees, getWidth() / 2f, getHeight() / 2f);
        super.onDraw(canvas);
    }

    public void setDegrees(int degrees) {
        mDegrees = degrees;
    }
}
