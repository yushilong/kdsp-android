package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.MethodCompat;

import org.json.JSONObject;

/**
 * Created by lindow on 22/03/2017.
 * 订单操作对话框
 */

public class DeleteOrderDialogFragment extends BaseDialogFragment {
    public static final int CANCEL = 1;            //取消
    public static final int DEL = 2;               //删除

    private String mOrderId;                        //整个订单ID
    private int mMode;                              //对话框样式

    /**
     * 构造函数
     *
     * @param mMode 对话框样式
     */
    public static DeleteOrderDialogFragment newInstance(int mMode, String orderId) {
        DeleteOrderDialogFragment rtn = new DeleteOrderDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, mMode);
        bundle.putString(IntentExtraConfig.EXTRA_ID, orderId);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.order_oper_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMode = bundle.getInt(IntentExtraConfig.EXTRA_MODE, CANCEL);
            mOrderId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_ID, "");
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

    public void initView() {
        TextView mContentView = (TextView) mMainLay.findViewById(R.id.content);


        TextView mCancelView = (TextView) mMainLay.findViewById(R.id.cancel);
        TextView mConfirmView = (TextView) mMainLay.findViewById(R.id.ok);

        switch (mMode) {
            case CANCEL:
                mContentView.setText("你是否确定要取消订单?");
                break;
            case DEL:
                mContentView.setText("确定要删除吗?");
                break;
        }

        mCancelView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
        mConfirmView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                    mActivity.showLoadingDialog();
                KDSPApiController.getInstance().delAuguryOrder(mOrderId, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BroadcastUtils.sendRefreshBroadcast(mOrderId);
                                mActivity.dismissLoadingDialog();
                                mActivity.finish();
                            }
                        });
                    }

                    @Override
                    public void handleAPIFailureMessage(final  Throwable error, String reqCode) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mActivity.dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });


                    }
                });

                    dismiss();

            }
        });
    }
}
