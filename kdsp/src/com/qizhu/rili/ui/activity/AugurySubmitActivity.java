package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
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
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 8/16/16.
 * 提交占卜界面
 */
public class AugurySubmitActivity extends BaseActivity {
    private String  mItemId;                 //问题id
    private int     mItemType;                  //问题类型  4测名起名 5八字婚姻
    private int     mItemSubType;               //额外类型
    private String  mLeftPath;               //左边图像
    private String  mRightPath;              //右边图像
    private boolean mIsConvert;             //是否是兑换福豆
    private int     mExtraMode;                 //额外的条件，1为string,2为nameType
    private int     mPrice;                 //价格
    private String  mMsgId;                  //礼品id
    private int mType ;                     //1为风水支付

    private AugurySubmitFragment mAugurySubmitFragment;         //支付fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augury_submit_lay);
        getIntentExtra();
        initUI();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mMsgId = intent.getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        mItemId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mIsConvert = intent.getBooleanExtra(IntentExtraConfig.EXTRA_IS_MINE, false);
        mLeftPath = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        mRightPath = intent.getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
        mItemType = intent.getIntExtra(IntentExtraConfig.EXTRA_MODE, 0);
        mItemSubType = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);
        mExtraMode = intent.getIntExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT, 0);
        mPrice = intent.getIntExtra(IntentExtraConfig.EXTRA_POST_ID, 0);
        mType = intent.getIntExtra(IntentExtraConfig.EXTRA_TYPE, 0);
    }

    private void initUI() {
        findViewById(R.id.header).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        TextView mTitle = (TextView) findViewById(R.id.title_txt);

        if (mItemType == 4) {
            if (mItemSubType == 1) {
                mTitle.setText("改名字");
            } else {
                mTitle.setText("宝宝取名");
            }
        } else if (mItemType == 5) {
            mTitle.setText(R.string.marrige_title);
        } else {
            mTitle.setText(R.string.inferring_detail);
        }


        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        if (mExtraMode == 2) {
            mAugurySubmitFragment = AugurySubmitFragment.newInstance(mMsgId, mItemId, mIsConvert, mLeftPath, mRightPath, mItemType, mItemSubType, mExtraMode);
        } else if (mExtraMode == 3) {
            mAugurySubmitFragment = AugurySubmitFragment.newInstance(mItemId, mItemType, mExtraMode, mPrice);
        } else {
            mAugurySubmitFragment = AugurySubmitFragment.newInstance(mMsgId, mItemId, mIsConvert, mLeftPath, mRightPath, mItemType);
        }

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
                RenewalsSuccessActivity.goToPage(AugurySubmitActivity.this, "续费成功", 4, mItemId, mAugurySubmitFragment.jsonObject.toString(), mAugurySubmitFragment.mUsePoint, mItemType);
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
                RenewalsSuccessActivity.goToPage(AugurySubmitActivity.this, "续费成功", 4, mItemId, mAugurySubmitFragment.jsonObject.toString(), mAugurySubmitFragment.mUsePoint, mItemType);
            } else {
                UIUtils.toastMsg("续费失败~");
            }
        } else if(mType == 1){
            setResult(RESULT_OK);
            finish();
        }else {
            if (isSuccess) {
                paySuccess();
                LogUtils.d("---->paySuccess();");
            } else {
                payFailed();
                LogUtils.d("---->payFailed();");
            }
        }
    }

    private void paySuccess() {
        //支付成功则减去对应的福豆
        if (mAugurySubmitFragment.mPointCheck.isChecked() || mIsConvert) {
            AppContext.mPointSum = AppContext.mPointSum - mAugurySubmitFragment.mCanUsePoint;
        }
        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
        if (mItemType != 0) {
            BroadcastUtils.sendPaySuccessBroadcast("");
        }
        PaySuccessActivity.goToPage(this, mItemType);
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
                LogUtils.d("---->cancelOrder()Fail;");
            }
        });
    }

    public static void goToPage(Context context, String itemId) {
        goToPage(context, itemId, false);
    }

    public static void goToPage(Context context, String itemId,int  type) {
        Intent intent = new Intent(context, AugurySubmitActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_TYPE, type);
        context.startActivity(intent);
    }


    public static void goToPage(Context context, String itemId, boolean isConvert) {
        Intent intent = new Intent(context, AugurySubmitActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_IS_MINE, isConvert);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String msgId, String itemId, String leftPath, String rightPath, int itemType) {
        Intent intent = new Intent(context, AugurySubmitActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, msgId);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, leftPath);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, rightPath);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, itemType);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String msgId, String itemId, String leftPath, String rightPath, int itemType, int subType, int extra) {
        Intent intent = new Intent(context, AugurySubmitActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, msgId);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, leftPath);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, rightPath);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, itemType);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, subType);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT, extra);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String itemId, int itemType, int extra, int price) {
        Intent intent = new Intent(context, AugurySubmitActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, itemType);
        intent.putExtra(IntentExtraConfig.EXTRA_SHARE_CONTENT, extra);
        intent.putExtra(IntentExtraConfig.EXTRA_POST_ID, price);
        context.startActivity(intent);
    }

}
