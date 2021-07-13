package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 11/17/17.
 * 兑换优惠券
 */
public class CouponsDialogFragment extends BaseDialogFragment {
    private String mContent;        //内容

    public static CouponsDialogFragment newInstance(String content) {
        CouponsDialogFragment rtn = new CouponsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.coupons_dialog, container, false);
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
     final    EditText mCouponsNumberEt = (EditText) mMainLay.findViewById(R.id.coupons_number_et);

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });
        mMainLay.findViewById(R.id.ok).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

             String coupons =   mCouponsNumberEt.getText().toString();
             if(TextUtils.isEmpty(coupons)){
                 UIUtils.toastMsg(getString(R.string.please_enter_coupons));
                 return;
             }else {
                 mActivity.showLoadingDialog();
                 KDSPApiController.getInstance().claimCoupon(coupons, new KDSPHttpCallBack() {
                     @Override
                     public void handleAPISuccessMessage(JSONObject response) {
                         mActivity.runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 dismiss();
                                 mActivity.dismissLoadingDialog();
                                 mActivity.setExtraData("ok");
                             }
                         });
                     }

                     @Override
                     public void handleAPIFailureMessage(Throwable error, String reqCode) {
                         mActivity.dismissLoadingDialog();
                         showFailureMessage(error);
                     }
                 });
             }






            }
        });
    }




}
