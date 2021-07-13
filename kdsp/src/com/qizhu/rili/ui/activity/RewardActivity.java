package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PayAmountDialogFragment;
import com.qizhu.rili.ui.dialog.PayChooseDialogFragment;
import com.qizhu.rili.utils.AliPayUtils;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.WeixinUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

/**
 * Created by lindow on 7/15/16.
 * 打赏
 */
public class RewardActivity extends BaseActivity {
    private TextView mAmount1;          //打赏1.8
    private TextView mAmount2;          //打赏5.2
    private TextView mAmount3;          //打赏9.9
    private TextView mOtherAmount;      //其余金额

    private YSRLDraweeView mUserAvatar; //用户头像

    private String mDtId;              //测字id

    private int mFee;                   //打赏金额,以分为单位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward_lay);

        mDtId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);

        initUI();
    }

    private void initUI() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.reward_amount);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mAmount1 = (TextView) findViewById(R.id.amount1);
        mAmount2 = (TextView) findViewById(R.id.amount2);
        mAmount3 = (TextView) findViewById(R.id.amount3);
        mOtherAmount = (TextView) findViewById(R.id.other_amount);
        mUserAvatar = (YSRLDraweeView) findViewById(R.id.user_avatar);

        mAmount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFee = 180;
                showDialogFragment(PayChooseDialogFragment.newInstance(), "选择支付方式");
            }
        });

        mAmount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFee = 520;
                showDialogFragment(PayChooseDialogFragment.newInstance(), "选择支付方式");
            }
        });

        mAmount3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFee = 990;
                showDialogFragment(PayChooseDialogFragment.newInstance(), "选择支付方式");
            }
        });

        mOtherAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFragment(PayAmountDialogFragment.newInstance(), "输入支付金额");
            }
        });

        if (AppContext.mUser != null) {
            UIUtils.displayBigAvatarImage(AppContext.mUser.imageUrl, mUserAvatar, R.drawable.default_avatar);
        }
    }

    @Override
    public <T> void setExtraData(T t) {
        if (YSRLConstants.WEIXIN_PAY.equals(t)) {
            showLoadingDialog();
            KDSPApiController.getInstance().unifiedorder(mFee, mDtId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(final JSONObject response) {
                    RewardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            WeixinUtils.getInstance().startPayByMM(RewardActivity.this, response);
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });
        } else if (YSRLConstants.ALIPAY.equals(t)) {
            showLoadingDialog();
            KDSPApiController.getInstance().sign(mFee, mDtId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(final JSONObject response) {
                    RewardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissLoadingDialog();
                            AliPayUtils.getInstance().startPay(RewardActivity.this, response);
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });
        }
    }

    @Override
    public void setImagePath(String path) {
        double amount = Double.valueOf(path);
        mFee = (int) (amount * 100);
        showDialogFragment(PayChooseDialogFragment.newInstance(), "选择支付方式");
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            UIUtils.toastMsg("打赏成功");
            BroadcastUtils.sendPaySuccessBroadcast(mDtId);
            finish();
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            UIUtils.toastMsg("打赏成功");
            BroadcastUtils.sendPaySuccessBroadcast(mDtId);
            finish();
        }
    }

    public static void goToPage(Context context, String dtId) {
        Intent intent = new Intent(context, RewardActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, dtId);
        context.startActivity(intent);
    }
}
