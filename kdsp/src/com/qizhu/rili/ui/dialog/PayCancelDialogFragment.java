package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;

/**
 * Created by lindow on 9/28/16.
 * 取消支付的对话框
 */
public class PayCancelDialogFragment extends BaseDialogFragment {

    public static PayCancelDialogFragment newInstance() {
        PayCancelDialogFragment rtn = new PayCancelDialogFragment();
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pay_cancel_dialog_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景为透明
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    public void initView() {
        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.setExtraData("cancel");
                dismiss();
            }
        });
        mMainLay.findViewById(R.id.ok).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.setExtraData("ok");
                dismiss();
            }
        });
    }
}
