package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autoscrollviewpager.CirclePageIndicator;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.ImagePagerAdapter;
import com.qizhu.rili.bean.Content;
import com.qizhu.rili.bean.Goods;
import com.qizhu.rili.bean.SKU;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.ui.dialog.SKUPickDialogFragment;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.MethodCompat;
import com.qizhu.rili.utils.ShareUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.FitWidthImageView;
import com.qizhu.rili.widget.LoopViewPager;
import com.qizhu.rili.widget.VerticalScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * Created by lindow on 22/02/2017.
 * 商品详情界面
 */

public class GoodsDetailActivity extends BaseActivity {
    public static LinkedHashMap<String, String> mSkuNameMap = new LinkedHashMap<>();      //保存sku属性名称的查询map,此列表必须保证顺序
    public static HashMap<String, SKU>          mSkuMap     = new HashMap<>();             //保存sku属性的查询map
    TextView     mPriceTipTv;
    TextView     mOldPrice;
    LinearLayout mOldPriceLl;

    private VerticalScrollView  mScrollView;
    private LoopViewPager       mImagePager;
    private CirclePageIndicator mCirclePageIndicator;
    private TextView            mGoodsTitle;
    private TextView            mGoodsPrice;
    private TextView            mSellPoint;
    private TextView            mAddCart;              //添加购物车
    private TextView            mBuy;                  //购买
    private TextView            mSoldOut;              //下架
    private LinearLayout        mDetailContainer;

    private String            mGoodsId;
    private Goods             mGoods;
    private ImagePagerAdapter mImagePagerAdapter;
    private ArrayList<SKU> mSkus = new ArrayList<>();
    private int     mHeight;                                                        //商品图文详情的高度
    private boolean mScrollDirection;                                           //滑动方向，true为上滑

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_detail_lay);
        mGoodsId = getIntent().getStringExtra(IntentExtraConfig.EXTRA_ID);
        initView();
        getData();
    }

    private void initView() {
        mPriceTipTv = findViewById(R.id.price_tip_tv);
        mOldPrice = findViewById(R.id.old_price);
        mOldPriceLl = findViewById(R.id.old_price_ll);

        TextView mTitle = (TextView) findViewById(R.id.title_txt);
        mTitle.setText(R.string.goods_detail);

        mScrollView = (VerticalScrollView) findViewById(R.id.content_scroll);
        mImagePager = (LoopViewPager) findViewById(R.id.image_pager);
        mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.page_indicator);
        mGoodsTitle = (TextView) findViewById(R.id.title);
        mGoodsPrice = (TextView) findViewById(R.id.price);
        mSellPoint = (TextView) findViewById(R.id.sell_point);
        mAddCart = (TextView) findViewById(R.id.add_to_shopping_cart);
        mBuy = (TextView) findViewById(R.id.buy_now);
        mSoldOut = (TextView) findViewById(R.id.sold_out);
        mDetailContainer = (LinearLayout) findViewById(R.id.detail_container);

        mScrollView.setListenEvents(false, true);
        mScrollView.setScrollViewListener(new VerticalScrollView.ScrollDistanceListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                LogUtils.d("onScrollChanged l = " + l + ",t=" + t + ",oldl=" + oldl + ",oldt=" + oldt);
                if (t > oldt) {
                    mScrollDirection = true;
                } else {
                    mScrollDirection = false;
                }
            }

            @Override
            public void onScrollTop(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollBottom(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollEnd() {
                if (mScrollView.getScrollY() < mHeight) {
                    //上滑
                    if (mScrollDirection) {
                        mScrollView.smoothScrollTo(0, mHeight);
                        findViewById(R.id.header).setVisibility(View.VISIBLE);
                        findViewById(R.id.cart_lay).setVisibility(View.GONE);
                    } else {
                        mScrollView.smoothScrollTo(0, 0);
                        findViewById(R.id.header).setVisibility(View.GONE);
                        findViewById(R.id.cart_lay).setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.back_circle).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                goBack();
            }
        });
        findViewById(R.id.shopping_cart).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CartListActivity.goToPage(GoodsDetailActivity.this);
            }
        });
        findViewById(R.id.shopping_cart_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                CartListActivity.goToPage(GoodsDetailActivity.this);
            }
        });
        findViewById(R.id.share).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                shareGoods();
            }
        });
        findViewById(R.id.share_btn).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                shareGoods();
            }
        });

        findViewById(R.id.buyer_message).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MessageListActivity.goToPage(GoodsDetailActivity.this, mGoods);
            }
        });
        mAddCart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mGoods.status == 1) {
                    showDialogFragment(SKUPickDialogFragment.newInstance(mGoods, false), "选择sku添加购物车");
                }
            }
        });
        mBuy.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mGoods.status == 1) {
                    showDialogFragment(SKUPickDialogFragment.newInstance(mGoods, true), "选择sku下单");
                }
            }
        });
        findViewById(R.id.header).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        findViewById(R.id.header).setVisibility(View.GONE);
    }

    private void getData() {
        showLoadingDialog();
        KDSPApiController.getInstance().getGoodsDetailsById(mGoodsId, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                mGoods = Goods.parseObjectFromJSON(response.optJSONObject("goods"));
                mSkuNameMap.clear();
                JSONArray array = response.optJSONArray("attrs");
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = array.optJSONObject(i);
                    Iterator<String> keyItr = jsonObject.keys();
                    while (keyItr.hasNext()) {
                        String key = keyItr.next();
                        String name = jsonObject.optString(key);
                        mSkuNameMap.put(key, name);
                    }
                }
                LogUtils.d("-------------> mSkuNameMap = " + mSkuNameMap);

                JSONArray jsonArray = response.optJSONArray("skus");
                mSkus = SKU.parseListFromJSON(jsonArray);
                mSkuMap.clear();
                //遍历sku
                for (SKU sku : mSkus) {
                    //sku没有名称，为通用sku，那么直接存储为特殊的key
                    if (TextUtils.isEmpty(sku.skuName)) {
                        mSkuMap.put("kdsp", sku);
                    } else {
                        String[] attrs = sku.skuName.split("-");
                        List<String> arrayList = Arrays.asList(attrs);

                        for (String str : getAttr(arrayList)) {
                            //若能精确定位sku，则放入精确的，否则放入虚拟的sku
                            if (str.equals(sku.skuName)) {
                                mSkuMap.put(str, sku);
                            } else {
                                SKU temp = new SKU();
                                temp.stock = sku.stock;
                                mSkuMap.put(str, temp);
                            }
                        }
                    }
                    //由于服务端不返回最高最低价格，所以遍历sku的时候赋值价格
                    if (mGoods.minPrice > sku.price || mGoods.minPrice == 0) {
                        mGoods.minPrice = sku.price;
                    }
                    if (mGoods.maxPrice < sku.price) {
                        mGoods.maxPrice = sku.price;
                    }
                }

                LogUtils.d("-------------> mSkuMap = " + mSkuMap);
                GoodsDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
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
        mImagePagerAdapter = new ImagePagerAdapter(GoodsDetailActivity.this, Arrays.asList(mGoods.images));
        mImagePager.setAdapter(mImagePagerAdapter);
        mCirclePageIndicator.setViewPager(mImagePager);
        mGoodsTitle.setText(mGoods.title);
        if (mGoods.minPrice == mGoods.maxPrice) {
            mGoodsPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mGoods.minPrice / 100, 2));
        } else {
            mGoodsPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mGoods.minPrice / 100, 2) + "-" + StringUtils.roundingDoubleStr((double) mGoods.maxPrice / 100, 2));
        }
        SKU sku = null;
        if(mSkus.size() != 0){
             sku = mSkus.get(0);
        }

        if(sku != null){
            if(sku.price == sku.originalPrice){
                mPriceTipTv.setVisibility(View.GONE);
                mOldPriceLl.setVisibility(View.GONE);
            }else {
                if(TextUtils.isEmpty(sku.tip)){
                    mPriceTipTv.setVisibility(View.GONE);
                }else {
                    mPriceTipTv.setVisibility(View.VISIBLE);
                }

                mOldPriceLl.setVisibility(View.VISIBLE);
                mPriceTipTv.setText(sku.tip);
                mOldPrice.setText("¥ "+ StringUtils.roundingDoubleStr((double) sku.originalPrice / 100, 2));
                UIUtils.setThruLine(mOldPrice);
            }
        }
        mSellPoint.setText(mGoods.sellPoint);

        if (mGoods.status != 1) {
            mSoldOut.setVisibility(View.VISIBLE);
            mAddCart.setTextColor(ContextCompat.getColor(this, R.color.white_transparent_50));
            mBuy.setTextColor(ContextCompat.getColor(this, R.color.white_transparent_50));
        } else {
            mSoldOut.setVisibility(View.GONE);
            mAddCart.setTextColor(ContextCompat.getColor(this, R.color.white));
            mBuy.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        for (Content content : mGoods.contents) {
            if (content.type == 2) {
                final FitWidthImageView fitWidthImageView = new FitWidthImageView(this);
                fitWidthImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                UIUtils.display600Image(content.content, fitWidthImageView, R.drawable.def_loading_img, new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        fitWidthImageView.setInfoHeight(AppContext.getScreenWidth(), imageInfo);
                    }
                });
                mDetailContainer.addView(fitWidthImageView);
            } else {
                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText(content.content);
                textView.setTextSize(16);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(ContextCompat.getColor(this, R.color.gray6));
                mDetailContainer.addView(textView);
            }
        }

        //这个时候获取滑动的高度会更准确，因为自适应高度的view都填充了内容
        ViewTreeObserver viewTreeObserver = mDetailContainer.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (MethodCompat.isCompatible(Build.VERSION_CODES.JELLY_BEAN)) {
                    mDetailContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mDetailContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                mHeight = mDetailContainer.getTop() - DisplayUtils.dip2px(50);
            }
        });
    }

    private void shareGoods() {
        if (mGoods.goodsType == 0) {
            //"开运手串随身带，好运时刻伴身边"
            ShareActivity.goToMiniShare(GoodsDetailActivity.this, mGoods.title, "我已经带上【适合" + mGoods.tag + "】的开光" + mGoods.title + "，不要再羡慕我好运连连了", "http://h5.ishenpo.com/app/share/goods_item?goodsId="  + mGoods.goodsId, mGoods.images[0], ShareUtils.Share_Type_Goods, "","pages/decrypt/goods/item?goodsId=" + mGoods.goodsId);
        } else {
            //"符到即福到，开光神符保佑你"
            ShareActivity.goToMiniShare(GoodsDetailActivity.this, mGoods.title, "连山易大师神符，保你万事如意", "http://h5.ishenpo.com/app/share/goods_item?goodsId=" + mGoods.goodsId, mGoods.images[0], ShareUtils.Share_Type_Goods, "","pages/decrypt/goods/item?goodsId=" + mGoods.goodsId);
        }

    }


    /**
     * 分割数组，获取可能的组合
     * 递归获取所有可能的属性组合
     */
    private List<String> getAttr(List<String> attrs) {
        ArrayList<String> result = new ArrayList<>();
        int size = attrs.size();
        if (attrs.size() > 1) {
            String last = attrs.get(size - 1);
            result.add(last);
            //再加上前一个子序列加上的递归
            ArrayList<String> sublist = new ArrayList<>();
            sublist.addAll(attrs);
            //子序列去除最后一个属性
            sublist.remove(size - 1);
            for (String str : getAttr(sublist)) {
                result.add(str);
                result.add(str + "-" + last);
            }
        } else {
            result.add(attrs.get(0));
        }
        LogUtils.d("------> result = " + result);
        return result;
    }

    public static void goToPage(Context context, String goodsId) {
        Intent intent = new Intent(context, GoodsDetailActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_ID, goodsId);
        context.startActivity(intent);
    }
}
