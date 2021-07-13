package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;

/**
 * Created by lindow on 8/17/16.
 * 单选对话框
 */
public class SinglePickDialogFragment extends BaseDialogFragment {
    private String mTitle;          //标题
    private String mItems;          //选项
    private static SinglePickListener mListener;
    public  interface SinglePickListener{
       void  setExtraData(String data);
    }

    public static SinglePickDialogFragment newInstance(String title, String items) {
        mListener = null;
        SinglePickDialogFragment rtn = new SinglePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, items);
        rtn.setArguments(bundle);
        return rtn;
    }
    public static SinglePickDialogFragment newInstance(String title, String items ,SinglePickListener singlePickListener) {
        SinglePickDialogFragment rtn = new SinglePickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, items);
        rtn.setArguments(bundle);
        mListener = singlePickListener;
        return rtn;
    }
    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.single_pick_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(IntentExtraConfig.EXTRA_PAGE_TITLE, "请选择");
            mItems = getArguments().getString(IntentExtraConfig.EXTRA_PARCEL, "");
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
        TextView mTitleText = (TextView) mMainLay.findViewById(R.id.title);
        mTitleText.setText(mTitle);
        LinearLayout mContainer = (LinearLayout) mMainLay.findViewById(R.id.container);

        if (!TextUtils.isEmpty(mItems)) {
            String[] items = mItems.split(",");
            for (final String item : items) {
                View itemLay = mInflater.inflate(R.layout.pick_item_lay, null);
                TextView name = (TextView) itemLay.findViewById(R.id.item_name);
                name.setText(item);
                name.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        //确定之后将结果返回给activity

                        if(mListener != null){
                            mListener.setExtraData(item);
                        }else {
                            if (mActivity != null) {
                                mActivity.setExtraData(item);
                            }
                        }
                        dismiss();
                    }
                });
                mContainer.addView(itemLay);
            }
        }
    }
}
