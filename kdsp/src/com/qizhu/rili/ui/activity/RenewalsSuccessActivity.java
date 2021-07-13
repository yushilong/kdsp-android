package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.fragment.InferringFragment;
import com.qizhu.rili.utils.UIUtils;

import org.json.JSONObject;

/**
 * Created by lindow on 22/12/2016.
 * 充值或者续费成功
 */
public class RenewalsSuccessActivity extends BaseActivity {
    private TextView mTip1;
    private TextView mTip2;
    private TextView mTip3;

    private String mTitle;
    private int mType;                  //充值或者续费类型，1为会员尊享充值成功，2为会员卡内部续费，3为定制运势续费
    private String mItemId;             //问题id
    private String mJson;               //参数
    private int mUsePoint = 0;          //已经使用的福豆
    private int mItemType;              //问题类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.renewals_success_lay);
        getIntentExtra();
        initView();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(IntentExtraConfig.EXTRA_PAGE_TITLE);
        mType = intent.getIntExtra(IntentExtraConfig.EXTRA_MODE, 1);
        mItemId = intent.getStringExtra(IntentExtraConfig.EXTRA_PARCEL);
        mJson = intent.getStringExtra(IntentExtraConfig.EXTRA_JSON);
        mUsePoint = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);
        mItemType = intent.getIntExtra(IntentExtraConfig.EXTRA_POST_ID, 0);
    }

    private void initView() {
        TextView mTitleText = (TextView) findViewById(R.id.title_txt);
        mTitleText.setText(mTitle);

        mTip1 = (TextView) findViewById(R.id.normal_tip1);
        mTip2 = (TextView) findViewById(R.id.normal_tip2);
        mTip3 = (TextView) findViewById(R.id.normal_tip3);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        switch (mType) {
            case 1:
                mTip1.setText("充值成功");
                mTip2.setVisibility(View.VISIBLE);
                mTip3.setText("查看会员福利");
                mTip3.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        MemberShipCardActivity.goToPage(RenewalsSuccessActivity.this);
                    }
                });
                break;
            case 2:
                mTip1.setText("续费成功");
                mTip2.setVisibility(View.GONE);
                mTip3.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        MemberShipCardActivity.goToPage(RenewalsSuccessActivity.this);
                    }
                });
                break;
            case 3:
                mTip1.setText("续费成功");
                mTip2.setVisibility(View.GONE);
                mTip3.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        MemberShipCardActivity.goToPage(RenewalsSuccessActivity.this);
                    }
                });
                break;
            case 4:
                mTip1.setText("续费成功");
                mTip2.setVisibility(View.GONE);
                mTip3.setText("支付订单");
                mTip3.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        showLoadingDialog();
                        KDSPApiController.getInstance().membership(mItemId, mJson, mUsePoint, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                RenewalsSuccessActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();
                                        InferringFragment.mNeedRefresh = true;
                                        AppContext.mPointSum = AppContext.mPointSum - mUsePoint;
                                        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
                                        PaySuccessActivity.goToPage(RenewalsSuccessActivity.this, mItemType);
                                        finish();
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
                });
                break;
            default:
                break;
        }
    }

    public static void goToPage(Context context, String title, int type) {
        Intent intent = new Intent(context, RenewalsSuccessActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, type);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String title, int type, String itemId, String json, int point, int itemType) {
        Intent intent = new Intent(context, RenewalsSuccessActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_PAGE_TITLE, title);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, type);
        intent.putExtra(IntentExtraConfig.EXTRA_PARCEL, itemId);
        intent.putExtra(IntentExtraConfig.EXTRA_JSON, json);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, point);
        intent.putExtra(IntentExtraConfig.EXTRA_POST_ID, itemType);
        context.startActivity(intent);
    }
}
