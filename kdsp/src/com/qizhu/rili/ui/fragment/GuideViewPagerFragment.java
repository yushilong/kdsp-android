package com.qizhu.rili.ui.fragment;

import com.qizhu.rili.ui.activity.GuideActivity;

/**
 * Created by lindow on 15/8/7.
 * 引导图的viewpager
 */
public class GuideViewPagerFragment extends BaseViewPagerFragment {

    public static GuideViewPagerFragment newInstance() {
        GuideViewPagerFragment fragment = new GuideViewPagerFragment();
        return fragment;
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
            rtn = GuideFragment.newInstance(position);
        } else {
            rtn = frag;
        }

        return rtn;
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        if (mActivity instanceof GuideActivity) {
            ((GuideActivity) mActivity).changeIndicator(position);
        }
    }
}
