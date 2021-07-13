package com.qizhu.rili.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.MonthGridAdapter;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 15/9/7.
 * 月历的fragment
 */
public class CalendarFragment extends BaseFragment {
    private GridView mMonthGrid;            //月份的grid
    private MonthGridAdapter mMonthGridAdapter;     //adapter

    private DateTime mDateTime;             //传入的日期
    private int mAction;                    //吉日的index
    private String mTitle;                  //日子的标题

    public static CalendarFragment newInstance(DateTime dateTime) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CalendarFragment newInstance(DateTime dateTime, int action, String title) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, action);
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
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
            mTitle = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_PAGE_TITLE, "");
        }

        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.calendar_month_lay, container, false);
    }

    protected void initView() {
        mMonthGrid = (GridView) mMainLay.findViewById(R.id.month_grid);
        ViewTreeObserver viewTreeObserver = mMonthGrid.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (MethodCompat.isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
                    mMonthGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mMonthGrid.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                mMonthGridAdapter = new MonthGridAdapter(mActivity, mDateTime.year, mDateTime.month, CalendarCore.daysInMonth(mDateTime.year, mDateTime.month), CalendarCore.getWeekDay(mDateTime), mAction, mTitle);
                mMonthGridAdapter.setHeight(mMonthGrid.getHeight());
                mMonthGrid.setAdapter(mMonthGridAdapter);
            }
        });
    }

    @Override
    public void refreshState(int position) {
        if (mMonthGridAdapter != null) {
            mMonthGridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选中某个index
     */
    public void setSelectedAction(int action) {
        mAction = action;
        if (mMonthGridAdapter != null) {
            mMonthGridAdapter.setSelectedAction(action);
        }
    }

    public int getSelectedAction() {
        return mAction;
    }
}