package com.qizhu.rili.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.HeaderAndFooterRecyclerViewAdapter;
import com.qizhu.rili.adapter.MasterAuguryCartListAdapter;
import com.qizhu.rili.bean.AuguryCart;
import com.qizhu.rili.controller.KDSPApiController;
import com.qizhu.rili.controller.KDSPHttpCallBack;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;
import com.qizhu.rili.utils.UIUtils;
import com.qizhu.rili.widget.KDSPRecyclerView;
import com.qizhu.rili.widget.ListViewHead;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhouyue on 18/04/2018.
 * 大师亲算购买
 */

public class MasterAuguryCartActivity extends BaseListActivity {
    private TextView  mTotalPrice;
    private TextView  mOldPrice;
    private TextView  mSubmit;
    private ImageView mImage;
    private Set<AuguryCart>       mSelectCarts = new HashSet<>();       //选择的购物车项
    private int                   mPrice       = 0;
    private ArrayList<AuguryCart> mItems       = new ArrayList<>();
    private ArrayList<AuguryCart>  mCarts       = new ArrayList<>();
    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mType = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 1);
        super.onCreate(savedInstanceState);
        mSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        mSubmit.setClickable(false);
        if (mType == 1) {
            mImage.setBackgroundResource(R.drawable.master_augury_banner1);
        } else if (mType == 2) {
            mImage.setBackgroundResource(R.drawable.master_augury_banner2);
        } else if (mType == 3) {
            mImage.setBackgroundResource(R.drawable.master_augury_banner3);
        }

    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
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
            mTitle.setText(getString(R.string.master_augur));

            titleView.addView(view, params);
        }
    }


    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }


    @Override
    protected void initAdapter() {
        if (mAdapter == null) {

            mAdapter = new MasterAuguryCartListAdapter(this, mItems);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        }
    }

    @Override
    protected void addHeadView(KDSPRecyclerView refreshableView, View view) {
        view = new ListViewHead(this, R.layout.master_augur_cart_head_lay);
        mImage = (ImageView) view.findViewById(R.id.master_item_image);
        super.addHeadView(refreshableView, view);

    }

    @Override
    protected void initBottomView(RelativeLayout bottomView) {
        View mBottomView = mInflater.inflate(R.layout.master_augur_cart_bottom_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DisplayUtils.dip2px(50));
        if (mBottomView != null) {
            mTotalPrice = (TextView) mBottomView.findViewById(R.id.total_price);
            mOldPrice = (TextView) mBottomView.findViewById(R.id.old_price);
            mTotalPrice.setText("合计：¥ " + StringUtils.roundingDoubleStr((double) mPrice * 0.01, 2));
            UIUtils.setThruLine(mOldPrice);
            mOldPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mPrice * 0.01, 2));
            mSubmit = (TextView) mBottomView.findViewById(R.id.submit);

            mSubmit.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    StringBuffer cartIds = new StringBuffer();
                    JSONObject jsonObject = new JSONObject();
                    List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
                    for (AuguryCart cart : mSelectCarts) {

                        JSONObject json = new JSONObject();
                        try {
                            json.put("itemId", cart.itemId);
                            String count ;
                            if(cart.type == 2){
                                count = "" + cart.price / cart.perPrice;
                            }else {
                                count = "" + 1;
                            }
                            json.put("count", count);
                            jsonObjects.add(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        cartIds.append(cart.cartId).append(",");
                    }

                    ArrayList<AuguryCart> auguryCarts = new ArrayList<>();
                    for (AuguryCart auguryCart : mSelectCarts) {
                        auguryCarts.add(auguryCart);
                    }


                    MasterAuguryCarOrderConfirmActivity.goToPage(MasterAuguryCartActivity.this, jsonObjects.toString(), mCarts);
                }
            });

            mBottomView.setVisibility(View.VISIBLE);
            bottomView.addView(mBottomView, params);
        }
    }

    public void deleteCart(ArrayList<AuguryCart> cart) {
//        mSelectCarts.remove(cart);
        refreshPrice(cart);
    }

    public void addCart(ArrayList<AuguryCart>  cart1) {
//        mSelectCarts.add(cart);
        refreshPrice(cart1);
    }


    public void refreshPrice(ArrayList<AuguryCart> carts) {
        mPrice = 0;
        mCarts = carts;
        mSelectCarts.clear();
            for (AuguryCart cart : carts) {
                if(cart.mHasSelected){
                    mPrice = cart.price + mPrice;
                    mSelectCarts.add(cart);
                }

                LogUtils.d("---- mSelectCarts" + cart.price );
            }
            if (mSelectCarts.size() >= 3) {
                mOldPrice.setText("¥ " + StringUtils.roundingDoubleStr((double) mPrice /100, 2));
                mOldPrice.setVisibility(View.VISIBLE);
                mPrice = mPrice / 2;
            } else {
                mOldPrice.setVisibility(View.GONE);
            }

        if (mSelectCarts.isEmpty()) {
            mSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            mSubmit.setClickable(false);
        } else {
            mSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.purple31));
            mSubmit.setClickable(true);
        }


        mTotalPrice.setText("合计：¥ " + StringUtils.roundingDoubleStr((double) mPrice * 0.01, 2));
    }

    @Override
    protected void getData() {

        KDSPApiController.getInstance().findOne2oneItems("" + mType, new KDSPHttpCallBack() {
            @Override
            public void handleAPISuccessMessage(JSONObject response) {
                Gson gson = new Gson();

//                mItems = gson.fromJson(response.optJSONArray("items").toString(), new TypeToken<List<AuguryCart>>() {
//                }.getType());
//                mItems =    gson.fromJson(response.optJSONArray("items").toString(),AuguryCart.class);
                mItems = AuguryCart.parseListFromJSON(response.optJSONArray("items"));
                MasterAuguryCartActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingDialog();
                        refreshViewByType(LAY_TYPE_NORMAL);
                        refreshListViewComplete();
                        mAdapter.reset(mItems);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void handleAPIFailureMessage(Throwable error, String reqCode) {
                dismissLoadingDialog();
                refreshViewByType(LAY_TYPE_BAD);
            }
        });

    }

    @Override
    protected void getNextData() {

    }

    @Override
    protected boolean canPullDownRefresh() {
        return false;
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, MasterAuguryCartActivity.class);
        context.startActivity(intent);
    }

    public static void goToPage(Context context, int type) {
        Intent intent = new Intent(context, MasterAuguryCartActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, type);
        context.startActivity(intent);
    }


}
