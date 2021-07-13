package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;

/**
 * Created by lindow on 15/9/6.
 * 主页的轮滑
 */
public class HomeViewPagerFragment extends BaseViewPagerFragment {
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;

    private int currentTab = 1;

    public static HomeViewPagerFragment newInstance(int position) {
        HomeViewPagerFragment fragment = new HomeViewPagerFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(IntentExtraConfig.EXTRA_POSITION, position);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //如果有保存的现场，则从现场恢复，否则从传入的参数中拿
        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(IntentExtraConfig.EXTRA_POSITION, 1);
        } else if (getArguments() != null) {
            currentTab = getArguments().getInt(IntentExtraConfig.EXTRA_POSITION, 1);
        }
    }

    @Override
    protected int onViewPagerGetCount() {
        return 4;
    }

    @Override
    protected BaseFragment onViewPagerGetItem(int position) {
        BaseFragment rtn;
        BaseFragment frag = getSelectedFragmentFromMap(position);
        if (frag == null) {
            switch (position) {
                case 0:
                    rtn = HomeGoodDayFragment.newInstance(position);
                    break;
                case 1:
                    rtn = HomeGoodDayFragment.newInstance(position);
                    break;
                case 2:
                    rtn = WeekFortuneFragment.newInstance(position);
                    break;
                case 3:
                    rtn = MonthFortuneFragment.newInstance(position);
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
    protected void afterInitView() {
        if (currentTab != -1) {
            mViewPager.setCurrentItem(currentTab);
        }
    }

    @Override
    protected void onViewPagerPageSelected(int position) {
        changeIndicator(position);
    }

    @Override
    protected View createBottomView() {
        View view = mInflater.inflate(R.layout.home_indicator_lay, null);
        if (view != null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView1 = (ImageView) view.findViewById(R.id.image1);
            imageView2 = (ImageView) view.findViewById(R.id.image2);
            imageView3 = (ImageView) view.findViewById(R.id.image3);
            imageView4 = (ImageView) view.findViewById(R.id.image4);
        }
        return view;
    }

    private void changeIndicator(int position) {
        switch (position) {
            case 0:
                imageView1.setImageResource(R.drawable.circle_purple);
                imageView2.setImageResource(R.drawable.circle_gray30);
                imageView3.setImageResource(R.drawable.circle_gray30);
                imageView4.setImageResource(R.drawable.circle_gray30);
                break;
            case 1:
                imageView1.setImageResource(R.drawable.circle_gray30);
                imageView2.setImageResource(R.drawable.circle_purple);
                imageView3.setImageResource(R.drawable.circle_gray30);
                imageView4.setImageResource(R.drawable.circle_gray30);
                break;
            case 2:
                imageView1.setImageResource(R.drawable.circle_gray30);
                imageView2.setImageResource(R.drawable.circle_gray30);
                imageView3.setImageResource(R.drawable.circle_purple);
                imageView4.setImageResource(R.drawable.circle_gray30);
                break;
            case 3:
                imageView1.setImageResource(R.drawable.circle_gray30);
                imageView2.setImageResource(R.drawable.circle_gray30);
                imageView3.setImageResource(R.drawable.circle_gray30);
                imageView4.setImageResource(R.drawable.circle_purple);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(IntentExtraConfig.EXTRA_POSITION, mViewPager.getCurrentItem());
        currentTab = 1;
    }
}
