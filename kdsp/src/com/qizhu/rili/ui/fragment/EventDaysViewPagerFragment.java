package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.ui.activity.CalendarGoodActivity;

/**
 * Created by lindow on 12/15/15.
 * 天时地利的吉日轮滑viewpager
 */
public class EventDaysViewPagerFragment extends BaseViewPagerFragment {
    private DateTime mDateTime;         //日期
    private int mActionIndex;           //活动id
    private int currentTab = -1;        //当前tab

    public static EventDaysViewPagerFragment newInstance(DateTime dateTime, int action) {
        EventDaysViewPagerFragment fragment = new EventDaysViewPagerFragment();
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
        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(IntentExtraConfig.EXTRA_POSITION, -1);
        } else if (bundle != null) {
            currentTab = bundle.getInt(IntentExtraConfig.EXTRA_POSITION, -1);
        }

        if (bundle != null) {
            mDateTime = bundle.getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mActionIndex = bundle.getInt(IntentExtraConfig.EXTRA_ID, -1);
        }
    }

    @Override
    protected void afterInitView() {
        if (currentTab != -1) {
            mViewPager.setCurrentItem(currentTab);
        }
    }

    @Override
    protected int onViewPagerGetCount() {
        return CalendarCore.mMonths1901To2100 - CalendarCore.monthsFrom1901(mDateTime);
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            rtn = EventDaysFragment.newInstance(CalendarCore.shiftMonth(mDateTime, position), mActionIndex);
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        //获取当天position月之后的日期
        DateTime mMonthDate = CalendarCore.shiftMonth(mDateTime, position);
        try {
            ((CalendarGoodActivity) mActivity).setPosition(position, mMonthDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventDaysFragment frag = (EventDaysFragment) getCurrentFragment();
        //如果当前action与选中的并不一样，那么更新之
        if (frag != null && mActionIndex != frag.getSelectedAction()) {
            frag.setSelectedAction(mActionIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }

    public void setActionIndex(int action) {
        if (mActionIndex != action) {
            mActionIndex = action;
            //先更新当前的fragment，剩下的2个滑动的时候更新
            EventDaysFragment frag = (EventDaysFragment) getCurrentFragment();
            if (frag != null) {
                frag.setSelectedAction(action);
            }
        }
    }
}
