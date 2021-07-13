package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.CalendarData;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.utils.LogUtils;

/**
 * Created by lindow on 15/9/8.
 * 幸运日的滑动viewPager
 */
public class GoodDayViewPagerFragment extends BaseViewPagerFragment {
    int currentTab = -1;

    public static GoodDayViewPagerFragment newInstance(int tab) {
        GoodDayViewPagerFragment fragment = new GoodDayViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(IntentExtraConfig.EXTRA_POSITION, -1);
        } else if (getArguments() != null) {
            currentTab = getArguments().getInt(IntentExtraConfig.EXTRA_POSITION, -1);
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
        return CalendarCore.mDays1901To2100;
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            rtn = GoodDayFragment.newInstance(CalendarCore.getDateTimeByDaysFrom1901(position));
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        //获取今天的日期
        DateTime mDayDate = CalendarCore.getDateTimeByDaysFrom1901(position);
        LogUtils.d("---> onViewPagerPageSelected mDayDate = " + mDayDate);
        //设置选取为今天
        CalendarData.getInstance().setSelectedDateItem(mDayDate);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }
}
