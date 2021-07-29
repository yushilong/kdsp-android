package com.qizhu.rili.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qizhu.rili.AppContext;
import com.qizhu.rili.IntentExtraConfig;
import com.qizhu.rili.R;
import com.qizhu.rili.adapter.OrderListAdapter;
import com.qizhu.rili.bean.OrderDetail;
import com.qizhu.rili.data.OrderListDataAccessor;
import com.qizhu.rili.listener.DataEmptyListener;
import com.qizhu.rili.listener.OnSingleClickListener;
import com.qizhu.rili.utils.BroadcastUtils;

/**
 * Created by lindow on 09/03/2017.
 * 我的订单页
 */

public class OrderListActivity extends BaseListActivity {
    private int mMode;
    private OrderListDataAccessor mDataAccessor;

    //状态变更刷新
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BroadcastUtils.ACTION_REFRESH.equals(action)) {
                pullDownToRefresh();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mMode = getIntent().getIntExtra(IntentExtraConfig.EXTRA_MODE, 0);
        super.onCreate(savedInstanceState);
        BroadcastUtils.getInstance().registerReceiver(mReceiver, new IntentFilter(BroadcastUtils.ACTION_REFRESH));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastUtils.getInstance().unregisterReceiver(mReceiver);
    }

    @Override
    protected void initAdapter() {
        if (mDataAccessor == null) {
            mDataAccessor = new OrderListDataAccessor(mMode);
        }
        if (mAdapter == null) {
            mAdapter = new OrderListAdapter(this, mDataAccessor.mData);
        }
    }

    @Override
    protected void initTitleView(RelativeLayout titleView) {
        View view = mInflater.inflate(R.layout.title_has_back_btn, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mResources.getDimension(R.dimen.header_height));
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            view.findViewById(R.id.go_back).setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    goBack();
                }
            });
            TextView mTitle = (TextView) view.findViewById(R.id.title_txt);
            switch (mMode) {
                case OrderDetail.WAIT_PAY:
                    mTitle.setText(R.string.wait_pay);
                    break;
                case OrderDetail.HAS_PAID:
                    mTitle.setText(R.string.has_pay);
                    break;
                case OrderDetail.HAS_SEND:
                    mTitle.setText(R.string.wait_receive);
                    break;
                case OrderDetail.COMPLETED:
                    mTitle.setText(R.string.completed_order);
                    break;
            }

            titleView.addView(view, params);
        }
    }

    @Override
    protected void initEmptyView(RelativeLayout emptyLay) {
        //数据为空时的无数据提示
        View view = mInflater.inflate(R.layout.tip_no_data, null);

        if (view != null) {
            //设置无数据文案
            TextView tip = (TextView) view.findViewById(R.id.no_data_text);
            tip.setText(R.string.no_orders);
            mEmptyLay.addView(view);
        }
    }

    @Override
    protected void getData() {
        mDataAccessor.getData(buildDefaultDataGetListener(mDataAccessor, true, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (isEmpty) {
                    refreshViewByType(LAY_TYPE_EMPTY);
                } else {
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            }
        }));
    }

    @Override
    protected void getNextData() {
        mDataAccessor.getNextData(buildDefaultDataGetListener(mDataAccessor));
    }

    @Override
    public void pullDownToRefresh() {
        mDataAccessor.getAllDataFromServer(buildDefaultDataGetListener(mDataAccessor, false, new DataEmptyListener() {
            @Override
            public void onDataGet(boolean isEmpty) {
                if (isEmpty) {
                    refreshViewByType(LAY_TYPE_EMPTY);
                } else {
                    refreshViewByType(LAY_TYPE_NORMAL);
                }
            }
        }));
    }

    @Override
    protected RecyclerView.OnScrollListener getScrollListener() {
        return mScrollListener;
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

    /**
     * 支付成功则更新到待发货页
     */
    private void paySuccess() {
        AppContext.getAppHandler().sendEmptyMessage(AppContext.GET_UNREAD_COUNT);
        mDataAccessor.setMode(OrderDetail.HAS_PAID);
        pullDownToRefresh();
    }

    @Override
    public <T> void setExtraData(T t) {
        if (t instanceof String) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.orderId = (String) t;
            ((OrderListAdapter) mAdapter).refreshPayOrder(orderDetail);
        }
    }

    public static void goToPage(Context context, int mode) {
        Intent intent = new Intent(context, OrderListActivity.class);
        intent.putExtra(IntentExtraConfig.EXTRA_MODE, mode);
        context.startActivity(intent);
    }
}
