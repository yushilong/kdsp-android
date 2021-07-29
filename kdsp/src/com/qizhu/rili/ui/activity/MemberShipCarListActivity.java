package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.CouponsAdapter;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.MembershipGift;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.CouponsDialogFragment;
import com.qizhu.rili.ui.dialog.PayDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhouyue on 9/1/17.
 * 优惠券会员卡列表页
 */
public class MemberShipCarListActivity extends BaseListActivity {


    private ArrayList<Coupons> mCouponsList = new ArrayList<>();
    private YSRLDraweeView mMemberCard;             //会员卡
    private TextView       mMemberShipTip;                //会员权益
    private LinearLayout   mContainer;                //会员权益项
    private TextView       mPrice;                        //当前余额

    private int    mBalance;               //会员卡余额，单位为分
    private String mVipFont;            //描述
    private String mVipImage;           //展示图片
    private int                       mVipStatus       = -1;        //会员状态，当为0时，不是vip，1为vip状态正常，2为余额不足
    private ArrayList<MembershipGift> mMembershipGifts = new ArrayList<>();
    private View           view;
    private RelativeLayout mNormalLay;
    private LinearLayout   mMemberLay;
    private LinearLayout   mWealLay;
    private LinearLayout   mRenewalsLay;
    private TextView       mRenewalsTv;
    private TextView       mCouponsTv;
    private TextView mRightText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKDSPRecyclerView.setBackgroundColor(ContextCompat.getColor(MemberShipCarListActivity.this,R.color.white));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_text, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            mRightText = (TextView) view.findViewById(R.id.right_text);
            mTitle.setText(R.string.my_card);
            mRightText.setText(R.string.get_my_card);

            mRightText.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    if(AppContext.mUser !=null){
                        if(TextUtils.isEmpty(AppContext.mUser.telephoneNumber)){
                            RegisterActivity.goToPageWithResult(MemberShipCarListActivity.this,1);
                        }else {
                            showDialogFragment(CouponsDialogFragment.newInstance(""), "兑换优惠券");
                        }
                    }

                }
            });
            titleView.addView(view, params);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        view = new ListViewHead(this, R.layout.head_coupons);
        mMemberCard = (YSRLDraweeView) view.findViewById(R.id.membership_card);
        mMemberShipTip = (TextView) view.findViewById(R.id.member_ship_tip);
        mContainer = (LinearLayout) view.findViewById(R.id.container);
        mNormalLay = (RelativeLayout) view.findViewById(R.id.normal_lay);
        mMemberLay = (LinearLayout) view.findViewById(R.id.member_lay);
        mWealLay = (LinearLayout) view.findViewById(R.id.weal_lay);
        mRenewalsLay = (LinearLayout) view.findViewById(R.id.renewals_lay);
        mPrice = (TextView) view.findViewById(R.id.price);
        mRenewalsTv = (TextView) view.findViewById(R.id.renewals);
        mCouponsTv = (TextView) view.findViewById(R.id.coupons_tv);
        view.findViewById(R.id.normal_tip3).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MemberShipActivity.goToPage(MemberShipCarListActivity.this);
            }
        });
        super.addHeadView(refreshableView, view);
    }

    private void getMemberCard() {
//        showLoadingDialog();
        KDSPApiController.getInstance().getMembershipMsg(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mBalance = response.optInt("balance");
                mVipStatus = response.optInt("vipStatus");
                mVipFont = response.optString("vipFont");
                mVipImage = response.optString("vipImage");
                mMembershipGifts = MembershipGift.parseListFromJSON(response.optJSONArray("membershipGifts"));
                MemberShipCarListActivity.this.runOnUiThread(new Runnable() {
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
                if (mCouponsList.isEmpty()) {
                    mNormalLay.setVisibility(View.GONE);
                }
                mMemberLay.setVisibility(View.GONE);
            }
        });
    }


    private void refreshUI() {
        dismissLoadingDialog();
        if (mVipStatus != 0) {
            mNormalLay.setVisibility(View.GONE);
            mMemberLay.setVisibility(View.VISIBLE);

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
                    mWealLay.setVisibility(View.VISIBLE);
                    mRenewalsLay.setVisibility(View.GONE);
                    mPrice.setText("金额：" + StringUtils.roundingDoubleStr((double) mBalance / 100, 2) + "元");
                    mMemberShipTip.setText(mVipFont);
                    if (!mMembershipGifts.isEmpty()) {
                        mContainer.removeAllViews();
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
                                        HandsOrFaceOrderActivity.goToPage(MemberShipCarListActivity.this, mMembershipGift.msgId, mMembershipGift.itemId, mMembershipGift.type == 1);
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
                    mWealLay.setVisibility(View.GONE);
                    mRenewalsLay.setVisibility(View.VISIBLE);
                    mRenewalsTv.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
                        }
                    });
                    break;
            }
        } else {
            mMemberLay.setVisibility(View.GONE);
        }

        if (mCouponsList.size() == 0) {
            mCouponsTv.setVisibility(View.GONE);
        }

        LogUtils.d("----member mVipStatus:" +mVipStatus +",mCouponsList:" + mCouponsList.size());

        if (mVipStatus == 0 && mCouponsList.size() == 0) {
            mNormalLay.setVisibility(View.VISIBLE);
        }else {
            mNormalLay.setVisibility(View.GONE);
        }

    }

    @Override
    public <T> void setExtraData(T t) {
        if (t instanceof String) {
           if(t.equals("ok")){
               getData();
           }
        }

    }

    @Override
    protected void initAdapter() {

        if (mAdapter == null) {
            mAdapter = new CouponsAdapter(this, mCouponsList);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);

        }
    }

    @Override
    protected void getData() {
//        showLoadingDialog();
        String phone = "";
        if (AppContext.mUser != null) {
            phone = AppContext.mUser.telephoneNumber;
        }

        KDSPApiController.getInstance().findMyCouponListByPhoneNum(phone, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {

                mCouponsList = Coupons.parseListFromJSON(response.optJSONArray("coupons"));
                LogUtils.d("----member mCouponsList:"  + mCouponsList.size());

                MemberShipCarListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        refreshViewByType(LAY_TYPE_NORMAL);
                        refreshListViewComplete();
                        mAdapter.reset(mCouponsList);
                        mAdapter.notifyDataSetChanged();
                        getMemberCard();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();

            }
        });


    }

    @Override
    protected void getNextData() {

    }

    @Override
    public void pullDownToRefresh() {
        super.pullDownToRefresh();
    }

    @Override
    protected boolean canPullDownRefresh() {
        return true;
    }


    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MemberShipCarListActivity.class);
        context.startActivity(intent);
    }


}
