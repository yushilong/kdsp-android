package com.qizhu.rili.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.utils.LogUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * fragment基类
 */
public class BaseFragment extends Fragment {
    protected BaseActivity mActivity;
    protected LayoutInflater mInflater;
    protected Resources mResources;

    public View mMainLay;
    protected boolean hasMainlayInit = false; //判断主布局是否已经初始化
    protected boolean mHasBeenDestroyed = false;
    private Unbinder unbinder;
    public BaseFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) getActivity();
        mInflater = LayoutInflater.from(mActivity);
        mResources = getResources();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d("BaseFragment 周期 onCreate -> this = " + this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mMainLay == null) {
            hasMainlayInit = false;
            mMainLay = inflateMainView(inflater, container);
        } else { //如果根布局的引用不为空，则从先前的父控件移除，重新加入到新的父控件
            hasMainlayInit = true;
            if ((mMainLay.getParent()) != null) {
                ((ViewGroup) mMainLay.getParent()).removeView(mMainLay);
            }
        }
        return  mMainLay;
    }
    public  void bind(){
        unbinder = ButterKnife.bind(this,mMainLay);
    }
    /**
     * 初始化主布局
     */
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d("BaseFragment lifeCycle onActivityCreated! this = " + this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("BaseFragment lifeCycle onResume! this = " + this);
    }

    /**
     * 销毁fragment中的数据项，主要用于在viewpager中切换时及时回收对象
     */
    protected void destroyFragment() {
        mHasBeenDestroyed = true;
        onDestroyFragment();
    }

    /**
     * 重建fragment中的数据项
     */
    protected void rebuildFragment() {
        if (mHasBeenDestroyed) {
            mHasBeenDestroyed = false;
            onRebuildFragment();
        }
    }

    protected void onDestroyFragment() {
    }

    protected void onRebuildFragment() {
    }

    /**
     * 强制刷新
     */
    public void forceRefresh() {
    }

    /**
     * 更新状态
     */
    public void refreshState(int position) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.d("BaseFragment lifeCycle onDetach! this = " + this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMainLay != null && (mMainLay.getParent()) != null) {
            ((ViewGroup) mMainLay.getParent()).removeView(mMainLay);
        }
        if(unbinder != null){
            unbinder.unbind();
        }
        mMainLay = null;
        LogUtils.d("BaseFragment lifeCycle onDestroyView! this = " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("BaseFragment lifeCycle onDestroy! this = " + this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d("BaseFragment lifeCycle onHiddenChanged! hidden = " + hidden + ", this = " + this);
    }
}
