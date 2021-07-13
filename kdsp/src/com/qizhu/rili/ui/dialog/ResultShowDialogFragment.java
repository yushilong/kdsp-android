package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 4/27/16.
 * 结果提示对话框
 */
public class ResultShowDialogFragment extends BaseDialogFragment {
    private String          mContent;        //内容


    /**
     * 构造函数
     *
     * @param content 对话框内容
     */
    public static ResultShowDialogFragment newInstance(String content) {
        ResultShowDialogFragment rtn = new ResultShowDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        rtn.setArguments(bundle);
        return rtn;
    }


    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.result_dialog_show_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mContent = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, "请检查您的网络重新刷新界面");
        }

        initView();
    }

    public void initView() {
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.content);
         mMainLay.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dismiss();
             }
         });

        mContentView.setText(mContent);

    }
}
