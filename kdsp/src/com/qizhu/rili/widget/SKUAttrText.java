package com.qizhu.rili.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.qizhu.rili.R;

/**
 * Created by lindow on 27/02/2017.
 * sku的属性Text
 */

public class SKUAttrText extends TextView {
    private Context mContext;

    public static final int ENABLE_MODE = 0;         //可选
    public static final int DISABLE_MODE = 1;        //不可选
    public static final int SELECT_MODE = 2;         //选中

    public SKUAttrText(Context context) {
        super(context);
        mContext = context;
    }

    public SKUAttrText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SKUAttrText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public SKUAttrText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    public void setContent(String text) {
        setText(text);
        setTag(text);
        setTextColor(ContextCompat.getColor(mContext, R.color.black));
        setTextSize(16);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.rectangle_gray20_white);
    }

    public void setMode(int mode) {
        switch (mode) {
            case ENABLE_MODE:
                setTextColor(ContextCompat.getColor(mContext, R.color.black));
                setBackgroundResource(R.drawable.rectangle_gray20_white);
                setClickable(true);
                break;
            case DISABLE_MODE:
                setTextColor(ContextCompat.getColor(mContext, R.color.white));
                setBackgroundResource(R.color.gray);
                setClickable(false);
                break;
            case SELECT_MODE:
                setTextColor(ContextCompat.getColor(mContext, R.color.white));
                setBackgroundResource(R.color.purple1);
                setClickable(false);
                break;
        }
    }
}
