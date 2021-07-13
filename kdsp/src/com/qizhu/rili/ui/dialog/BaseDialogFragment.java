package com.qizhu.rili.ui.dialog;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.qizhu.rili.ui.activity.BaseActivity;
import com.qizhu.rili.utils.LogUtils;

/**
 * Created by lindow on 15/8/24.
 * 基础的对话框fragment, 采用onCreateView的形式能提供更大的灵活性
 */
public class BaseDialogFragment extends DialogFragment {
    protected BaseActivity mActivity;
    protected LayoutInflater mInflater;
    protected Resources mResources;

    public View mMainLay;
    protected boolean hasMainlayInit = false; //判断主布局是否已经初始化

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        mInflater = LayoutInflater.from(mActivity);
        mResources = getResources();

        LogUtils.d("BaseDialogFragment 周期 onCreate -> this = " + this);
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
        //去掉原生对话框的标题栏，标题栏放在fragment自己的xml里去实现
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return mMainLay != null ? mMainLay : new View(mActivity);
    }

    /**
     * 初始化主布局
     */
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }
}
