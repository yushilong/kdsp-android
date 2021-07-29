package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PayCancelDialogFragment;
import com.qizhu.rili.ui.fragment.AugurySubmitFragment;
import com.qizhu.rili.ui.fragment.InferringFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by  zhouyue   11/05/2017.
 * 大师问答
 */

public class MasterAskDetailActivity extends BaseActivity {
    private String mItemId;                 //问题id

    private EditText mQuestionEt;       //问题
    private final static int mMaxCount = 100;

    private AugurySubmitFragment mAugurySubmitFragment;         //支付fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_ask_detail_activity);
        initView();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.master_augur);
        mQuestionEt = (EditText) findViewById(R.id.question_et);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        mQuestionEt.addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = mQuestionEt.getSelectionStart();
                selectionEnd = mQuestionEt.getSelectionEnd();
                if (s.length() > mMaxCount) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    mQuestionEt.setText(s);
                    mQuestionEt.setSelection(tempSelection);
                    UIUtils.toastMsg(getString(R.string.master_ask_toast));
                }
                mAugurySubmitFragment.mExtraString = s.toString();
            }
        });

        mItemId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        mAugurySubmitFragment = AugurySubmitFragment.newInstance("", mItemId, false, "", "", 5, 1);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mAugurySubmitFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public <T> void setExtraData(T t) {
        if (mAugurySubmitFragment != null && mAugurySubmitFragment.mClickType == 0) {
            mAugurySubmitFragment.setExtraData(t);
        } else {
            if ("cancel".equals(t)) {
                finish();
            }
        }
    }

    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
        if (mAugurySubmitFragment != null) {
            mAugurySubmitFragment.setPickDateTime(dateTime, mode);
        }
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (mAugurySubmitFragment.mPayLay.getVisibility() == View.VISIBLE) {
            showDialogFragment(PayCancelDialogFragment.newInstance(), "弹出取消对话框");
            return true;
        }
        return super.onClickBackBtnEvent();
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (mAugurySubmitFragment.mIsRenewals) {
            if (isSuccess) {
                mAugurySubmitFragment.mRenewalsLay.setVisibility(View.GONE);
                InferringFragment.mNeedRefresh = true;
                RenewalsSuccessActivity.goToPage(MasterAskDetailActivity.this, "续费成功", 4, mItemId, mAugurySubmitFragment.jsonObject.toString(), mAugurySubmitFragment.mUsePoint, 5);
            } else {
                UIUtils.toastMsg("续费失败~");
            }
        } else {
            if (isSuccess) {
                paySuccess();
            } else {
                payFailed();
            }
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (mAugurySubmitFragment.mIsRenewals) {
            if (isSuccess) {
                mAugurySubmitFragment.mRenewalsLay.setVisibility(View.GONE);
                InferringFragment.mNeedRefresh = true;
                RenewalsSuccessActivity.goToPage(MasterAskDetailActivity.this, "续费成功", 4, mItemId, mAugurySubmitFragment.jsonObject.toString(), mAugurySubmitFragment.mUsePoint, 5);
            } else {
                UIUtils.toastMsg("续费失败~");
            }
        } else {
            if (isSuccess) {
                paySuccess();
            } else {
                payFailed();
            }
        }
    }

    private void paySuccess() {
        //支付成功则减去对应的福豆
        if (mAugurySubmitFragment.mPointCheck.isChecked()) {
            AppContext.mPointSum = AppContext.mPointSum - mAugurySubmitFragment.mCanUsePoint;
        }
        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
        BroadcastUtils.sendPaySuccessBroadcast("");
        PaySuccessActivity.goToPage(this, 5);
        finish();
    }

    /**
     * 支付失败，取消订单更新福豆
     */
    private void payFailed() {
        KDSPApiController.getInstance().cancelOrder(mAugurySubmitFragment.mIoId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                AppContext.mPointSum = response.optInt("pointSum");
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    public static void goToPage(Context context, String itemId) {
        Intent intent = new Intent(context, MasterAskDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        context.startActivity(intent);
    }
}
