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
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.LogUtils;
import com.weigan.loopview.LoopView;

import java.util.Arrays;

/**
 * Created by lindow on 4/26/16.
 * 血型选择框
 */
public class BloodPickDialogFragment extends BaseDialogFragment {
    private LoopView mWheelView;                   //血型选择滚轮

    private String[] mBloods = {"A型", "B型", "AB型", "O型"};
    private int mPosition = 0;
    private static  String mType;

    public static BloodPickDialogFragment newInstance(String type) {
        mType = type ;
        BloodPickDialogFragment rtn = new BloodPickDialogFragment();
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.blood_select_lay, container, false);
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
        LogUtils.d("-----"+mType);
        for(int i = 0 ,len= mBloods.length;i<len;i++){
            if(mType.equals(mBloods[i])){
                mPosition = i;
                break;
            }

        }
        LogUtils.d("-----"+mPosition);
        mWheelView = (LoopView) mMainLay.findViewById(R.id.blood);
        mWheelView.setItems(Arrays.asList(mBloods));
        mWheelView.setItemsVisibleCount(5);
        mWheelView.setInitPosition(mPosition);

        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //确定之后将血型返回给activity
                if (mActivity != null) {
                    mActivity.setExtraData(mBloods[mWheelView.getSelectedItem()]);
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
