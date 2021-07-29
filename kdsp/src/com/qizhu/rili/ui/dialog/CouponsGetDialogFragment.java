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
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;


/**
 * Created by lindow on 11/17/17.
 * 兑换优惠券
 */
public class
CouponsGetDialogFragment extends BaseDialogFragment {
    TextView mTitleTv;
    TextView mTitleTypeTv;
    TextView mTitleTipTv;
    TextView mPriceTv;
    private String mContent;        //内容
    private String mMcid;
    private int mPrice;

    public static CouponsGetDialogFragment newInstance(String content,String mcId,int price) {
        CouponsGetDialogFragment rtn = new CouponsGetDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_SHARE_CONTENT, content);
        bundle.putString(IntentExtraConfig.EXTRA_ID, mcId);
        bundle.putInt(IntentExtraConfig.EXTRA_GROUP_ID, price);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.coupons_get_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mContent = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_SHARE_CONTENT, "重大更新");
            mMcid = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_ID);
            mPrice = MethodCompat.getIntFromBundle(bundle, IntentExtraConfig.EXTRA_GROUP_ID,0);
        }

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景为透明
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        this.setCancelable(false);
    }

    public void initView() {
        mTitleTv = mMainLay.findViewById(R.id.title_tv);
        mTitleTypeTv = mMainLay.findViewById(R.id.title_type_tv);
        mTitleTipTv = mMainLay.findViewById(R.id.title_tip_tv);
        mPriceTv = mMainLay.findViewById(R.id.price_tv);

        mTitleTv.setText(getString(R.string.coupons_title,mPrice));
        mTitleTypeTv.setText(mContent);
        mTitleTipTv.setText(getString(R.string.coupons_title_tip,mPrice));
        mPriceTv.setText(mPrice + "");


        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                KDSPApiController.getInstance().activationOrCancelCoupon(mMcid,3,new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {


                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {

                    }
                });
                dismiss();
            }
        });
        mMainLay.findViewById(R.id.ok).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                KDSPApiController.getInstance().activationOrCancelCoupon(mMcid,0,new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        UIUtils.toastMsg(getString(R.string.get_coupons_success));
                        dismiss();
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {

                    }
                });


            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
