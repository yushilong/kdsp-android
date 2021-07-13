package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.MembershipGift;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PayDialogFragment;
import com.qizhu.rili.ui.fragment.InferringFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lindow on 16/12/2016.
 * 会员卡
 */
public class MemberShipCardActivity extends BaseActivity {
    private YSRLDraweeView mMemberCard;             //会员卡
    private TextView mMemberShipTip;                //会员权益
    private LinearLayout mContainer;                //会员权益项
    private TextView mPrice;                        //当前余额

    private int mBalance;               //会员卡余额，单位为分
    private String mVipFont;            //描述
    private String mVipImage;           //展示图片
    private int mVipStatus = -1;        //会员状态，当为0时，不是vip，1为vip状态正常，2为余额不足
    private ArrayList<MembershipGift> mMembershipGifts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membership_card_lay);
        initView();
        getMemberCard();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.membership_card);

        mMemberCard = (YSRLDraweeView) findViewById(R.id.membership_card);
        mMemberShipTip = (TextView) findViewById(R.id.member_ship_tip);
        mContainer = (LinearLayout) findViewById(R.id.container);
        mPrice = (TextView) findViewById(R.id.price);

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.normal_tip3).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MemberShipActivity.goToPage(MemberShipCardActivity.this);
            }
        });
    }

    private void getMemberCard() {
        showLoadingDialog();
        KDSPApiController.getInstance().getMembershipMsg(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mBalance = response.optInt("balance");
                mVipStatus = response.optInt("vipStatus");
                mVipFont = response.optString("vipFont");
                mVipImage = response.optString("vipImage");
                mMembershipGifts = MembershipGift.parseListFromJSON(response.optJSONArray("membershipGifts"));
                MemberShipCardActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                mVipStatus = 0;
                showFailureMessage(error);
                findViewById(R.id.normal_lay).setVisibility(View.VISIBLE);
                findViewById(R.id.member_lay).setVisibility(View.GONE);
            }
        });
    }

    private void refreshUI() {
        dismissLoadingDialog();
        if (mVipStatus != 0) {
            findViewById(R.id.normal_lay).setVisibility(View.GONE);
            findViewById(R.id.member_lay).setVisibility(View.VISIBLE);

            final int picWidth = AppContext.getScreenWidth() - DisplayUtils.dip2px(26);
            UIUtils.displayImage(mVipImage, mMemberCard, 600, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    mMemberCard.setInfoHeight(picWidth, imageInfo);
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    super.onFailure(id, throwable);
                }
            });
            switch (mVipStatus) {
                case 1:
                    findViewById(R.id.weal_lay).setVisibility(View.VISIBLE);
                    findViewById(R.id.renewals_lay).setVisibility(View.GONE);
                    mPrice.setText("金额：" + StringUtils.roundingDoubleStr((double) mBalance / 100, 2) + "元");
                    mMemberShipTip.setText(mVipFont);
                    if (!mMembershipGifts.isEmpty()) {
                        for (final MembershipGift mMembershipGift : mMembershipGifts) {
                            View itemLay = mInflater.inflate(R.layout.member_ship_item, null);
                            TextView name = (TextView) itemLay.findViewById(R.id.right_title);
                            name.setText(mMembershipGift.msgName);
                            TextView oldPrice = (TextView) itemLay.findViewById(R.id.old_price);
                            TextView newPrice = (TextView) itemLay.findViewById(R.id.price);
                            TextView mUse = (TextView) itemLay.findViewById(R.id.use_right);
                            oldPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) (mMembershipGift.originalPrice) / 100, 2));
                            newPrice.setText(StringUtils.roundingDoubleStr((double) (mMembershipGift.currentPrice) / 100, 2) + "元");
                            UIUtils.setThruLine(oldPrice);
                            if (mMembershipGift.isUserful == 0) {
                                mUse.setBackgroundResource(R.drawable.round_purple1);
                                mUse.setText("去测算");
                                mUse.setOnClickListener(new OnSingleClickListener() {
                                    @Override
                                    public void onSingleClick(View v) {
                                        HandsOrFaceOrderActivity.goToPage(MemberShipCardActivity.this, mMembershipGift.msgId, mMembershipGift.itemId, mMembershipGift.type == 1);
                                    }
                                });
                            } else {
                                mUse.setBackgroundResource(R.drawable.round_gray38);
                                mUse.setText("已使用");
                                mUse.setOnClickListener(null);
                            }
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, DisplayUtils.dip2px(10));

                            mContainer.addView(itemLay, layoutParams);
                        }
                    }
                    break;
                case 2:
                    findViewById(R.id.weal_lay).setVisibility(View.GONE);
                    findViewById(R.id.renewals_lay).setVisibility(View.VISIBLE);
                    findViewById(R.id.renewals).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
                        }
                    });
                    break;
            }
        } else {
            findViewById(R.id.normal_lay).setVisibility(View.VISIBLE);
            findViewById(R.id.member_lay).setVisibility(View.GONE);
        }
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            InferringFragment.mNeedRefresh = true;
            RenewalsSuccessActivity.goToPage(MemberShipCardActivity.this, "续费成功", 2);
        } else {
            showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            InferringFragment.mNeedRefresh = true;
            RenewalsSuccessActivity.goToPage(MemberShipCardActivity.this, "续费成功", 2);
        } else {
            showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
        }
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MemberShipCardActivity.class);
        context.startActivity(intent);
    }
}
