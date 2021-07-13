package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.WeixinUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 22/12/2016.
 * 支付对话框
 */

public class PayDialogFragment extends BaseDialogFragment {
    private ImageView mWeixinSelect;        //微信
    private ImageView mAliSelect;           //支付宝
    private EditText  mEnterPrice;           //输入金额
    private TextView  mPriceText;            //金额
    private TextView  mConfirm;              //确认

    private String mMsgId;                  //会员id
    private int    mPrice;                     //金额
    private int    mCurrentPrice;              //当前金额
    private String mPayMode;                //支付方式
    private boolean mIsEnable = true;       //是否可支付

    public static PayDialogFragment newInstance(String msgId, int price) {
        PayDialogFragment rtn = new PayDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_ID, msgId);
        bundle.putInt(IntentExtraConfig.EXTRA_PARCEL, price);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pay_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMsgId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_ID, "");
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
    }

    private void initView() {
        mWeixinSelect = (ImageView) mMainLay.findViewById(R.id.weixin_selected);
        mAliSelect = (ImageView) mMainLay.findViewById(R.id.ali_selected);
        mEnterPrice = (EditText) mMainLay.findViewById(R.id.enter_price);
        mPriceText = (TextView) mMainLay.findViewById(R.id.pay_price);
        mConfirm = (TextView) mMainLay.findViewById(R.id.pay_confirm);

        if (0 == mPrice) {
            mEnterPrice.setVisibility(View.VISIBLE);
            mPriceText.setText("元");
        } else {
            mEnterPrice.setVisibility(View.INVISIBLE);
            mPriceText.setText(StringUtils.roundingDoubleStr((double) mPrice / 100, 2) + "元");
        }

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        mEnterPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LogUtils.i("----->" + "beforeTextChanged");

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                LogUtils.i("----->" + "onTextChanged");
                try {
                    String amount = charSequence.toString();
                    double money = Double.valueOf(amount);
                    if (TextUtils.isEmpty(amount)) {
                        setConfirmUnable();
                    } else if (amount.contains(".") && (amount.length() - amount.indexOf(".") > 3)) {
                        setConfirmUnable();
                    } else if (money < 0.01) {
                        setConfirmUnable();
                    } else {
                        setConfirmEnable();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                if (mIsEnable) {
                    if (0 == mPrice) {
                        String amount = mEnterPrice.getText().toString();
                        if (TextUtils.isEmpty(amount)) {
                            UIUtils.toastMsg("请输入金额~");
                            return;
                        }
                        mCurrentPrice = (int) (Double.valueOf(amount) * 100);
                    } else {
                        mCurrentPrice = mPrice;
                    }
                    if (YSRLConstants.WEIXIN_PAY.equals(mPayMode)) {
                        mActivity.showLoadingDialog();
                        KDSPApiController.getInstance().wxPayMember(mMsgId, mCurrentPrice, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        WeixinUtils.getInstance().startPayByMM(mActivity, response);
                                        dismiss();
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
                        KDSPApiController.getInstance().aliPayMember(mMsgId, mCurrentPrice, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        AliPayUtils.getInstance().startPay(mActivity, response);
                                        dismiss();
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });
                    } else {
                        UIUtils.toastMsg("请选择支付方式~");
                    }
                } else {
                    UIUtils.toastMsg("输入金额有误，请重新输入~");
                }
            }
        });
    }

    private void setConfirmUnable() {
        mIsEnable = false;
    }

    private void setConfirmEnable() {
        mIsEnable = true;
    }
}
