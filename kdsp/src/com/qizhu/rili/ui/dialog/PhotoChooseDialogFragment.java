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
import com.qizhu.rili.utils.PhotoUtils;

/**
 * Created by lindow on 15/8/31.
 * 选择照片对话框，直接使用dialog fragment实现，防止跳转2次
 */
public class PhotoChooseDialogFragment extends BaseDialogFragment {
    private String cameraFilePath = "";         //相机拍摄时存储的路径

    public static PhotoChooseDialogFragment newInstance() {
        PhotoChooseDialogFragment rtn = new PhotoChooseDialogFragment();
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.photo_choose_lay, container, false);
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
        mMainLay.findViewById(R.id.camera_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                cameraFilePath = PhotoUtils.chooseImageFromCamera(mActivity);
                //将拍照的路径返回给activity
                mActivity.setImagePath(cameraFilePath);
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.photo_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                PhotoUtils.chooseImageFromPhoto(mActivity);
                dismiss();
            }
        });
    }
}
