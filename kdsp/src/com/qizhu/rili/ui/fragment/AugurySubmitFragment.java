package com.qizhu.rili.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.YSRLConstants;
import com.qizhu.rili.adapter.CouponsChooseAdapter;
import com.qizhu.rili.bean.Coupons;
import com.qizhu.rili.bean.DateTime;
import com.qizhu.rili.bean.Features;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.controller.QiNiuUploadCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.PaySuccessActivity;
import com.qizhu.rili.ui.dialog.PayDialogFragment;
import com.qizhu.rili.ui.dialog.SinglePickDialogFragment;
import com.qizhu.rili.ui.dialog.TimePickDialogFragment;
import com.qizhu.rili.utils.AliPayUtils;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.ChinaDateUtil;
import com.qizhu.rili.utils.DateUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.utils.WeixinUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by lindow on 08/05/2017.
 * 问题提交的fragment
 */

public class AugurySubmitFragment extends BaseFragment {
    @BindView(R.id.my_eight_word_tv)
    TextView     mMyEightWordTv;
    @BindView(R.id.my_gender_tv)
    TextView     mMyGenderTv;
    @BindView(R.id.it_eight_word_tv)
    TextView     mItEightWordTv;
    @BindView(R.id.it_gender_tv)
    TextView     mItGenderTv;
    @BindView(R.id.marriaged_llay)
    LinearLayout mMarriagedLlay;
    @BindView(R.id.no_marriage_no_lover)
    RadioButton  mNoMarriageNoLover;
    @BindView(R.id.no_marriage_have_lover)
    RadioButton  mNoMarriageHaveLover;
    @BindView(R.id.marriaged)
    RadioButton  mMarriaged;
    @BindView(R.id.relationship_group)
    RadioGroup   mRelationshipGroup;
    @BindView(R.id.marriage_price)
    TextView     mMarriagePrice;
    @BindView(R.id.marriage_llay)
    LinearLayout mMarriageLlay;
    Unbinder unbinder;
    private View              mNormalLay;                //正常布局
    private View              mBadLay;                   //失败布局
    public  View              mPayLay;                    //支付布局
    public  View              mRenewalsLay;               //续费布局
    private FitWidthImageView mBlurBg;      //背景
    private YSRLDraweeView    mAvatar;         //大师头像
    private TextView          mMasterName;           //大师
    private TextView          mItemName;             //问题名称
    private LinearLayout      mContainer;        //问题容器
    private TextView          mPriceText;            //价格
    private TextView          mPayPriceText;         //价格
    private TextView          mPriceNameTv;         //价格名字
    private ImageView         mWeixinSelect;        //微信
    private ImageView         mAliSelect;           //支付宝
    private ImageView         mArrowimage;           //优惠券箭头
    private TextView          mTip;                  //tip提示
    private TextView          mPointConvert;         //福豆转换
    public  CheckBox          mPointCheck;            //选中
    private TextView          mOldPrice;             //原价格
    private TextView          mConfirm;              //确认支付
    private TextView          mPoint;                //福豆
    private TextView          mBalanceText;          //余额
    private LinearLayout      mCouponsLLayout;   //优惠券
    private TextView          mCouponsTv;          //优惠券
    private ListView          mCouponsListView;    //优惠券
    private View              mCouponsLine;          //优惠券
    private View              mConvertLine;          //优惠券
    private boolean           isCouponsListShow;
    private int               mCurrentCoupons;          //当前选择的优惠券
    private int mMarriageStatus = 1; //1未婚无对象 2未婚有对象 3已婚

    private String  mItemId;                 //问题id
    private String  mLeftPath;               //左边图像
    private String  mRightPath;              //右边图像
    private String  mMsgId;                  //礼品id
    private int     mItemType;                  //问题类型
    private int     mItemSubType;               //额外类型
    private boolean mIsConvert;             //是否是兑换福豆
    private boolean mIsMarriage;             //是否是八字婚姻
    private int     mExtraMode;                 //额外的条件，1为string,2为nameType

    private String mQuestion;               //问题
    private String mBlurBgUrl;              //背景url
    private String mName;                   //大师名称
    private String mAvatarUrl;              //大师头像
    private int    mPrice;                     //价格
    private int    mTempPrice;                     //价格
    private String mPayMode;                //支付方式
    private int    mBalance;                   //会员余额
    private int    mVipStatus;                 //会员状态，当为0时，不是vip，1为vip状态正常，2为余额不足

    public TextView mCurrentText;           //当前点击的表单
    public Features mCurrentFeatures;       //当前选中的Features
    public int mClickType = 0;              //点击类别，0为问题，1为支付方式
    public  JSONObject jsonObject;           //支付参数
    private String     mLeftKey;                //左边图像的key
    private String     mRightKey;               //右边图像的key
    public int mCanUsePoint = 0;            //可用福豆
    public int mUsePoint    = 0;               //已经使用的福豆
    private double               mCanUsePrice;            //可支付金额
    public  String               mIoId;                    //当前的订单id
    public  boolean              mIsRenewals;             //是否续费
    public  String               mExtraString;            //额外的string
    private CouponsChooseAdapter mCouponsChooseAdapter;
    private Coupons              mCoupons;
    private String               mItEightWord;
    private String               mMyEightWord;
    private String               mMyGender;
    private String               mItGender;
    private DateTime mYourBirth;        //出生日期
    private int mYourBirthMode = 0;         //阴阳历
     private DateTime mMyEightWordBirth;        //出生日期
    private int mMyEightWordMode = 0;         //阴阳历
     private DateTime mItEightWordBirth;        //出生日期
    private int mItEightWordBirthMode = 0;         //阴阳历

    private ArrayList<Features> mFeatures    = new ArrayList<>();
    private ArrayList<Coupons>  mCouponsList = new ArrayList<>();

    private TimePickDialogFragment mMyTimePickDialog;
    private TimePickDialogFragment mMyEightWordTimePickDialog;
    private TimePickDialogFragment mItEightWordTimePickDialog;

    public static AugurySubmitFragment newInstance(String msgId, String itemId, boolean isConvert, String leftPath, String rightPath, int itemType) {
        AugurySubmitFragment fragment = new AugurySubmitFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, msgId);
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        bundle.putBoolean(IntentExtraConfig.EXTRA_IS_MINE, isConvert);
        bundle.putString(IntentExtraConfig.EXTRA_JSON, leftPath);
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, rightPath);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, itemType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AugurySubmitFragment newInstance(String msgId, String itemId, boolean isConvert, String leftPath, String rightPath, int itemType, int extra) {
        AugurySubmitFragment fragment = new AugurySubmitFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, msgId);
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        bundle.putBoolean(IntentExtraConfig.EXTRA_IS_MINE, isConvert);
        bundle.putString(IntentExtraConfig.EXTRA_JSON, leftPath);
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, rightPath);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, itemType);
        bundle.putInt(IntentExtraConfig.EXTRA_SHARE_CONTENT, extra);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AugurySubmitFragment newInstance(String msgId, String itemId, boolean isConvert, String leftPath, String rightPath, int itemType, int subType, int extra) {
        AugurySubmitFragment fragment = new AugurySubmitFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_GROUP_ID, msgId);
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        bundle.putBoolean(IntentExtraConfig.EXTRA_IS_MINE, isConvert);
        bundle.putString(IntentExtraConfig.EXTRA_JSON, leftPath);
        bundle.putString(IntentExtraConfig.EXTRA_PARCEL, rightPath);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, itemType);
        bundle.putInt(IntentExtraConfig.EXTRA_POSITION, subType);
        bundle.putInt(IntentExtraConfig.EXTRA_SHARE_CONTENT, extra);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static AugurySubmitFragment newInstance(String itemId, int itemType, int extra, int price) {
        AugurySubmitFragment fragment = new AugurySubmitFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentExtraConfig.EXTRA_ID, itemId);
        bundle.putInt(IntentExtraConfig.EXTRA_POST_ID, price);
        bundle.putInt(IntentExtraConfig.EXTRA_MODE, itemType);
        bundle.putInt(IntentExtraConfig.EXTRA_SHARE_CONTENT, extra);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mMsgId = bundle.getString(IntentExtraConfig.EXTRA_GROUP_ID);
            mItemId = bundle.getString(IntentExtraConfig.EXTRA_ID);
            mLeftPath = bundle.getString(IntentExtraConfig.EXTRA_JSON);
            mRightPath = bundle.getString(IntentExtraConfig.EXTRA_PARCEL);
            mItemType = bundle.getInt(IntentExtraConfig.EXTRA_MODE, 0);
            mIsConvert = bundle.getBoolean(IntentExtraConfig.EXTRA_IS_MINE, false);
            mItemSubType = bundle.getInt(IntentExtraConfig.EXTRA_POSITION, 0);
            mExtraMode = bundle.getInt(IntentExtraConfig.EXTRA_SHARE_CONTENT, 0);
//            mPrice = bundle.getInt(IntentExtraConfig.EXTRA_POST_ID, 0);
        }

        initView();
        mNormalLay.setVisibility(View.GONE);
        mBadLay.setVisibility(View.GONE);
        getQuestions();


    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.angury_submit_frag, container, false);
    }

    private void initView() {
        mNormalLay = mMainLay.findViewById(R.id.normal_lay);
        mBadLay = mMainLay.findViewById(R.id.bad_lay);
        mPayLay = mMainLay.findViewById(R.id.pay_lay);
        mRenewalsLay = mMainLay.findViewById(R.id.renewals_lay);

        mBlurBg = (FitWidthImageView) mMainLay.findViewById(R.id.blur_bg);
        mAvatar = (YSRLDraweeView) mMainLay.findViewById(R.id.master_avatar);
        mMasterName = (TextView) mMainLay.findViewById(R.id.master_name);
        mItemName = (TextView) mMainLay.findViewById(R.id.item_name);
        mContainer = (LinearLayout) mMainLay.findViewById(R.id.container);
        mPriceText = (TextView) mMainLay.findViewById(R.id.price);
        mPayPriceText = (TextView) mMainLay.findViewById(R.id.pay_price);
        mTip = (TextView) mMainLay.findViewById(R.id.tip);
        mPointConvert = (TextView) mMainLay.findViewById(R.id.point_convert);
        mPointCheck = (CheckBox) mMainLay.findViewById(R.id.point_check);
        mOldPrice = (TextView) mMainLay.findViewById(R.id.old_price);
        mConfirm = (TextView) mMainLay.findViewById(R.id.confirm);
        mPoint = (TextView) mMainLay.findViewById(R.id.point);
        mBalanceText = (TextView) mMainLay.findViewById(R.id.renewals_tip);
        mPriceNameTv = (TextView) mMainLay.findViewById(R.id.price_name_tv);
        mCouponsLLayout = (LinearLayout) mMainLay.findViewById(R.id.coupons_llayout);
        mCouponsTv = (TextView) mMainLay.findViewById(R.id.coupons_tv);
        mCouponsListView = (ListView) mMainLay.findViewById(R.id.coupons_listview);
        mArrowimage = (ImageView) mMainLay.findViewById(R.id.arrow_image);
        mCouponsLine = mMainLay.findViewById(R.id.coupons_line);
        mConvertLine = mMainLay.findViewById(R.id.convert_line);


        SpannableStringBuilder span = new SpannableStringBuilder();
        span.append("价格合计:   " + "¥ " + StringUtils.roundingDoubleStr((double) mPrice / 100, 2));
        span.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.black)), 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMarriagePrice.setText(span);
        mBlurBg.setDefheight(AppContext.getScreenWidth(), 750, 400);

        if (AppContext.mUser != null) {
            if(AppContext.mUser.isLunar == 0){
                mYourBirthMode = 0 ;
                mMyEightWordMode = 0;
                mItEightWordBirthMode = 0;
                mYourBirth = new DateTime(AppContext.mUser.birthTime);
                mMyEightWordBirth = new DateTime(AppContext.mUser.birthTime);
                mItEightWordBirth = new DateTime(AppContext.mUser.birthTime);

            }else {
                mYourBirthMode = 1 ;
                mMyEightWordMode = 1;
                mItEightWordBirthMode = 1;
                mYourBirth = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime)) ;
                mMyEightWordBirth = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime)) ;
                mItEightWordBirth = ChinaDateUtil.solarToLunar(new DateTime(AppContext.mUser.birthTime)) ;
            }

        }

        mMainLay.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getQuestions();
            }
        });


        mMyEightWordTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(mMyEightWordTimePickDialog == null){

                    mMyEightWordTimePickDialog = TimePickDialogFragment.newInstance(mMyEightWordBirth, TimePickDialogFragment.PICK_HOUR, mMyEightWordMode, true, new TimePickDialogFragment.TimePickListener() {
                        @Override
                        public void setPickDateTime(DateTime dateTime, int mode) {
                            if (0 == mode) {
                                mMyEightWordTv.setText("公历" + dateTime.toHourString() + "点");
                                mMyEightWord = DateUtils.getWebTimeFormatDate(dateTime.getDate());
                            } else {
                                mMyEightWordTv.setText("阴历" + dateTime.toHourString() + "点1");
                                mMyEightWord = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(dateTime).getDate());
                            }

                        }
                    });

                }

//                mActivity.showDialogFragment(mMyEightWordTimePickDialog, "我的八字设置日期");
            }
        });

        mItEightWordTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                if(mItEightWordTimePickDialog == null){

                    mItEightWordTimePickDialog = TimePickDialogFragment.newInstance(mItEightWordBirth, TimePickDialogFragment.PICK_HOUR, mItEightWordBirthMode, true, new TimePickDialogFragment.TimePickListener() {
                        @Override
                        public void setPickDateTime(DateTime dateTime, int mode) {
                            if (0 == mode) {
                                mItEightWordTv.setText("公历" + dateTime.toHourString() + "点");
                                mItEightWord = DateUtils.getWebTimeFormatDate(dateTime.getDate());
                            } else {
                                mItEightWordTv.setText("阴历" + dateTime.toHourString() + "点");
                                mItEightWord = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(dateTime).getDate());
                            }

                        }
                    });

                }
                mActivity.showDialogFragment(mItEightWordTimePickDialog, "Ta的八字设置日期");
            }
        });

        mMyGenderTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String s = "男,女";
                mActivity.showDialogFragment(SinglePickDialogFragment.newInstance("性别", s, new SinglePickDialogFragment.SinglePickListener() {
                    @Override
                    public void setExtraData(String data) {
                        mMyGenderTv.setText(data);
                        mMyGender = data;
                    }
                }), "设置单选");
            }
        });

        mItGenderTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String s = "男,女";
                mActivity.showDialogFragment(SinglePickDialogFragment.newInstance("性别", s, new SinglePickDialogFragment.SinglePickListener() {
                    @Override
                    public void setExtraData(String data) {
                        mItGenderTv.setText(data);
                        mItGender = data;
                    }
                }), "设置单选");
            }
        });


        mRelationshipGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.no_marriage_no_lover:
                        mMarriageStatus = 1;
                        mMarriagedLlay.setVisibility(View.GONE);
                        break;
                    case R.id.no_marriage_have_lover:
                        mMarriageStatus = 2;
                        mMarriagedLlay.setVisibility(View.VISIBLE);
                        break;
                    case R.id.marriaged:
                        mMarriageStatus = 3;
                        mMarriagedLlay.setVisibility(View.VISIBLE);
                        break;
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
                    mCouponsChooseAdapter = new CouponsChooseAdapter(mActivity, mCouponsList);
                    mCouponsListView.setAdapter(mCouponsChooseAdapter);
                }

            }
        });


        mConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mConfirm.setClickable(false);
                if (TextUtils.isEmpty(mLeftPath) && TextUtils.isEmpty(mRightPath)) {
                    readyToPay();
                    mConfirm.setClickable(true);
                } else {
                    File leftFile = new File(mLeftPath);
                    mLeftKey = KDSPApiController.getInstance().generateUploadKey(mLeftPath);

                    KDSPApiController.getInstance().uploadImageToQiNiu(mLeftKey, leftFile, new QiNiuUploadCallBack() {
                        @Override
                        public void handleAPISuccessMessage(JSONObject response) {
                            File rightFile = new File(mRightPath);
                            mRightKey = KDSPApiController.getInstance().generateUploadKey(mRightPath);
                            KDSPApiController.getInstance().uploadImageToQiNiu(mRightKey, rightFile, new QiNiuUploadCallBack() {
                                @Override
                                public void handleAPISuccessMessage(JSONObject response) {
                                    readyToPay();
                                    mConfirm.setClickable(true);
                                }

                                @Override
                                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                    UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                                    mConfirm.setClickable(true);
                                }
                            });
                        }

                        @Override
                        public void handleAPIFailureMessage(Throwable error, String reqCode) {
                            UIUtils.toastMsgByStringResource(R.string.http_request_failure);
                            mConfirm.setClickable(true);
                        }
                    });
                }
            }
        });

        mWeixinSelect = (ImageView) mMainLay.findViewById(R.id.weixin_selected);
        mAliSelect = (ImageView) mMainLay.findViewById(R.id.ali_selected);

        mMainLay.findViewById(R.id.weixin_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayMode = YSRLConstants.WEIXIN_PAY;
                mWeixinSelect.setImageResource(R.drawable.pay_selected);
                mAliSelect.setImageResource(R.drawable.pay_unselected);
            }
        });

        mMainLay.findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayMode = YSRLConstants.ALIPAY;
                mWeixinSelect.setImageResource(R.drawable.pay_unselected);
                mAliSelect.setImageResource(R.drawable.pay_selected);
            }
        });
        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mActivity.goBack();
            }
        });
        mMainLay.findViewById(R.id.pay_confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (TextUtils.isEmpty(mPayMode)) {
                    UIUtils.toastMsg("请选择支付方式");
                } else {
                    pay();
                }
            }
        });
        mMainLay.findViewById(R.id.blank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPayLay.setVisibility(View.GONE);
            }
        });
        mMainLay.findViewById(R.id.renewals_blank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRenewalsLay.setVisibility(View.GONE);
            }
        });
        mMainLay.findViewById(R.id.cancel_renewals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRenewalsLay.setVisibility(View.GONE);
            }
        });
        mMainLay.findViewById(R.id.renewals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsRenewals = true;
                mActivity.showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
            }
        });
        mPointCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshPrice(isChecked);
            }
        });

        mCouponsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCouponsListView.setVisibility(View.GONE);
                mArrowimage.setImageResource(R.drawable.arrow_up_coupons_gray);
                isCouponsListShow = false;
                Coupons coupons = (Coupons) mCouponsChooseAdapter.getItem(position);
                ;
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
                    mPrice = mTempPrice;
                    computePointPrice(mTempPrice);
                    refreshPrice(mPointCheck.isChecked());
                    mCouponsTv.setText(coupons.endTime);
                } else {
                    mPrice = mTempPrice;
                    computeCouponsPrice();
                    refreshPrice(mPointCheck.isChecked());
                    if (coupons.isDiscount == 0) {
                        mCouponsTv.setText(StringUtils.price2String(coupons.price) + "元");
                    } else {
                        mCouponsTv.setText(StringUtils.price2String(coupons.price) + "折");
                    }
                }
            }
        });


    }

    private void getQuestions() {
        mActivity.showLoadingDialog();
        KDSPApiController.getInstance().getItemInfoByItemId(mItemId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                LogUtils.d("---->" + response.toString());
                mName = response.optString("nickName");
                mAvatarUrl = response.optString("imageUrl");
                mQuestion = response.optString("itemName");
                mBlurBgUrl = response.optString("background");
                mPrice = response.optInt("price");
                mTempPrice = mPrice;
                AppContext.mPointSum = response.optInt("pointSum");
                mBalance = response.optInt("balance");
                mVipStatus = response.optInt("vipStatus");
                mFeatures = Features.parseListFromJSON(response.optJSONArray("features"));
                mCouponsList = Coupons.parseListFromJSON(response.optJSONArray("myCoupons"));
                if (!mCouponsList.isEmpty()) {
                    Coupons coupons = new Coupons();
                    coupons.endTime = "不使用优惠券";
                    mCouponsList.add(0, coupons);
                }

                //倒序排列
//                Collections.sort(mFeatures, new Comparator<Features>() {
//                    @Override
//                    public int compare(Features features, Features t1) {
//                        return features.sort < t1.sort ? 1 : -1;
//                    }
//                });

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.dismissLoadingDialog();
                        computePointPrice(mPrice);
                        refreshUI();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                mActivity.dismissLoadingDialog();
                showFailureMessage(error);
                mNormalLay.setVisibility(View.GONE);
                mBadLay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void refreshUI() {
        if (mCouponsList.isEmpty()) {
            mCouponsLLayout.setVisibility(View.GONE);
            mCouponsLine.setVisibility(View.GONE);

        } else {
            mCouponsLLayout.setVisibility(View.VISIBLE);
            mCouponsLine.setVisibility(View.VISIBLE);
        }

        mNormalLay.setVisibility(View.VISIBLE);
        mBadLay.setVisibility(View.GONE);

        UIUtils.displayBlurImage(mBlurBg, mBlurBgUrl, 600, 5, null);

        mMasterName.setText(mName);
        UIUtils.display200Image(mAvatarUrl, mAvatar, R.drawable.default_avatar);
        mItemName.setText(mQuestion);
        UIUtils.setThruLine(mOldPrice);
        mOldPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mPrice / 100, 2));
//        mPriceText.setText("¥ " + StringUtils.roundingDoubleStr((double) mPrice / 100, 2));
//        mPayPriceText.setText(StringUtils.roundingDoubleStr((double) mPrice / 100, 2) + "元");

        if (!mFeatures.isEmpty()) {
            int index = 0;
            for (final Features features : mFeatures) {
                if (features.inputType != 2) {
                    View itemLay = mInflater.inflate(R.layout.features_item_lay, null);
                    if (0 == index) {
                        itemLay.findViewById(R.id.divider_line).setVisibility(View.GONE);
                    }

                    itemLay.findViewById(R.id.feature_llay).setVisibility(View.VISIBLE);
                    TextView name = (TextView) itemLay.findViewById(R.id.feature_name);
                    RadioGroup radioGroup = (RadioGroup) itemLay.findViewById(R.id.relationship_group);
                    final TextView content = (TextView) itemLay.findViewById(R.id.feature_content);
                    final EditText editContent = (EditText) itemLay.findViewById(R.id.feature_edit);
                    name.setText(features.featureName);
                    if(mExtraMode == 3){
                        if(features.alias.equals("his_birthday") || features.alias.equals("his_sex")){
                            content.setHint(getString(R.string.marriage_hit));
                        }
                    }

                    if (features.inputType == 3) {
                        content.setVisibility(View.GONE);
                        editContent.setVisibility(View.VISIBLE);
                        editContent.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                mCurrentFeatures = features;
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                mClickType = 0;
                                mCurrentFeatures.content = editable.toString();
                            }
                        });
                    } else if (features.inputType == 4) {
                        itemLay.findViewById(R.id.feature_llay).setVisibility(View.GONE);
                        itemLay.findViewById(R.id.relationship_group).setVisibility(View.VISIBLE);
                        ((RadioButton) itemLay.findViewById(R.id.no_marriage_no_lover)).setChecked(true);
                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId) {
                                    case R.id.no_marriage_no_lover:
                                        mMarriageStatus = 1;
                                        break;
                                    case R.id.no_marriage_have_lover:
                                        mMarriageStatus = 2;
                                        break;
                                    case R.id.marriaged:
                                        mMarriageStatus = 3;
                                        break;
                                }
                            }
                        });

                    }else if((features.inputType == 0)){
                        features.dateTime = mYourBirth;
                        features.mode = mYourBirthMode;
                    }
                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCurrentText = content;
                            mCurrentFeatures = features;
                            if (features.inputType == 0) {
                                mActivity.showDialogFragment(TimePickDialogFragment.newInstance( features.dateTime, TimePickDialogFragment.PICK_ALL, features.mode, true,features.alias,
                                        new TimePickDialogFragment.TimePickListener() {
                                            @Override
                                            public void setPickDateTime(DateTime dateTime, int mode) {
                                                if (0 == mode) {
                                                    content.setText(getString(R.string.solar) + dateTime.toMinString() );
                                                    features.content = DateUtils.getWebTimeFormatDate(dateTime.getDate());
                                                } else {
                                                    content.setText(getString(R.string.lunar) + dateTime.toMinString() );
                                                    features.content = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(dateTime).getDate());
                                                }
                                                features.dateTime = dateTime;
                                                features.mode = mode;
                                            }
                                        }), "设置日期");
                            } else if (features.inputType == 1) {
                                mActivity.showDialogFragment(SinglePickDialogFragment.newInstance(features.featureName, features.selectValues), "设置单选");
                            }
                            mClickType = 0;
                        }
                    });

                    mContainer.addView(itemLay);
                    index++;
                }
            }
        }
        if (mExtraMode == 3) {
            mTip.setText(R.string.marriage_tip);
        } else {
            if (TextUtils.isEmpty(mLeftPath)) {
                mTip.setText(R.string.inferring_tip);
            } else {
                mTip.setText(R.string.master_inferring);
            }

        }


    }

    private void computeCouponsPrice() {
        if (mCoupons.isDiscount == 0) {
            mPrice = mPrice - mCoupons.price;

        } else {
            mPrice = mPrice * mCoupons.price / 1000;
        }

        if (0 >= mPrice) {
            mPrice = 0;
        }
        computePointPrice(mPrice);

    }

    @SuppressLint("StringFormatMatches")
    private void computePointPrice(int price) {
        if (AppContext.mPointSum * 5 >= price) {
            double count = StringUtils.roundingUpDouble((double) price / 5, 0);
            mCanUsePoint = (int) count;
            if (mIsConvert) {
                mPoint.setVisibility(View.VISIBLE);
                mPoint.setText(price / 5 + "福豆");
                mMainLay.findViewById(R.id.price_lay).setVisibility(View.INVISIBLE);
                mMainLay.findViewById(R.id.convert_lay).setVisibility(View.GONE);
                mMainLay.findViewById(R.id.convert_line).setVisibility(View.GONE);
            } else {
                mPoint.setVisibility(View.GONE);
                mMainLay.findViewById(R.id.price_lay).setVisibility(View.VISIBLE);
                mMainLay.findViewById(R.id.convert_lay).setVisibility(View.VISIBLE);
                mMainLay.findViewById(R.id.convert_line).setVisibility(View.VISIBLE);
            }
        } else {
            mCanUsePoint = AppContext.mPointSum;
            mMainLay.findViewById(R.id.convert_lay).setVisibility(View.VISIBLE);
            mMainLay.findViewById(R.id.convert_line).setVisibility(View.VISIBLE);
            mMainLay.findViewById(R.id.price_lay).setVisibility(View.VISIBLE);
            mPoint.setVisibility(View.GONE);
            if (mIsConvert) {
                mOldPrice.setVisibility(View.GONE);
            } else {
                mOldPrice.setVisibility(View.VISIBLE);
            }
        }

        if (AppContext.mPointSum > 0) {
            mMainLay.findViewById(R.id.convert_lay).setVisibility(View.VISIBLE);
            mMainLay.findViewById(R.id.convert_line).setVisibility(View.VISIBLE);
        } else {
            mMainLay.findViewById(R.id.convert_lay).setVisibility(View.GONE);
            mMainLay.findViewById(R.id.convert_line).setVisibility(View.GONE);
        }

        mCanUsePrice = (double) (price - mCanUsePoint * 5) / 100;
        if (mCanUsePrice <= 0) {
            mCanUsePrice = 0.0;
        }

        if (TextUtils.isEmpty(mMsgId)) {
            if (mIsConvert) {
                mConfirm.setText(R.string.confirm_convert);
                mPointCheck.setVisibility(View.GONE);
                mPriceText.setText("¥ " + StringUtils.roundingDoubleStr(mCanUsePrice, 2));
                mPayPriceText.setText(StringUtils.roundingDoubleStr(mCanUsePrice, 2) + "元");
                mPointConvert.setText(mResources.getString(R.string.point_convert, mCanUsePoint, StringUtils.roundingDoubleStr((double) mCanUsePoint * 0.05, 2)));
            } else {
                if (mVipStatus == 1) {
                    mPriceNameTv.setText(R.string.member_price);
                    mConfirm.setText(R.string.member_pay);
                } else {
                    mConfirm.setText(R.string.confirm_order);
                }

                mPointConvert.setText(mResources.getString(R.string.point_tip, mCanUsePoint, StringUtils.roundingDoubleStr((double) mCanUsePoint * 0.05, 2)));
                refreshPrice(mPointCheck.isChecked());
            }
        } else {
            mPriceNameTv.setText(R.string.member_price);
            mConfirm.setText(R.string.member_pay);
            mMainLay.findViewById(R.id.extra_lay).setVisibility(View.GONE);
        }
    }

    private void refreshPrice(boolean isChecked) {
        if (isChecked) {
            mOldPrice.setVisibility(View.VISIBLE);
            mPriceText.setText("¥ " + StringUtils.roundingDoubleStr(mCanUsePrice, 2));
            mPayPriceText.setText(StringUtils.roundingDoubleStr(mCanUsePrice, 2) + "元");
        } else {
            mOldPrice.setVisibility(View.GONE);
            mPriceText.setText("¥ " + StringUtils.roundingDoubleStr((double) mPrice / 100, 2));
            mPayPriceText.setText(StringUtils.roundingDoubleStr((double) mPrice / 100, 2) + "元");
        }
    }

    private void readyToPay() {
        try {
            jsonObject = new JSONObject();
            //额外拼接项
            if (mExtraMode == 1) {
                if (TextUtils.isEmpty(mExtraString)) {
                    UIUtils.toastMsg("请填写完所有问题");
                    return;
                } else {
                    jsonObject.put("askSth", mExtraString);
                }
            } else if (mExtraMode == 2) {
                jsonObject.put("nameType", mItemSubType);
            } else if (mExtraMode == 3) {
//                if(!TextUtils.isEmpty(mMyEightWordTv.getText().toString()) && !TextUtils.isEmpty(mMyGenderTv.getText().toString())&& mMarriageStatus != 0){
//                    if(mMarriageStatus != 1){
//                        if(TextUtils.isEmpty(mItEightWordTv.getText().toString()) || TextUtils.isEmpty(mItGenderTv.getText().toString())){
//                            UIUtils.toastMsg("请填写完所有问题");
//                            return;
//                        }else {
//                            jsonObject.put("your_birthday",mMyEightWord);
//                            jsonObject.put("your_sex",mMyGender);
//                            jsonObject.put("his_birthday",mItEightWord);
//                            jsonObject.put("his_sex",mItGender);
//                            jsonObject.put("has_married",mMarriageStatus == 2? getString(R.string.no_marriage_have_lover):getString(R.string.marriaged));
//                        }
//                    }else{
//                        jsonObject.put("your_birthday",mMyEightWord);
//                        jsonObject.put("your_sex",mMyGender);
//                        jsonObject.put("has_married",getString(R.string.no_marriage_no_lover));
//                    }
//                }else {
//                    UIUtils.toastMsg("请填写完所有问题");
//                    return;
//                }
            }
            if (mFeatures.size() != 0) {
                for (final Features features : mFeatures) {
                    if ("image_url1".equals(features.alias)) {
                        features.content = mLeftKey;
                    } else if ("image_url2".equals(features.alias)) {
                        features.content = mRightKey;
                    } else if ("familyName".equals(features.alias)) {
                        if (!StringUtils.isChineseStr(features.content)) {
                            UIUtils.toastMsg("姓中有非法字符");
                            return;
                        } else if (features.content.length() > 2) {
                            UIUtils.toastMsg("姓不符合规则");
                            return;
                        }
                    }
                    if (mMarriageStatus == 1) {
                        if ("his_birthday".equals(features.alias) || "his_sex".equals(features.alias)) {
                            continue;
                        }
                    }


                    if (TextUtils.isEmpty(features.content)) {
                        if ("has_married".equals(features.alias)) {
                            String content = getString(R.string.no_marriage_no_lover);

                            if (mMarriageStatus == 1) {
                                content = getString(R.string.no_marriage_no_lover);
                            } else if (mMarriageStatus == 2) {
                                content = getString(R.string.no_marriage_have_lover);
                            } else if (mMarriageStatus == 3) {
                                content = getString(R.string.marriaged);
                            }
                            jsonObject.put(features.alias, content);

                            continue;
                        }

                        UIUtils.toastMsg("请填写完所有问题");
                        return;
                    } else {
                        jsonObject.put(features.alias, features.content);
                    }
                }
            }
            if (TextUtils.isEmpty(mMsgId)) {
                //会员有余额走会员支付渠道
                if (mVipStatus == 1) {
                    int mNeedPrice;
                    if (mPointCheck.isChecked()) {
                        mNeedPrice = mPrice - mCanUsePoint * 5;
                        mUsePoint = mCanUsePoint;
                    } else {
                        mNeedPrice = mPrice;
                        mUsePoint = 0;
                    }
                    if (mBalance >= mNeedPrice) {
                        mActivity.showLoadingDialog();
                        String couponsId = "";
                        if (mCoupons != null) couponsId = mCoupons.mcId;

                        KDSPApiController.getInstance().membershipPay(mItemId, jsonObject.toString(), couponsId, mUsePoint, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        InferringFragment.mNeedRefresh = true;
                                        paySuccess();
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });
                    } else {
                        mBalanceText.setText("您的余额只有" + StringUtils.roundingDoubleStr((double) mBalance / 100, 2) + "元");
                        mRenewalsLay.setVisibility(View.VISIBLE);
                    }
                } else if (AppContext.mPointSum * 5 >= mPrice && (mPointCheck.isChecked() || mIsConvert) || mPrice == 0) {
                    //福豆完全可以支付这次的订单且用户选择福豆支付则直接支付，否则增加人民币支付                    mActivity.showLoadingDialog();
                    if (mIsConvert) {
                        KDSPApiController.getInstance().pointPay(mItemId, jsonObject.toString(), new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        paySuccess();
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });
                    } else {
                        String couponsId = "";
                        if (mCoupons != null) couponsId = mCoupons.mcId;
                        KDSPApiController.getInstance().membershipPay(mItemId, jsonObject.toString(), couponsId, mCanUsePoint, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mActivity.dismissLoadingDialog();
                                        paySuccess();
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                mActivity.dismissLoadingDialog();
                                showFailureMessage(error);
                            }
                        });
                    }

                } else {
                    mPayLay.setVisibility(View.VISIBLE);
                }
            } else {
                mActivity.showLoadingDialog();
                KDSPApiController.getInstance().exchangeMembershipGift(mMsgId, mItemId, jsonObject.toString(), new KDSPHttpCallBack() {
                    @Override
                    public void handleAPISuccessMessage(JSONObject response) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.dismissLoadingDialog();
                                paySuccess();
                            }
                        });
                    }

                    @Override
                    public void handleAPIFailureMessage(Throwable error, String reqCode) {
                        mActivity.dismissLoadingDialog();
                        showFailureMessage(error);
                    }
                });
            }

            mClickType = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pay() {
        if (mPointCheck.isChecked() || mIsConvert) {
            mUsePoint = mCanUsePoint;
        } else {
            mUsePoint = 0;
        }
        String couponsId = "";
        if (mCoupons != null) couponsId = mCoupons.mcId;
        if (YSRLConstants.WEIXIN_PAY.equals(mPayMode)) {
            mActivity.showLoadingDialog();


            KDSPApiController.getInstance().wechatPay(mItemId, jsonObject.toString(), mUsePoint, couponsId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(final JSONObject response) {
                    mIoId = response.optString("ioId");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.dismissLoadingDialog();
                            mIsRenewals = false;
                            WeixinUtils.getInstance().startPayByMM(mActivity, response);
                        }
                    });
                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    mActivity.dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });


        } else if (YSRLConstants.ALIPAY.equals(mPayMode)) {
            mActivity.showLoadingDialog();
            KDSPApiController.getInstance().aliPay(mItemId, jsonObject.toString(), mUsePoint, couponsId, new KDSPHttpCallBack() {
                @Override
                public void handleAPISuccessMessage(final JSONObject response) {
                    mIoId = response.optString("ioId");
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.dismissLoadingDialog();
                            mIsRenewals = false;
                            AliPayUtils.getInstance().startPay(mActivity, response);
                        }
                    });

                }

                @Override
                public void handleAPIFailureMessage(Throwable error, String reqCode) {
                    mActivity.dismissLoadingDialog();
                    showFailureMessage(error);
                }
            });
        }
    }

    private void paySuccess() {
        //支付成功则减去对应的福豆
        if (mPointCheck.isChecked() || mIsConvert) {
            AppContext.mPointSum = AppContext.mPointSum - mCanUsePoint;
        }
        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
        if (mItemType != 0) {
            BroadcastUtils.sendPaySuccessBroadcast("");
        }
        PaySuccessActivity.goToPage(mActivity, mItemType);
        mActivity.finish();
    }

    public <T> void setExtraData(T t) {
        if (mClickType == 0) {
            mCurrentText.setText(t.toString());
            mCurrentFeatures.content = t.toString();
        }
    }

    public void setPickDateTime(DateTime dateTime, int mode) {
        if (0 == mode) {
            mCurrentText.setText("公历" + dateTime.toHourString() + "点");
            mCurrentFeatures.content = DateUtils.getWebTimeFormatDate(dateTime.getDate());
        } else {
            mCurrentText.setText("阴历" + dateTime.toHourString() + "点");
            mCurrentFeatures.content = DateUtils.getWebTimeFormatDate(ChinaDateUtil.getSolarByDate(dateTime).getDate());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
