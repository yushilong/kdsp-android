package com.qizhu.rili.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.qizhu.rili.R;

/**
 * Created by lindow on 6/13/16.
 * 划线的view
 */
public class DrawLineView extends View {
    private Context mContext;                       //上下文
    private Resources mResources;                   //资源
    public float mX;                                //先前点x
    public float mY;                                //先前点y
    public float mStartX;                           //初始点x坐标
    public float mStartY;                           //初始点y坐标
    public float mEndX = 0;                         //结束点x坐标
    public float mEndY = 0;                         //结束点y坐标

    private Paint mGesturePaint = new Paint();
    private Path mPath = new Path();
    private Path mDrawPath = new Path();             //动画路径

    private boolean mIsAnim;                        //是否正在播放动画
    private boolean mCanDraw;                       //是否可以划线
    private PathMeasure mPathMeasure;               //path的轨迹
    private ValueAnimator mPhareAnimator;           //动画
    private float mPhare;                           //执行比例

    public DrawLineView(Context context) {
        super(context);
        init(context);
    }

    public DrawLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mResources = context.getResources();
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Paint.Style.STROKE);
        mGesturePaint.setStrokeWidth(10);
        mGesturePaint.setColor(ContextCompat.getColor(context, R.color.pink3));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanDraw) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDown(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    mCanDraw = false;
                    mEndX = event.getX();
                    mEndY = event.getY();
                    break;
            }
            //更新绘制
            invalidate();
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (mIsAnim) {
            canvas.drawPath(mDrawPath, mGesturePaint);
        } else {
            //通过画布绘制多点形成的图形
            canvas.drawPath(mPath, mGesturePaint);
        }
    }

    //手指点下屏幕时调用
    private void touchDown(MotionEvent event) {
        if (mCanDraw) {
            //重置绘制路线，即隐藏之前绘制的轨迹
            mPath.reset();
            float x = event.getX();
            float y = event.getY();

            mStartX = x;
            mStartY = y;
            mX = x;
            mY = y;
            //mPath绘制的绘制起点
            mPath.moveTo(x, y);
        }
    }

    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {
        if (mCanDraw) {
            final float x = event.getX();
            final float y = event.getY();

            final float previousX = mX;
            final float previousY = mY;

            final float dx = Math.abs(x - previousX);
            final float dy = Math.abs(y - previousY);

            //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
            if (dx >= 3 || dy >= 3) {
                //设置贝塞尔曲线的操作点为起点和终点的一半
                float cX = (x + previousX) / 2;
                float cY = (y + previousY) / 2;

                //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
                mPath.quadTo(previousX, previousY, cX, cY);

                //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
                mX = x;
                mY = y;
            }
        }
    }

    /**
     * 重置
     */
    public void reset() {
        mCanDraw = true;
        clearLine();
    }

    /**
     * 清除划线
     */
    public void clearLine() {
        mPath.reset();
        invalidate();
        mStartX = 0;
        mStartY = 0;
        mEndX = 0;
        mEndY = 0;
    }

    public void stopAnim() {
        if (mIsAnim) {
            mIsAnim = false;
            mPhareAnimator.cancel();
            reset();
        }
    }

    public String getStartPoint() {
        return mStartX + "," + mStartY;
    }

    public String getEndPoint() {
        return mEndX + "," + mEndY;
    }

    public void setCanDraw(boolean canDraw) {
        mCanDraw = canDraw;
    }

    /**
     * 划线动画,绝对坐标
     * 传入绝对坐标点划线
     *
     * @param startX     初始点x坐标
     * @param startY     初始点y坐标
     * @param endX       结束点x坐标
     * @param endY       结束点y坐标
     * @param width      drawline本身的宽度
     * @param height     drawline本身的高度
     * @param viewWidth  相对view的宽度，即默认模板的宽度
     * @param viewHeight 相对view的高度，即默认模板的高度
     * @param time       动画执行时间，ms
     * @param type       每条线的贝塞尔点，2为一线类推
     * @param isMale     是否是男生
     */
    public void drawLine(float startX, float startY, float endX, float endY, int width, int height, int viewWidth, int viewHeight, int time, int type, boolean isMale) {
        drawLine(startX * width / viewWidth, startY * height / viewHeight, endX * width / viewWidth, endY * height / viewHeight, time, type, isMale);
    }

    /**
     * 划线动画,相对坐标
     *
     * @param startX 初始点x坐标
     * @param startY 初始点y坐标
     * @param endX   结束点x坐标
     * @param endY   结束点y坐标
     * @param time   动画执行时间，ms
     * @param type   每条线的贝塞尔点，2为一线类推
     * @param isMale 是否是男生
     */
    public void drawLine(float startX, float startY, float endX, float endY, int time, int type, boolean isMale) {
        mIsAnim = true;
        mStartX = startX;
        mStartY = startY;

        mPath.reset();
        mPath.moveTo(startX, startY);
        switch (type) {
            case 2:
                mPath.quadTo((startX + endX) / 2, endY - 35, endX, endY);
                break;
            case 3:
                if (isMale) {
                    mPath.quadTo((startX + endX) / 2, startY + 20, endX, endY);
                } else {
                    mPath.quadTo((startX + endX) / 2, startY + 30, endX, endY);
                }
                break;
            case 4:
                if (isMale) {
                    mPath.quadTo(endX + 40, startY, endX, endY);
                } else {
                    mPath.quadTo(endX + 20, startY, endX, endY);
                }
                break;
            case 5:
                mPath.quadTo((startX + endX) / 2, (startY + endY) / 2, endX, endY);
                break;
            case 6:
                mPath.quadTo((startX + endX) / 2, (startY + endY) / 2, endX, endY);
                break;
        }

        mPathMeasure = new PathMeasure(mPath, false);

        startPhareAnimation(time);
    }

    public void startPhareAnimation(int time) {
        if (mPhareAnimator == null) {
            mPhareAnimator = ValueAnimator.ofFloat(0.0F, 1.0F);
            mPhareAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    setPhare(value);
                }
            });

            mPhareAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    mDrawPath.reset();
                    invalidate();
                }
            });

            mPhareAnimator.setDuration(time);
            mPhareAnimator.setInterpolator(new LinearInterpolator());
            mPhareAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mPhareAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
        mPhare = 0;
        mPhareAnimator.start();
    }

    private void setPhare(float phare) {
        mPhare = phare;
        updatePhare();
        invalidate();
    }

    //更新动画执行比例
    private void updatePhare() {
        if (mPathMeasure.getSegment(0, mPhare * mPathMeasure.getLength(), mDrawPath, true)) {
            //rLineTo兼容4.4以下，防止画不出线
            mDrawPath.rLineTo(0, 0);
        }
    }
}
