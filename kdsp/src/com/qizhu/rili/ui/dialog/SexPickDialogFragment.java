package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;

/**
 * Created by lindow on 15/9/15.
 * 性别选择对话框
 */
public class SexPickDialogFragment extends BaseDialogFragment {
    private boolean mIsMine;            //是否是我的

    public static SexPickDialogFragment newInstance(boolean isMine) {
        SexPickDialogFragment rtn = new SexPickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IntentExtraConfig.EXTRA_IS_MINE, isMine);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sex_choose_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mIsMine = getArguments().getBoolean(IntentExtraConfig.EXTRA_ID, false);
        }
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
        TextView mTip = (TextView) mMainLay.findViewById(R.id.tip);
        if (mIsMine) {
            mTip.setText(R.string.please_select_sex);
        } else {
            mTip.setText(R.string.select_sex);
        }
        mMainLay.findViewById(R.id.boy).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //确定之后将性别返回给activity
                if (mActivity != null) {
                    mActivity.setExtraData(1);
                }
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.girl).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mActivity != null) {
                    mActivity.setExtraData(2);
                }
                dismiss();
            }
        });
    }
}
