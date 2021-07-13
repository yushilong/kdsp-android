package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.AliPayUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.WeixinUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 16/03/2017.
 * sku的支付对话框
 */

public class SkuPayDialogFragment extends BaseDialogFragment {
    private ImageView mWeixinSelect;        //微信
    private ImageView mAliSelect;           //支付宝

    private String mOrderId;
    private int mPrice;                     //金额
    private String mPayMode;                //支付方式

    public static SkuPayDialogFragment newInstance(String orderId, int price) {
        SkuPayDialogFragment rtn = new SkuPayDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_ID, orderId);
        bundle.putInt(IntentExtraConfig.EXTRA_PARCEL, price);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sku_pay_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrderId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_ID, "");
            mPrice = MethodCompat.getIntFromBundle(bundle, IntentExtraConfig.EXTRA_PARCEL, 0);
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

        //点击返回不消失
//        getDialog().setCancelable(false);
    }

    private void initView() {
        mWeixinSelect = (ImageView) mMainLay.findViewById(R.id.weixin_selected);
        mAliSelect = (ImageView) mMainLay.findViewById(R.id.ali_selected);
        TextView mPriceText = (TextView) mMainLay.findViewById(R.id.pay_price);

        mPriceText.setText(StringUtils.roundingDoubleStr((double) mPrice / 100, 2) + "元");

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消支付
                mActivity.setExtraData(YSRLConstants.CANCEL_PAY);
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayMode = YSRLConstants.ALIPAY;
                mWeixinSelect.setImageResource(R.drawable.pay_unselected);
                mAliSelect.setImageResource(R.drawable.pay_selected);
            }
        });

        mMainLay.findViewById(R.id.weixin_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayMode = YSRLConstants.WEIXIN_PAY;
                mWeixinSelect.setImageResource(R.drawable.pay_selected);
                mAliSelect.setImageResource(R.drawable.pay_unselected);
            }
        });

        mMainLay.findViewById(R.id.pay_confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (YSRLConstants.WEIXIN_PAY.equals(mPayMode)) {
                    mActivity.showLoadingDialog();
                    KDSPApiController.getInstance().toPay(mOrderId, 1, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(final JSONObject response) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.dismissLoadingDialog();
                                    dismiss();
                                    WeixinUtils.getInstance().startPayByMM(mActivity, response);
                                }
                            });
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            mActivity.dismissLoadingDialog();
                            showFailureMessage(error);
                        }
                    });
                } else if (YSRLConstants.ALIPAY.equals(mPayMode)) {
                    mActivity.showLoadingDialog();
                    KDSPApiController.getInstance().toPay(mOrderId, 2, new KDSPHttpCallBack() {
                        @Override
                        public void handleAPISuccessMessage(final JSONObject response) {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivity.dismissLoadingDialog();
                                    dismiss();
                                    AliPayUtils.getInstance().startPay(mActivity, response);
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
