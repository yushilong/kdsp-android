package com.qizhu.rili.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.RequestCodeConfig;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.adapter.CouponsChooseAdapter;
import com.qizhu.rili.bean.Address;
import com.qizhu.rili.bean.AuguryCart;
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PayCancelDialogFragment;
import com.qizhu.rili.utils.AliPayUtils;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.WeixinUtils;
import com.qizhu.rili.widget.MyListView;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.qizhu.rili.R.id.content_lay;

/**
 * Created by zhouyue on 23/04/2018.
 * 大师一对一确认订单界面
 */

public class MasterAuguryCarOrderConfirmActivity extends BaseActivity {
    private LinearLayout mSkuContainer;
    private LinearLayout mCouponsLLayout;
    private View         mCouponsLine;          //优惠券
    private TextView     mCouponsTv;          //优惠券
    private MyListView   mCouponsListView;    //优惠券
    private ImageView    mArrowimage;           //优惠券箭头
    private TextView     mPostage;
    private TextView     mPonitConvert;
    private CheckBox     mPointCheck;
    private TextView     mTotalPrice;
    private TextView     mSubmit;
    private EditText     mRemarkTv;
    private View         mPayLay;                   //支付布局
    private TextView     mPayPriceText;         //价格
    private ImageView    mWeixinSelect;        //微信
    private ImageView    mAliSelect;           //支付宝
    public  View         mRenewalsLay;               //续费布局
    private TextView     mBalanceText;          //余额

    private String  mSkuId;
    private int     mCount;
    private String  mCartIds;
    private Address mAddress;
    private int     mFee;                       //总金额
    private int     mShipFee;                       //邮费
    private String  mPayMode;                //支付方式
    private ArrayList<AuguryCart> mOrderItems = new ArrayList<>();
    private String               mOrderIds;
    private DateTime             mBirthDate;                //出生日期
    private boolean              isCouponsListShow;
    private CouponsChooseAdapter mCouponsChooseAdapter;
    private ArrayList<Coupons> mCouponsList = new ArrayList<>();
    private int        mCurrentCoupons;          //当前选择的优惠券
    private ScrollView mContentLay;
    private Coupons    mCoupons;
    private int        mGoodsType;
    private int        mYourBirthMode;
    private int        mVipStatus;                 //会员状态，当为0时，不是vip，1为vip状态正常，2为余额不足
    private int        mTempPrice;                     //价格
    private int        mBalance;                   //会员余额
    private double               mCanUsePrice;            //可支付金额
    public int mCanUsePoint = 0;            //可用福豆
    public int mUsePoint    = 0;               //已经使用的福豆
    private String mItemId;
    private ArrayList<AuguryCart> mSelectCarts = new ArrayList<>();       //选择的购物车项
    private String mOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_augury_order_confirm_lay);
        initView();
        getExtraData();
        getData();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.submit_order);

        mRenewalsLay = findViewById(R.id.renewals_lay);
        mBalanceText = (TextView) findViewById(R.id.renewals_tip);
        mSkuContainer = (LinearLayout) findViewById(R.id.sku_container);
        mPostage = (TextView) findViewById(R.id.postage);
        mPonitConvert = (TextView) findViewById(R.id.point_convert);
        mPointCheck = (CheckBox) findViewById(R.id.point_check);
        mTotalPrice = (TextView) findViewById(R.id.total_price);
        mSubmit = (TextView) findViewById(R.id.submit);
        mPayLay = findViewById(R.id.pay_lay);
        mPayPriceText = (TextView) findViewById(R.id.pay_price);
        mWeixinSelect = (ImageView) findViewById(R.id.weixin_selected);
        mAliSelect = (ImageView) findViewById(R.id.ali_selected);
        mCouponsLLayout = (LinearLayout) findViewById(R.id.coupons_llayout);
        mCouponsLine = findViewById(R.id.coupons_line);
        mCouponsTv = (TextView) findViewById(R.id.coupons_tv);
        mCouponsListView = (MyListView) findViewById(R.id.coupons_listview);
        mArrowimage = (ImageView) findViewById(R.id.arrow_image);
        mContentLay = (ScrollView) findViewById(R.id.content_lay);
        mRemarkTv = (EditText) findViewById(R.id.remark_tv);


        if (mBirthDate == null) {
            mBirthDate = new DateTime(AppContext.mUser.birthTime);
        }
        if (AppContext.mUser != null) {
            if (AppContext.mUser.isLunar == 0) {
                mYourBirthMode = 0;
                mBirthDate = new DateTime(AppContext.mUser.birthTime);
            } else {
                mYourBirthMode = 1;
                mBirthDate = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime));
            }

        }
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (AppContext.mUser != null && !TextUtils.isEmpty(AppContext.mUser.telephoneNumber)) {
                    getOrderInfo();
                } else {
                    RegisterActivity.goToPageWithResult(MasterAuguryCarOrderConfirmActivity.this, 1);
                }
            }
        });

        findViewById(R.id.blank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.weixin_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayMode = YSRLConstants.WEIXIN_PAY;
                mWeixinSelect.setImageResource(R.drawable.pay_selected);
                mAliSelect.setImageResource(R.drawable.pay_unselected);
            }
        });

        findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayMode = YSRLConstants.ALIPAY;
                mWeixinSelect.setImageResource(R.drawable.pay_unselected);
                mAliSelect.setImageResource(R.drawable.pay_selected);
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFragment(PayCancelDialogFragment.newInstance(), "弹出取消对话框");
            }
        });

        findViewById(R.id.pay_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mPayMode)) {
                    UIUtils.toastMsg("请选择支付方式");
                } else {
                    if (mPointCheck.isChecked() ) {
                        mUsePoint = mCanUsePoint;
                    } else {
                        mUsePoint = 0;
                    }
                    String couponsId = "";
                    if (mCoupons != null) couponsId = mCoupons.mcId;
                    if (YSRLConstants.WEIXIN_PAY.equals(mPayMode)) {
                        showLoadingDialog();
                        KDSPApiController.getInstance().toPay(mCartIds, mUsePoint,couponsId,1, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                MasterAuguryCarOrderConfirmActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();
                                        mOrderId = response.optString("orderId");
                                        WeixinUtils.getInstance().startPayByMM(MasterAuguryCarOrderConfirmActivity.this, response);

                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });
                    } else if (YSRLConstants.ALIPAY.equals(mPayMode)) {
                        showLoadingDialog();
                        KDSPApiController.getInstance().toPay(mCartIds, mUsePoint,couponsId, 2, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                MasterAuguryCarOrderConfirmActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();
                                        mOrderId = response.optString("orderId");
                                        AliPayUtils.getInstance().startPay(MasterAuguryCarOrderConfirmActivity.this, response);
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
            }
        });

        mCouponsLLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (isCouponsListShow) {
                    mCouponsListView.setVisibility(View.GONE);
                    isCouponsListShow = false;
                    mArrowimage.setImageResource(R.drawable.arrow_up_coupons_gray);
                } else {
                    mArrowimage.setImageResource(R.drawable.arrow_down_coupons_gray);
                    isCouponsListShow = true;
                    mCouponsListView.setVisibility(View.VISIBLE);
                    mCouponsChooseAdapter = new CouponsChooseAdapter(MasterAuguryCarOrderConfirmActivity.this, mCouponsList);
                    mCouponsListView.setAdapter(mCouponsChooseAdapter);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mContentLay.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                }

            }
        });

        mCouponsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCouponsListView.setVisibility(View.GONE);
                isCouponsListShow = false;
                Coupons coupons = (Coupons) mCouponsChooseAdapter.getItem(position);
                mArrowimage.setImageResource(R.drawable.arrow_up_coupons_gray);

                if (mCurrentCoupons != position) {
                    coupons.isChoose = true;
                    if (mCurrentCoupons != 0) {
                        Coupons tempCoupons = (Coupons) mCouponsChooseAdapter.getItem(mCurrentCoupons);
                        tempCoupons.isChoose = false;
                    }

                    mCoupons = coupons;

                    mCouponsChooseAdapter.notifyDataSetChanged();
                    mCurrentCoupons = position;
                }
                if (position == 0) {
                    mFee = mTempPrice;
                    mCoupons = null;
                    computePointPrice(mTempPrice);
                    refreshPrice(mPointCheck.isChecked());
                    mCouponsTv.setText(coupons.endTime);
                } else {
                    mFee = mTempPrice;
                    computeCouponsPrice();
                    computePointPrice(mFee);
                    refreshPrice(mPointCheck.isChecked());
                    if (coupons.isDiscount == 0) {
                        mCouponsTv.setText(StringUtils.price2String(coupons.price) + "元");
                    } else {
                        mCouponsTv.setText(StringUtils.price2String(coupons.price) + "折");
                    }
                }

            }
        });

        mPointCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshPrice(isChecked);
            }
        });
    }
    @SuppressLint("StringFormatMatches")
    private void refreshPrice(boolean isChecked) {
        if (isChecked) {
//            double tempPrice ;
//            if(mCoupons == null){
//                tempPrice = mCanUsePrice;
//            }else {
//                tempPrice = mCanUsePrice - mCoupons.price;
//            }
//            if(tempPrice < 0){
//                tempPrice = 0;
//            }
            mTotalPrice.setText("¥ " + StringUtils.roundingDoubleStr(mCanUsePrice / 100, 2));
            mPayPriceText.setText(StringUtils.roundingDoubleStr(mCanUsePrice / 100, 2) + "元");
        } else {
            LogUtils.d("isChecked  mFee:" + mFee  + StringUtils.roundingDoubleStr((double) mFee / 100, 2));
            mTotalPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mFee / 100, 2));
            mPayPriceText.setText(StringUtils.roundingDoubleStr((double) mFee / 100, 2) + "元");
        }
        mPonitConvert.setText(mResources.getString(R.string.point_tip, mCanUsePoint, StringUtils.roundingDoubleStr((double) mCanUsePoint * 0.05, 2)));
    }
    private void computeCouponsPrice() {

            mFee = mFee - mCoupons.price;
            if(mFee < 0){
                mFee = 0;
            }

    }

    private void getExtraData() {
        Intent intent = getIntent();
        mSkuId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mCount = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);
        mCartIds = intent.getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        mSelectCarts = intent.getParcelableArrayListExtra(IntentExtraConfig.EXTRA_PARCEL);
        LogUtils.d("mCartIds:" + mCartIds + "mSkuId:" + mSkuId);
    }

    /**
     * 拿到用户准备提交的信息，sku，地址等
     */
    private void getData() {
        showLoadingDialog();

            KDSPApiController.getInstance().confirmSubmitOrder(mCartIds, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    LogUtils.d("response:" + response);
                   refreshOrderData(response);
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    LogUtils.d("response  reqCode:" + reqCode);
                    dismissLoadingDialog();
                    showFailureMessage(error);
                    findViewById(content_lay).setVisibility(View.INVISIBLE);
                    findViewById(R.id.submit_lay).setVisibility(View.INVISIBLE);
                }
            });





    }

    /**
     * 根据订单信息刷新界面
     */
    private void refreshOrderData(JSONObject response) {
        mCouponsList = Coupons.parseListFromJSON(response.optJSONArray("myCoupons"));
        if (!mCouponsList.isEmpty()) {
            Coupons coupons = new Coupons();
            coupons.endTime = "不使用优惠券";
            mCouponsList.add(0, coupons);
        }

        AppContext.mPointSum = response.optInt("pointSum");
        if (mCoupons != null) {
            computeCouponsPrice();
        }
        MasterAuguryCarOrderConfirmActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
                refreshUI();
            }
        });
    }


    /**
     * 刷新界面
     */
    @SuppressLint("StringFormatMatches")
    private void refreshUI() {
        mSkuContainer.removeAllViews();
        LogUtils.d("---- mSelectCarts:" + mSelectCarts.size());

        for(AuguryCart item : mSelectCarts){
            if(item.mHasSelected){
                mOrderItems.add(item);
            }
        }

        LogUtils.d("---- mOrderItems:" + mOrderItems.size());
        int size = mOrderItems.size();


        for (final AuguryCart orderItem : mOrderItems) {
            View view = mInflater.inflate(R.layout.master_augury_order_sku_lay, null);
            YSRLDraweeView image = (YSRLDraweeView) view.findViewById(R.id.sku_image);
            UIUtils.display400Image(orderItem.imgUrl, image, R.drawable.def_loading_img);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView spec = (TextView) view.findViewById(R.id.spec);
            TextView price = (TextView) view.findViewById(R.id.price);

            title.setText(orderItem.itemName);
            spec.setText(orderItem.title);

            mFee += orderItem.price;
            String itemPrice ;
            if(size > 2){
                 itemPrice  = StringUtils.roundingDoubleStr((double) orderItem.price / 100 /2, 2);
            }else {
                 itemPrice  = StringUtils.roundingDoubleStr((double) orderItem.price / 100 , 2);
            }

            price.setText("¥ " + itemPrice);
            mSkuContainer.addView(view);
        }

        if(size > 2){
            mFee = mFee/2;
        }
        mTempPrice = mFee;
        computePointPrice(mFee);
        mPonitConvert.setText(getString(R.string.point_tip, mCanUsePoint, StringUtils.roundingDoubleStr((double) mCanUsePoint * 0.05, 2)));
        mTotalPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mFee / 100, 2));
        mPayPriceText.setText(StringUtils.roundingDoubleStr((double) mFee / 100, 2));
        if (mCouponsList.isEmpty()) {
            mCouponsLLayout.setVisibility(View.GONE);
            mCouponsLine.setVisibility(View.GONE);

        } else {
            mCouponsLLayout.setVisibility(View.VISIBLE);
            mCouponsLine.setVisibility(View.VISIBLE);
        }
    }

    private void readyToPay() {
        try {

            if (mPointCheck.isChecked() ) {
                mUsePoint = mCanUsePoint;
            } else {
                mUsePoint = 0;
            }
      if (AppContext.mPointSum * 5 >= mFee && (mPointCheck.isChecked()) || mFee == 0) {
                //福豆完全可以支付这次的订单且用户选择福豆支付则直接支付，否则增加人民币支付                    mActivity.showLoadingDialog();

                String couponsId = "";
                if (mCoupons != null) couponsId = mCoupons.mcId;


                KDSPApiController.getInstance().toPay(mCartIds, mUsePoint, couponsId, 3, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        MasterAuguryCarOrderConfirmActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MasterAuguryCarOrderConfirmActivity.this.dismissLoadingDialog();
                                paySuccess();
                            }
                        });
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        MasterAuguryCarOrderConfirmActivity.this.dismissLoadingDialog();
                        showFailureMessage(error);
                    }
                });


            } else {
                mPayLay.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void computePointPrice(int price) {
        if (AppContext.mPointSum * 5 >= price) {
            double count = StringUtils.roundingUpDouble((double) price / 5, 0);
            mCanUsePoint = (int) count;

        } else {
            mCanUsePoint = AppContext.mPointSum;

        }

        if (AppContext.mPointSum > 0) {
            findViewById(R.id.convert_lay).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.convert_lay).setVisibility(View.GONE);
        }

        mCanUsePrice = (double) (price - mCanUsePoint * 5) ;
        if (mCanUsePrice <= 0) {
            mCanUsePrice = 0.0;
        }

    }


    /**
     * 获取生成的订单信息
     */
    private void getOrderInfo() {
        String couponsId = "";
        if (mCoupons != null) {
            couponsId = mCoupons.mcId;
        }


       readyToPay();

    }





    /**
     * 显示支付view
     */
    private void submitOrder(JSONObject response) {
        mOrderIds = response.optString("orderIds");
        MasterAuguryCarOrderConfirmActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.pay_lay).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 支付成功
     */
    private void paySuccess() {
        //支付成功则减去对应的福豆
//        if (mPointCheck.isChecked()) {
//            AppContext.mPointSum = AppContext.mPointSum - mCanUsePoint;
//        }
        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
//        BroadcastUtils.sendPaySuccessBroadcast("");
        UIUtils.toastMsg("支付成功");
        //商品类型定义为100
        WeChatCouponsActivity.goToPage(this);
        finish();
    }

    @Override
    public <T> void setExtraData(T t) {
        if ("cancel".equals(t)) {
            if(!TextUtils.isEmpty(mOrderId)){
                KDSPApiController.getInstance().auguryCancelOrCloseOrder(mOrderId, new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(final JSONObject response) {

                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        dismissLoadingDialog();
                        showFailureMessage(error);
                    }
                });
            }
            AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
            mPayLay.setVisibility(View.GONE);
        }
    }

    @Override
    public void payWxSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            paySuccess();
        }
    }

    @Override
    public void payAliSuccessd(boolean isSuccess, String errStr) {
        if (isSuccess) {
            paySuccess();
        }
    }

    @Override
    public boolean onClickBackBtnEvent() {
        if (mPayLay.getVisibility() == View.VISIBLE) {
            showDialogFragment(PayCancelDialogFragment.newInstance(), "弹出取消对话框");
            return true;
        }
        return super.onClickBackBtnEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RequestCodeConfig.REQUEST_SELECT_ADDRESS) {
                //选择地址成功，包括直接添加或者列表选择


                getData();
            } else if (requestCode == RequestCodeConfig.REQUEST_BIND_PHONE_SUCCESS) {
                //绑定手机成功获取订单信息
                getOrderInfo();
            }
        }
    }

    public static void goToPage(Context context, String skuid, int count) {
        Intent intent = new Intent(context, MasterAuguryCarOrderConfirmActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, skuid);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, count);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String cartIds, ArrayList<AuguryCart> auguryCarts) {
        Intent intent = new Intent(context, MasterAuguryCarOrderConfirmActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, cartIds);
        intent.putParcelableArrayListExtra(IntentExtraConfig.EXTRA_PARCEL, auguryCarts);
        context.startActivity(intent);
    }
}
