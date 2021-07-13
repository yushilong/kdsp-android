package com.qizhu.rili.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.WheelChangeListener;
import com.qizhu.rili.utils.LogUtils;

/**
 * Created by lindow on 11/19/15.
 * 圆形菜单,圆心部分不接收滑动事件,同时保证子view的竖直方向，所以每次重新requestLayout
 */
public class CircleMenuLayout extends ViewGroup {
    //该容器的内边距,无视padding属性，如需边距请用该变量,为占mRadius的比例,默认为0，即无内边距
    private static final float RADIO_PADDING_LAYOUT = 0;
    //当每秒移动角度达到该值时，认为是快速移动
    private static final int FLINGABLE_VALUE = 300;
    //如果移动角度达到该值，则屏蔽点击
    private static final int NOCLICK_VALUE = 3;

    //整个View的宽度
    private int mRadius;
    //该容器的内边距,无视padding属性，如需边距请用该变量
    private float mPadding;
    //边距比例
    private float mPaddingRadio;
    //设置的宽度
    private int mWidth;
    //该容器内child item的默认尺寸,为占mRadius的比例
    private float mChildWidthRadio;
    //布局时的开始角度
    private float mStartAngle = 0;
    //菜单的个数
    private int mMenuItemCount;
    //angle of each division
    private float mDivAngle;
    //检测按下到抬起时旋转的角度
    private float mTmpAngle;
    //检测按下到抬起时使用的时间
    private long mDownTime;
    //判断是否正在自动滚动
    private boolean isFling;
    //the section currently selected by the user.
    private int mSelectedPosition;
    //滚动监听器
    private WheelChangeListener mWheelChangeListener;

    private boolean mShouldLayout = false;          //是否应该OnLayout
    private boolean mIsTouched = false;             //是否已经触摸

    //记录上一次的x，y坐标
    private float mLastX;
    private float mLastY;

    //自动滚动的Runnable
    private AutoFlingRunnable mFlingRunnable;
    //当每秒移动角度达到该值时，认为是快速移动
    private int mFlingableValue = FLINGABLE_VALUE;

    public CircleMenuLayout(Context context) {
        super(context);
        setPadding(0, 0, 0, 0);
        mSelectedPosition = 0;
    }

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 无视padding
        setPadding(0, 0, 0, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateLayout);
        mPaddingRadio = a.getFloat(R.styleable.RollerLayout_paddingRadio, RADIO_PADDING_LAYOUT);
        a.recycle();
        mSelectedPosition = 0;
    }

    public CircleMenuLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPadding(0, 0, 0, 0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotateLayout);
        mPaddingRadio = a.getFloat(R.styleable.RollerLayout_paddingRadio, RADIO_PADDING_LAYOUT);
        a.recycle();
        mSelectedPosition = 0;
    }

    /**
     * 设置布局的宽高，并策略menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;

        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * 如果宽或者高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
            resHeight = getSuggestedMinimumHeight();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            // 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }

        setMeasuredDimension(resWidth, resHeight);

        // 获得直径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        // menu item数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * mChildWidthRadio);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;

        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;

            makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        mPadding = mPaddingRadio * mRadius;
    }

    /**
     * MenuItem的点击事件接口
     */
    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);
    }

    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mShouldLayout) {
            mShouldLayout = false;
            int layoutRadius = mRadius;

            // Laying out the child views
            final int childCount = getChildCount();

            int left, top;
            // menu item 的尺寸
            int cWidth = (int) (layoutRadius * mChildWidthRadio);

            LogUtils.d("--->CircleMenuLayout start mStartAngle =" + mStartAngle);
            // 遍历去设置menuitem的位置
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);

                if (child.getVisibility() == GONE) {
                    continue;
                }

                mStartAngle %= 360;

                // 计算，中心点到menu item中心的距离
                float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;

                // tmp cosa 即menu item中心点的横坐标
                left = layoutRadius / 2 + (int) Math.round(tmp * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);
                // tmp sina 即menu item的纵坐标
                top = layoutRadius / 2 + (int) Math.round(tmp * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f * cWidth);

                child.layout(left, top, left + cWidth, top + cWidth);

                LogUtils.d("--->CircleMenuLayout i = " + i + ", mStartAngle =" + mStartAngle);

                if (mStartAngle > 45 && mStartAngle < 135) {
                    child.setVisibility(VISIBLE);
                } else {
                    child.setVisibility(INVISIBLE);
                }

                // 叠加尺寸
                mStartAngle += mDivAngle;
            }
            LogUtils.d("--->CircleMenuLayout end mStartAngle =" + mStartAngle);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        //如果触摸点在外面，并且没有被触摸，那么直接拦截
//        if (isTouchInCircle(x, y)) {
//            return super.dispatchTouchEvent(event);
//        }

        LogUtils.d("--->CircleMenuLayout x = " + x + ",y = " + y + ", mStartAngle =" + mStartAngle);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTmpAngle = 0;

                // 如果当前已经在快速滚动
                if (isFling) {
                    // 移除快速滚动的回调
                    removeCallbacks(mFlingRunnable);
                    isFling = false;
                    return true;
                }
                mIsTouched = true;

                break;
            case MotionEvent.ACTION_MOVE:

                //获得开始的角度
                float start = getAngle(mLastX, mLastY);
                //获得当前的角度
                float end = getAngle(x, y);

                LogUtils.d("--->CircleMenuLayout start = " + start + " , end =" + end);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                    mTmpAngle += end - start;
                } else {
                    // 二、三象限，角度值是负值
                    mStartAngle += start - end;
                    mTmpAngle += start - end;
                }
                if (mWheelChangeListener != null) {
                    setSelectedView(mSelectedPosition, getSelection());
                    //若没有快速滑动，那么就设置当前的view为选中状态
                    mWheelChangeListener.onSelectionChange(mSelectedPosition);
                }
                // 重新绘制
                mShouldLayout = true;
                requestLayout();

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                // 计算，每秒移动的角度
                float anglePerSecond = mTmpAngle * 1000 / (System.currentTimeMillis() - mDownTime);

                LogUtils.d("--->CircleMenuLayout anglePerSecond = " + anglePerSecond + " , mTmpAngel = " + mTmpAngle);

                // 如果达到该值认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingableValue && !isFling) {
                    // post一个任务，去自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));
                    return true;
                } else {
                    rollerToItemCenter(mSelectedPosition, false);
                }

                // 如果当前旋转角度超过NOCLICK_VALUE屏蔽点击
                if (Math.abs(mTmpAngle) > NOCLICK_VALUE) {
                    return true;
                }
                mIsTouched = false;

                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch 触摸的x坐标
     * @param yTouch 触摸的y坐标
     * @return 返回此坐标点的角度
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据触摸的位置，计算是否是在中心触摸，此时并不触发事件拦截
     * 中心即为去除边距和item的位置
     */
    private boolean isTouchInCircle(float xTouch, float yTouch) {
        //中心view的半径
        double r = mRadius * (1 - mChildWidthRadio - mPaddingRadio - 0.1) / 2d;
        double x = xTouch - mRadius / 2d;
        double y = yTouch - mRadius / 2d;
        return x * x + y * y <= r * r;
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x x轴坐标
     * @param y y轴坐标
     * @return 返回象限
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param disableIds 图片资源id
     * @param enableIds  可用的图片资源id
     */
    public void setMenuItemIcons(int[] disableIds, int[] enableIds) {
        mStartAngle = 0;
        removeAllViews();
        // 参数检查
        if (disableIds != null) {
            // 初始化mMenuCount
            mMenuItemCount = disableIds.length * 4;

            mDivAngle = ((float) 360) / mMenuItemCount;

            Context mContext = getContext();
            //根据用户设置的参数，初始化view
            for (int i = 0; i < mMenuItemCount; i++) {
                final int j = i;
                RollerItemView rollerItemView = new RollerItemView(mContext);

                rollerItemView.setImage(disableIds[i % 5], enableIds[i % 5], i);
                rollerItemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rollerToItemCenter(j, true);
                    }
                });

                // 添加view到容器中
                addView(rollerItemView);
            }
            mShouldLayout = true;
            //旋转到第5个
            if (mWheelChangeListener != null) {
                mSelectedPosition = 5;
                setSelectedView(0, 5);
                mWheelChangeListener.onSelectionChange(5);
            }
        }
    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param disableIds 图片资源id
     * @param enableIds  可用的图片资源id
     */
    public void setMenuItemIconsAndTexts(int[] disableIds, int[] enableIds, String[] texts) {

        // 参数检查
        if (disableIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }

        // 初始化mMenuCount
        mMenuItemCount = disableIds == null ? texts.length : disableIds.length;

        if (disableIds != null && texts != null) {
            mMenuItemCount = Math.min(disableIds.length, texts.length);
        }
        mDivAngle = ((float) 360) / mMenuItemCount;

        Context mContext = getContext();
        //根据用户设置的参数，初始化view
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            RollerItemView rollerItemView = new RollerItemView(mContext);

            rollerItemView.setImageAndText(disableIds[i], enableIds[i], texts[i], i);
            rollerItemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    rollerToItemCenter(j, true);
                }
            });

            // 添加view到容器中
            addView(rollerItemView);
        }
        mShouldLayout = true;
        //旋转到以约么为中心，并选中约么
        mStartAngle = (360 + 270 - 16 * mDivAngle) % 360;
        if (mWheelChangeListener != null) {
            mSelectedPosition = 16;
            setSelectedView(0, 16);
            mWheelChangeListener.onSelectionChange(16);
        }
    }

    /**
     * 如果每秒旋转角度到达该值，则认为是自动滚动
     *
     * @param flingableValue 旋转角度
     */
    public void setFlingableValue(int flingableValue) {
        mFlingableValue = flingableValue;
    }

    /**
     * 设置内边距的比例
     *
     * @param padding 边距
     */
    public void setPadding(float padding) {
        mPadding = padding;
    }

    /**
     * 设置宽度
     *
     * @param width 宽度
     */
    public void setWidth(int width) {
        mWidth = width;
    }

    /**
     * 设置宽度
     *
     * @param radio 子item宽度比例
     */
    public void setItemWidthRadio(float radio) {
        mChildWidthRadio = radio;
    }

    /**
     * 获得默认该layout的尺寸,返回宽和高中较小的值
     */
    private int getDefaultWidth() {
        if (mWidth != 0) {
            return mWidth;
        }
        return Math.min(AppContext.getScreenWidth(), AppContext.getScreenHeight());
    }

    /**
     * 自动滚动的任务
     */
    private class AutoFlingRunnable implements Runnable {

        private float angelPerSecond;

        public AutoFlingRunnable(float velocity) {
            this.angelPerSecond = velocity;
        }

        public void run() {
            // 如果小于20,则停止
            if ((int) Math.abs(angelPerSecond) < 20) {
                isFling = false;
                if (mWheelChangeListener != null) {
                    setSelectedView(mSelectedPosition, getSelection());
                    mWheelChangeListener.onSelectionChange(mSelectedPosition);
                }
                rollerToItemCenter(mSelectedPosition, false);
                return;
            }
            isFling = true;
            // 不断改变mStartAngle，让其滚动，/30为了避免滚动太快,每30ms就开始转动
            mStartAngle += (angelPerSecond / 30);
            // 逐渐减小这个值
            angelPerSecond /= 1.0666F;
            postDelayed(this, 30);
            // 重新绘制
            mShouldLayout = true;
            requestLayout();
        }
    }

    private int getSelection() {
        mSelectedPosition = (int) (((mStartAngle + mDivAngle / 2) % 360) / mDivAngle);
        //转化对应的selection
        mSelectedPosition = (20 + 5 - mSelectedPosition) % 20;
        LogUtils.d("CircleMenuLayout ---> mSelectedPosition -> " + mSelectedPosition + ",mStartAngle = " + mStartAngle);
        return mSelectedPosition;
    }

    private void setSelectedView(int oldSelected, int newSelected) {
        try {
            //当前选中若未发生变化，则更新状态
            if (oldSelected != newSelected) {
                RollerItemView oldItem = (RollerItemView) getChildAt(oldSelected);
                oldItem.setMode(RollerItemView.UN_SELECTED_MODE, oldSelected);
                RollerItemView newItem = (RollerItemView) getChildAt(newSelected);
                newItem.setMode(RollerItemView.SELECTED_MODE, newSelected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 滚动到item的中间部位
     *
     * @param position item的id
     * @param changed  是否发送选中变更
     */
    private void rollerToItemCenter(int position, boolean changed) {
        mStartAngle = (360 + 90 - position * mDivAngle) % 360;
        mShouldLayout = true;
        requestLayout();
        if (changed) {
            if (mWheelChangeListener != null) {
                setSelectedView(mSelectedPosition, getSelection());
                mWheelChangeListener.onSelectionChange(mSelectedPosition);
            }
        }
    }

    /**
     * Returns the position currently selected by the user.
     *
     * @return the currently selected position between 1 and divCount.
     */
    public int getmSelectedPosition() {
        return mSelectedPosition;
    }

    /**
     * Add a new listener to observe user selection changes.
     */
    public void setRollerChangeListener(WheelChangeListener rollerChangeListener) {
        mWheelChangeListener = rollerChangeListener;
    }
}
