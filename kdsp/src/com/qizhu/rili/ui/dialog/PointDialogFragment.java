package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.utils.MethodCompat;

/**
 * Created by lindow on 09/12/2016.
 * 福豆的dialog
 */
public class PointDialogFragment extends BaseDialogFragment {
    private int mPoint;             //领取的福豆数

    public static PointDialogFragment newInstance(int point) {
        PointDialogFragment rtn = new PointDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_SHARE_CONTENT, point);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.add_point_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPoint = MethodCompat.getIntFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, 2);
        }
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景为透明
        try {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawableResource(R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.add_point);
        mContentView.setText(mResources.getString(R.string.add_point, mPoint));

        mMainLay.findViewById(R.id.dialog_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        mContentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dismiss();
//            }
//        }, 5000);
    }
}
