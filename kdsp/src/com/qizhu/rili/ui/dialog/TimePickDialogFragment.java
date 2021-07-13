package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.widget.WheelMain;
import com.sina.weibo.sdk.utils.UIUtils;

import java.util.Date;

/**
 * Created by lindow on 15/8/24.
 * 滚轮时间选择对话框
 */
public class TimePickDialogFragment extends BaseDialogFragment {
    public static final int PICK_ALL   = 0;           //选择全部
    public static final int PICK_DAY   = 1;           //选择精确到天
    public static final int PICK_MIN   = 2;           //仅仅选择小时和分钟
    public static final int PICK_MONTH = 3;         //选择精确到月
    public static final int PICK_HOUR  = 4;          //选择精确到小时

    private WheelMain mWheelMain;                   //时间选择滚轮
    private ImageView mSolar;                       //阳历
    private ImageView mLunar;                       //阴历

    private DateTime mDateTime;         //传输过来的日期
    private int mMode      = PICK_ALL;       //传输过来的模式,默认为全部选择
    private int mSolarMode = 0;         //选择阳历还是阴历,0为选择阳历
    private        boolean          mShowSolar;         //是否显示阴阳历切换
    private        String          mCalendarGood;         //择日页面不限制选择时间
    private        String          mBirthday;         //择日页面不限制选择时间
    private static TimePickListener mListener;

    public interface TimePickListener {
        void setPickDateTime(DateTime dateTime, int mode);
    }


    public static TimePickDialogFragment newInstance(DateTime dateTime) {
        mListener = null;
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        rtn.setArguments(bundle);

        return rtn;
    }

    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode) {
        mListener = null;
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        rtn.setArguments(bundle);
        return rtn;
    }

    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode ,String s) {
        mListener = null;
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, s);
        rtn.setArguments(bundle);
        return rtn;
    }

    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode, boolean show,String s) {
        mListener = null;
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        bundle.putBoolean(IntentExtraConfig.EXTRA_JSON, show);
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, s);
        rtn.setArguments(bundle);
        return rtn;
    }

    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode, int solarMode,String s) {
        mListener = null;
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, solarMode);
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, s);
        rtn.setArguments(bundle);
        LogUtils.d("-------newInstance:"+dateTime.toString() + "--"+solarMode);
        return rtn;
    }



    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode, int solarMode, boolean show ,String s) {
        mListener = null;
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        bundle.putBoolean(IntentExtraConfig.EXTRA_JSON, show);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, solarMode);
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, s);
        rtn.setArguments(bundle);
        return rtn;
    }

    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode, int solarMode, boolean show, TimePickListener listener) {
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        bundle.putBoolean(IntentExtraConfig.EXTRA_JSON, show);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, solarMode);
        rtn.setArguments(bundle);
        mListener = listener;
        return rtn;
    }

    public static TimePickDialogFragment newInstance(DateTime dateTime, int mode, int solarMode, boolean show,String s, TimePickListener listener) {
        TimePickDialogFragment rtn = new TimePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, mode);
        bundle.putBoolean(IntentExtraConfig.EXTRA_JSON, show);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, solarMode);
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, s);
        rtn.setArguments(bundle);
        mListener = listener;
        return rtn;
    }


    public  TimePickDialogFragment(){

    }
//    public  TimePickDialogFragment (DateTime dateTime, int mode, int solarMode, boolean show, TimePickListener listener) {
//        this.mDateTime = dateTime;
//        this.mMode = mode;
//
//        mListener = listener;
//    }



    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.time_select_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(mDateTime == null){
            if (bundle != null) {
                mDateTime = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
                mMode = bundle.getInt(IntentExtraConfig.EXTRA_ID, PICK_ALL);
                mSolarMode = bundle.getInt(IntentExtraConfig.EXTRA_MODE, 0);
                mShowSolar = bundle.getBoolean(IntentExtraConfig.EXTRA_JSON, false);
                mCalendarGood = bundle.getString(IntentExtraConfig.EXTRA_POST_ID);
                mBirthday = bundle.getString(IntentExtraConfig.EXTRA_GROUP_ID);

            }
        }


        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //将对话框置于底部,并设置满屏宽
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = AppContext.getScreenWidth();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    public void initView() {
        LogUtils.d("-------initView:"+mDateTime.toString() + "--"+mSolarMode);
        mWheelMain = new WheelMain(mMainLay.findViewById(R.id.select_lay));
        mSolar = (ImageView) mMainLay.findViewById(R.id.solar);
        mLunar = (ImageView) mMainLay.findViewById(R.id.lunar);

        switch (mMode) {
            case PICK_DAY:
                mMainLay.findViewById(R.id.hours).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.minute).setVisibility(View.GONE);
                if (mShowSolar) {
                    mMainLay.findViewById(R.id.solar_lunar_text).setVisibility(View.GONE);
                } else {
                    mMainLay.findViewById(R.id.solar_lunar_lay).setVisibility(View.GONE);
                }

                break;
            case PICK_MIN:
                mMainLay.findViewById(R.id.year).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.month).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.day).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.solar_lunar_lay).setVisibility(View.GONE);
                break;
            case PICK_MONTH:
                mMainLay.findViewById(R.id.day).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.hours).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.minute).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.solar_lunar_lay).setVisibility(View.GONE);
                break;
            case PICK_HOUR:
                mMainLay.findViewById(R.id.minute).setVisibility(View.GONE);
                if (mShowSolar) {
                    mMainLay.findViewById(R.id.solar_lunar_text).setVisibility(View.GONE);
                } else {
                    mMainLay.findViewById(R.id.solar_lunar_lay).setVisibility(View.GONE);
                }
                break;
        }

        mWheelMain.initDateTimePicker(mDateTime, mSolarMode);
        if (0 == mSolarMode) {
            mSolar.setImageResource(R.drawable.solar_selected);
            mLunar.setImageResource(R.drawable.lunar_unselected);
        } else {
            mSolar.setImageResource(R.drawable.solar_unselected);
            mLunar.setImageResource(R.drawable.lunar_selected);
        }

        //转换阳历
        mSolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSolarMode = 0;
                mWheelMain.setDateMode(0);
                mSolar.setImageResource(R.drawable.solar_selected);
                mLunar.setImageResource(R.drawable.lunar_unselected);
                LogUtils.d("-------initView setOnClickListener:"+mDateTime.toString() + "--"+mSolarMode);
            }
        });

        //转换阴历
        mLunar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSolarMode = 1;
                mWheelMain.setDateMode(1);
                mSolar.setImageResource(R.drawable.solar_unselected);
                mLunar.setImageResource(R.drawable.lunar_selected);
                LogUtils.d("-------initView setOnClickListener:"+mDateTime.toString() + "--"+mSolarMode);

            }
        });

        LogUtils.d("-------initView setOnClickListener:"+mDateTime.toString() + "--"+mSolarMode);

        mMainLay.findViewById(R.id.confirm).setVisibility(View.VISIBLE);
        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //确定之后将时间返回给activity
                if(mWheelMain.getDateTime().getDate().getTime() > new Date().getTime()){

                    if( "my_birthday".equals(mBirthday)){
                        UIUtils.showToast(mActivity,"不能大于当前时间", Toast.LENGTH_LONG);
                        return;
                    }
                }

                if (mListener != null) {
                    mListener.setPickDateTime(mWheelMain.getDateTime(), mWheelMain.getMode());
                }else {
                    if (mActivity != null) {

                        mActivity.setPickDateTime(mWheelMain.getDateTime(), mWheelMain.getMode());
                    }
                }
                mDateTime = mWheelMain.getDateTime();
                dismiss();
            }
        });
        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
    }
}
