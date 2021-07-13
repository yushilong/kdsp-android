package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lindow on 15/9/22.
 * 实现自定义字体的textview
 */
public class CustomFontTextView extends TextView{
    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
//        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/pangwajianti.ttf"));
    }
}
