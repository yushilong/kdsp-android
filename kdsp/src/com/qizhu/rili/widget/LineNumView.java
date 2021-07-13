
package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.qizhu.rili.bean.Line;
import com.qizhu.rili.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class LineNumView extends View {
    private Paint paintNormal;
    private Paint paintInnerCycle;
    private Paint paintLines;
    private Paint mRectPaint;
    private Paint mTextPaint;
    int perSize = 0;
    private int    mWidth;
    private int    mHeight;
    private Line[] cycles;
    private Path          linePath    = new Path();
    private List<Integer> linedCycles = new ArrayList<Integer>();


    public ArrayList<Line> mArrayList = new ArrayList<>();




    public LineNumView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LineNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineNumView(Context context) {
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
        if (cycles == null && (perSize = getWidth() / 6) > 0) {
            cycles = new Line[9];
            for (int j= 0; j < 3; j++) {
                for (int i = 0; i < 3; i++) {
                    Line cycle = new Line();
                    cycle.x = j * getWidth() / 3;
                    cycle.y = i * getHeight() / 3;
                    cycle.centerX = j * getWidth() / 3 + perSize;
                    cycle.centerY = i * getHeight() / 3 + perSize;
                    cycle.positonX = j;
                    cycle.positonY = i;
                    cycle.r = perSize * 0.5f;
                    cycles[j * 3 + i] = cycle;
                }
            }
        }
    }

    private void init() {
        paintNormal = new Paint();
        paintNormal.setAntiAlias(true);
//		paintNormal.setStrokeWidth(3);
//		paintNormal.setStyle(Paint.Style.STROKE);
        paintNormal.setColor(Color.WHITE);

        paintInnerCycle = new Paint();
        paintInnerCycle.setAntiAlias(true);
        paintInnerCycle.setStyle(Paint.Style.FILL);

        paintLines = new Paint();
        paintLines.setAntiAlias(true);
        paintLines.setStyle(Paint.Style.STROKE);
        paintLines.setStrokeWidth(6);


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextSize(60);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextPaint.setColor(Color.parseColor("#747373"));

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < cycles.length; i++) {

            if (i % 2 == 0) {
                mRectPaint.setColor(Color.parseColor("#fefbdd"));
            } else {
                mRectPaint.setColor(Color.parseColor("#b39f98"));
            }
            Rect rect = new Rect(cycles[i].x, cycles[i].y, cycles[i].x + mWidth / 3, cycles[i].y + mHeight / 3);
            canvas.drawRect(rect, mRectPaint);


        }

        for (Line cycle : mArrayList) {
            drawLine(canvas, cycle);
        }


        for (int i = 0; i < cycles.length; i++) {
            Rect rect = new Rect(cycles[i].x, cycles[i].y, cycles[i].x + mWidth / 3, cycles[i].y + mHeight / 3);
            canvas.drawCircle(cycles[i].centerX, cycles[i].centerY, cycles[i].r, paintNormal);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            canvas.drawText("" + (i + 1), rect.centerX(), baseline, mTextPaint);
        }


    }

    public void setData(ArrayList<Line> mArrayList) {
        this.mArrayList = mArrayList;
        invalidate();
    }


    private void drawLine(Canvas canvas, Line cycle) {

        if (cycle == null) {
            return;
        }
        linedCycles.clear();
        int num = StringUtils.toInt(cycle.line);
        int numOne = 0;
        int numTwo = 0;
        if (num > 100) {
            numOne = num / 100 - 1;
            numTwo = num % 10 -1 ;
        }else {
            numOne = num / 10 - 1;
            numTwo = num % 10 -1 ;
        }
        linedCycles.add(numOne);
        linedCycles.add(numTwo);
        linePath.reset();
        paintLines.setColor(Color.parseColor("#"+cycle.lineColor));
        if (linedCycles.size() > 0) {
            for (int i = 0; i < linedCycles.size(); i++) {
                int index = linedCycles.get(i);
                float x = cycles[index].centerX;
                float y = cycles[index].centerY;
                if (i == 0) {
                    linePath.moveTo(x, y);
                } else {
                    linePath.lineTo(x, y);
                }
            }

            canvas.drawPath(linePath, paintLines);
        }
    }


}
