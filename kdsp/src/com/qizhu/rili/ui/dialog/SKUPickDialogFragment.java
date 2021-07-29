package com.qizhu.rili.ui.dialog;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.bean.Goods;
import com.qizhu.rili.bean.SKU;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.listener.RefreshListener;
import com.qizhu.rili.ui.activity.GoodsDetailActivity;
import com.qizhu.rili.ui.activity.OrderConfirmActivity;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.SKUChooseLayout;
import com.qizhu.rili.widget.YSRLDraweeView;

import org.json.JSONObject;

/**
 * Created by lindow on 02/03/2017.
 * sku的选择对话框
 */

public class SKUPickDialogFragment extends BaseDialogFragment {
    private SKUChooseLayout mSkuChoose;            //sku的选择布局
    private TextView mPrice;                        //价格
    private TextView mCountText;                    //数量
    private TextView mPickCount;                   //选择数量

    private Goods mGoods;
    private boolean mBuy;                           //是否购买
    private int mCount = 1;

    private Toast mToast;                           //单独的toast

    public static SKUPickDialogFragment newInstance(Goods goods, boolean buy) {
        SKUPickDialogFragment rtn = new SKUPickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(IntentExtraConfig.EXTRA_PARCEL, goods);
        bundle.putBoolean(IntentExtraConfig.EXTRA_MODE, buy);
        rtn.setArguments(bundle);
        return rtn;
    }

    @Override
    public View inflateMainView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sku_pick_lay, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mGoods = getArguments().getParcelable(IntentExtraConfig.EXTRA_PARCEL);
            mBuy = getArguments().getBoolean(IntentExtraConfig.EXTRA_MODE, true);
        }
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        //将对话框置于底部,设置背景为透明
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = AppContext.getScreenWidth();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
        window.setBackgroundDrawableResource(R.color.transparent);
    }

    private void initView() {
        mSkuChoose = (SKUChooseLayout) mMainLay.findViewById(R.id.sku_choose_lay);

        YSRLDraweeView mImage = (YSRLDraweeView) mMainLay.findViewById(R.id.sku_image);
        mPrice = (TextView) mMainLay.findViewById(R.id.sku_price);
        mCountText = (TextView) mMainLay.findViewById(R.id.sku_count);
        mPickCount = (TextView) mMainLay.findViewById(R.id.count);

        UIUtils.display400Image(mGoods.images[0], mImage, R.drawable.def_loading_img);
        if (mGoods.minPrice == mGoods.maxPrice) {
            mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mGoods.minPrice / 100, 2));
        } else {
            mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mGoods.minPrice / 100, 2) + "-" + StringUtils.roundingDoubleStr((double) mGoods.maxPrice / 100, 2));
        }

        mCountText.setText("库存：" + mCount + "件");

        if (GoodsDetailActivity.mSkuNameMap.isEmpty()) {
            mMainLay.findViewById(R.id.divider_line).setVisibility(View.GONE);
        }
        mMainLay.findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCount > 1) {
                    mCount = mCount - 1;
                    mPickCount.setText("" + mCount);
                }
            }
        });

        mMainLay.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCount = mCount + 1;
                mPickCount.setText("" + mCount);
            }
        });

        mMainLay.findViewById(R.id.confirm).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                SKU sku = mSkuChoose.getSelectedSku();
                if (sku == null) {
                    UIUtils.toastMsg("请选择商品属性~");
                } else {
                    if (mBuy) {
                        OrderConfirmActivity.goToPage(mActivity, sku.skuId, mCount);
                    } else {
                        KDSPApiController.getInstance().addCart(sku.skuId, mCount, new KDSPHttpCallBack() {
                            @Override
                            public void handleAPISuccessMessage(JSONObject response) {
                                //刷新购物车数量
                                AppContext.mCartCount = response.optInt("count");
                                //弹出添加成功提示
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAddCartToast();
                                    }
                                });
                            }

                            @Override
                            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                                showFailureMessage(error);
                            }
                        });
                    }
                    dismiss();
                }
            }
        });

        mMainLay.findViewById(R.id.cancel).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dismiss();
            }
        });

        mSkuChoose.setAttrs(GoodsDetailActivity.mSkuNameMap, GoodsDetailActivity.mSkuMap, new RefreshListener() {
            @Override
            public void refresh(int position) {
                refreshSKU();
            }
        });
    }

    private void refreshSKU() {
        SKU sku = mSkuChoose.getSelectedSku();
        if (sku != null && !TextUtils.isEmpty(sku.skuId)) {
            mPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) sku.price / 100, 2));
            mCountText.setText("库存：" + sku.stock + "件");
            mMainLay.findViewById(R.id.confirm).setClickable(true);
            mMainLay.findViewById(R.id.confirm).setBackgroundColor(ContextCompat.getColor(mActivity, R.color.purple31));
        } else {
            mMainLay.findViewById(R.id.confirm).setClickable(false);
            mMainLay.findViewById(R.id.confirm).setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gray));
        }
    }

    /**
     * 显示添加成功提示
     */
    private void showAddCartToast() {
        if (mToast == null) {
            mToast = new Toast(AppContext.baseContext);
            LayoutInflater inflater = LayoutInflater.from(AppContext.baseContext);
            View view = inflater.inflate(R.layout.add_success_lay, null);
            if (view != null) {
                TextView text = (TextView) view.findViewById(R.id.content);
                text.setText("添加成功!");
                mToast.setView(view);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            }
        }

        mToast.show();
        LogUtils.d("---> start time ");

        mMainLay.postDelayed(new Runnable() {
            @Override
            public void run() {
                mToast.cancel();
                LogUtils.d("---> end time ");
            }
        }, 1000);
    }
}
