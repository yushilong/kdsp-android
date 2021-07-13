package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 7/25/16.
 * 版本升级对话框
 */
public class UpdateVersionDialogFragment extends BaseDialogFragment {
    private String mContent;        //内容

    public static UpdateVersionDialogFragment newInstance(String content) {
        UpdateVersionDialogFragment rtn = new UpdateVersionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.update_version_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mContent = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, "重大更新");
        }

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
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.content);
        mContentView.setText(mContent);

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
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
