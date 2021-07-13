package com.qizhu.rili.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.EventDaysGridAdapter;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.utils.MethodCompat;

import java.util.ArrayList;

/**
 * Created by lindow on 12/15/15.
 * 天时地利的吉日fragment
 */
public class EventDaysFragment extends BaseFragment {
    private GridView mDaysGrid;             //天的grid
    private ImageView mUpAdjust;            //向上调整
    private ImageView mDownAdjust;          //向下调整
    private View mEmptyLay;                 //空布局

    private DateTime mDateTime;             //传入的日期
    private DateTime mBirthDateTime;        //生日
    private int mSex = 2;                   //性别
    private int mAction;                    //吉日的index
    private int days;                       //这个月有多少天
    private ArrayList<DateTime> mGoodTimes = new ArrayList<DateTime>();     //吉日
    private EventDaysGridAdapter mEventDaysGridAdapter;     //adapter
    private int mHeight;                    //grid的高度

    public static EventDaysFragment newInstance(DateTime dateTime, int action) {
        EventDaysFragment fragment = new EventDaysFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, action);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mDateTime = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mAction = bundle.getInt(IntentExtraConfig.EXTRA_ID, -1);
        }

        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.event_days_lay, container, false);
    }

    protected void initView() {
        mDaysGrid = (GridView) mMainLay.findViewById(R.id.days_grid);
        mUpAdjust = (ImageView) mMainLay.findViewById(R.id.up_adjust);
        mDownAdjust = (ImageView) mMainLay.findViewById(R.id.down_adjust);
        mEmptyLay = mMainLay.findViewById(R.id.empty_lay);
        days = CalendarCore.daysInMonth(mDateTime.year, mDateTime.month);
        if (AppContext.mUser != null) {
            mBirthDateTime = new DateTime(AppContext.mUser.birthTime);
            mSex = AppContext.mUser.userSex;
        } else {
            mBirthDateTime = new DateTime();
        }
        ViewTreeObserver viewTreeObserver = mDaysGrid.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (MethodCompat.isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
                    mDaysGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mDaysGrid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                mHeight = mDaysGrid.getHeight();
                mEventDaysGridAdapter = new EventDaysGridAdapter(mActivity, mGoodTimes);
                mEventDaysGridAdapter.setHeight(mHeight);
                mDaysGrid.setAdapter(mEventDaysGridAdapter);
                setContent();
            }
        });
        mUpAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDaysGrid.smoothScrollByOffset(-mHeight);
            }
        });
        mDownAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDaysGrid.smoothScrollByOffset(mHeight);
            }
        });
    }

    private void setContent() {
        mGoodTimes.clear();
        for (int i = 1; i <= days; i++) {
            DateTime dateTime = new DateTime(mDateTime.year, mDateTime.month, i);
            if (CalendarCore.isGoodDay4Act(dateTime, mBirthDateTime, mAction, mSex)) {
                mGoodTimes.add(dateTime);
            }
        }
        if (mGoodTimes.isEmpty()) {
            mDaysGrid.setVisibility(View.GONE);
            mEmptyLay.setVisibility(View.VISIBLE);
            mUpAdjust.setVisibility(View.INVISIBLE);
            mDownAdjust.setVisibility(View.INVISIBLE);
        } else {
            mDaysGrid.setVisibility(View.VISIBLE);
            mEmptyLay.setVisibility(View.GONE);
            if (mGoodTimes.size() > 10) {
                mUpAdjust.setVisibility(View.VISIBLE);
                mDownAdjust.setVisibility(View.VISIBLE);
            } else {
                mUpAdjust.setVisibility(View.INVISIBLE);
                mDownAdjust.setVisibility(View.INVISIBLE);
            }
        }
        if (mEventDaysGridAdapter != null) {
            mEventDaysGridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选中某个index
     */
    public void setSelectedAction(int action) {
        if (mEventDaysGridAdapter != null) {
            mAction = action;
            setContent();
        }
    }

    /**
     * 获取选中的index
     */
    public int getSelectedAction() {
        return mAction;
    }

    /**
     * 获取适宜日期列表
     */
    public ArrayList<DateTime> getDays() {
        return mGoodTimes;
    }
}
