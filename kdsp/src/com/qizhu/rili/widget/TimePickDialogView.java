package com.qizhu.rili.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.GoodsListActivity;
import com.sina.weibo.sdk.utils.UIUtils;

import java.util.Date;

/**
 * Created by lindow on 02/03/2017.
 * 自定义的时间选择框
 */

public class TimePickDialogView extends LinearLayout {
    private WheelMain mWheelMain;                   //时间选择滚轮
    private ImageView mSolar;                       //阳历
    private ImageView mLunar;                       //阴历

    private Context mContext;
    private int mode;

    public TimePickDialogView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public TimePickDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public TimePickDialogView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public TimePickDialogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
    }

    public void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.time_select_lay, this);
        mWheelMain = new WheelMain(findViewById(R.id.select_lay));
        mSolar = (ImageView) findViewById(R.id.solar);
        mLunar = (ImageView) findViewById(R.id.lunar);

        mSolar.setImageResource(R.drawable.solar_selected);
        mLunar.setImageResource(R.drawable.lunar_unselected);

        //转换阳历
        mSolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWheelMain.setDateMode(0);
                mSolar.setImageResource(R.drawable.solar_selected);
                mLunar.setImageResource(R.drawable.lunar_unselected);
            }
        });

        //转换阴历
        mLunar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWheelMain.setDateMode(1);
                mSolar.setImageResource(R.drawable.solar_unselected);
                mLunar.setImageResource(R.drawable.lunar_selected);
            }
        });

        findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //确定之后将时间返回给activity
                if (mContext instanceof GoodsListActivity) {

                    if(mWheelMain.getDateTime().getDate().getTime() > new Date().getTime()){
                        UIUtils.showToast(mContext,"生日不能大于当前时间", Toast.LENGTH_LONG);
                        return;
                    }
                    ((GoodsListActivity) mContext).pickDateTime(mWheelMain.getDateTime(), mWheelMain.getMode());
                    ((GoodsListActivity) mContext).cancelPick();
                }
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mContext instanceof GoodsListActivity) {
                    ((GoodsListActivity) mContext).cancelPick();
                }
            }
        });
    }

    public void setDateTime(DateTime dateTime) {
        mWheelMain.initDateTimePicker(dateTime, 1);
    }
    public void setDateTime(DateTime dateTime,int mode) {
        mWheelMain.initDateTimePicker(dateTime, mode);
        if (0 == mode) {
            mSolar.setImageResource(R.drawable.solar_selected);
            mLunar.setImageResource(R.drawable.lunar_unselected);
        } else {
            mSolar.setImageResource(R.drawable.solar_unselected);
            mLunar.setImageResource(R.drawable.lunar_selected);
        }
    }
}
