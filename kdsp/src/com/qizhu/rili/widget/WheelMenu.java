package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.qizhu.rili.listener.WheelChangeListener;
import com.qizhu.rili.utils.LogUtils;

public class WheelMenu extends ImageView {
    private int mDefWidth = 0;
    private Bitmap imageOriginal, imageScaled;     //variables for original and re-sized image
    private Matrix matrix;                         //Matrix used to perform rotations
    private int wheelHeight, wheelWidth;           //height and width of the view
    private int top;                               //the current top of the wheel (calculated in
    // wheel divs)
    private double totalRotation;                  //variable that counts the total rotation
    // during a given rotation of the wheel by the
    // user (from ACTION_DOWN to ACTION_UP)
    private int divCount;                          //no of divisions in the wheel
    private double divAngle;                       //angle of each division
    private int selectedPosition;                  //the section currently selected by the user.
    private boolean snapToCenterFlag = true;       //variable that determines whether to snap the
    // wheel to the center of a div or not
    private Context context;
    private WheelChangeListener wheelChangeListener;
    //是否可以被选择过
    private boolean mCanChoose = false;
    private double startAngle;
    private TouchChangeListener mTouchChangeListener;       //用户触摸事件
    private float mDownX;                                   //down事件的x坐标
    private float mDownY;                                   //down事件的y坐标

    public WheelMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //initializations
    private void init(Context context) {
        this.context = context;
        this.setScaleType(ScaleType.MATRIX);
        selectedPosition = 0;

        // initialize the matrix only once
        if (matrix == null) {
            matrix = new Matrix();
        } else {
            matrix.reset();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDefWidth != 0) {
            setMeasuredDimension(mDefWidth, mDefWidth);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setWidth(int width) {
        mDefWidth = width;
    }

    /**
     * Add a new listener to observe user selection changes.
     *
     * @param wheelChangeListener
     */
    public void setWheelChangeListener(WheelChangeListener wheelChangeListener) {
        this.wheelChangeListener = wheelChangeListener;
    }

    /**
     * Returns the position currently selected by the user.
     *
     * @return the currently selected position between 1 and divCount.
     */
    public int getSelectedPosition() {
        return selectedPosition;
    }

    /**
     * Set no of divisions in the wheel menu.
     *
     * @param divCount no of divisions.
     */
    public void setDivCount(int divCount) {
        this.divCount = divCount;

        divAngle = 360 / divCount;
        totalRotation = -1 * (divAngle / 2);
    }

    /**
     * Set the snap to center flag. If true, wheel will always snap to center of current section.
     *
     * @param snapToCenterFlag
     */
    public void setSnapToCenterFlag(boolean snapToCenterFlag) {
        this.snapToCenterFlag = snapToCenterFlag;
    }

    /**
     * Set a different top position. Default top position is 0.
     * Should be set after {#setDivCount(int) setDivCount} method and the value should be greater
     * than 0 and lesser
     * than divCount, otherwise the provided value will be ignored.
     *
     * @param newTopDiv
     */
    public void setAlternateTopDiv(int newTopDiv) {
        if (newTopDiv < 0 || newTopDiv >= divCount) {
            return;
        } else {
            top = newTopDiv;
        }

        selectedPosition = top;
    }

    /**
     * Set the wheel image.
     *
     * @param drawableId the id of the drawable to be used as the wheel image.
     */
    public void setWheelImage(int drawableId) {
        imageOriginal = BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    public void changeWheelImage(int drawableId) {
        imageOriginal = BitmapFactory.decodeResource(context.getResources(), drawableId);
        setMatrixImage();
    }

    private void setMatrixImage() {
        // resize the image
        Matrix resize = new Matrix();
        resize.postScale((float) Math.min(wheelWidth, wheelHeight) / (float) imageOriginal
                .getWidth(), (float) Math.min(wheelWidth,
                wheelHeight) / (float) imageOriginal.getHeight());
        imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(),
                imageOriginal.getHeight(), resize, false);
        // translate the matrix to the image view's center
        float translateX = wheelWidth / 2 - imageScaled.getWidth() / 2;
        float translateY = wheelHeight / 2 - imageScaled.getHeight() / 2;
        matrix.postTranslate(translateX, translateY);
        setImageBitmap(imageScaled);
        setImageMatrix(matrix);
    }

    /*
     * We need this to get the dimensions of the view. Once we get those,
     * We can scale the image to make sure it's proper,
     * Initialize the matrix and align it with the views center.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // method called multiple times but initialized just once
        if (wheelHeight == 0 || wheelWidth == 0) {
            wheelHeight = h;
            wheelWidth = w;
            // resize the image
            setMatrixImage();
        }
    }

    /**
     * get the angle of a touch event.
     */
    private double getAngle(double x, double y) {
        x = x - (wheelWidth / 2d);
        y = wheelHeight - y - (wheelHeight / 2d);

        switch (getQuadrant(x, y)) {
            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    /**
     * get the quadrant of the wheel which contains the touch point (x,y)
     *
     * @return quadrant 1,2,3 or 4
     */
    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    /**
     * rotate the wheel by the given angle
     *
     * @param degrees 旋转的角度
     */
    private void rotateWheel(float degrees) {
        matrix.postRotate(degrees, wheelWidth / 2, wheelHeight / 2);
        setImageMatrix(matrix);

        //add the rotation to the total rotation
        totalRotation = totalRotation + degrees;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        //先判断按下事件
        if (MotionEvent.ACTION_DOWN == action && isTouchInCircle(x, y)) {
            return super.dispatchTouchEvent(event);
        }
        LogUtils.d("--->WheelMenu x = " + x + ",y = " + y + ", mStartAngle =" + startAngle);
        if (mCanChoose) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = event.getX();
                    mDownY = event.getY();
                    //get the start angle for the current move event
                    startAngle = getAngle(mDownX, mDownY);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    //get the current angle for the current move event
                    double currentAngle = getAngle(event.getX(), event.getY());

                    float rotateAngle = (float) (startAngle - currentAngle);

                    //如果移动角度为0，那么不触发触摸响应
                    if (rotateAngle != 0 && mTouchChangeListener != null)  {
                        mTouchChangeListener.onTouchChange();
                    }
                    //rotate the wheel by the difference
                    rotateWheel(rotateAngle);

                    //current angle becomes start angle for the next motion
                    startAngle = currentAngle;
                    break;

                case MotionEvent.ACTION_UP:
                    //如果是点击事件，则按点击事件处理
                    if (mDownX == event.getX() && mDownY == event.getY()) {
                        rotateWheel((float) (startAngle - 90));
                        startAngle = 90;
                    }
                    //get the total angle rotated in 360 degrees
                    totalRotation = totalRotation % 360;

                    //represent total rotation in positive value
                    if (totalRotation < 0) {
                        totalRotation = 360 + totalRotation;
                    }

                    //calculate the no of divs the rotation has crossed
                    int no_of_divs_crossed = (int) ((totalRotation) / divAngle);

                    //calculate current top
                    top = (divCount + top - no_of_divs_crossed) % divCount;

                    //set the currently selected option
                    if (top == 0) {
                        selectedPosition = divCount - 1;//loop around the array
                    } else {
                        selectedPosition = top - 1;
                    }

                    if (wheelChangeListener != null) {
                        wheelChangeListener.onSelectionChange(selectedPosition);
                    }

                    //for next rotation, the initial total rotation will be the no of degrees
                    // inside the current top
                    totalRotation = totalRotation % divAngle;

                    //snapping to the top's center
                    if (snapToCenterFlag) {

                        //calculate the angle to be rotated to reach the top's center.
                        double leftover = divAngle / 2 - totalRotation;
                        if (3 == selectedPosition) {
                            leftover = leftover + 10;
                        }

                        rotateWheel((float) (leftover));

                        //re-initialize total rotation
                        totalRotation = divAngle / 2;
                        //当为3时，需要向右多偏移10度
                        if (3 == selectedPosition) {
                            totalRotation = totalRotation + 10;
                        }
                    }
                    return true;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 根据触摸的位置，计算是否是在中心或者外面触摸，此时并不触发事件拦截
     * 中心即为去除边距和item的位置
     */
    private boolean isTouchInCircle(float xTouch, float yTouch) {
        //中心view的半径
        double r = wheelWidth * 400 / 750 / 2d;
        //整个的半径
        double d = wheelWidth * 650 / 750 / 2d;
        double x = xTouch - wheelWidth / 2d;
        double y = yTouch - wheelWidth / 2d;
        return x * x + y * y <= r * r || x * x + y * y >= d * d;
    }

    public void setCanChoose(boolean canChoose) {
        mCanChoose = canChoose;
    }

    public void setTouchListener(TouchChangeListener touchListener) {
        mTouchChangeListener = touchListener;
    }

    /**
     * Created by lindow on 11/24/15.
     * Interface to to observe user touch changes.
     */
    public interface TouchChangeListener {
        /**
         * Called when user touch in the wheel menu.
         */
        void onTouchChange();
    }
}
