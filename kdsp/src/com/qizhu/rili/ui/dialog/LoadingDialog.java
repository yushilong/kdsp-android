package com.qizhu.rili.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import com.qizhu.rili.R;

/**
 * Created by lindow on 15/7/12.
 * 进度对话框
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.MyDialogStyleNoDimBG);
        setContentView(R.layout.loading_dialog);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
