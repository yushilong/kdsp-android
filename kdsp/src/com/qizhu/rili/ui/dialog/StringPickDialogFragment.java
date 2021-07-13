package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.weigan.loopview.LoopView;

import java.util.ArrayList;

/**
 * Created by lindow on 17/03/2017.
 * 选择字符串的对话框
 */

public class StringPickDialogFragment extends BaseDialogFragment {
    private LoopView mWheelView;                   //原因选择滚轮

    private ArrayList<String> mList = new ArrayList<>();

    public static StringPickDialogFragment newInstance(ArrayList<String> list) {
        StringPickDialogFragment rtn = new StringPickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(IntentExtraConfig.EXTRA_PARCEL, list);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.string_select_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mList = bundle.getStringArrayList(IntentExtraConfig.EXTRA_PARCEL);
        }

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //将对话框置于底部,并设置满屏宽
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = AppContext.getScreenWidth();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    private void initView() {
        mWheelView = (LoopView) mMainLay.findViewById(R.id.list);
        mWheelView.setItems(mList);
        mWheelView.setItemsVisibleCount(7);
        mWheelView.setInitPosition(1);

        if (mList.size() < 3) {
            mWheelView.setNotLoop();
        }

        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //确定之后将选择项返回给activity
                if (mActivity != null) {
                    mActivity.setExtraData(mList.get(mWheelView.getSelectedItem()));
                }
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
