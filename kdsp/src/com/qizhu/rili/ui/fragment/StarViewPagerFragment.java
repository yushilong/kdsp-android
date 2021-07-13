package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.ui.activity.StarActivity;

/**
 * Created by lindow on 15/9/15.
 * 星宿的viewpager
 */
public class StarViewPagerFragment extends BaseViewPagerFragment {
    private DateTime mDateTime;
    private int currentTab = -1;

    public static StarViewPagerFragment newInstance(DateTime dateTime, int tab) {
        StarViewPagerFragment fragment = new StarViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, dateTime);
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, tab);
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
        return 5;
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            rtn = StarFragment.newInstance(mDateTime, position);
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        if (mActivity instanceof StarActivity) {
            ((StarActivity) mActivity).changeIndicator(position);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }
}
