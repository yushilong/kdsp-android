package com.qizhu.rili.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.OrderItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.RefundProgressActivity;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 17/03/2017.
 * 退款进度
 */

public class RefundProgressFragment extends BaseFragment {
    private View mCancelRefund;
    private View mAgree;
    private TextView mAddressText;
    private TextView mNumberText;
    private TextView mPeopleText;
    private View mInputLay;
    private EditText mInputCompany;
    private EditText mInputNumber;
    private TextView mSubmit;
    private View mSendLay;
    private TextView mCompanyText;
    private TextView mShipNumText;
    private View mRefuseLay;
    private TextView mRefuseReasonText;
    private View mSuccessLay;
    private TextView mSuccessReasonText;
    private View mFailedLay;
    private TextView mFailedReasonText;

    private String mOrderId = "";               //整个订单ID
    private String mOdId = "";                  //订单明细ID
    private String mRefundId = "";              //退款id
    private int mRefundAmount;                  //退款金额
    private String mAddress;                    //地址
    private String mNumber;                     //电话
    private String mPeople;                     //收货人
    private String mCompany;                    //快递公司
    private String mShipNum;                    //快递单号
    private String mFailedReason;               //失败理由
    private int mRefundStatus = 0;              //退款状态，0是填写表单，1是等待处理，2是卖家同意，3是卖家拒绝,4是退款成功

    public static RefundProgressFragment newInstance(String orderId, String odId, int status) {
        RefundProgressFragment fragment = new RefundProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, orderId);
        bundle.putString(IntentExtraConfig.EXTRA_ID, odId);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, status);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOrderId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_GROUP_ID, "");
            mOdId = MethodCompat.getStringFromBundle(bundle, IntentExtraConfig.EXTRA_ID, "");
            mRefundStatus = MethodCompat.getIntFromBundle(bundle, IntentExtraConfig.EXTRA_MODE, 1);
        }

        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.refund_progress_lay, container, false);
    }

    protected void initView() {
        mCancelRefund = mMainLay.findViewById(R.id.cancel_refund);
        mAgree = mMainLay.findViewById(R.id.agree_lay);
        mAddressText = (TextView) mMainLay.findViewById(R.id.address);
        mNumberText = (TextView) mMainLay.findViewById(R.id.number);
        mPeopleText = (TextView) mMainLay.findViewById(R.id.people);
        mInputLay = mMainLay.findViewById(R.id.input_lay);
        mInputCompany = (EditText) mMainLay.findViewById(R.id.input_company);
        mInputNumber = (EditText) mMainLay.findViewById(R.id.input_number);
        mSubmit = (TextView) mMainLay.findViewById(R.id.submit);
        mSendLay = mMainLay.findViewById(R.id.send_lay);
        mCompanyText = (TextView) mMainLay.findViewById(R.id.company);
        mShipNumText = (TextView) mMainLay.findViewById(R.id.ship_number);
        mRefuseLay = mMainLay.findViewById(R.id.refuse_lay);
        mRefuseReasonText = (TextView) mMainLay.findViewById(R.id.refuse_reason);
        mSuccessLay = mMainLay.findViewById(R.id.success_lay);
        mSuccessReasonText = (TextView) mMainLay.findViewById(R.id.success_reason);
        mFailedLay = mMainLay.findViewById(R.id.failed_lay);
        mFailedReasonText = (TextView) mMainLay.findViewById(R.id.fail_reason);

        mRefuseReasonText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mSuccessReasonText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mFailedReasonText.setMovementMethod(ScrollingMovementMethod.getInstance());

        refreshUI(mRefundStatus);
    }

    private void refreshUI(int status) {
        switch (status) {
            case OrderItem.REFUNDING:
            case OrderItem.WAITING:
                mCancelRefund.setVisibility(View.VISIBLE);
                mCancelRefund.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        mActivity.showLoadingDialog();
                        KDSPApiController.getInstance().cancelRefundApply(mRefundId, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                //取消申请关闭页面
                                BroadcastUtils.sendRefreshBroadcast(mOrderId);
                                mActivity.finish();
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                            }
                        });
                    }
                });
                break;
            case OrderItem.REFUNDED:
                mAgree.setVisibility(View.VISIBLE);
                mInputLay.setVisibility(View.VISIBLE);

                mAddressText.setText("退货地址：" + mAddress);
                mNumberText.setText("号码：" + mNumber);
                mPeopleText.setText("收货人：" + mPeople);

                mSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String company = mInputCompany.getText().toString();
                        String number = mInputNumber.getText().toString();
                        mActivity.showLoadingDialog();
                        KDSPApiController.getInstance().submitRefundShippingMsg(mRefundId, number, company, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                //提交物流进入等待
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        mAgree.setVisibility(View.GONE);
                                        mInputLay.setVisibility(View.GONE);
                                        refreshUI(OrderItem.WAITING);
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                            }
                        });
                    }
                });
                break;
            case OrderItem.REFUND_REJECT:
                mRefuseLay.setVisibility(View.VISIBLE);
                mRefuseReasonText.setText(mFailedReason);

                //重新申请
                mMainLay.findViewById(R.id.refund_again);
                mMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.showLoadingDialog();
                        KDSPApiController.getInstance().againRefundApply(mRefundId, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        //重新进去申请页面
                                        ((RefundProgressActivity) mActivity).toRequest(response);
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
                });
                break;
            case OrderItem.REFUND_SUCCESS:
                //用户的快递为空说明是仅退款
                if (TextUtils.isEmpty(mShipNum)) {
                    mAgree.setVisibility(View.GONE);
                    mSendLay.setVisibility(View.GONE);
                    mMainLay.findViewById(R.id.success_tip).setVisibility(View.VISIBLE);
                } else {
                    mAgree.setVisibility(View.VISIBLE);
                    mSendLay.setVisibility(View.VISIBLE);
                    mMainLay.findViewById(R.id.success_tip).setVisibility(View.GONE);
                    mAddressText.setText("退货地址：" + mAddress);
                    mNumberText.setText("号码：" + mNumber);
                    mPeopleText.setText("收货人：" + mPeople);
                    mCompanyText.setText("快递公司：" + mCompany);
                    mShipNumText.setText("快递单号：" + mShipNum);
                }

                mSuccessLay.setVisibility(View.VISIBLE);

                mSuccessReasonText.setText(StringUtils.roundingDoubleStr((double) mRefundAmount / 100, 2) + "元");
                break;
            case OrderItem.REFUND_FEFUSE:
                mAgree.setVisibility(View.VISIBLE);
                mSendLay.setVisibility(View.VISIBLE);
                mFailedLay.setVisibility(View.VISIBLE);

                mAddressText.setText("退货地址：" + mAddress);
                mNumberText.setText("号码：" + mNumber);
                mPeopleText.setText("收货人：" + mPeople);
                mCompanyText.setText("快递公司：" + mCompany);
                mShipNumText.setText("快递单号：" + mShipNum);

                mFailedReasonText.setText(mFailedReason);
                break;
        }
    }

    public void setData(JSONObject response) {
        mRefundId = response.optJSONObject("refund").optString("refundId");
        mRefundAmount = response.optJSONObject("refund").optInt("refundAmount");
        mAddress = response.optJSONObject("refund").optString("refundAddress");
        mNumber = response.optJSONObject("refund").optString("refundPhone");
        mPeople = response.optJSONObject("refund").optString("refundName");
        mCompany = response.optJSONObject("refund").optString("expressCompany");
        mShipNum = response.optJSONObject("refund").optString("shipCode");
        mFailedReason = response.optJSONObject("refund").optString("refuseReason");
    }
}
