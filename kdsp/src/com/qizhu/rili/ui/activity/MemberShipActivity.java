package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.MembershipCat;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.LoginSuccessListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PayDialogFragment;
import com.qizhu.rili.ui.fragment.InferringFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by lindow on 16/12/2016.
 * 会员尊享
 */

public class MemberShipActivity extends BaseActivity {
    private LinearLayout mContainer;            //会员卡项
    private TextView mVipAgreementText;         //会员协议

    private ArrayList<MembershipCat> mMembershipCats = new ArrayList<>();
    private String mVipAgreement;               //会员协议
    private int mVipStatus = -1;                //会员状态，当为0时，不是vip，1为vip状态正常，2为余额不足

    private String mCurMscId;                   //当前id
    private int mCurPrice;                      //当前金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membership_lay);
        initView();
        getMemberShip();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.membership_rights);
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        mContainer = (LinearLayout) findViewById(R.id.container);
        mVipAgreementText = (TextView) findViewById(R.id.vip_agreement);
    }

    private void getMemberShip() {
        showLoadingDialog();
        KDSPApiController.getInstance().getMembershipCat(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mMembershipCats = MembershipCat.parseListFromJSON(response.optJSONArray("membershipCats"));
                mVipStatus = response.optInt("vipStatus");
                mVipAgreement = response.optString("vipAgreement").replace("/", "\n");
                MemberShipActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshUI();
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

    private void refreshUI() {
        dismissLoadingDialog();
        final int picWidth = AppContext.getScreenWidth() - DisplayUtils.dip2px(32);

        if (!mMembershipCats.isEmpty()) {
            for (final MembershipCat mMembershipCat : mMembershipCats) {
                View itemLay = mInflater.inflate(R.layout.membership_cat_item, null);
                final YSRLDraweeView mImage = (YSRLDraweeView) itemLay.findViewById(R.id.membership_card);

                UIUtils.displayImage(mMembershipCat.imageUrl, mImage, 600, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mImage.setInfoHeight(picWidth, imageInfo);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                });

                TextView mTip = (TextView) itemLay.findViewById(R.id.member_ship_tip);
                mTip.setText(mMembershipCat.mscDesc);

                if (mVipStatus != 0) {
                    itemLay.findViewById(R.id.to_be_member).setVisibility(View.GONE);
                } else {
                    itemLay.findViewById(R.id.to_be_member).setVisibility(View.VISIBLE);
                    itemLay.findViewById(R.id.to_be_member).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            if (AppContext.isAnonymousUser()) {
                                LoginChooserActivity.goToPage(MemberShipActivity.this, new LoginSuccessListener() {
                                    @Override
                                    public void success() {
                                        mCurMscId = mMembershipCat.mscId;
                                        mCurPrice = mMembershipCat.price;
                                        showDialogFragment(PayDialogFragment.newInstance(mCurMscId, mCurPrice), "充值会员卡");
                                    }
                                });
                            } else {
                                mCurMscId = mMembershipCat.mscId;
                                mCurPrice = mMembershipCat.price;
                                showDialogFragment(PayDialogFragment.newInstance(mCurMscId, mCurPrice), "充值会员卡");
                            }
                        }
                    });
                }

                mContainer.addView(itemLay);
            }
        }

        mVipAgreementText.setText(mVipAgreement);
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            InferringFragment.mNeedRefresh = true;
            RenewalsSuccessActivity.goToPage(MemberShipActivity.this, "充值成功", 1);
        } else {
            showDialogFragment(PayDialogFragment.newInstance(mCurMscId, mCurPrice), "充值会员卡");
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            InferringFragment.mNeedRefresh = true;
            RenewalsSuccessActivity.goToPage(MemberShipActivity.this, "充值成功", 1);
        } else {
            showDialogFragment(PayDialogFragment.newInstance(mCurMscId, mCurPrice), "充值会员卡");
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MemberShipActivity.class);
        context.startActivity(intent);
    }
}
