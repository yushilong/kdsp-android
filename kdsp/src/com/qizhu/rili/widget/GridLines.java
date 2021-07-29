package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.qizhu.rili.R;
import com.qizhu.rili.utils.DisplayUtils;

/**
 * Created by lindow on 21/04/2017.
 * 网格线
 */

public class GridLines extends View {
    private Paint paint;  //绘图


    public GridLines(Context context) {
        super(context);
        init(context);
    }

    public GridLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridLines(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public GridLines(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.gray13));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DisplayUtils.dip2px(1));
    }

    /**
     * 绘制网格线
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = DisplayUtils.dip2px(595);  //长宽，只有一个地方用到，那么就写死了
        int height = DisplayUtils.dip2px(181);
        int hortspace = DisplayUtils.dip2px(53 + 1);    //宽度间隔,注意加上线宽
        int vertpace = DisplayUtils.dip2px(35 + 1);     //高度间隔,注意加上线宽
        int vertz = 0;
        int hortz = 0;
        //横向
        for (int j = 0; j < 6; j++) {
            canvas.drawLine(0, vertz, width, vertz, paint);
            vertz += vertpace;
        }
        //纵向
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(hortz, 0, hortz, height, paint);
            hortz += hortspace;
        }
    }
}