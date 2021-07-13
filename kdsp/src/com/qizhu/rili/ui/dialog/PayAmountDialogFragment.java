package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.UIUtils;

/**
 * Created by lindow on 7/18/16.
 * 支付金额对话框
 */
public class PayAmountDialogFragment extends BaseDialogFragment {
    private EditText mAmount;           //金额
    private TextView mConfirm;          //确定

    public static PayAmountDialogFragment newInstance() {
        PayAmountDialogFragment rtn = new PayAmountDialogFragment();
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pay_amount_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置背景为透明
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    private void initView() {
        mAmount = (EditText) mMainLay.findViewById(R.id.amount);
        mConfirm = (TextView) mMainLay.findViewById(R.id.confirm);

        mAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    String amount = charSequence.toString();
                    double money = Double.valueOf(amount);
                    if (TextUtils.isEmpty(amount)) {
                        setConfirmUnable();
                    } else if (amount.contains(".") && (amount.length() - amount.indexOf(".") > 3)) {
                        setConfirmUnable();
                    } else if (money > 200) {
                        setConfirmUnable();
                    } else if (money < 0.99) {
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

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });

        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                try {
                    String amount = mAmount.getText().toString();
                    //用另一个接口与setExtra相区分，其实本质上是一样的
                    mActivity.setImagePath(amount);
                    dismiss();
                } catch (Exception e) {
                    UIUtils.toastMsg("输入金额有误，请重新输入~");
                    e.printStackTrace();
                }
            }
        });
    }

    private void setConfirmEnable() {
        mAmount.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
        mConfirm.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        mConfirm.setBackgroundResource(R.drawable.round_purple1);
        mConfirm.setEnabled(true);
    }

    private void setConfirmUnable() {
        mAmount.setTextColor(ContextCompat.getColor(mActivity, R.color.red));
        mConfirm.setTextColor(ContextCompat.getColor(mActivity, R.color.white_transparent_37));
        mConfirm.setBackgroundResource(R.drawable.round_purple4);
        mConfirm.setEnabled(false);
    }
}
