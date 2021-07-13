
package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.qizhu.rili.bean.Line;

import java.util.ArrayList;
import java.util.List;

public class CirclePercentView extends View {
    private Paint paintNormal;
    private Paint paintOnTouch;
    private Paint paintInnerCycle;
    private Paint paintLines;
    private Paint paintKeyError;
    private Paint mRectPaint;
    private Paint mTextPaint;
    int perSize = 0;
    private int    mWidth;
    private int    mHeight;
    private Line[] cycles;
    private Path          linePath    = new Path();
    private List<Float> linedCycles = new ArrayList<Float>();
    private ArrayList<String> mColors = new ArrayList<>();



    public CirclePercentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CirclePercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CirclePercentView(Context context) {
        super(context);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();

    }

    private void init() {
        paintNormal = new Paint();
        paintNormal.setAntiAlias(true);
//		paintNormal.setStrokeWidth(3);
//		paintNormal.setStyle(Paint.Style.STROKE);
        paintNormal.setColor(Color.WHITE);



        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextSize(60);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextPaint.setColor(Color.parseColor("#c8b6d5"));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;//获取中心点X坐标
        int certerY = getHeight() / 2;//获取中心点Y坐标
        int radius = (int) (centerX - 100 / 2);//圆环的半径
        RectF oval = new RectF(centerX - radius, centerX - radius, centerX
                + radius, centerX + radius); //用于定义的圆弧的形状和大小的界

        for (int i = 0; i < linedCycles.size(); i++) {

                mTextPaint.setColor(Color.parseColor(mColors.get(i)));

            canvas.drawArc(oval, getTotalNum(i,linedCycles), linedCycles.get(i)  * 360/ 100, true, mTextPaint);
        }
        canvas.drawCircle(centerX, certerY, 60, paintNormal);
    }

    public void setData(ArrayList<Float> mArrayList,ArrayList<String> color) {
        this.linedCycles = mArrayList;
        this.mColors = color;
        invalidate();
    }

    private float getTotalNum(int num,List<Float> linedCycle) {
        float total = 180;
        for (int i = 0; i < num; i++) {
            total += linedCycle.get(i) * 360/ 100 ;
        }
        return total;
    }


}
