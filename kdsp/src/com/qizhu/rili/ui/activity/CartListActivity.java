package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.R;
import com.qizhu.rili.adapter.CartListAdapter;
import com.qizhu.rili.bean.Cart;
import com.qizhu.rili.data.CartDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;
import com.qizhu.rili.utils.DisplayUtils;
import com.qizhu.rili.utils.LogUtils;
import com.qizhu.rili.utils.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lindow on 08/03/2017.
 * 购物车列表
 */

public class CartListActivity extends BaseListActivity {
    private View     mBottomView;           //底部view
    private TextView mTotalPrice;
    private TextView mSubmit;

    private int mPrice = 0;
    private CartDataAccessor mDataAccessor;
    private Set<Cart> mSelectCarts = new HashSet<>();       //选择的购物车项

    //状态变更刷新
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_REFRESH.equals(action)) {
                pullDownToRefresh();
                mSelectCarts.clear();
                mSubmit.setBackgroundColor(ContextCompat.getColor(CartListActivity.this, R.color.gray));
                mSubmit.setClickable(false);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_REFRESH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);

            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            mTitle.setText(R.string.shopping_cart);

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initBottomView(RelativeLayout bottomView) {
        mBottomView = mInflater.inflate(R.layout.cart_bottom_lay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DisplayUtils.dip2px(50));
        if (mBottomView != null) {
            mTotalPrice = (TextView) mBottomView.findViewById(R.id.total_price);
            mTotalPrice.setText("合计：¥ " + StringUtils.roundingDoubleStr((double) mPrice / 100, 2));

            mSubmit = (TextView) mBottomView.findViewById(R.id.submit);

            mSubmit.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    StringBuffer cartIds = new StringBuffer();
                    for (Cart cart : mSelectCarts) {
                        cartIds.append(cart.cartId).append(",");
                    }
                    LogUtils.d("---" + String.valueOf(cartIds));
                    OrderConfirmActivity.goToPage(CartListActivity.this, String.valueOf(cartIds));
                }
            });

            bottomView.addView(mBottomView, params);
        }
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new CartDataAccessor();
        }
        if (mAdapter == null) {
            mAdapter = new CartListAdapter(this, mDataAccessor.mData);
        }
        mSelectCarts.clear();
        mSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        mSubmit.setClickable(false);
    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (isEmpty) {
                    mBottomView.setVisibility(View.GONE);
                    refreshViewByType(LAY_TYPE_EMPTY);
                } else {
                    mBottomView.setVisibility(View.VISIBLE);
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            }
        }));
    }

    @Override
    protected void getNextData() {

    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor, false, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (isEmpty) {
                    mBottomView.setVisibility(View.GONE);
                    refreshViewByType(LAY_TYPE_EMPTY);
                } else {
                    mBottomView.setVisibility(View.VISIBLE);
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            }
        }));
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
    }

    public void addCart(Cart cart) {
        mSelectCarts.add(cart);
        refreshPrice();
    }

    public void deleteCart(Cart cart) {
        mSelectCarts.remove(cart);
        refreshPrice();
    }

    public void displayEmpty() {
        mBottomView.setVisibility(View.GONE);
    }

    public void refreshPrice() {
        mPrice = 0;
        if (mSelectCarts.isEmpty()) {
            mSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            mSubmit.setClickable(false);
        } else {
            for (Cart cart : mSelectCarts) {
                mPrice = cart.price * cart.count + mPrice;
            }
            mSubmit.setBackgroundColor(ContextCompat.getColor(this, R.color.purple31));
            mSubmit.setClickable(true);
        }

        mTotalPrice.setText("合计：¥ " + StringUtils.roundingDoubleStr((double) mPrice / 100, 2));
    }

    public static void goToPage(Context context) {
        Intent intent = new Intent(context, CartListActivity.class);
        context.startActivity(intent);
    }
}
