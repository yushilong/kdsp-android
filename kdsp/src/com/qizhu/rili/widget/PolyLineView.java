package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.qizhu.rili.R;

import java.util.ArrayList;

/**
 * Created by lindow on 9/11/16.
 * 折线图
 */
public class PolyLineView extends View {
    private Paint mGesturePaint = new Paint();
    private Path mPath = new Path();

    public PolyLineView(Context context) {
        super(context);
        init(context);
    }

    public PolyLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PolyLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeWidth(2);
        mGesturePaint.setColor(ContextCompat.getColor(context, R.color.gray));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //通过画布绘制多点形成的图形
        canvas.drawPath(mPath, mGesturePaint);
    }

    public void setPath(ArrayList<Point> points) {
        mPath.reset();
        if (points != null && points.size() > 0) {
            mPath.moveTo(points.get(0).x, points.get(0).y);
            for (Point point : points) {
                mPath.lineTo(point.x, point.y);
            }
        }

        invalidate();
    }
}
