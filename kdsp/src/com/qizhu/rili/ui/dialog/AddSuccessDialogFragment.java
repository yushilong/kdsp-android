package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 30/03/2017.
 * 添加成功对话框
 */

public class AddSuccessDialogFragment extends BaseDialogFragment {
    private String mContent;        //内容

    /**
     * 构造函数
     *
     * @param content 对话框内容
     */
    public static AddSuccessDialogFragment newInstance(String content) {
        AddSuccessDialogFragment rtn = new AddSuccessDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        rtn.setArguments(bundle);
        return rtn;
    }


    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.add_success_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mContent = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, "添加成功!");
        }

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //将对话框置于底部,设置背景为透明
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        //设置外部背景透明
        layoutParams.dimAmount = 0.0f;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    public void initView() {
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.content);
        if (TextUtils.isEmpty(mContent)) {
            mContentView.setVisibility(View.GONE);
        } else {
            mContentView.setText(mContent);
        }

        mMainLay.findViewById(R.id.dialog_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
    }
}
