package com.qizhu.rili.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 倒计时textview
 */
public class TimeTextView extends AppCompatTextView {
    public static final int TYPE_CODE = 0;          //验证码倒计时
    public static final int TYPE_SPLASH = 1;        //闪屏倒计时
    public static final int TYPE_REPLY = 2;         //回复问题倒计时
    public static final int TYPE_REPLY_HANDS = 3;   //回复手相面相倒计时
    private int[] times;
    private int mday, mhour, mmin, msecond;         // 天、时、分、秒
    private static final int TIME_TYPE_DAH = 0;
    private static final int TIME_TYPE_HAM = 1;
    private static final int TIME_TYPE_MAS = 2;
    private static final int TIME_TYPE_SAS = 3;

    private CountDownTimer mCountDownTimer;

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TimeTextView(Context context) {
        super(context);
    }

    public int[] getTimes() {
        return times;
    }

    public void setTimes(int[] times, int type) {
        this.times = times;
        mday = times[0];
        mhour = times[1];
        mmin = times[2];
        msecond = times[3];

        switch (type) {
            case TYPE_CODE:
                buildCodeStrTime();
                break;
            case TYPE_SPLASH:
                buildSplashStrTime();
                break;
            case TYPE_REPLY:
                buildReplyStrTime();
                break;
            case TYPE_REPLY_HANDS:
                buildHandsReplyStrTime();
                break;
            default:
                break;
        }
    }

    public void setCountDownTimer(CountDownTimer countDownTimer) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mCountDownTimer = countDownTimer;
    }

    public void cancelCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    /**
     * 根据时间大小 构造不同的字符串
     */
    private void buildCodeStrTime() {
        if (mday > 0) {
            buildStrTime(TIME_TYPE_DAH);
        } else if (mhour > 0) {
            buildStrTime(TIME_TYPE_HAM);
        } else if (mmin > 0) {
            buildStrTime(TIME_TYPE_MAS);
        } else if (msecond > 0) {
            buildStrTime(TIME_TYPE_SAS);
        }
    }

    private void buildSplashStrTime() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("跳过（").append(msecond).append("S").append("）");
        setText(sBuilder);
    }

    private void buildReplyStrTime() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(mmin).append(":").append(msecond);
        setText(sBuilder);
    }

    private void buildHandsReplyStrTime() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(mhour).append(":").append(mmin).append(":").append(msecond);
        setText(sBuilder);
    }

    /**
     * 构造时间字符串
     *
     * @param timeType 时间类别
     */
    public void buildStrTime(int timeType) {
        StringBuilder sBuilder = new StringBuilder();
        switch (timeType) {
            case TIME_TYPE_DAH:
                sBuilder.append(mday).append("天").append(mhour).append("时");
                break;
            case TIME_TYPE_HAM:
                sBuilder.append(mhour).append("时").append(mmin).append("分");
                break;
            case TIME_TYPE_MAS:
                sBuilder.append(mmin).append("分").append(msecond).append("秒");
                break;
            case TIME_TYPE_SAS:
                sBuilder.append(msecond).append("S后重发");
                break;
        }
        setText(sBuilder);
    }

    private StringBuilder getTimeBuilder(StringBuilder builder) {
        if (0 == mday) {
            if (0 == mhour) {
                if (0 == mmin) {
                    builder.append(msecond).append("秒");
                } else {
                    builder.append(mmin).append("分").append(msecond).append("秒");
                }
            } else {
                builder.append(mhour).append("小时").append(mmin).append("分").append(msecond).append("秒");
            }
        } else {
            builder.append(mday).append("天").append(mhour).append("小时").append(mmin).append("分").append(msecond).append("秒");
        }
        return builder;
    }

    /**
     * 计算倒计时时间
     */
    public void ComputeTime(int type) {
        msecond--;
        if (msecond < 0) {
            mmin--;
            msecond = 59;
            if (mmin < 0) {
                mmin = 59;
                mhour--;
                if (mhour < 0) {
                    mhour = 23;
                    mday--;
                }
            }
        }

        switch (type) {
            case TYPE_CODE:
                buildCodeStrTime();
                break;
            case TYPE_REPLY:
                buildReplyStrTime();
                break;
            case TYPE_REPLY_HANDS:
                buildHandsReplyStrTime();
                break;
            default:
                break;
        }
    }
}