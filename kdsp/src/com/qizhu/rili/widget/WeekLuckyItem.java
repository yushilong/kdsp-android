package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.utils.DisplayUtils;

import java.util.ArrayList;

/**
 * Created by lindow on 2/22/16.
 * 周运的圆柱形标记
 */
public class WeekLuckyItem extends LinearLayout {
    private Context mContext;
    private int mHeight;            //总高度

    private TextView mTitle;        //标题
    private View mDaysLay;          //布局
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mText5;

    public WeekLuckyItem(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public WeekLuckyItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public WeekLuckyItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.week_lucky_item, this);
        mTitle = (TextView) findViewById(R.id.title);
        mDaysLay = findViewById(R.id.days_lay);
        mText1 = (TextView) findViewById(R.id.text1);
        mText2 = (TextView) findViewById(R.id.text2);
        mText3 = (TextView) findViewById(R.id.text3);
        mText4 = (TextView) findViewById(R.id.text4);
        mText5 = (TextView) findViewById(R.id.text5);
    }

    /**
     * 设置高度
     */
    public void setHeight(int height) {
        mHeight = height;
    }

    /**
     * 设置字符串数组
     */
    public void setTextArray(String title, ArrayList<String> array) {
        mText5.setVisibility(GONE);
        mText4.setVisibility(GONE);
        mText3.setVisibility(GONE);
        mText2.setVisibility(GONE);
        mText1.setVisibility(GONE);

        mTitle.setText(title);
        int length = array.size();
        int itemHeight = (mHeight - DisplayUtils.dip2px(30)) * (length > 5 ? 5 : length) / 5;
        ViewGroup.LayoutParams layoutParams = mDaysLay.getLayoutParams();
        layoutParams.height = itemHeight;
        switch (length) {
            case 0:
                break;
            case 1:
                mText5.setVisibility(VISIBLE);
                mText5.setText(array.get(0));
                break;
            case 2:
                mText5.setVisibility(VISIBLE);
                mText5.setText(array.get(0));
                mText4.setVisibility(VISIBLE);
                mText4.setText(array.get(1));
                break;
            case 3:
                mText5.setVisibility(VISIBLE);
                mText5.setText(array.get(0));
                mText4.setVisibility(VISIBLE);
                mText4.setText(array.get(1));
                mText3.setVisibility(VISIBLE);
                mText3.setText(array.get(2));
                break;
            case 4:
                mText5.setVisibility(VISIBLE);
                mText5.setText(array.get(0));
                mText4.setVisibility(VISIBLE);
                mText4.setText(array.get(1));
                mText3.setVisibility(VISIBLE);
                mText3.setText(array.get(2));
                mText2.setVisibility(VISIBLE);
                mText2.setText(array.get(3));
                break;
            case 5:
                mText5.setVisibility(VISIBLE);
                mText5.setText(array.get(0));
                mText4.setVisibility(VISIBLE);
                mText4.setText(array.get(1));
                mText3.setVisibility(VISIBLE);
                mText3.setText(array.get(2));
                mText2.setVisibility(VISIBLE);
                mText2.setText(array.get(3));
                mText1.setVisibility(VISIBLE);
                mText1.setText(array.get(4));
                break;
            default:
                mText5.setVisibility(VISIBLE);
                mText5.setText(array.get(0));
                mText4.setVisibility(VISIBLE);
                mText4.setText(array.get(1));
                mText3.setVisibility(VISIBLE);
                mText3.setText(array.get(2));
                mText2.setVisibility(VISIBLE);
                mText2.setText(array.get(3));
                mText1.setVisibility(VISIBLE);
                mText1.setText(array.get(4));
                break;
        }
    }
}
