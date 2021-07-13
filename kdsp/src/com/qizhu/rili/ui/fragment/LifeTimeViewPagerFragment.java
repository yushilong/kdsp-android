package com.qizhu.rili.ui.fragment;

import android.os.Bundle;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.bean.User;
import com.qizhu.rili.ui.activity.LifeTimeActivity;

/**
 * Created by lindow on 2/21/16.
 * 我的一生的viewpager
 */
public class LifeTimeViewPagerFragment extends BaseViewPagerFragment {
    private String mElement;            //五行
    private int mUserSex;               //性别
    private int currentTab = -1;

    public static LifeTimeViewPagerFragment newInstance(String element, int sex, int tab) {
        LifeTimeViewPagerFragment fragment = new LifeTimeViewPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, element);
        bundle.putInt(IntentExtraConfig.EXTRA_USER_SEX, sex);
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
            mElement = bundle.getString(IntentExtraConfig.EXTRA_PARCEL);
            mUserSex = bundle.getInt(IntentExtraConfig.EXTRA_USER_SEX, User.BOY);
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
        return 3;
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            switch (position) {
                case 0:
                    rtn = LifeTimeFirstFragment.newInstance(mElement, mUserSex, position);
                    break;
                case 1:
                    rtn = LifeTimeSecondFragment.newInstance(mElement, mUserSex, position);
                    break;
                case 2:
                    rtn = LifeTimeThirdFragment.newInstance(mElement, mUserSex, position);
                    break;
                default:
                    rtn = new BaseFragment();
                    break;
            }
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        if (mActivity instanceof LifeTimeActivity) {
            ((LifeTimeActivity) mActivity).changeIndicator(position);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = -1;
    }
}
