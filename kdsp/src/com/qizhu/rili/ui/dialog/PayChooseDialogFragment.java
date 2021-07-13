package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.listener.OnSingleClickListener;

/**
 * Created by lindow on 15/8/31.
 * 支付选择对话框
 */
public class PayChooseDialogFragment extends BaseDialogFragment {

    public static PayChooseDialogFragment newInstance() {
        PayChooseDialogFragment rtn = new PayChooseDialogFragment();
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pay_choose_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //将对话框置于底部,设置背景为透明
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = AppContext.getScreenWidth();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    private void initView() {
        mMainLay.findViewById(R.id.weixin_pay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.setExtraData(YSRLConstants.WEIXIN_PAY);
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.alipay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.setExtraData(YSRLConstants.ALIPAY);
                dismiss();
            }
        });
        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
    }
}
