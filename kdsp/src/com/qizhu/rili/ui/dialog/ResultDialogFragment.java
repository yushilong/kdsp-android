package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.listener.RefreshListener;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 4/27/16.
 * 结果提示对话框
 */
public class ResultDialogFragment extends BaseDialogFragment {
    private String mTitle;          //标题
    private String mContent;        //内容
    private String mConfirm;        //确认
    private RefreshListener mRefreshListener;

    /**
     * 构造函数
     *
     * @param title   对话框标题
     * @param content 对话框内容
     * @param confirm 确定键
     */
    public static ResultDialogFragment newInstance(String title, String content, String confirm, RefreshListener refreshListener) {
        ResultDialogFragment rtn = new ResultDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, confirm);
        rtn.mRefreshListener = refreshListener;
        rtn.setArguments(bundle);
        return rtn;
    }


    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.result_dialog_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_PAGE_TITLE, "网络出错啦");
            mContent = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, "请检查您的网络重新刷新界面");
            mConfirm = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_PARCEL, "确定");
        }

        initView();
    }

    public void initView() {
        TextView mTitleView = (TextView) mMainLay.findViewById(R.id.title);
        mTitleView.setText(mTitle);
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.content);
        TextView mConfirmView = (TextView) mMainLay.findViewById(R.id.ok);
        if (TextUtils.isEmpty(mContent)) {
            mContentView.setVisibility(View.GONE);
        } else {
            mContentView.setText(mContent);
        }

        if (TextUtils.isEmpty(mConfirm)) {
            mConfirmView.setVisibility(View.GONE);
        } else {
            mConfirmView.setText(mConfirm);
        }

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.ok).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mRefreshListener != null) {
                    mRefreshListener.refresh(0);
                }
                dismiss();
            }
        });
    }
}
