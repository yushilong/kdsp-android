package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qizhu.rili.R;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.widget.LoopViewPager;

/**
 * 使用viewpager的fragment
 */
public abstract class BaseViewPagerFragment extends BaseFragment {
    private static final int TO_RIGHT = 0, TO_LEFT = 1;

    protected RelativeLayout mAlignTopTitle;        //与ViewPager顶部对齐的Title
    protected RelativeLayout mAlignBottomView;      //与ViewPager底部对齐的布局
    protected RelativeLayout mTitleView;            //顶部布局
    protected RelativeLayout mBottomView;           //底部布局
    protected AppBarLayout mAppBarLayout;            //滚动控件
    protected RelativeLayout mScrollEnterView;      //滑动隐藏布局
    protected RelativeLayout mScrollFixedView;      //滑动固定布局
    public LoopViewPager mViewPager;                //当前的viewpager
    protected PagerAdapter mAdapter;                //viewpager的adapter

    protected int mViewPagerCurrentPos = -1;        //记录viewPager当前的位置
    protected int mViewPagerLastPos = -1;           //记录viewPager上次的位置

    protected SparseArray<BaseFragment> mFragMap = new SparseArray<BaseFragment>();  //记录viewPager中的Frag引用

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.viewpager_frag_base, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        beforeInitView();

        initAlignTopTitle();
        initAlignBottomView();
        initTitleView();
        initScrollEnterView();
        initScrollFixedView();
        initBottomView();
    }

    /**
     * 定位控件
     */
    private void findView() {
        mAlignTopTitle = (RelativeLayout) mMainLay.findViewById(R.id.my_alin_top_view_pager);
        mAlignBottomView = (RelativeLayout) mMainLay.findViewById(R.id.my_alin_bottom_view_pager);
        mTitleView = (RelativeLayout) mMainLay.findViewById(R.id.my_title);
        mBottomView = (RelativeLayout) mMainLay.findViewById(R.id.my_bottom);
        mAppBarLayout = (AppBarLayout) mMainLay.findViewById(R.id.appbar);
        mScrollEnterView = (RelativeLayout) mMainLay.findViewById(R.id.scroll_enter);
        mScrollFixedView = (RelativeLayout) mMainLay.findViewById(R.id.scroll_fixed);
    }

    /**
     * 获取指定位置的Fragment
     *
     * @param idx 当前索引
     */
    public BaseFragment getSelectedFragment(int idx) {
        //由于mFragMap未保存状态，所以有可能是空的，此时通过恢复的fragment对象来重新设置mFragMap
        BaseFragment fragment = mFragMap.get(idx);
        if (fragment != null) {
            return fragment;
        } else if (mViewPager != null) {
            try {
                //instantiateItem会返回已经初始化过的fragment，若存在则直接返回，否则会调用getItem()生成
                fragment = (BaseFragment) mAdapter.instantiateItem(mViewPager, idx);
                //得到当前索引的fragment后，将之放入mFragMap
                mFragMap.put(idx, fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    /**
     * 从map中获取指定位置的Fragment
     * 用在getItem中生成，其实是没必要的，getItem理论上只会被instantiateItem初始化一次
     * 生成的对象会保存在adapter的列表里并保存状态
     */
    public BaseFragment getSelectedFragmentFromMap(int idx) {
        return mFragMap.get(idx);
    }

    /**
     * 获取当前位置的fragment
     */
    public BaseFragment getCurrentFragment() {
        if (mFragMap == null || mViewPager == null) {
            return null;
        }
        return getSelectedFragment(mViewPager.getCurrentItem());
    }

    /**
     * 设置当前位置的fragment
     */
    public void setCurrentFragment(int position, boolean smoothScroll) {
        if (mViewPager != null && mAdapter != null && position < mAdapter.getCount()) {
            mViewPager.setCurrentItem(position, smoothScroll);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initViewPager();

        doOnStart();
    }

    /**
     * 初始化布局
     */
    protected final void initViewPager() {
        if (mViewPager == null) {
            mViewPager = (LoopViewPager) mMainLay.findViewById(R.id.view_pager);
            mAdapter = createViewpagerAdapter();
            mViewPager.setAdapter(mAdapter);
            mViewPager.addOnPageChangeListener(createOnPageChangeListener());
            afterInitView();
        }
    }

    /**
     * 初始化与viewPager顶部对齐的Title
     */
    protected final void initAlignTopTitle() {
        if (mAlignTopTitle.getChildCount() == 0) {
            View tmp = createAlignTopTitle();
            if (tmp != null) {
                mAlignTopTitle.addView(tmp);
            }
        }
    }

    /**
     * 初始化与viewPager底部对齐的View
     */
    protected final void initAlignBottomView() {
        if (mAlignBottomView.getChildCount() == 0) {
            View tmp = createAlignBottomView();
            if (tmp != null) {
                mAlignBottomView.addView(tmp);
            }
        }
    }

    /**
     * 初始化头部布局
     */
    protected final void initTitleView() {
        if (mTitleView.getChildCount() == 0) {
            View tmp = createTitleView();
            if (tmp != null) {
                mTitleView.addView(tmp);
            }
        }
    }

    /**
     * 初始化头部布局
     */
    protected final void initScrollEnterView() {
        if (mScrollEnterView.getChildCount() == 0) {
            View tmp = createScrollEnterView();
            if (tmp != null) {
                mScrollEnterView.addView(tmp);
            }
        }
    }

    /**
     * 初始化头部布局
     */
    protected final void initScrollFixedView() {
        if (mScrollFixedView.getChildCount() == 0) {
            View tmp = createScrollFixedView();
            if (tmp != null) {
                mScrollFixedView.addView(tmp);
            }
        }
    }

    /**
     * 初始化底部布局
     */
    protected final void initBottomView() {
        if (mBottomView.getChildCount() == 0) {
            View tmp = createBottomView();
            if (tmp != null) {
                mBottomView.addView(tmp);
            }
        }
    }

    /**
     * 创建viewpager adapter
     */
    protected PagerAdapter createViewpagerAdapter() {
        return new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                setViewPagerCurrentPos(position);
                BaseFragment fragment = onViewPagerGetItem(position);
                //若map中不包含此fragment，则将此fragment放入map
                if (fragment != null && mFragMap.indexOfValue(fragment) == -1) {
                    mFragMap.put(position, fragment);
                }
                onAfterGetItem(position);
                return fragment;
            }

            @Override
            public int getCount() {
                return onViewPagerGetCount();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                onViewPagerDestroyItem(container, position, object);
                if (needRemoveFromMap()) {
                    super.destroyItem(container, position, object);
                    mFragMap.remove(position);
                }
            }
        };
    }

    protected boolean needRemoveFromMap() {
        return true;
    }

    /**
     * 记录位置信息
     *
     * @param pos 当前位置
     */
    protected final void setViewPagerCurrentPos(int pos) {
        if (pos != mViewPagerCurrentPos) {
            mViewPagerLastPos = mViewPagerCurrentPos;
            mViewPagerCurrentPos = pos;
        }
    }

    /**
     * 创建viewpager 页面切换监听类
     */
    protected ViewPager.OnPageChangeListener createOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                onScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                onViewPagerPageSelected(position);
                setViewPagerCurrentPos(position);
                switch (getScrollDirection()) {
                    case TO_RIGHT:
                        onViewPagerScrollToRight();
                        break;
                    case TO_LEFT:
                        onViewPagerScrollToLeft();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                onScrollStateChanged(state);
            }
        };
    }

    public void onScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    public void onScrollStateChanged(int state) {

    }

    /**
     * 获取滑动的方向
     */
    protected final int getScrollDirection() {
        LogUtils.d("viewpager scroll pos last {} now {}", mViewPagerLastPos, mViewPagerCurrentPos);
        int rtn = TO_RIGHT;
        if (mViewPagerLastPos > mViewPagerCurrentPos) {
            rtn = TO_LEFT;
        } else if (mViewPagerCurrentPos == mViewPagerLastPos) {
            rtn = -1;
        }
        return rtn;
    }

    /**
     * 保存传入position的fragment，其他的都调用其destroyFragment方法
     *
     * @param positionArr 需要保存的位置数组
     */
    protected final void retainFragment(int... positionArr) {
        //先得到map的size，然后遍历一边map，注意传入的pos值为key
        int size = mFragMap.size();
        for (int i = 0; i < size; i++) {
            int key = mFragMap.keyAt(i);
            if (notInArr(positionArr, key)) {
                BaseFragment frag = mFragMap.get(key);
                if (frag != null) {
                    frag.destroyFragment();
                }
            }
        }
    }

    private boolean notInArr(int[] positionArr, int pos) {
        for (int tmp : positionArr) {
            if (pos == tmp) {
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化view之前做的事情
     */
    protected void beforeInitView() {
    }

    /**
     * 初始化view之后做的事情
     */
    protected void afterInitView() {
    }

    /**
     * 在onStart方法中做的事情
     */
    protected void doOnStart() {
    }

    /**
     * 创建底部布局
     */
    protected View createBottomView() {
        return null;
    }

    /**
     * 创建与viewPager顶部对齐的Title
     */
    protected View createAlignTopTitle() {
        return null;
    }

    /**
     * 创建与viewPager底部对齐的View
     */
    protected View createAlignBottomView() {
        return null;
    }

    /**
     * 创建头部布局
     */
    protected View createTitleView() {
        return null;
    }

    /**
     * 创建viewpager滑动隐藏布局
     */
    protected View createScrollEnterView() {
        return null;
    }

    /**
     * 创建viewpager滑动固定布局
     */
    protected View createScrollFixedView() {
        return null;
    }

    /**
     * 销毁fragmnet的回调
     */
    protected void onViewPagerDestroyItem(ViewGroup container, int position, Object object) {
    }

    /**
     * 获取viewPager的fragment的个数
     */
    protected abstract int onViewPagerGetCount();

    /**
     * 初始化指定位置的fragment
     */
    protected abstract BaseFragment onViewPagerGetItem(int position);

    /**
     * viewpager滑动停止时的回调处理
     *
     * @param position item的position
     */
    protected abstract void onViewPagerPageSelected(int position);

    /**
     * 获取Item后的处理
     *
     * @param position item的position
     */
    protected void onAfterGetItem(int position) {
    }

    /**
     * viewpager向右滑动后的处理
     */
    protected void onViewPagerScrollToRight() {
        retainFragment(mViewPagerCurrentPos, mViewPagerCurrentPos + 1);
    }

    /**
     * viewpager向左滑动后的处理
     */
    protected void onViewPagerScrollToLeft() {
        retainFragment(mViewPagerCurrentPos, mViewPagerCurrentPos - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //若销毁的话，清除viewpager
        mViewPager = null;
    }
}
