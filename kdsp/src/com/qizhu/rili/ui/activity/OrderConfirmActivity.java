package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
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
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.bean.OrderItem;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.PayCancelDialogFragment;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.AliPayUtils;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
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
 * Created by lindow on 09/03/2017.
 * 确认订单界面
 */

public class OrderConfirmActivity extends BaseActivity {
    private TextView     mName;
    private TextView     mPhone;
    private TextView     mAddressText;
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

    private int     mCanUsePoint;
    private String  mSkuId;
    private int     mCount;
    private String  mCartIds;
    private Address mAddress;
    private int     mFee;                       //总金额
    private int     mTempFee;                     //价格
    private int     mShipFee;                       //邮费
    private String  mPayMode;                //支付方式
    private ArrayList<OrderItem> mOrderItems = new ArrayList<>();
    private String               mOrderIds;
    private DateTime             mBirthDate;                //出生日期
    private boolean              isCouponsListShow;
    private CouponsChooseAdapter mCouponsChooseAdapter;
    private ArrayList<Coupons> mCouponsList = new ArrayList<>();
    private int        mCurrentCoupons;          //当前选择的优惠券
    private ScrollView mContentLay;
    private Coupons    mCoupons;
    private int mGoodsType ;
    private int mYourBirthMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirm_lay);
        initView();
        getExtraData();
        getData();
    }

    private void initView() {
        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.submit_order);

        mName = (TextView) findViewById(R.id.name);
        mPhone = (TextView) findViewById(R.id.phone);
        mAddressText = (TextView) findViewById(R.id.address);
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
            if(AppContext.mUser.isLunar == 0){
                mYourBirthMode = 0 ;
                mBirthDate = new DateTime(AppContext.mUser.birthTime);
            }else {
                mYourBirthMode = 1 ;
                mBirthDate = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime)) ;
            }

        }
        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });

        findViewById(R.id.enter_address).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AddAddressActivity.goToPageWithResult(OrderConfirmActivity.this, true);
            }
        });
        findViewById(R.id.address_lay).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AddressListActivity.goToPageWithResult(OrderConfirmActivity.this, true);
            }
        });


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddress == null) {
                    UIUtils.toastMsg("请填写收货地址");
                    return;
                }
                for (OrderItem orderItem : mOrderItems) {
                    if (orderItem.goodsType == 1) {
                        if (TextUtils.isEmpty(orderItem.birthday)) {
                            UIUtils.toastMsg("请填写出生日期");
                            return;
                        }
                    }
                }

                if (AppContext.mUser != null && !TextUtils.isEmpty(AppContext.mUser.telephoneNumber)) {
                    getOrderInfo();
                } else {
                    RegisterActivity.goToPageWithResult(OrderConfirmActivity.this, 1);
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
                    if (YSRLConstants.WEIXIN_PAY.equals(mPayMode)) {
                        showLoadingDialog();
                        KDSPApiController.getInstance().toPay(mOrderIds, 1, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                OrderConfirmActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();

                                        WeixinUtils.getInstance().startPayByMM(OrderConfirmActivity.this, response);

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
                        KDSPApiController.getInstance().toPay(mOrderIds, 2, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(final JSONObject response) {
                                OrderConfirmActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoadingDialog();
                                        AliPayUtils.getInstance().startPay(OrderConfirmActivity.this, response);
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
                    mCouponsChooseAdapter = new CouponsChooseAdapter(OrderConfirmActivity.this, mCouponsList);
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
                mArrowimage.setImageResource(R.drawable.arrow_up_coupons_gray);
                isCouponsListShow = false;
                Coupons coupons = (Coupons) mCouponsChooseAdapter.getItem(position);


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
                    mFee = mTempFee;
                    mCouponsTv.setText(coupons.endTime);
                } else {
                    mFee = mTempFee;
                    computeCouponsPrice();
                    if (coupons.isDiscount == 0) {
                        mCouponsTv.setText(StringUtils.price2String(coupons.price) + "元");
                    } else {
                        mCouponsTv.setText(StringUtils.price2String(coupons.price) + "折");
                    }
                }
                refreshUI();
            }
        });
    }

    private void computeCouponsPrice() {
        if (mCoupons.isDiscount == 0) {
            mFee = mFee - mCoupons.price;

        } else {
            mFee = (mFee - mShipFee) * mCoupons.price / 1000 + mShipFee;
        }

        if (0 >= mFee) {
            mFee = mShipFee;
        }


    }

    private void getExtraData() {
        Intent intent = getIntent();
        mSkuId = intent.getStringExtra(IntentExtraConfig.EXTRA_ID);
        mCount = intent.getIntExtra(IntentExtraConfig.EXTRA_POSITION, 0);
        mCartIds = intent.getStringExtra(IntentExtraConfig.EXTRA_GROUP_ID);
        LogUtils.d("mCartIds:" + mCartIds + "mSkuId:" + mSkuId);
    }

    /**
     * 拿到用户准备提交的信息，sku，地址等
     */
    private void getData() {
        showLoadingDialog();
        if (TextUtils.isEmpty(mCartIds)) {
            KDSPApiController.getInstance().confirmSubmitOrder(mSkuId, mCount, mAddress == null ? "" : mAddress.shipId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    refreshOrderData(response);
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                    findViewById(content_lay).setVisibility(View.INVISIBLE);
                    findViewById(R.id.submit_lay).setVisibility(View.INVISIBLE);
                }
            });
        } else {
            KDSPApiController.getInstance().confirmSubmitOrderByCart(mCartIds, mAddress == null ? "" : mAddress.shipId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    LogUtils.d("response:" + response);
                    refreshOrderData(response);
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    dismissLoadingDialog();
                    showFailureMessage(error);
                    findViewById(content_lay).setVisibility(View.INVISIBLE);
                    findViewById(R.id.submit_lay).setVisibility(View.INVISIBLE);
                }
            });
        }
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

        mAddress = Address.parseObjectFromJSON(response.optJSONObject("shipping"));
        mFee = response.optInt("totalFee");
        mTempFee = mFee;
        mShipFee = response.optInt("shipFee");
        if (mCoupons != null) {
            computeCouponsPrice();
        }
        mOrderItems = OrderItem.parseListFromJSON(response.optJSONArray("orderDetails"));
        OrderConfirmActivity.this.runOnUiThread(new Runnable() {
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
    private void refreshUI() {
        mSkuContainer.removeAllViews();
        refreshAddress();
        mTotalPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mFee / 100, 2));
        mPostage.setText("¥ " + StringUtils.roundingDoubleStr((double) mShipFee / 100, 2));
        mPayPriceText.setText(StringUtils.roundingDoubleStr((double) mFee / 100, 2));
        for (final OrderItem orderItem : mOrderItems) {
            View view = mInflater.inflate(R.layout.order_sku_lay, null);
            YSRLDraweeView image = (YSRLDraweeView) view.findViewById(R.id.sku_image);
            UIUtils.display400Image(orderItem.images[0], image, R.drawable.def_loading_img);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView spec = (TextView) view.findViewById(R.id.spec);
            TextView price = (TextView) view.findViewById(R.id.price);
            final TextView birthdayTv = (TextView) view.findViewById(R.id.birthday_tv);
            LinearLayout birthdayLlayout = (LinearLayout) view.findViewById(R.id.birthday_llayout);
            orderItem.dateTime = mBirthDate;
            orderItem.mode = mYourBirthMode;
            birthdayLlayout.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    showDialogFragment(TimePickDialogFragment.newInstance(orderItem.dateTime, TimePickDialogFragment.PICK_ALL, orderItem.mode, true , "my_birthday", new TimePickDialogFragment.TimePickListener() {
                        @Override
                        public void setPickDateTime(DateTime dateTime, int mode) {
                            String birthday;
                            if (0 == mode) {
                                birthdayTv.setText(getString(R.string.solar) + dateTime.toMinString() );
                                birthday = DateUtils.getWebTimeFormatDate(dateTime.getDate());
                            } else {
                                birthdayTv.setText(getString(R.string.lunar) + dateTime.toMinString() );
                                birthday = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(dateTime).getDate());
                            }
                            orderItem.birthday = birthday;
                            orderItem.dateTime = dateTime;
                            orderItem.mode = mode;
                        }
                    }), "设置生日");
                }
            });
            title.setText(orderItem.goodsName);

            if (orderItem.goodsType == 1) {
                birthdayLlayout.setVisibility(View.VISIBLE);
                mGoodsType = 1;
            } else {
                birthdayLlayout.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(orderItem.spec)) {
                spec.setText("数量：" + orderItem.count);
            } else {
                spec.setText(orderItem.spec + ",数量：" + orderItem.count);
            }
            price.setText("¥ " + StringUtils.roundingDoubleStr((double) orderItem.price / 100, 2));
            mSkuContainer.addView(view);
        }

        if (mCouponsList.isEmpty()) {
            mCouponsLLayout.setVisibility(View.GONE);
            mCouponsLine.setVisibility(View.GONE);

        } else {
            mCouponsLLayout.setVisibility(View.VISIBLE);
            mCouponsLine.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新地址
     */
    private void refreshAddress() {
        if (mAddress != null) {
            findViewById(R.id.enter_address).setVisibility(View.GONE);
            findViewById(R.id.address_lay).setVisibility(View.VISIBLE);
            mName.setText("收件人：" + mAddress.receiverName);
            mPhone.setText(mAddress.receiverMobile);
            mAddressText.setText(mAddress.province + mAddress.city + mAddress.area + mAddress.receiverAddress);
        } else {
            findViewById(R.id.enter_address).setVisibility(View.VISIBLE);
            findViewById(R.id.address_lay).setVisibility(View.GONE);
        }
    }

    /**
     * 获取生成的订单信息
     */
    private void getOrderInfo() {
        String couponsId = "" ;
        String remark ;
        remark = mRemarkTv.getText().toString();
        if(mCoupons != null){
            couponsId = mCoupons.mcId;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(remark);
        if(mGoodsType == 1){
            sb.append("（");
            for (int i=0;i<mOrderItems.size();i++){
                if(mOrderItems.get(i).goodsType == 1){
                    sb.append(" 生日：");
                    sb.append(mOrderItems.get(i).birthday);
                    if(i != mOrderItems.size() -1){
                        sb.append(" ,");
                    }
                }

            }

            sb.append(" ）");
        }

        if (TextUtils.isEmpty(mCartIds)) {

            KDSPApiController.getInstance().submitOrder(mSkuId, mCount, mAddress.shipId,couponsId,sb.toString(), new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    submitOrder(response);
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
        } else {
            KDSPApiController.getInstance().submitOrderByCart(mCartIds, mAddress.shipId,couponsId,sb.toString(), new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(JSONObject response) {
                    //刷新购物车数量
                    AppContext.mCartCount = response.optInt("count");
                    submitOrder(response);
                    BroadcastUtils.sendRefreshBroadcast(mCartIds);
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {

                }
            });
        }
    }


    @Override
    public void setPickDateTime(DateTime dateTime, int mode) {
//        SPUtils.putIntValue(YSRLConstants.USER_BIRTH_MODE + AppContext.userId, mode);
//        if (1 == mode) {
//            mBirthDate = dateTime;
//            mBirthdayTv.setText(R.string.birthday_solar);
//        } else {
//            mBirthDate = ChinaDateUtil.getSolarByDate(dateTime);
//            mBirthdayTv.setText(R.string.birthday_lunar);
//        }
//        mBirthdayTv.setText(dateTime.toMinString());


    }


    /**
     * 显示支付view
     */
    private void submitOrder(JSONObject response) {
        mOrderIds = response.optString("orderIds");
        OrderConfirmActivity.this.runOnUiThread(new Runnable() {
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
        PaySuccessActivity.goToPage(this, OrderDetail.COMPLETED);
        finish();
    }

    @Override
    public <T> void setExtraData(T t) {
        if ("cancel".equals(t)) {
            AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
            finish();
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
                mAddress = data.getParcelableExtra(IntentExtraConfig.EXTRA_JSON);
                refreshAddress();
                getData();
            } else if (requestCode == RequestCodeConfig.REQUEST_BIND_PHONE_SUCCESS) {
                //绑定手机成功获取订单信息
                getOrderInfo();
            }
        }
    }

    public static void goToPage(Context context, String skuid, int count) {
        Intent intent = new Intent(context, OrderConfirmActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, skuid);
        intent.putExtra(IntentExtraConfig.EXTRA_POSITION, count);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, String cartIds) {
        Intent intent = new Intent(context, OrderConfirmActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_GROUP_ID, cartIds);
        context.startActivity(intent);
    }
}
