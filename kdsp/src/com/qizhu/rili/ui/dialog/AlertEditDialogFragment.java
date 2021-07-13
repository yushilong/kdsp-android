package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 11/2/15.
 * 用户的输入输出编辑框
 */
public class AlertEditDialogFragment extends BaseDialogFragment {
    public static final int NO_TITLE_MODE = 1;            //无标题的对话框
    public static final int TITLE_MODE = 2;               //有标题的对话框
    public static final int EDIT_MODE = 3;                //编辑对话框

    private String mTitle;          //标题
    private String mContent;        //内容
    private String mLeftBtnTxt;     //左边按钮
    private String mRightBtnTxt;    //右边按钮
    private int mMode;              //对话框样式

    /**
     * 构造函数
     *
     * @param title   对话框标题
     * @param content 对话框内容
     * @param mMode   对话框样式
     */
    public static AlertEditDialogFragment newInstance(String title, String content, String leftBtnTxt, String rightBtnTxt, int mMode) {
        AlertEditDialogFragment rtn = new AlertEditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_IMAGE, leftBtnTxt);      //复用原来的字段，不再增加新的
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_URL, rightBtnTxt);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, mMode);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.alert_edit_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_PAGE_TITLE, "神婆提示");
            mContent = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, "神婆提示");
            mLeftBtnTxt = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_IMAGE, "取消");
            mRightBtnTxt = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_URL, "确定");
            mMode = bundle.getInt(IntentExtraConfig.EXTRA_MODE, TITLE_MODE);
        }

        initView();
    }

    public void initView() {
        TextView mTitleView = (TextView) mMainLay.findViewById(R.id.title);
        mTitleView.setText(mTitle);
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.content);
        mContentView.setText(mContent);
        final EditText mEditView = (EditText) mMainLay.findViewById(R.id.edit_text);
        TextView mCancelView = (TextView) mMainLay.findViewById(R.id.cancel);
        TextView mConfirmView = (TextView) mMainLay.findViewById(R.id.ok);

        switch (mMode) {
            case NO_TITLE_MODE:
                mTitleView.setVisibility(View.GONE);
                mMainLay.findViewById(R.id.divider_line).setVisibility(View.GONE);
                break;
            case TITLE_MODE:
                mContentView.setVisibility(View.VISIBLE);
                mEditView.setVisibility(View.GONE);
                break;
            case EDIT_MODE:
                mContentView.setVisibility(View.GONE);
                mEditView.setVisibility(View.VISIBLE);
                break;
            default:
                mContentView.setVisibility(View.VISIBLE);
                mEditView.setVisibility(View.GONE);
                break;
        }

        mCancelView.setText(mLeftBtnTxt);
        mConfirmView.setText(mRightBtnTxt);

        mCancelView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
        mConfirmView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (EDIT_MODE == mMode) {
                    String mContext = mEditView.getText().toString();
                    if (TextUtils.isEmpty(mContext)) {
                        UIUtils.toastMsgByStringResource(R.string.input_not_null);
                    } else {
                        //将此值以string的泛型返回
                        mActivity.setExtraData(mContext);
                        dismiss();
                    }
                } else {
                    mActivity.setExtraData("ok");
                    dismiss();
                }
            }
        });
    }
}
