package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.ui.dialog.PayCancelDialogFragment;
import com.qizhu.rili.ui.fragment.AugurySubmitFragment;
import com.qizhu.rili.ui.fragment.InferringFragment;
import com.qizhu.rili.ui.fragment.TestNameViewPagerFragment;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 03/05/2017.
 * 测名
 */

public class TestNameActivity extends BaseActivity {
    private String mItemId;                 //问题id

    private TestNameViewPagerFragment mTestNameViewPagerFragment;
    private AugurySubmitFragment mAugurySubmitFragment;         //支付fragment

    private ArrayList<String> mCats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_name_activity);
        mCats.add("免费测名");
        mCats.add("宝宝起名");
        mItemId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        int type = getIntent().getIntExtra(IntentExtraConfig.EXTRA_TYPE ,0);
        mTestNameViewPagerFragment = TestNameViewPagerFragment.newInstance(mCats, mItemId ,type);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.body_fragment, mTestNameViewPagerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public <T> void setExtraData(T t) {
        mAugurySubmitFragment = ((AugurySubmitFragment) mTestNameViewPagerFragment.getCurrentFragment());
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
        mAugurySubmitFragment = ((AugurySubmitFragment) mTestNameViewPagerFragment.getCurrentFragment());
        if (mAugurySubmitFragment != null) {
            mAugurySubmitFragment.setPickDateTime(dateTime, mode);
        }
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (mAugurySubmitFragment != null && mAugurySubmitFragment.mPayLay.getVisibility() == View.VISIBLE) {
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
                RenewalsSuccessActivity.goToPage(TestNameActivity.this, "续费成功", 4, mItemId, mAugurySubmitFragment.jsonObject.toString(), mAugurySubmitFragment.mUsePoint, 4);
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
                RenewalsSuccessActivity.goToPage(TestNameActivity.this, "续费成功", 4, mItemId, mAugurySubmitFragment.jsonObject.toString(), mAugurySubmitFragment.mUsePoint, 4);
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

    public static void goToPage(Context context, String id ,int type) {
        Intent intent = new Intent(context, TestNameActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, id);
        intent.putExtra(IntentExtraConfig.EXTRA_TYPE, type);
        context.startActivity(intent);
    }
}
