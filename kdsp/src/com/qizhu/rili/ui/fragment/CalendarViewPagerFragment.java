package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.CalendarData;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.core.CalendarCore;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 15/6/15.
 * 日历部分的viewpagerFragment
 */
public class CalendarViewPagerFragment extends BaseViewPagerFragment {
    private int currentTab = -1;
    private int mAction;
    private String mTitle;                  //日子的标题

    public static CalendarViewPagerFragment newInstance(int tab) {
        CalendarViewPagerFragment fragment = new CalendarViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, tab);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CalendarViewPagerFragment newInstance(int tab, int action, String title) {
        CalendarViewPagerFragment fragment = new CalendarViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, tab);
        bundle.putInt(IntentExtraConfig.EXTRA_ID, action);
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
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
            mAction = bundle.getInt(IntentExtraConfig.EXTRA_ID, -1);
            mTitle = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_PAGE_TITLE, "");
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
        return CalendarCore.mMonths1901To2100;
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            if (mAction != -1) {
                rtn = CalendarFragment.newInstance(getDateTime(position), mAction, mTitle);
            } else {
                rtn = CalendarFragment.newInstance(getDateTime(position));
            }
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        //获取本月的日期，默认返回1号
        DateTime mMonthDate = CalendarCore.getDateTimeByMonthsFrom1901(position);
        //如果当前fragment的月份和选中的月份不一样，那么切换选中的月份
        if (mMonthDate.month != CalendarData.getInstance().getSelectedDateItem().month) {
            LogUtils.d("---> onViewPagerPageSelected month = " + mMonthDate.month);
            CalendarData.getInstance().setSelectedDateItem(mMonthDate);
        }
        CalendarFragment frag = (CalendarFragment) getCurrentFragment();
        //如果当前action与选中的并不一样，那么更新之
        if (frag != null && mAction != frag.getSelectedAction()) {
            frag.setSelectedAction(mAction);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }

    private DateTime getDateTime(int position) {
        int year = position / 12 + 1901;
        int month = position % 12;

        int day = 1;
        //注意此时的月为0-11，不需要减1操作
        return new DateTime(year, month, day);
    }

    public void refreshUI() {
        BaseFragment frag = getCurrentFragment();
        if (frag != null) {
            frag.refreshState(0);
        }
    }

    /**
     * 选中某个index
     */
    public void setSelectedAction(int action) {
        mAction = action;
        //先更新当前的fragment，剩下的2个滑动的时候更新
        CalendarFragment frag = (CalendarFragment) getCurrentFragment();
        if (frag != null) {
            frag.setSelectedAction(action);
        }
    }
}
