package com.qizhu.rili.ui.fragment;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.InferringAdapter;
import com.qizhu.rili.bean.AdverInfo;
import com.qizhu.rili.bean.FateItem;
import com.qizhu.rili.bean.Theme;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.activity.GoodsListActivity;
import com.qizhu.rili.ui.activity.HandsOrFaceOrderActivity;
import com.qizhu.rili.ui.activity.HotAskListActivity;
import com.qizhu.rili.ui.activity.MasterAskActivity;
import com.qizhu.rili.ui.activity.MasterAuguryActivity;
import com.qizhu.rili.ui.activity.MemberShipActivity;
import com.qizhu.rili.ui.activity.MemberShipCardActivity;
import com.qizhu.rili.ui.activity.TenYearsFortuneActivity;
import com.qizhu.rili.ui.activity.TestNameActivity;
import com.qizhu.rili.ui.activity.YSRLWebActivity;
import com.qizhu.rili.ui.dialog.PayDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.MyPageView;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by lindow on 6/7/16.
 * 好运铺
 */
public class InferringFragment extends BaseFragment {
    public static boolean mNeedRefresh = false;             //是否需要刷新会员卡价格
    LinearLayout mHandsLlay;
    LinearLayout          mFaceLlay;
    LinearLayout          mTenYearsFortuneLlay;
    LinearLayout          mMasterAugurLlay;
    LinearLayout          mTestNameLlay;
    LinearLayout          mBagLlay;
    LinearLayout          mGoodItemsLlay;
    View                  mLine;
    PtrClassicFrameLayout mPtrFrame;
    private ScrollView        mContent;
    private YSRLDraweeView    mMemberCard;             //会员卡
    private TextView          mBalanceText;                  //会员卡余额
    private FitWidthImageView mMasterAugurThemeImage;
    private FitWidthImageView mGoodLuckThemeImage;
    private FitWidthImageView mMasterAskThemeImage;
    private FitWidthImageView mBagThemeImage;
    private TextView          mMasterAugurThemeName;
    private TextView          mGoodLuckThemeName;
    private TextView          mMasterAskThemeName;
    private TextView          mBagThemeName;
    private TextView          mMasterAugurThemeDes;
    private TextView          mGoodLuckThemeDes;
    private TextView          mMasterAskThemeDes;
    private TextView          mBagThemeDes;
    private LinearLayout      mContentLlayout;
    private KDSPRecyclerView  mMasterAugurView;      //大师亲算
    private KDSPRecyclerView  mGoodLuckView;         //开运物品
    private KDSPRecyclerView  mMasterAskView;        //大师问答
    private KDSPRecyclerView  mBagView;        //开运锦囊
    private View              mBadView;                //bad布局
    private View              mloadingView;        //loading
    private int               mBalance;               //会员卡余额，单位为分
    private String            mVipImage;           //展示图片
    private int              mVipStatus = -1;        //会员状态，当为0时，不是vip，1为vip状态正常，2为余额不足
    private ArrayList<Theme> mThemes    = new ArrayList<>();


    private LinearLayout mPageView;
    private MyPageView   mMyPageView;
    private List<AdverInfo> mAdverInfoList = new ArrayList<AdverInfo>();
    private boolean         isFirst        = true;
    private boolean         isUIReset      = false;
    private boolean         isHasAd        = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {

        return inflater.inflate(R.layout.inferrring_lay, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNeedRefresh) {
            getMemberShip();
        }
        if (isUIReset) {
            if (isHasAd) {
                mContent.scrollTo(0, 0);
            }

        }
    }

    private void initView() {
        mHandsLlay = mMainLay.findViewById(R.id.hands_llay);
        mFaceLlay = mMainLay.findViewById(R.id.face_llay);
        mTenYearsFortuneLlay = mMainLay.findViewById(R.id.ten_years_fortune_llay);
        mMasterAugurLlay = mMainLay.findViewById(R.id.master_augur_llay);
        mTestNameLlay = mMainLay.findViewById(R.id.test_name_llay);
        mBagLlay = mMainLay.findViewById(R.id.bag_llay);
        mGoodItemsLlay = mMainLay.findViewById(R.id.good_items_llay);
        mLine = mMainLay.findViewById(R.id.line);
        PtrClassicFrameLayout mPtrFrame = mMainLay.findViewById(R.id.ptr_frame);
        mMainLay.findViewById(R.id.hands_llay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterAskActivity.goToPage(mActivity,-1);
            }
        });

        mContent = (ScrollView) mMainLay.findViewById(R.id.content_lay);
        mContentLlayout = (LinearLayout) mMainLay.findViewById(R.id.content_llayout);
        mMasterAugurThemeImage = (FitWidthImageView) mMainLay.findViewById(R.id.master_item_image);
        mGoodLuckThemeImage = (FitWidthImageView) mMainLay.findViewById(R.id.luck_item_image);
        mMasterAskThemeImage = (FitWidthImageView) mMainLay.findViewById(R.id.ask_item_image);
        mBagThemeImage = (FitWidthImageView) mMainLay.findViewById(R.id.bag_item_image);
        mMasterAugurThemeName = (TextView) mMainLay.findViewById(R.id.master_item_name);
        mGoodLuckThemeName = (TextView) mMainLay.findViewById(R.id.luck_item_name);
        mMasterAskThemeName = (TextView) mMainLay.findViewById(R.id.ask_item_name);
        mBagThemeName = (TextView) mMainLay.findViewById(R.id.bag_item_name);
        mMasterAugurThemeDes = (TextView) mMainLay.findViewById(R.id.master_item_des);
        mGoodLuckThemeDes = (TextView) mMainLay.findViewById(R.id.luck_item_des);
        mMasterAskThemeDes = (TextView) mMainLay.findViewById(R.id.ask_item_des);
        mBagThemeDes = (TextView) mMainLay.findViewById(R.id.bag_item_des);
        mBadView = mMainLay.findViewById(R.id.bad_lay);
        mloadingView = mMainLay.findViewById(R.id.progress_lay);


        mMemberCard = (YSRLDraweeView) mMainLay.findViewById(R.id.membership_card);
        mBalanceText = (TextView) mMainLay.findViewById(R.id.price);

        mMasterAugurView = (KDSPRecyclerView) mMainLay.findViewById(R.id.master_view);
        mMasterAugurView.instanceForListView(LinearLayoutManager.HORIZONTAL, false);
        mGoodLuckView = (KDSPRecyclerView) mMainLay.findViewById(R.id.luck_view);
        mGoodLuckView.instanceForListView(LinearLayoutManager.HORIZONTAL, false);
        mMasterAskView = (KDSPRecyclerView) mMainLay.findViewById(R.id.ask_view);
        mMasterAskView.instanceForListView(LinearLayoutManager.HORIZONTAL, false);
        mBagView = (KDSPRecyclerView) mMainLay.findViewById(R.id.bag_view);
        mBagView.instanceForListView(LinearLayoutManager.HORIZONTAL, false);

        mPageView = (LinearLayout) mMainLay.findViewById(R.id.ll_page_view);

        mPtrFrame.setPullToRefresh(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getTheme();
                getMemberShip();
                getAdData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //是否可以下拉刷新
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }


        });

        mPtrFrame.getHeader().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.d("------onFocusChange");
            }
        });

        mPtrFrame.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {
                LogUtils.d("------onUIReset");
                if (isHasAd) {
                    mContent.scrollTo(0, 1);
                }
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });

        getTheme();
        getMemberShip();
        getAdData();

    }

    private void getAdData() {

        KDSPApiController.getInstance().findCarousel(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mAdverInfoList = AdverInfo.parseListFromJSON(response.optJSONArray("carousels"));
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdverInfoList.size() == 0) {
                            mPageView.setVisibility(View.GONE);
                            mLine.setVisibility(View.GONE);
                            isHasAd = false;
                        } else {
                            mPageView.setVisibility(View.VISIBLE);
                            mLine.setVisibility(View.VISIBLE);
                            setPagerView();
                            isHasAd = true;
                            mContent.scrollTo(0, 1);
                        }

                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    private void clickCarousel(String id) {

        KDSPApiController.getInstance().clickCarousel(id, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {

            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {

            }
        });
    }

    private void setPagerView() {
        mPageView.removeAllViews();

        View view = LayoutInflater.from(mActivity).inflate(
                R.layout.layout_slideshow, null);
        mPageView.addView(view);
        mMyPageView = new MyPageView(view, mActivity, mAdverInfoList, new MyPageView.AdClickListen() {
            @Override
            public void click(String id, String linkUrl) {
                clickCarousel(id);
                YSRLWebActivity.goToPage(mActivity, linkUrl);
            }
        });
    }


    private void getTheme() {
        mloadingView.setVisibility(View.VISIBLE);
        KDSPApiController.getInstance().getCustomFortuneData(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                LogUtils.d("response:", response.toString());
                mThemes = Theme.parseListFromJSON(response.optJSONArray("themes"));
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContentLlayout.setVisibility(View.VISIBLE);
                        mloadingView.setVisibility(View.GONE);
                        mBadView.setVisibility(View.GONE);
                        refreshUI();
                        mPtrFrame.refreshComplete();

                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContentLlayout.setVisibility(View.GONE);
                        mloadingView.setVisibility(View.GONE);
                        mBadView.setVisibility(View.VISIBLE);
                        mMainLay.findViewById(R.id.reload).setOnClickListener(new OnSingleClickListener() {
                            @Override
                            public void onSingleClick(View v) {
                                getTheme();
                                getMemberShip();
                            }
                        });
                    }
                });
            }
        });
    }

    //获取定制运势
    private void getMemberShip() {
        KDSPApiController.getInstance().getMembership(new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mBalance = response.optInt("balance");
                mVipStatus = response.optInt("vipStatus");
                mVipImage = response.optString("vipImage");
                LogUtils.d("mVipImage = " + mVipImage);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshMemberCard();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                mVipStatus = -1;
                showFailureMessage(error);
                refreshMemberCard();
            }
        });
    }

    private void refreshUI() {
        mloadingView.setVisibility(View.GONE);
        for (final Theme theme : mThemes) {
            if (theme.type == 1) {
                mMainLay.findViewById(R.id.master_item_lay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MasterAuguryActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(5, theme.mFateItem)).itemId);
                    }
                });

                mHandsLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        HandsOrFaceOrderActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(1, theme.mFateItem)).itemId, true);
                    }
                });

                mFaceLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        HandsOrFaceOrderActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(2, theme.mFateItem)).itemId, false);
                    }
                });

                mTestNameLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        TestNameActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(4, theme.mFateItem)).itemId, 0);
                    }
                });
                mTenYearsFortuneLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        TenYearsFortuneActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(3, theme.mFateItem)).itemId);
                    }
                });
                mMasterAugurLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        MasterAuguryActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(5, theme.mFateItem)).itemId);
//                        MasterAskDetailActivity.goToPage(mActivity, theme.mFateItem.get(getPositionByType(5, theme.mFateItem)).itemId);
                    }
                });


                UIUtils.displayImage(theme.imageUrl, mMasterAugurThemeImage, 400, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mMasterAugurThemeImage.setInfoHeight(AppContext.getScreenWidth(), imageInfo);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                });

                mMasterAugurThemeName.setText(theme.name);
                mMasterAugurThemeDes.setText(theme.desc);
//                mMainLay.findViewById(R.id.master_item_lay).setVisibility(View.VISIBLE);
//                mMasterAugurView.setVisibility(View.VISIBLE);
//                mMasterAugurView.setAdapter(new InferringAdapter(mActivity, theme.mFateItem, theme.type));
            } else if (theme.type == 2) {
                mMainLay.findViewById(R.id.luck_item_lay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoodsListActivity.goToPage(mActivity, true, theme.name, theme.type);
                    }
                });

                mGoodItemsLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        GoodsListActivity.goToPage(mActivity, true, getString(R.string.good_items), theme.type);
                    }
                });

                UIUtils.displayImage(theme.imageUrl, mGoodLuckThemeImage, 400, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mGoodLuckThemeImage.setInfoHeight(AppContext.getScreenWidth(), imageInfo);
                        LogUtils.d("---mGoodLuckThemeImage" + AppContext.getScreenWidth() + theme.imageUrl);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                });

                mGoodLuckThemeName.setText(theme.name);
                mGoodLuckThemeDes.setText(theme.desc);
                mMainLay.findViewById(R.id.luck_item_lay).setVisibility(View.VISIBLE);
                mGoodLuckView.setVisibility(View.VISIBLE);
                mGoodLuckView.setAdapter(new InferringAdapter(mActivity, theme.mGoods, theme.type));
            } else if (theme.type == 3) {
                mMainLay.findViewById(R.id.ask_item_lay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HotAskListActivity.goToPage(mActivity, theme.name);
                    }
                });

                UIUtils.displayImage(theme.imageUrl, mMasterAskThemeImage, 400, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mMasterAskThemeImage.setInfoHeight(AppContext.getScreenWidth(), imageInfo);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                });

                mMasterAskThemeName.setText(theme.name);
                mMasterAskThemeDes.setText(theme.desc);
                mMainLay.findViewById(R.id.ask_item_lay).setVisibility(View.VISIBLE);
                mMasterAskView.setVisibility(View.VISIBLE);
                mMasterAskView.setAdapter(new InferringAdapter(mActivity, theme.mFateItem, theme.type));
            } else if (theme.type == 0) {
                mMainLay.findViewById(R.id.bag_item_lay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoodsListActivity.goToPage(mActivity, true, theme.name, theme.type);
                    }
                });


                mBagLlay.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        GoodsListActivity.goToPage(mActivity, true, theme.name, theme.type);
                    }
                });

                UIUtils.displayImage(theme.imageUrl, mBagThemeImage, 400, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        mBagThemeImage.setInfoHeight(AppContext.getScreenWidth(), imageInfo);
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                    }
                });

                mBagThemeName.setText(theme.name);
                mBagThemeDes.setText(theme.desc);
                mMainLay.findViewById(R.id.bag_item_lay).setVisibility(View.VISIBLE);
                mBagView.setVisibility(View.VISIBLE);
                mBagView.setAdapter(new InferringAdapter(mActivity, theme.mGoods, theme.type));
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirst = false;
            }
        }, 5000);
        mContent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (isFirst) {
                    if (isHasAd) {
                        mContent.scrollTo(0, 1);
                        LogUtils.d("------onLayoutChange:" + String.valueOf(isFirst));
                    }
                }

            }
        });
//        ViewTreeObserver viewTreeObserver = mContent.getViewTreeObserver();
//        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (MethodCompat.isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
//                    mContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                } else {
//                    mContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                }
//                LogUtils.d("---> height = " + mContent.getHeight());
//            }
//        });
    }


    private int getPositionByType(int type, ArrayList<FateItem> mFateItem) {
        int position = 0;
        for (int i = 0, len = mFateItem.size(); i < len; i++) {

            if (type == mFateItem.get(i).type) {
                position = i;
                break;
            }

        }
        return position;
    }

    //刷新会员卡布局
    private void refreshMemberCard() {
        if (TextUtils.isEmpty(mVipImage)) {
            mMainLay.findViewById(R.id.membership_card_lay).setVisibility(View.GONE);
        } else {
            mMainLay.findViewById(R.id.membership_card_lay).setVisibility(View.VISIBLE);
            final int picWidth = AppContext.getScreenWidth() - DisplayUtils.dip2px(40);
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
                case 0:
                    mBalanceText.setVisibility(View.GONE);
                    mMainLay.findViewById(R.id.membership_card_lay).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            MemberShipActivity.goToPage(mActivity);
                        }
                    });
                    break;
                case 1:
                    mBalanceText.setVisibility(View.VISIBLE);
                    mBalanceText.setText("金额：" + StringUtils.splitDoubleStr((double) mBalance / 100, 2));
                    mMainLay.findViewById(R.id.membership_card_lay).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            MemberShipCardActivity.goToPage(mActivity);
                        }
                    });
                    break;
                case 2:
                    mBalanceText.setVisibility(View.GONE);
                    mMainLay.findViewById(R.id.membership_card_lay).setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            mActivity.showDialogFragment(PayDialogFragment.newInstance("", 0), "会员卡续费");
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
