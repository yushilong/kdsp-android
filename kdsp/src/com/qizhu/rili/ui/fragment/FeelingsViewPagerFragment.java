package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.ui.activity.FeelingsActivity;
import com.qizhu.rili.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lindow on 10/29/15.
 * 感情世界的viewpager
 */
public class FeelingsViewPagerFragment extends BaseViewPagerFragment {
    private DateTime mDateTime;
    private int currentTab = -1;
    private int mCount;                     //viewpager的个数
    private ArrayList<Character> tempList;

    public static FeelingsViewPagerFragment newInstance(DateTime dateTime, int tab) {
        FeelingsViewPagerFragment fragment = new FeelingsViewPagerFragment();
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
        tempList = new ArrayList<Character>();
        char[] charArray = mDateTime.toDayInt().toCharArray();
        Arrays.sort(charArray);
        for (char temp : charArray) {
            if (!tempList.contains(temp) && temp != '0') {
                tempList.add(temp);
            }
        }
        mCount = tempList.size();
    }

    @Override
    protected void afterInitView() {
        if (currentTab != -1) {
            mViewPager.setCurrentItem(currentTab);
        }
    }

    @Override
    protected int onViewPagerGetCount() {
        return mCount;
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            char temp = tempList.get(position);
            rtn = FeelingsFragment.newInstance(mDateTime, position, temp, StringUtils.getCountofChar(mDateTime.toDayInt(), temp));
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        if (mActivity instanceof FeelingsActivity) {
            ((FeelingsActivity) mActivity).changeIndicator(position);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }
}
